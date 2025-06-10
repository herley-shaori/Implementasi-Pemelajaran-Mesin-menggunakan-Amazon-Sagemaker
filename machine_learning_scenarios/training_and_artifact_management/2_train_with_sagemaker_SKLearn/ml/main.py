import pandas as pd
from sklearn.datasets import make_classification
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report
from sagemaker.sklearn.estimator import SKLearn
import joblib
import os
from decouple import config

# Generate synthetic data
X, y = make_classification(n_samples=500, n_features=5, n_informative=3, n_redundant=0, random_state=42)
columns = [f'feature_{i+1}' for i in range(X.shape[1])]
df = pd.DataFrame(X, columns=columns)
df['target'] = y

print('Data shape:', df.shape)
print('First 5 rows:')
print(df.head())
print('\nDescribe:')
print(df.describe())
print('\nTarget value counts:')
print(df['target'].value_counts())

# Split data
X_train, X_test, y_train, y_test = train_test_split(df[columns], df['target'], test_size=0.2, random_state=42)

# Save train/test to CSV for SageMaker (in the same directory as main.py)
ml_dir = os.path.dirname(__file__)
train_path = os.path.join(ml_dir, 'train.csv')
test_path = os.path.join(ml_dir, 'test.csv')
df_train = pd.concat([X_train, y_train], axis=1)
df_test = pd.concat([X_test, y_test], axis=1)
df_train.to_csv(train_path, index=False)
df_test.to_csv(test_path, index=False)

# Define a simple training script for SageMaker SKLearn
train_script = '''
import pandas as pd
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report
import joblib
import argparse
import os

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--train', type=str, default='train.csv')
    parser.add_argument('--model-dir', type=str, default=os.environ.get('SM_MODEL_DIR', '.'))
    args = parser.parse_args()

    df = pd.read_csv(args.train)
    X = df.drop('target', axis=1)
    y = df['target']
    model = LogisticRegression()
    model.fit(X, y)
    joblib.dump(model, os.path.join(args.model_dir, 'model.pkl'))

if __name__ == '__main__':
    main()
'''
train_script_path = os.path.join(os.path.dirname(__file__), 'train_script.py')
with open(train_script_path, 'w') as f:
    f.write(train_script)

# Set up SageMaker SKLearn Estimator (local mode)
import sagemaker
from sagemaker.local import LocalSession

sagemaker_local_session = LocalSession()
sagemaker_local_session.config = {'local': {'local_code': True}}
role = config('SAGEMAKER_ROLE_ARN')  # Load SageMaker role ARN from .env

sklearn_estimator = SKLearn(
    entry_point=train_script_path,
    role=role,
    instance_type='local',
    sagemaker_session=sagemaker_local_session,
    framework_version='1.2-1',
    py_version='py3',
)

# Train the model using SageMaker SKLearn (local mode)
sklearn_estimator.fit({'train': f'file://{train_path}'})

# Load the trained model
def find_model_pkl(extract_dir):
    """Recursively find model.pkl in the extracted SageMaker model directory."""
    import glob
    model_files = glob.glob(os.path.join(extract_dir, '**', 'model.pkl'), recursive=True)
    if not model_files:
        raise FileNotFoundError('model.pkl not found in extracted SageMaker model archive')
    return model_files[0]

model_archive_path = sklearn_estimator.model_data.replace('file://', '')
extract_dir = '/tmp/sagemaker_model'
if model_archive_path.endswith('.tar.gz'):
    import tarfile
    with tarfile.open(model_archive_path, 'r:gz') as tar:
        tar.extractall(path=extract_dir)
    model_path = find_model_pkl(extract_dir)
else:
    model_path = os.path.join(model_archive_path, 'model.pkl')
model = joblib.load(model_path)

# Evaluate
def print_classification_report(model, X_test, y_test):
    y_pred = model.predict(X_test)
    print('\nClassification report:')
    print(classification_report(y_test, y_pred))

print_classification_report(model, df_test[columns], df_test['target'])

# Inference example
def print_inference_example(model, X_test):
    print('\n--- Inference Example ---')
    sample = X_test.iloc[0:1]
    pred = model.predict(sample)
    print('Sample features:', sample.values)
    print('Predicted class:', pred[0])

print_inference_example(model, df_test[columns])

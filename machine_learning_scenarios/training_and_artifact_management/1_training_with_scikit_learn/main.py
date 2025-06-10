import pandas as pd
from sklearn.datasets import make_classification
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report
import joblib

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

# Train model
model = LogisticRegression()
model.fit(X_train, y_train)

# Evaluate
y_pred = model.predict(X_test)
print('\nClassification report:')
print(classification_report(y_test, y_pred))

# Save model (to a git-ignored file)
model_path = 'machine_learning_scenarios/training_and_artifact_management/1_training_with_scikit_learn/model.pkl'
joblib.dump(model, model_path)
print(f'Model saved to {model_path}')

# Inference example
print('\n--- Inference Example ---')
loaded_model = joblib.load(model_path)
sample = X_test.iloc[0:1]
pred = loaded_model.predict(sample)
print('Sample features:', sample.values)
print('Predicted class:', pred[0])

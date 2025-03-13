package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Tags;

public class PrivateSagemakerDomainApp {
    public static void main(final String[] args) {
        App app = new App();

        // Create environment based on configuration
        Environment environment = Environment.builder()
                .region(AppConstants.REGION)
                .build();

        StackProps stackProps = StackProps.builder()
                .env(environment)
                .description(AppConstants.PROJECT_DESCRIPTION)
                .build();

        // Create VPC Stack first
        VpcStack vpcStack = new VpcStack(app, "VpcStack", stackProps);

        S3BucketStack s3BucketStack = new S3BucketStack(app, "S3BucketStack", stackProps);

        // Create SageMaker Domain Stack
        SagemakerDomainStack sagemakerDomainStack = new SagemakerDomainStack(app, "SagemakerDomainStack", vpcStack, s3BucketStack, stackProps, vpcStack.getVpc());

        // Create SageMaker User Profile Stack
        new SagemakerUserProfileStack(app, "SagemakerUserProfileStack", stackProps, sagemakerDomainStack.getSagemakerDomain().getAttrDomainId(), sagemakerDomainStack.getSagemakerExecutionRole());

        // Add tags to all resources
        AppConstants.TAGS.forEach((key, value) -> Tags.of(app).add(key, value));

        app.synth();
    }
}
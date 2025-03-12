package com.myorg;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketProps;
import software.constructs.Construct;

public class S3BucketStack extends Stack {

    private final Bucket s3Bucket;

    public S3BucketStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Retrieve the bucket name from the 'Name' attribute in the application configuration
        String bucketName = AppConstants.TAGS.get("Name");

        // Create the S3 bucket with the specified configurations
        this.s3Bucket = new Bucket(this, "SageMakerDataBucket", BucketProps.builder()
                .bucketName(bucketName)
                .removalPolicy(RemovalPolicy.DESTROY) // Delete the bucket when the stack is destroyed
                .autoDeleteObjects(true) // Automatically delete objects in the bucket upon stack deletion
                .build());
    }

    public Bucket getS3Bucket() {
        return this.s3Bucket;
    }
}

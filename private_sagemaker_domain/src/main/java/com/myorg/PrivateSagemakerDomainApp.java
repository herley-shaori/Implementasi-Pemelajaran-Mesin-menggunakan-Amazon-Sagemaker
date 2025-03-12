package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Tags;

public class PrivateSagemakerDomainApp {
    public static void main(final String[] args) {
        App app = new App();

        // Buat environment berdasarkan konfigurasi
        Environment environment = Environment.builder()
                .region(AppConstants.REGION)
                .build();

        StackProps stackProps = StackProps.builder()
                .env(environment)
                .description(AppConstants.PROJECT_DESCRIPTION)
                .build();

        // Membuat VPC Stack terlebih dahulu
        VpcStack vpcStack = new VpcStack(app, "VpcStack", stackProps);

        S3BucketStack s3BucketStack = new S3BucketStack(app, "S3BucketStack", stackProps);

        // Membuat SageMaker Domain Stack
        new SagemakerDomainStack(app, "SagemakerDomainStack", vpcStack, s3BucketStack, stackProps, vpcStack.getVpc());

        // Menambahkan tag ke semua resource
        AppConstants.TAGS.forEach((key, value) -> Tags.of(app).add(key, value));

        app.synth();
    }
}

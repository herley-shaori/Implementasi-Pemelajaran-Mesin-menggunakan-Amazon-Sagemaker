package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Tags;
import software.amazon.awscdk.Environment;

public class PrivateSagemakerDomainApp {
    public static void main(final String[] args) {
        App app = new App();

        // Create Environment with region from AppConstants
        Environment environment = Environment.builder()
                .region(AppConstants.REGION)
                .build();

        // Create StackProps with tags, environment, and description from AppConstants
        StackProps stackProps = StackProps.builder()
                .tags(AppConstants.TAGS)
                .env(environment)
                .description(AppConstants.PROJECT_DESCRIPTION)
                .build();

        new PrivateSagemakerDomainStack(app, "PrivateSagemakerDomainStack", stackProps);

        // Apply tags to the stack
        AppConstants.TAGS.forEach((key, value) -> Tags.of(app).add(key, value));

        // Initialize VPC stack first
        VpcStack vpcStack = new VpcStack(app, "VpcStack", stackProps);

        app.synth();
    }
}
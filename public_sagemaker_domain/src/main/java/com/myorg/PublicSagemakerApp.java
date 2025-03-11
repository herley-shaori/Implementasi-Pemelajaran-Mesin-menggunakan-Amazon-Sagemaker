package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class PublicSagemakerApp {
    public static void main(final String[] args) {
        App app = new App();

        StackProps stackProps = StackProps.builder()
                .env(Environment.builder().region(AppConstants.REGION).build())
                .description(AppConstants.PROJECT_DESCRIPTION)
                .tags(AppConstants.TAGS)
                .build();

        // Only instantiate the stack; SageMakerVPC is now inside PublicSagemakerStack
        new PublicSagemakerStack(app, AppConstants.TAGS.get("Name"), stackProps);

        app.synth();
    }
}
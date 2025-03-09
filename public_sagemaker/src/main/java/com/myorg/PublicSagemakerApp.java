package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class PublicSagemakerApp {
    public static void main(final String[] args) {
        App app = new App();

        // replace lines 11 to 13
        new PublicSagemakerStack(app, "PublicSagemakerStack", StackProps.builder()
                        .env(Environment.builder().region(AppConstants.REGION).build())
                        .description(AppConstants.PROJECT_DESCRIPTION)
                        .build());

        app.synth();
    }
}


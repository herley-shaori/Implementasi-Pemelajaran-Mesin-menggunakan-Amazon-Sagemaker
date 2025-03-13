package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.sagemaker.CfnUserProfile;
import software.constructs.Construct;

public class SagemakerUserProfileStack extends Stack {

    public SagemakerUserProfileStack(final Construct scope, final String id, final StackProps props, final String domainId, final Role executionRole) {
        super(scope, id, props);

        // Create SageMaker User Profile
        CfnUserProfile userProfile = CfnUserProfile.Builder.create(this, "SagemakerUserProfile")
                .domainId(domainId)
                .userProfileName("herley")
                .userSettings(CfnUserProfile.UserSettingsProperty.builder()
                        .executionRole(executionRole.getRoleArn())
                        .build())
                .build();
    }
}
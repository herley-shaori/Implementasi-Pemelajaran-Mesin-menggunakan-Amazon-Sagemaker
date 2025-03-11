package com.myorg;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Tags;
import software.amazon.awscdk.services.ec2.ISubnet;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.sagemaker.CfnDomain;
import software.amazon.awscdk.services.sagemaker.CfnUserProfile;
import software.constructs.Construct;

import java.util.List;

public class PublicSagemakerStack extends Stack {
    private final SageMakerVPC sageMakerVPC;

    public PublicSagemakerStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Instantiate SageMakerVPC within this Stack
        this.sageMakerVPC = new SageMakerVPC(this, "SageMakerVPC", props);

        // Create SageMaker Execution Role
        Role sagemakerRole = Role.Builder.create(this, "SageMakerExecutionRole")
                .roleName("SageMakerExecutionRole")
                .assumedBy(new ServicePrincipal("sagemaker.amazonaws.com"))
                .description("IAM role for SageMaker to access AWS resources")
                .build();

        // Grant SageMaker full access (you can customize this based on your needs)
        sagemakerRole.addToPolicy(PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .actions(List.of("sagemaker:*",
                        "s3:*",
                        "ecr:*",
                        "cloudwatch:*",
                        "logs:*",
                        "ec2:*",
                        "glue:*"))
                .resources(List.of("*"))
                .build());

        // Create SageMaker Domain with public internet access
        CfnDomain domain = CfnDomain.Builder.create(this, "SageMakerDomain")
                .domainName(AppConstants.TAGS.get("Name"))
                .authMode("IAM") // Use IAM for authentication
                .vpcId(this.sageMakerVPC.getVpc().getVpcId())
                .subnetIds(this.sageMakerVPC.getVpc().getPublicSubnets().stream()
                        .map(ISubnet::getSubnetId)
                        .collect(java.util.stream.Collectors.toList()))
                .appNetworkAccessType("PublicInternetOnly") // Enable public internet access
                .defaultUserSettings(CfnDomain.UserSettingsProperty.builder()
                        .executionRole(sagemakerRole.getRoleArn()) // Use the ARN of the created role
                        .build())
                .build();

        // Secara manual propagasi tags ke SageMaker Domain
        if (props != null && props.getTags() != null) {
            props.getTags().forEach((key, value) -> {
                Tags.of(domain).add(key, value);
            });
        }

        // Create User Profile for "herley"
        CfnUserProfile userProfile = CfnUserProfile.Builder.create(this, "HerleyUserProfile")
                .domainId(domain.getAttrDomainId())
                .userProfileName("herley")
                .build();

        // Set removal policies to DESTROY to ensure resources are deleted on stack destroy
        domain.applyRemovalPolicy(RemovalPolicy.DESTROY);
        userProfile.applyRemovalPolicy(RemovalPolicy.DESTROY);

        // Ensure that the User Profile is deleted before the Domain
        userProfile.getNode().addDependency(domain);
    }

    public SageMakerVPC getSageMakerVPC() {
        return this.sageMakerVPC;
    }
}

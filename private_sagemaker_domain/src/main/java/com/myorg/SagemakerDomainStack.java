package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.sagemaker.CfnDomain;
import software.constructs.Construct;

import java.util.List;
import java.util.stream.Collectors;

public class SagemakerDomainStack extends Stack {
    private final CfnDomain sagemakerDomain;
    private final Role sagemakerExecutionRole;

    public SagemakerDomainStack(final Construct scope, final String id, final VpcStack vpcStack, final S3BucketStack s3BucketStack, final StackProps props, final Vpc vpc) {
        super(scope, id, props);

        // IAM role for SageMaker
        this.sagemakerExecutionRole = Role.Builder.create(this, "SagemakerExecutionRole")
                .assumedBy(new ServicePrincipal("sagemaker.amazonaws.com"))
                .managedPolicies(List.of(
                        software.amazon.awscdk.services.iam.ManagedPolicy.fromAwsManagedPolicyName("AmazonSageMakerFullAccess"),
                        software.amazon.awscdk.services.iam.ManagedPolicy.fromAwsManagedPolicyName("AmazonS3FullAccess")
                ))
                .build();

        // Create SageMaker Domain with updated configuration
        this.sagemakerDomain = CfnDomain.Builder.create(this, "SagemakerVpcDomain")
                .authMode("IAM")
                .defaultUserSettings(CfnDomain.UserSettingsProperty.builder()
                        .executionRole(sagemakerExecutionRole.getRoleArn())
                        .securityGroups(List.of(vpcStack.getSecurityGroup().getSecurityGroupId()))
                        .build())
                .domainName("private-sagemaker-domain")
                .subnetIds(vpc.getPrivateSubnets().stream()
                        .map(ISubnet::getSubnetId)
                        .collect(Collectors.toList()))
                .vpcId(vpc.getVpcId())
                .appNetworkAccessType("VpcOnly") // Specify network access type
                .build();
    }

    public CfnDomain getSagemakerDomain() {
        return sagemakerDomain;
    }

    public Role getSagemakerExecutionRole() {
        return sagemakerExecutionRole;
    }
}
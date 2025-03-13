package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.customresources.AwsCustomResource;
import software.amazon.awscdk.customresources.AwsCustomResourcePolicy;
import software.amazon.awscdk.customresources.AwsSdkCall;
import software.amazon.awscdk.customresources.PhysicalResourceId;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.sagemaker.CfnDomain;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SagemakerDomainStack extends Stack {

    public SagemakerDomainStack(final Construct scope, final String id, final VpcStack vpcStack, final S3BucketStack s3BucketStack, final StackProps props, final Vpc vpc) {
        super(scope, id, props);

        // IAM role for SageMaker
        Role sagemakerExecutionRole = Role.Builder.create(this, "SagemakerExecutionRole")
                .assumedBy(new ServicePrincipal("sagemaker.amazonaws.com"))
                .managedPolicies(List.of(
                        software.amazon.awscdk.services.iam.ManagedPolicy.fromAwsManagedPolicyName("AmazonSageMakerFullAccess"),
                        software.amazon.awscdk.services.iam.ManagedPolicy.fromAwsManagedPolicyName("AmazonS3FullAccess")
                ))
                .build();

        // Security Group for SageMaker Domain
        SecurityGroup sagemakerSecurityGroup = SecurityGroup.Builder.create(this, "SagemakerSecurityGroup")
                .vpc(vpc)
                .description("Security group for SageMaker Domain")
                .allowAllOutbound(true)
                .build();

        // Allow all traffic from within the VPC
        sagemakerSecurityGroup.addIngressRule(
                Peer.ipv4(vpc.getVpcCidrBlock()),
                Port.allTraffic(),
                "Allow all traffic from within the VPC"
        );

        // Create SageMaker Domain with updated configuration
        CfnDomain sagemakerDomain = CfnDomain.Builder.create(this, "SagemakerVpcDomain")
                .authMode("IAM")
                .defaultUserSettings(CfnDomain.UserSettingsProperty.builder()
                        .executionRole(sagemakerExecutionRole.getRoleArn())
                        .securityGroups(List.of(sagemakerSecurityGroup.getSecurityGroupId()))
                        .build())
                .domainName("private-sagemaker-domain")
                .subnetIds(vpc.getPrivateSubnets().stream()
                        .map(ISubnet::getSubnetId)
                        .collect(Collectors.toList()))
                .vpcId(vpc.getVpcId())
                .appNetworkAccessType("VpcOnly") // Specify network access type
                .build();

        // Custom Resource to delete domain with RetentionPolicy
        AwsSdkCall deleteDomainCall = AwsSdkCall.builder()
                .service("SageMaker")
                .action("deleteDomain")
                .parameters(Map.of(
                        "DomainId", sagemakerDomain.getAttrDomainId(),
                        "RetentionPolicy", Map.of("HomeEfsFileSystem", "Delete")
                ))
                .physicalResourceId(PhysicalResourceId.of(sagemakerDomain.getAttrDomainId()))
                .build();

        AwsCustomResource deleteDomainResource = AwsCustomResource.Builder.create(this, "DeleteSagemakerDomain")
                .onDelete(deleteDomainCall)
                .policy(AwsCustomResourcePolicy.fromStatements(List.of(
                        PolicyStatement.Builder.create()
                                .actions(List.of("sagemaker:DeleteDomain"))
                                .resources(List.of("*"))
                                .effect(Effect.ALLOW)
                                .build()
                )))
                .build();

        deleteDomainResource.getNode().addDependency(sagemakerDomain);
    }
}

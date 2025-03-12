package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.sagemaker.CfnDomain;
import software.constructs.Construct;

import java.util.List;

public class SagemakerDomainStack extends Stack {

    public SagemakerDomainStack(final Construct scope, final String id, final VpcStack vpcStack, final StackProps props) {
        super(scope, id, props);

        // IAM role untuk SageMaker
        Role sagemakerExecutionRole = Role.Builder.create(this, "SagemakerExecutionRole")
                .assumedBy(new ServicePrincipal("sagemaker.amazonaws.com"))
                .managedPolicies(List.of(
                        software.amazon.awscdk.services.iam.ManagedPolicy.fromAwsManagedPolicyName("AmazonSageMakerFullAccess"),
                        software.amazon.awscdk.services.iam.ManagedPolicy.fromAwsManagedPolicyName("AmazonS3FullAccess")
                ))
                .build();

        // Definisi SageMaker Domain dengan mode VPC
        CfnDomain sagemakerDomain = CfnDomain.Builder.create(this, "SagemakerVpcDomain")
                .authMode("IAM")
                .defaultUserSettings(CfnDomain.UserSettingsProperty.builder()
                        .executionRole(sagemakerExecutionRole.getRoleArn())
                        .build())
                .domainName("private-sagemaker-domain")
                .subnetIds(List.of(vpcStack.getVpc().getPrivateSubnets().get(0).getSubnetId()))
                .vpcId(vpcStack.getVpc().getVpcId())
                .build();
    }
}

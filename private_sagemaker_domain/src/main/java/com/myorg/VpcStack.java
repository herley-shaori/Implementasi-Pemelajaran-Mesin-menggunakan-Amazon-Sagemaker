package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Tags;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import java.util.List;

public class VpcStack extends Stack {

    private final Vpc vpc;
    private final SecurityGroup securityGroup;

    public VpcStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        assert AppConstants.VPC_CIDR != null;
        assert AppConstants.PUBLIC_SUBNET_CIDR != null;
        assert AppConstants.PRIVATE_SUBNET_CIDR != null;

        this.vpc = Vpc.Builder.create(this, "PrivateVpc")
                .ipAddresses(IpAddresses.cidr(AppConstants.VPC_CIDR))
                .maxAzs(1)
                .natGateways(1) // NAT Gateway for internet access from private subnet
                .subnetConfiguration(List.of(
                        SubnetConfiguration.builder()
                                .name("PublicSubnet")
                                .subnetType(SubnetType.PUBLIC)
                                .cidrMask(Integer.parseInt(AppConstants.PUBLIC_SUBNET_CIDR))
                                .build(),
                        SubnetConfiguration.builder()
                                .subnetType(SubnetType.PRIVATE_WITH_EGRESS)
                                .name("PrivateSubnet")
                                .cidrMask(Integer.parseInt(AppConstants.PRIVATE_SUBNET_CIDR))
                                .build()
                ))
                .build();

        this.securityGroup = SecurityGroup.Builder.create(this, "SagemakerSG")
                .vpc(this.vpc)
                .description("Security group for private SageMaker domain")
                .allowAllOutbound(true)
                .build();

        // Allow all traffic from within the VPC
        this.securityGroup.addIngressRule(
                Peer.ipv4(this.vpc.getVpcCidrBlock()),
                Port.allTraffic(),
                "Allow all traffic from within the VPC"
        );

        // Add Gateway VPC Endpoint for S3
        this.vpc.addGatewayEndpoint("S3GatewayEndpoint", GatewayVpcEndpointOptions.builder()
                .service(GatewayVpcEndpointAwsService.S3)
                .subnets(List.of(SubnetSelection.builder()
                        .subnetType(SubnetType.PRIVATE_WITH_EGRESS)
                        .build()))
                .build());

        // Existing Interface VPC Endpoints
        InterfaceVpcEndpoint sageMakerApiEndpoint = InterfaceVpcEndpoint.Builder.create(this, "SageMakerApiEndpoint")
                .vpc(this.vpc)
                .service(InterfaceVpcEndpointAwsService.SAGEMAKER_API)
                .securityGroups(List.of(this.securityGroup))
                .build();

        InterfaceVpcEndpoint sageMakerRuntimeEndpoint = InterfaceVpcEndpoint.Builder.create(this, "SageMakerRuntimeEndpoint")
                .vpc(this.vpc)
                .service(InterfaceVpcEndpointAwsService.SAGEMAKER_RUNTIME)
                .securityGroups(List.of(this.securityGroup))
                .build();

        InterfaceVpcEndpoint s3InterfaceEndpoint = InterfaceVpcEndpoint.Builder.create(this, "S3InterfaceEndpoint")
                .vpc(this.vpc)
                .service(InterfaceVpcEndpointAwsService.S3)
                .securityGroups(List.of(this.securityGroup))
                .build();
        Tags.of(s3InterfaceEndpoint).add("Name", "S3InterfaceEndpoint");
        Tags.of(sageMakerApiEndpoint).add("Name", "SageMakerApiEndpoint");
        Tags.of(sageMakerRuntimeEndpoint).add("Name", "SageMakerRuntimeEndpoint");
    }

    public Vpc getVpc() {
        return this.vpc;
    }

    public SecurityGroup getSecurityGroup() {
        return this.securityGroup;
    }
}
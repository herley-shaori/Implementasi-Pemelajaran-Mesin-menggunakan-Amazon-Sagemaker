package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;

import java.util.List;

public class VpcStack extends Stack {

    private final Vpc vpc;
    private final SecurityGroup securityGroup;

    public VpcStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        assert AppConstants.VPC_CIDR != null;
        this.vpc = Vpc.Builder.create(this, "PrivateVpc")
                .ipAddresses(IpAddresses.cidr(AppConstants.VPC_CIDR))
                .maxAzs(1)
                .natGateways(1) // NAT Gateway untuk akses internet dari subnet private
                .subnetConfiguration(List.of(
                        SubnetConfiguration.builder()
                                .name("PublicSubnet")
                                .subnetType(SubnetType.PUBLIC)
                                .cidrMask(24)
                                .build(),
                        SubnetConfiguration.builder()
                                .subnetType(SubnetType.PRIVATE_WITH_EGRESS)
                                .name("PrivateSubnet")
                                .cidrMask(24)
                                .build()
                ))
                .build();

        this.securityGroup = SecurityGroup.Builder.create(this, "SagemakerSG")
                .vpc(this.vpc)
                .description("Security group for private SageMaker domain")
                .allowAllOutbound(true)
                .build();
    }

    public Vpc getVpc() {
        return this.vpc;
    }

    public SecurityGroup getSecurityGroup() {
        return this.securityGroup;
    }
}

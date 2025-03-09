package com.myorg;

import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.IpAddresses; // Import the IpAddresses class
import software.amazon.awscdk.StackProps; // Import StackProps
import software.amazon.awscdk.Tags; // Import Tags utility for applying tags
import software.constructs.Construct;

public class SageMakerVPC {

    private final Vpc vpc;

    /**
     * Creates a new SageMakerVPC.
     *
     * @param scope The scope in which to define this construct.
     * @param id    The id of this construct.
     * @param props The stack properties to apply.
     */
    public SageMakerVPC(final Construct scope, final String id, final StackProps props) {
        assert AppConstants.VPC_CIDR != null; // Retain the assertion from the original
        this.vpc = Vpc.Builder.create(scope, id)
                .ipAddresses(IpAddresses.cidr(AppConstants.VPC_CIDR)) // Use IpAddresses.cidr
                .maxAzs(3)
                .build();

        // Apply tags from StackProps if provided
        if (props != null && props.getTags() != null) {
            props.getTags().forEach((key, value) -> Tags.of(this.vpc).add(key, value));
        }
    }

    /**
     * Returns the VPC.
     *
     * @return The VPC.
     */
    public Vpc getVpc() {
        return this.vpc;
    }
}
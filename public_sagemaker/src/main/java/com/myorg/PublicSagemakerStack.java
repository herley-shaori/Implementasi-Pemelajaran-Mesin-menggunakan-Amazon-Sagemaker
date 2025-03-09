package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.constructs.Construct;

public class PublicSagemakerStack extends Stack {
    private final SageMakerVPC sageMakerVPC;

    public PublicSagemakerStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Instantiate SageMakerVPC within this Stack
        this.sageMakerVPC = new SageMakerVPC(this, "SageMakerVPC", props);
    }

    // Optional: Provide a getter if you need to access the VPC elsewhere
    public SageMakerVPC getSageMakerVPC() {
        return this.sageMakerVPC;
    }
}
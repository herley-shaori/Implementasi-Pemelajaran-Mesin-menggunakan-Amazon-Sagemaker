package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class PublicSagemakerStack extends Stack {
    public PublicSagemakerStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
    }
}

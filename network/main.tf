resource "aws_vpc" "main" {
  cidr_block              = var.vpc_cidr
  enable_dns_support      = true
  enable_dns_hostnames    = true
  tags = merge({
    Name = "vpc-${var.general_suffix}"
  }, var.common_tags)
}

resource "aws_security_group" "sagemaker_domain" {
  name        = "${var.sg_name_prefix}-${var.general_suffix}"
  description = "Security group for SageMaker Domain"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = [aws_vpc.main.cidr_block]
  }

  ingress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    self      = true
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge({
    Name = "${var.sg_name_prefix}-${var.general_suffix}"
  }, var.common_tags)
}

resource "aws_subnet" "sagemaker_domain" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.subnet_cidr
  availability_zone       = var.availability_zone
  map_public_ip_on_launch = true
  tags = merge({
    Name = "${var.subnet_name_prefix}-${var.general_suffix}"
  }, var.common_tags)
}

resource "aws_vpc_endpoint" "sagemaker_api" {
  vpc_id            = aws_vpc.main.id
  service_name      = "com.amazonaws.${var.region}.sagemaker.api"
  vpc_endpoint_type = "Interface"
  subnet_ids        = [aws_subnet.sagemaker_domain.id]
  security_group_ids = [aws_security_group.sagemaker_domain.id]
  private_dns_enabled = true
  tags = merge({
    Name = "sagemaker-api-endpoint-${var.general_suffix}"
  }, var.common_tags)
}

resource "aws_vpc_endpoint" "sagemaker_runtime" {
  vpc_id            = aws_vpc.main.id
  service_name      = "com.amazonaws.${var.region}.sagemaker.runtime"
  vpc_endpoint_type = "Interface"
  subnet_ids        = [aws_subnet.sagemaker_domain.id]
  security_group_ids = [aws_security_group.sagemaker_domain.id]
  private_dns_enabled = true
  tags = merge({
    Name = "sagemaker-runtime-endpoint-${var.general_suffix}"
  }, var.common_tags)
}

resource "aws_vpc_endpoint" "sts" {
  vpc_id            = aws_vpc.main.id
  service_name      = "com.amazonaws.${var.region}.sts"
  vpc_endpoint_type = "Interface"
  subnet_ids        = [aws_subnet.sagemaker_domain.id]
  security_group_ids = [aws_security_group.sagemaker_domain.id]
  private_dns_enabled = true
  tags = merge({
    Name = "sts-endpoint-${var.general_suffix}"
  }, var.common_tags)
}

resource "aws_vpc_endpoint" "s3" {
  vpc_id       = aws_vpc.main.id
  service_name = "com.amazonaws.${var.region}.s3"
  vpc_endpoint_type = "Gateway"
  route_table_ids = [
    aws_vpc.main.default_route_table_id
  ]
  tags = merge({
    Name = "s3-endpoint-${var.general_suffix}"
  }, var.common_tags)
}
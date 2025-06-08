resource "aws_vpc" "main" {
  cidr_block = var.vpc_cidr
  tags = {
    Name = "${var.vpc_name_prefix}-${var.vpc_suffix}"
  }
}

resource "aws_security_group" "sagemaker_domain" {
  name        = "${var.sg_name_prefix}-${var.vpc_suffix}"
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

  tags = {
    Name = "${var.sg_name_prefix}-${var.vpc_suffix}"
  }
}

resource "aws_subnet" "sagemaker_domain" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.subnet_cidr
  availability_zone       = var.availability_zone
  map_public_ip_on_launch = true
  tags = {
    Name = "${var.subnet_name_prefix}-${var.vpc_suffix}"
  }
}
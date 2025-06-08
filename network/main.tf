resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  tags = {
    Name = "ml-vpc"
  }
}

resource "aws_security_group" "sagemaker_domain" {
  name        = "sagemaker-domain-sg-${random_string.vpc_suffix.result}"
  description = "Security group for SageMaker Domain"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = [aws_vpc.main.cidr_block]
  }

  ingress {
    from_port       = 0
    to_port         = 0
    protocol        = "-1"
    security_groups = [aws_security_group.sagemaker_domain.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "sagemaker-domain-sg-${random_string.vpc_suffix.result}"
  }
}

resource "aws_subnet" "sagemaker_domain" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "ap-southeast-3a"
  map_public_ip_on_launch = true
  tags = {
    Name = "sagemaker-domain-subnet-${random_string.vpc_suffix.result}"
  }
}
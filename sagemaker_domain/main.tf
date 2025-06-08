resource "aws_iam_role" "sagemaker_execution_role" {
  name = "sagemaker-execution-role-${random_string.vpc_suffix.result}"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect = "Allow",
      Principal = {
        Service = "sagemaker.amazonaws.com"
      },
      Action = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "sagemaker_execution_policy" {
  role       = aws_iam_role.sagemaker_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSageMakerFullAccess"
}

resource "aws_sagemaker_domain" "main" {
  domain_name = "ml-domain-${random_string.vpc_suffix.result}"
  auth_mode   = "IAM"
  vpc_id      = aws_vpc.main.id
  subnet_ids  = [aws_subnet.sagemaker_domain.id]
  app_network_access_type = "VpcOnly"

  default_user_settings {
    execution_role = aws_iam_role.sagemaker_execution_role.arn
  }
}
resource "aws_iam_role" "sagemaker_execution_role" {
  name = "sagemaker-execution-role-${var.general_suffix}"
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
  tags = var.common_tags
}

resource "aws_iam_role_policy_attachment" "sagemaker_execution_policy" {
  role       = aws_iam_role.sagemaker_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSageMakerFullAccess"
}

resource "aws_sagemaker_domain" "main" {
  domain_name = "ml-domain-${var.general_suffix}"
  auth_mode   = "IAM"
  vpc_id      = var.vpc_id
  subnet_ids  = [var.subnet_id]
  app_network_access_type = "VpcOnly"

  default_user_settings {
    execution_role = aws_iam_role.sagemaker_execution_role.arn
  }
  tags = var.common_tags
}
locals {
  common_tags = {
    Project     = "Machine Learning Implementation using SageMaker"
    ManagedBy   = "Terraform"
    Owner       = "ML Team",
    Name        = "ml-implementation-sagemaker-domain"
    Environment = "Production"
    Version     = "1.0"
  }
}

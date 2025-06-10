variable "vpc_id" {}
variable "sg_id" {}

variable "common_tags" {
  description = "Common tags to apply to all resources"
  type        = map(string)
}

variable "general_suffix" {
  description = "General suffix to append to resource names"
  type        = string
}

variable "sagemaker_user_instance_type" {
  description = "Instance type for SageMaker user profile Jupyter server."
  type        = string
}

variable "subnet_ids" {
  description = "List of subnet IDs for SageMaker Domain"
  type        = list(string)
}

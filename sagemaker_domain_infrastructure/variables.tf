variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
}

# variable "subnet_cidr" {
#   description = "CIDR block for the SageMaker subnet"
#   type        = string
# }

# variable "availability_zone" {
#   description = "Availability zone for the subnet"
#   type        = string
# }

variable "region" {
  description = "AWS region"
  type        = string
}

variable "domain_name" {
  description = "SageMaker domain name"
  type        = string
}

variable "sg_name_prefix" {
  description = "Prefix for security group name"
  type        = string
  default     = "sagemaker-domain-sg"
}

variable "subnet_name_prefix" {
  description = "Prefix for subnet name"
  type        = string
  default     = "sagemaker-domain-subnet"
}

variable "sagemaker_user_instance_type" {
  description = "Instance type for SageMaker user profile Jupyter server."
  type        = string
}

variable "subnet_cidrs" {
  description = "List of CIDR blocks for the SageMaker subnets in multiple AZs"
  type        = list(string)
}

variable "availability_zones" {
  description = "List of availability zones for the subnets"
  type        = list(string)
}

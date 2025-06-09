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

variable "general_suffix" {
  description = "General suffix to append to resource names"
  type        = string
}

variable "common_tags" {
  description = "Common tags to apply to all resources"
  type        = map(string)
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
}

variable "region" {
  description = "AWS region for VPC endpoints"
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

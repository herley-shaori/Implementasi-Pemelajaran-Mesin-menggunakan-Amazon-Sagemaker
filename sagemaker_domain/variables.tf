variable "vpc_id" {}
variable "subnet_id" {}
variable "sg_id" {}

variable "common_tags" {
  description = "Common tags to apply to all resources"
  type        = map(string)
}

variable "general_suffix" {
  description = "General suffix to append to resource names"
  type        = string
}

resource "random_string" "vpc_suffix" {
  length  = 6
  upper   = false
  special = false
}
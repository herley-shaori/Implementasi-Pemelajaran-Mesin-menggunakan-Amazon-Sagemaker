resource "random_string" "vpc_suffix" {
  length  = 6
  upper   = false
  special = false
}

resource "random_string" "general_suffix" {
  length  = 6
  upper   = false
  special = false
}
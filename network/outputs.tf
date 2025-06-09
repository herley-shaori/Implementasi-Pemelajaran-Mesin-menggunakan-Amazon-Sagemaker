output "vpc_id" {
  value = aws_vpc.main.id
}

output "subnet_ids" {
  value = aws_subnet.sagemaker_domain[*].id
}

output "sg_id" {
  value = aws_security_group.sagemaker_domain.id
}

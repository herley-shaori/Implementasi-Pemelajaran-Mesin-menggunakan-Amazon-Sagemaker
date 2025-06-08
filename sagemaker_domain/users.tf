resource "aws_sagemaker_user_profile" "data_scientist_one" {
  domain_id         = aws_sagemaker_domain.main.id
  user_profile_name = "data-scientist-one-${var.general_suffix}"
  tags              = var.common_tags

  user_settings {
    execution_role = aws_iam_role.sagemaker_execution_role.arn
    jupyter_server_app_settings {
      default_resource_spec {
        instance_type = "system"
      }
    }
  }
}
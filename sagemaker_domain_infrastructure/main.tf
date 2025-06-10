module "network" {
  source              = "./network"
  vpc_cidr            = var.vpc_cidr
  subnet_cidrs        = var.subnet_cidrs
  availability_zones  = var.availability_zones
  sg_name_prefix      = var.sg_name_prefix
  subnet_name_prefix  = var.subnet_name_prefix
  general_suffix      = random_string.general_suffix.result
  common_tags         = local.common_tags
  region              = var.region
}

module "sagemaker_domain" {
  source      = "./sagemaker_domain"
  vpc_id      = module.network.vpc_id
  subnet_ids  = module.network.subnet_ids
  sg_id       = module.network.sg_id
  general_suffix = random_string.general_suffix.result
  common_tags = local.common_tags
  sagemaker_user_instance_type = var.sagemaker_user_instance_type
}
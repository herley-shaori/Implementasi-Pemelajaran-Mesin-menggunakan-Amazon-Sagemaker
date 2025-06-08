module "network" {
  source              = "./network"
  vpc_cidr            = var.vpc_cidr
  subnet_cidr         = var.subnet_cidr
  availability_zone   = var.availability_zone
  sg_name_prefix      = var.sg_name_prefix
  subnet_name_prefix  = var.subnet_name_prefix
  vpc_name_prefix     = var.vpc_name_prefix
  vpc_suffix          = random_string.vpc_suffix.result
}

# module "sagemaker_domain" {
#   source     = "./sagemaker_domain"
#   vpc_id     = module.network.vpc_id
#   subnet_id  = module.network.subnet_id
#   sg_id      = module.network.sg_id
#   vpc_suffix = random_string.vpc_suffix.result
# }
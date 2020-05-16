def call(Map config) {

	def details = [:]

	details.'engine'		= "${config.engine}"
	details.'branch'		= "${env.JOB_BASE_NAME.toLowerCase()}".replace("%2f","-")

	if ("${config.type}" == 'app') {
		switch("${details.branch}") {
			case 'master':
				details.'environment' = 'prd'
				details.'registry_port' = "${env.PRD_REGISTRY_PORT}"
				break
			case 'develop':
				details.'environment' = 'hml'
				details.'registry_port' = "${env.HML_REGISTRY_PORT}"
				break
			case ~/^(feature|hotfix).*$/:
				details.'environment' = 'dev'
				details.'registry_port' = "${env.DEV_REGISTRY_PORT}"
				break
			default:
				return 'Undefined environment'
				break
		}
	}
	else {
		details.'environment' = 'ine'
		details.'registry_port' = "${env.INE_REGISTRY_PORT}"
	}

	echo "Repository: ${details.git_repo}"
	echo "Branch: ${details.branch}"
	echo "Environment: ${details.environment}"

	details.'agentLabel' = "${details.engine}"+'-build-image'

	if( "${details.environment}" == 'ine' ){
		if ( "${details.engine}" == 'docker' ) {
			dockerPipelineDefinition(details)
		}
	}
	else if ( "${details.environment}" == 'dev' ) {
		if ( "${details.engine}" == 'php' ) {
			phpPipelineDefinition(details)
		}
	} 
	else if ( "${details.environment}" == 'hml' ) {
		if ( "${details.engine}" == 'php' ) {
			phpPipelineDefinition(details)
		}
	} 
	else if ( "${details.environment}" == 'prd' ) {
		if ( "${details.engine}" == 'php' ) {
			phpPipelineDefinition(details)
		}
	} 
	else {
		error( "[DeliveryPipeline] Pipeline not found for environment ${environment}" )
	}
}

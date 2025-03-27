#!/usr/bin/env groovy

def kaniko(Map config = [:]) {
    sh """
    printenv
    mkdir -p /kaniko/.docker
    echo '{"auths":{"${env.REGISTRY_HOST}":{"auth":"'"\$(echo -n ${env.REGISTRY_USER}:${env.REGISTRY_PASSWORD} | base64 | tr -d '\\n')"'"}}}' > /kaniko/.docker/config.json
    
    /kaniko/executor \
      --context ${env.WORKSPACE} \
      --dockerfile ${env.DOCKERFILE_PATH} \
      --destination ${env.REGISTRY_HOST}${env.REPOSITORY}:${env.IMAGE_TAG} \
      --destination ${env.REGISTRY_HOST}${env.REPOSITORY}:latest
    """
}

return this
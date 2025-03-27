#!/usr/bin/env groovy

/**
 * Docker 빌드를 위한 Pod Template을 제공하는 함수
 * @param customYaml 기본 Pod Template에 추가할 YAML (선택사항)
 * @param inheritFrom 상속받을 다른 Pod Template의 이름 (선택사항)
 * @param nodeSelector 특정 노드 선택을 위한 레이블 맵 (선택사항)
 * @return Kubernetes Pod Template 설정
 */
def call(Map params = [:]) {
    def customYaml = params.customYaml ?: ""
    def inheritFrom = params.inheritFrom ?: ""
    def nodeSelector = params.nodeSelector ?: [:]
    
    // 기본 Pod Template YAML 로드
    def defaultYaml = libraryResource('podTemplates/dockerBuildPodTemplate.yaml')
    
    // 노드 셀렉터가 제공된 경우 YAML에 추가
    if (nodeSelector) {
        def nodeSelectorYaml = "  nodeSelector:\n"
        nodeSelector.each { key, value ->
            nodeSelectorYaml += "    ${key}: ${value}\n"
        }
        defaultYaml = defaultYaml.replaceAll(/(?m)^  nodeSelector:.*(?:\n    .*)*/, nodeSelectorYaml)
    }
    
    // 커스텀 YAML이 제공된 경우 병합
    def finalYaml = customYaml ? defaultYaml + "\n" + customYaml : defaultYaml
    
    return [
        cloud: 'kubernetes',
        yaml: finalYaml,
        inheritFrom: inheritFrom
    ]
}

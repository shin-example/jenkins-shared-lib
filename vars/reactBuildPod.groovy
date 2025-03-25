#!/usr/bin/env groovy

/**
 * React 애플리케이션 빌드를 위한 Pod Template을 제공하는 함수
 * @param nodeVersion Node.js 버전 (기본값: 16)
 * @param customYaml 기본 Pod Template에 추가할 YAML (선택사항)
 * @param inheritFrom 상속받을 다른 Pod Template의 이름 (선택사항)
 * @param includeSonarqube SonarQube 스캐너 컨테이너 포함 여부 (기본값: true)
 * @return Kubernetes Pod Template 설정
 */
def call(Map params = [:]) {
    def nodeVersion = params.nodeVersion ?: "16"
    def customYaml = params.customYaml ?: ""
    def inheritFrom = params.inheritFrom ?: ""
    def includeSonarqube = params.containsKey('includeSonarqube') ? params.includeSonarqube : true
    
    // 기본 Pod Template YAML 로드
    def defaultYaml = libraryResource('podTemplates/reactBuildPodTemplate.yaml')
    
    // Node.js 버전 변경이 필요한 경우
    if (nodeVersion != "16") {
        defaultYaml = defaultYaml.replace('image: node:16-alpine', "image: node:${nodeVersion}-alpine")
    }
    
    // SonarQube 스캐너 제외가 필요한 경우
    if (!includeSonarqube) {
        defaultYaml = defaultYaml.replaceAll(/(?m)^  - name: sonarqube-scanner.*(?:\n    .*)*/, '')
    }
    
    // 커스텀 YAML이 제공된 경우 병합
    def finalYaml = customYaml ? defaultYaml + "\n" + customYaml : defaultYaml
    
    return [
        cloud: 'kubernetes',
        yaml: finalYaml,
        inheritFrom: inheritFrom,
        workspaceVolume: persistentVolumeClaimWorkspaceVolume(claimName: "jenkins-agent-pvc", readOnly: false)
    ]
}

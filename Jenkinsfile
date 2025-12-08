pipeline {
    agent any

    environment {
        SONAR_HOST_URL = "http://192.168.220.7:9000"
        SONAR_TOKEN = credentials('SonarToken')

        REGISTRY = "192.168.220.8:5000/docker-app" 
        NEXUS_URL = "http://192.168.220.8:8081/repository/docker-app/"
        NEXUS_USERNAME = credentials('Nexus')
        NEXUS_PASSWORD = credentials('Nexus')
    }

    tools {
        maven 'Maven'
        jdk 'JDK17'
    }

    options {
        timeout(time: 2, unit: 'HOURS')
        timestamps()
    }

    stages {

        stage('Clone') {
            steps {
                retry(3) {
                    git branch: 'master', url: 'https://github.com/Ahmedbm99/javaApp.git'
                }
            }
        }

        stage('Database Migration (Flyway)') {
            steps {
                retry(2) {
                    echo "Running Flyway migrations..."
                    sh 'mvn clean compile flyway:migrate'
                }
            }
        }

        stage('Build & Unit Tests') {
            steps {
                script {
                    try {
                        sh 'mvn clean test'
                    } finally {
                        junit 'target/surefire-reports/*.xml'
                        archiveArtifacts artifacts: '**/*.jar', allowEmptyArchive: true
                    }
                }
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Sonar Code Analysis') {
            environment { scannerHome = tool 'Sonar6.2' }
            steps {
                retry(2) {
                    withSonarQubeEnv('SonarServer') {
                        sh """
                            ${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=javaApp \
                            -Dsonar.projectName=javaApp-repo \
                            -Dsonar.projectVersion=1.0 \
                            -Dsonar.sources=src/ \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.junit.reportsPath=target/surefire-reports/ \
                            -Dsonar.java.checkstyle.reportPaths=target/checkstyle-result.xml
                        """
                    }
                }
            }
        }

        stage('Sonar Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    def commitId = env.GIT_COMMIT ?: "build-${env.BUILD_NUMBER}"

                    withCredentials([
                        usernamePassword(
                            credentialsId: 'docker-registry-cred',
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASSWORD'
                        )
                    ]) {
                        retry(3) {
                            sh """
                                docker login 192.168.220.8:5000 -u ${DOCKER_USER} -p ${DOCKER_PASSWORD}
                                docker build -t ${REGISTRY}:${commitId} -f ./Dockerfile .
                                docker push ${REGISTRY}:${commitId}
                            """
                        }
                    }
                }
            }
        }

        stage('Deploy to VM3') {
            steps {
                script {
                    def targetIP = "192.168.220.9"
                    def reachable = sh(script: "ping -c 1 ${targetIP} >/dev/null 2>&1", returnStatus: true)

                    if (reachable != 0) {
                        echo "VM3 unreachable. Creating new VM via Vagrant..."
                        sh """
                            cd vagrant/
                            vagrant up
                        """
                        // récupérer la nouvelle IP
                        targetIP = sh(
                            script: "cd vagrant && vagrant ssh -c \"hostname -I | awk '{print \\$2}'\" | tr -d '\\n'",
                            returnStdout: true
                        )
                        echo "New VM IP: ${targetIP}"
                        sh "sed -i 's/192.168.220.9/${targetIP}/g' ansible/inventory.ini"
                    }

                    def commitId = env.GIT_COMMIT ?: "build-${env.BUILD_NUMBER}"
                    def fullImage = "${env.REGISTRY}:${commitId}"

                    withCredentials([
                        sshUserPrivateKey(
                            credentialsId: 'deploy-vm-ssh',
                            keyFileVariable: 'SSH_KEY',
                            usernameVariable: 'SSH_USER'
                        ),
                        usernamePassword(
                            credentialsId: 'docker-registry-cred',
                            usernameVariable: 'REGISTRY_USERNAME',
                            passwordVariable: 'REGISTRY_PASSWORD'
                        )
                    ]) {
                        retry(3) {
                            sh """
                                ansible-playbook -i ansible/inventory.ini ansible/deploy.yml \
                                --extra-vars "image=${fullImage} registry_username=${REGISTRY_USERNAME} registry_password=${REGISTRY_PASSWORD} ansible_ssh_user=${SSH_USER} ansible_ssh_private_key_file=${SSH_KEY}"
                            """
                        }
                    }

                    // Health check sur la nouvelle IP
                    def retries = 5
                    def status = 1
                    for (int i = 0; i < retries; i++) {
                        status = sh(
                            script: """
                                RESPONSE=\$(curl -s --max-time 5 http://${targetIP}:3000/actuator/health)
                                STATUS=\$(echo "\$RESPONSE" | sed -n 's/.*"status": *"\\\\([^"]*\\\\)".*/\\\\1/p')
                                if [ "\$STATUS" = "UP" ]; then
                                    echo "Service is UP"
                                    exit 0
                                else
                                    echo "Service is DOWN"
                                    exit 1
                                fi
                            """,
                            returnStatus: true
                        )

                        if (status == 0) {
                            echo "Application is healthy at ${targetIP}!"
                            break
                        } else {
                            echo "Health check failed. Retrying in 5s..."
                            sleep 5
                        }
                    }

                    if (status != 0) {
                        error("Application failed health check on ${targetIP}. Triggering rollback...")
                    }
                }
            }
        }
    }

    post {
        failure {
            echo "Pipeline failed! Triggering rollback..."
            script {
                def targetIP = "192.168.220.9"
                def reachable = sh(script: "ping -c 1 ${targetIP} >/dev/null 2>&1", returnStatus: true)

                if (reachable != 0) {
                    echo "VM3 unreachable. Creating new VM via Vagrant..."
                    sh """
                        cd vagrant/
                        vagrant up
                    """
                    targetIP = sh(
                        script: "cd vagrant && vagrant ssh -c \"hostname -I | awk '{print \\$2}'\" | tr -d '\\n'",
                        returnStdout: true
                    )
                    echo "New VM IP: ${targetIP}"
                    sh "sed -i 's/192.168.220.9/${targetIP}/g' ansible/inventory.ini"
                }

                withCredentials([
                    usernamePassword(
                        credentialsId: 'docker-registry-cred', 
                        usernameVariable: 'DOCKER_USER', 
                        passwordVariable: 'DOCKER_PASSWORD'
                    ),
                    sshUserPrivateKey(
                        credentialsId: 'deploy-vm-ssh',
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                    )
                ]) {
                    sh """
                        ansible-playbook -i ansible/inventory.ini ansible/rollback.yml \
                        --extra-vars "registry_username=${DOCKER_USER} registry_password=${DOCKER_PASSWORD} ansible_ssh_user=${SSH_USER} ansible_ssh_private_key_file=${SSH_KEY}"
                    """
                }
            }
        }
    }
}

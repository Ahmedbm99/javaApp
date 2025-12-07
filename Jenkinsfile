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
        timeout(time: 2, unit: 'HOURS') // Safety timeout
        timestamps()                    // Log timestamps
    }

    stages {

        // ------------------
        stage('Clone') {
            steps {
                retry(3) {
                    git branch: 'master', url: 'https://github.com/Ahmedbm99/javaApp.git'
                }
            }
        }

        // ------------------
        stage('Database Migration (Flyway)') {
            steps {
                retry(2) {
                    echo "Running Flyway migrations..."
                    sh 'mvn clean compile flyway:migrate'
                }
            }
        }

        // ------------------
        stage('Build & Unit Tests') {
            steps {
                script {
                    try {
                        sh 'mvn clean test'
                    } finally {
                        // Archive test results even if tests fail
                        junit 'target/surefire-reports/*.xml'
                        archiveArtifacts artifacts: '**/*.jar', allowEmptyArchive: true
                    }
                }
                sh 'mvn clean package -DskipTests'
            }
        }

        // ------------------
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

        // ------------------
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
                                echo "Logging in to Docker registry..."
                                docker login 192.168.220.8:5000 -u ${DOCKER_USER} -p ${DOCKER_PASSWORD}

                                echo "Building Docker image..."
                                docker build -t ${REGISTRY}:${commitId} -f ./Dockerfile .

                                echo "Pushing Docker image..."
                                docker push ${REGISTRY}:${commitId}
                            """
                        }
                    }
                }
            }
        }

        // ------------------
        stage('Deploy to VM3') {
            steps {
                script {
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
                                echo "Running Ansible deployment..."
                                ansible-playbook -i ansible/inventory.ini ansible/deploy.yml \
                                    --extra-vars "image=${fullImage} registry_username=${REGISTRY_USERNAME} registry_password=${REGISTRY_PASSWORD} ansible_ssh_user=${SSH_USER} ansible_ssh_private_key_file=${SSH_KEY}"
                            """
                        }
                    }
                }
            }
        }
        stage('Health Check') {
          steps {
              script {
                  def retries = 5
                  def status = 1
                  for (int i = 0; i < retries; i++) {
                      status = sh(script: 'bash ansible/healthcheck.sh', returnStatus: true)
                      if (status == 0) {
                          echo "Application is healthy!"
                          break
                      } else {
                          echo "Health check failed. Retrying in 5s..."
                          sleep 5
                      }
                  }
                  if (status != 0) {
                      error("Application failed health check. Triggering rollback...")
                  }
              }
          }
        }
    }
     

    post {
 
        failure {
            echo "Pipeline failed! Triggering rollback..."
            script {
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
                        echo "Running rollback..."
                        ansible-playbook -i ansible/inventory.ini ansible/rollback.yml \
                            --extra-vars "registry_username=${DOCKER_USER} registry_password=${DOCKER_PASSWORD} ansible_ssh_user=${SSH_USER} ansible_ssh_private_key_file=${SSH_KEY}"
                    """
                }
            }
        }
    }
}

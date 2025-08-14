pipeline {
    agent any
    
    tools {
        maven 'Maven 3.9.10'
        jdk 'Temurin JDK 21'
    }

    environment {
        // --- GCP ---
        GCP_PROJECT_ID = 'model-parsec-465503-p3'
        GCP_CLUSTER_NAME = 'gke-secure-onboarding-system'
        GCP_ZONE = 'asia-southeast1-a'
        USE_GKE_GCLOUD_AUTH_PLUGIN = "True"

        // --- Checkout ---
        GIT_URL = 'https://github.com/bostang/backend-secure-onboarding-system'
        GIT_BRANCH = 'deploy/gke'

        // --- SonarQube ---
        SONAR_PROJECT_KEY = 'be-sonarqube'
        SONAR_PROJECT_NAME = 'be-sonarqube'
        SONAR_URL = 'https://sonarqube.wondrdesktop.my.id'

        // --- Image Build ---
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        GCR_HOSTNAME = 'gcr.io'
        IMAGE_NAME = 'backend-secure-onboarding-system'
    }

    stages {
        stage('Checkout') {
            steps {
                git url: GIT_URL, branch: GIT_BRANCH
            }
        }

        stage('Unit Test') {
            steps {
                sh 'mvn package'
            }

            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Sonarqube Test') {
            steps {
                sh """
                    SONAR_TOKEN=\$(gcloud secrets versions access latest --secret=sonar-token)
                    mvn clean verify sonar:sonar \
                    -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                    -Dsonar.projectName=${SONAR_PROJECT_NAME} \
                    -Dsonar.host.url=${SONAR_URL} \
                    -Dsonar.token=\${SONAR_TOKEN}
                """
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    def fullImageName = "${GCR_HOSTNAME}/${GCP_PROJECT_ID}/${IMAGE_NAME}:${IMAGE_TAG}"
                    sh """
                        gcloud auth configure-docker ${GCR_HOSTNAME} --quiet
                        gcloud secrets versions access latest --secret=firebase-otp-cred > firebase-cred.json
                        mv firebase-cred.json ./src/main/resources

                        docker build --build-arg FIREBASE_SA_CRED_FILE=firebase-cred.json -t ${fullImageName} -f Dockerfile .
                        docker push ${fullImageName}
                    """
                }
            }
        }

        stage('Deploy to GKE') {
            steps {
                sh """
                    sed -i 's|<CHANGE_IMAGE_TAG>|${IMAGE_TAG}|' k8s/deployment.yaml
                    
                    gcloud container clusters get-credentials ${GCP_CLUSTER_NAME} --zone ${GCP_ZONE} --project ${GCP_PROJECT_ID}

                    kubectl apply -f k8s/deployment.yaml
                """
            }
        }
    }

    post {
        success {
            echo "Pipeline success! 🚀"
        }
        failure {
            echo "Pipeline failed! 💥"
        }
        always {
            cleanWs()
        }
    }
}

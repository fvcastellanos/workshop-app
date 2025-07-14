node {

    def repositoryUrl = 'https://github.com/fvcastellanos/workshop-app.git'
    def mavenImageName = 'maven:3.9-eclipse-temurin-21'
    def postgresImageName = 'postgres:17'

    def DB_NAME = credentials('workshop-db')
    def DB_SCHEMA = credentials('workshop-schema')

    stages {
        stage('Prepare Data Services') {
            withCredentials([userNamePassword(credentialsId: 'workshop-db-credentials', variable: 'DB_CREDENTIALS')]) {
                sh "echo ${DB_CREDENTIALS} | docker login -u ${DB_CREDENTIALS_USR} --password-stdin"
                sh "docker compose -f ./docker/services.yaml up -d"
                sh "while ! docker exec postgres pg_isready -U ${DB_CREDENTIALS_USR} -d ${DB_NAME}; do sleep 2; done"
                sh "docker exec -i postgres psql -U ${DB_CREDENTIALS_USR} -d ${DB_NAME} -c 'CREATE SCHEMA IF NOT EXISTS ${DB_SCHEMA};'"
            }
        }

        stage('Checkout') {

            checkout scm: scmGit(
                branches: [[name: '$BRANCH_NAME']], 
                extensions: [], 
                userRemoteConfigs: [[
                    credentialsId: 'git-credentials', 
                    url: repositoryUrl
                ]]
            )
        }

        stage('Build') {

            docker.image(mavenImageName)
                .inside {
                    sh 'mvn -B clean package -DskipTests'
                }
        }

    }

    post {
        always {
            echo 'Cleaning up...'
            sh "docker compose -f ./docker/services.yaml down --remove-orphans"
            sh "docker volume prune -f"
            sh "docker network prune -f"
        }
    }
}

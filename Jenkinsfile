node {

    def repositoryUrl = 'https://github.com/fvcastellanos/workshop-app.git'
    def mavenImageName = 'maven:3.9-eclipse-temurin-21'
    def postgresImageName = 'postgres:17'

    withCredentials([
        usernamePassword(credentialsId: 'workshop-db-credentials', usernameVariable: 'DB_CREDENTIALS_USR', passwordVariable: 'DB_CREDENTIALS_PSW'),
        string(credentialsId: 'workshop-db', variable: 'DB_NAME'),
        string(credentialsId: 'workshop-schema', variable: 'DB_SCHEMA')
    ]) {
        try {
            stage('Prepare Data Services') {
                sh '''
                    docker compose -f ./docker/services.yaml up -d
                    while ! docker exec postgres pg_isready -U $DB_CREDENTIALS_USR -d $DB_NAME; do sleep 2; done
                    docker exec -i postgres psql -U $DB_CREDENTIALS_USR -d $DB_NAME -c 'CREATE SCHEMA IF NOT EXISTS $DB_SCHEMA;'
                '''
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
        } catch (Exception exception) {
            echo "An error occurred: ${exception.getMessage()}"
            currentBuild.result = 'FAILURE'
            throw exception
        } finally {

                echo 'Cleaning up...'
                sh "docker compose -f ./docker/services.yaml down --remove-orphans"
                sh "docker volume prune -f"
                sh "docker network prune -f"
                sh "docker rmi -f ${mavenImageName} ${postgresImageName}"    
        }
    }
}

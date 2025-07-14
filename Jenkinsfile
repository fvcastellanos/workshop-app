node {

    def repositoryUrl = 'https://github.com/fvcastellanos/workshop-app.git'
    def mavenImageName = 'maven:3.9-eclipse-temurin-21'
    def postgresImageName = 'postgres:17'

    withCredentials([
        usernamePassword(credentialsId: 'workshop-db-credentials', usernameVariable: 'DB_CREDENTIALS_USR', passwordVariable: 'DB_CREDENTIALS_PSW'),
        string(credentialsId: 'workshop-db', variable: 'DB_NAME'),
        string(credentialsId: 'workshop-schema', variable: 'DB_SCHEMA'),
        string(credentialsId: 'auth0-client-id', variable: 'AUTH0_CLIENT_ID'),
        string(credentialsId: 'auth0-client-secret', variable: 'AUTH0_CLIENT_SECRET'),
        string(credentialsId: 'workshop-cors-origins', variable: 'WORKSHOP_CORS_ORIGINS')
    ]) {
        try {

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

            stage('Prepare Data Services') {
                sh '''
                    docker compose -f ./docker/services.yaml up -d
                    max_attempts=10 \
                    attempt=1 \
                    while ! docker exec postgres pg_isready -U $DB_CREDENTIALS_USR -d $DB_NAME; do \
                        if [ $attempt -ge $max_attempts ]; then \
                            echo "Postgres did not become ready after $max_attempts attempts." \
                            exit 1 \
                        fi \
                        sleep 2; \
                        attempt=$((attempt + 1)) \
                    done
                    docker exec -i postgres psql -U $DB_CREDENTIALS_USR -d $DB_NAME -c "CREATE SCHEMA IF NOT EXISTS $DB_SCHEMA;"
                '''
            }

            stage('Build') {

                sh 'set DATASOURCE_URL="jdbc:postgresql://localhost:5432/$DB_NAME?user=$DB_CREDENTIALS_USR&password=$DB_CREDENTIALS_PSW&currentSchema=$DB_SCHEMA"'
                docker.image(mavenImageName)
                    .inside {
                        sh 'mvn -B clean test verify'
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

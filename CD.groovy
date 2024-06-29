pipeline {
    agent { label 'ubuntu-latest' }
    
    environment {
        SERVICECONNECTION = credentials('YOUR_AZURE_SERVICE_CONNECTION_CREDENTIALS_ID')
    }
    
    stages {
        stage('Terraform Init') {
            steps {
                script {
                    sh """
                    terraform init \
                    -backend-config="resource_group_name=demo-resources" \
                    -backend-config="storage_account_name=techtutorialswithpiyush" \
                    -backend-config="container_name=prod-tfstate" \
                    -backend-config="key=prod.terraform.tfstate" \
                    -backend-config="access_key=${env.SERVICECONNECTION}"
                    """
                }
            }
        }
        
        stage('Terraform Validate') {
            steps {
                script {
                    sh 'terraform validate'
                }
            }
        }
        
        stage('Terraform Format') {
            steps {
                script {
                    sh 'terraform fmt'
                }
            }
        }
        
        stage('Terraform Plan') {
            steps {
                script {
                    sh 'terraform plan -out=tfplanfile'
                }
            }
        }
        
        stage('Archive Files') {
            steps {
                script {
                    sh """
                    zip -r ${env.BUILD_NUMBER}.zip ${env.WORKSPACE}
                    """
                }
            }
        }
        
        stage('Publish Build Artifacts') {
            steps {
                archiveArtifacts artifacts: "${env.BUILD_NUMBER}.zip", allowEmptyArchive: true
            }
        }
        
        stage('Terraform Apply') {
            steps {
                input message: 'Approve Deployment?', ok: 'Deploy'
                script {
                    sh 'terraform apply -input=false tfplanfile'
                }
            }
        }
        
        stage('Post Deployment Steps') {
            steps {
                // Add any additional steps you need after deployment, such as verification or notification
                echo 'Deployment complete!'
            }
        }
    }
}

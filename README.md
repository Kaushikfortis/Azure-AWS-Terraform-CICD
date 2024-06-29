# terraform-azure-pipeline

## Description

This repository contains the CI/CD pipeline setup for automating the deployment of infrastructure on Microsoft Azure using Terraform and Jenkins. The pipeline script (Jenkinsfile) orchestrates various stages including initialization, validation, formatting, planning, and applying Terraform configurations. It also includes stages for archiving and publishing build artifacts.

## Key Features

- **Terraform Initialization**: Configures backend state management for Azure resources.
- **Validation and Formatting**: Ensures Terraform configuration files are syntactically correct and properly formatted.
- **Planning and Applying**: Creates and applies an execution plan for the infrastructure changes.
- **Artifact Management**: Archives and publishes build artifacts for traceability.
- **Manual Approval**: Optional manual approval step before applying Terraform changes to production.
- **Post Deployment Steps**: Placeholder for post-deployment verification and notifications.

## Prerequisites

- Jenkins installed and configured.
- Terraform installed on Jenkins agents.
- Azure service principal for authentication.
- Necessary Jenkins plugins: Pipeline, Terraform, Credentials, etc.

## Getting Started

1. Clone this repository to your local machine.
2. Configure the Jenkins pipeline with the provided Jenkinsfile.
3. Set up the necessary environment variables and credentials in Jenkins.
4. Run the pipeline to automate the deployment of your Azure infrastructure.

## Repository Structure

- **Jenkinsfile**: Pipeline script for Jenkins.
- **terraform/**: Directory containing Terraform configuration files.
- **docs/**: Documentation and guides.

## Using Azure DevOps CI/CD

Azure DevOps provides a comprehensive suite of tools for managing CI/CD pipelines, making it an excellent choice for integrating Terraform.

### Pipeline Configuration

Here's a sample pipeline configuration using Azure DevOps for Terraform:

```yaml
trigger: 
- main

stages:
- stage: Build
  jobs:
  - job: Build
    pool:
      vmImage: 'ubuntu-latest'
    steps:
    - task: TerraformTaskV4@4
      displayName: Tf init
      inputs:
        provider: 'azurerm'
        command: 'init'
        backendServiceArm: '${SERVICECONNECTION}'
        backendAzureRmResourceGroupName: 'demo-resources'
        backendAzureRmStorageAccountName: 'techtutorialswithpiyush'
        backendAzureRmContainerName: 'prod-tfstate'
        backendAzureRmKey: 'prod.terraform.tfstate'
    - task: TerraformTaskV4@4
      displayName: Tf validate
      inputs:
        provider: 'azurerm'
        command: 'validate'
    - task: TerraformTaskV4@4
      displayName: Tf fmt
      inputs:
        provider: 'azurerm'
        command: 'custom'
        customCommand: 'fmt'
        outputTo: 'console'
        environmentServiceNameAzureRM: '${SERVICECONNECTION}'
    - task: TerraformTaskV4@4
      displayName: Tf plan
      inputs:
        provider: 'azurerm'
        command: 'plan'
        commandOptions: '-out $(Build.SourcesDirectory)/tfplanfile'
        environmentServiceNameAzureRM: '${SERVICECONNECTION}'
    - task: ArchiveFiles@2
      displayName: Archive files
      inputs:
        rootFolderOrFile: '$(Build.SourcesDirectory)/'
        includeRootFolder: false
        archiveType: 'zip'
        archiveFile: '$(Build.ArtifactStagingDirectory)/$(Build.BuildId).zip'
        replaceExistingArchive: true
    - task: PublishBuildArtifacts@1
      inputs:
        PathtoPublish: '$(Build.ArtifactStagingDirectory)'
        ArtifactName: '$(Build.BuildId)-build'
        publishLocation: 'Container'

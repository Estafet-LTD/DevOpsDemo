pipeline {
    agent {
   any {}
    }
    environment {
    // Get the maven tool
        // ** NOTE: This 'M3' maven tool must be configured in the global configuration
        def mvnHome = tool 'M3'
        def dockerHome = tool 'Docker'
   }
    
    stages {
        stage('test') {
      steps {
        echo 'testing'
        sh "${mvnHome}/bin/mvn -B test"
      }
      }
  
       stage('SonarQube analysis') {
     steps{
     withSonarQubeEnv('sonarqube') {
                   sh "${mvnHome}/bin/mvn -DskipTests clean install sonar:sonar"
                    }
    }
  }
  stage('Build App') {
      steps {
        sh "${mvnHome}/bin/mvn -DskipTests clean install"
      }
    }
    
   stage('Build Image') {
     steps {
        script {
          openshift.withCluster() {
           openshift.withProject('example-project') {
           openshift.newApp('target/example-0.0.1-SNAPSHOT.jar')
          }
         }
        }
      }
    }
 // stage('Create Image Builder') {
 //      steps {
 //       script {
 //         openshift.withCluster() {
 //         openshift.withProject('example-project') {
 //          openshift.newBuild("--name=example", "--image-stream=redhat-openjdk18-openshift", "--binary")
 //        }
 //         }
 //       }
 //     }
 //   }
   
 // stage('Build Image') {
 //  steps {
 //    script {
 //       openshift.withCluster() {
 //        openshift.withProject('example-project') {
 //          openshift.selector("bc", "example").startBuild("--from-file=target/example-0.0.1-SNAPSHOT.jar", "--wait")
 //       }
 //        }
 //      }
 //   }
 // }

        stage('Final Stage') {
            steps {
                echo 'Goodbye, world!' 
            }
        }
    }
}
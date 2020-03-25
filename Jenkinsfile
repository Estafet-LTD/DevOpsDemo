pipeline {
    agent {
   any {}
    }
    environment {
    // Get the maven tool
        // ** NOTE: This 'M3' maven tool must be configured in the global configuration
        def mvnHome = tool 'M3'
        def VERSION = readMavenPom().getVersion()
        //def dockerHome = tool 'Docker'
   }
   stages {
   stage('First Stage') {
            steps {
                echo 'Beginning pipeline!'
                echo "pom version is ${VERSION}"
                echo "jenkins build is ${BUILD_NUMBER}"
                }
 }
   
        stage('Test') {
      steps {
        echo 'Running tests'
        sh "${mvnHome}/bin/mvn -B test"
      }
      }
      
      stage('SonarQube analysis') {
     steps{
    withSonarQubeEnv('Sonar') {
                  sh "${mvnHome}/bin/mvn -DskipTests clean install sonar:sonar"
                  }
    }
  }
  
  stage('Build App') {
     steps {
      sh "${mvnHome}/bin/mvn -DskipTests clean install"
      }
   }
    
 stage('Create Builder') {
 when {
        expression {
          openshift.withCluster() {
          openshift.withProject('devops-example') {
            return !openshift.selector("bc", "example-build-jenkins").exists();
          }
        }
        }
      }
       steps {
        script {
          openshift.withCluster() {
          openshift.withProject('devops-example') {
           openshift.newBuild("--name=example-build-jenkins", "--image-stream=openshift/openjdk18-openshift", "--binary", "--to-docker=true", "--to=docker-registry.default.svc.cluster.local:5000/devops-example/example-build-jenkins")
         }
          }
        }
      }
   }
   
  stage('Build Image') {
   steps {
     script {
        openshift.withCluster() {
        openshift.withProject('devops-example') {
           openshift.selector("bc", "example-build-jenkins").startBuild("--from-file=target/example-${VERSION}.jar", "--wait")
        }
         }
       }
    }
  }

stage('Create deployment config') {
      when {
        expression {
          openshift.withCluster() {
          openshift.withProject('devops-example') {
            return !openshift.selector('dc', 'example-deploy-jenkins').exists();
          }
        }
        }
      }
      steps {
        script {
          openshift.withCluster() {   
                  openshift.withProject('devops-example') {
            openshift.newApp("--image-stream=devops-example/example-build-jenkins:latest", "--name=example-deploy-jenkins").narrow('svc').expose()
          }
          }
        }
      }
    }
    
     stage('Tag image') {
    steps {
      script {
        openshift.withCluster() {
            openshift.withProject('devops-example') {
          openshift.tag("example-build-jenkins:latest", "example-build-jenkins:${VERSION}-${BUILD_NUMBER}")
        }
      }
      }
    }
   }
   
    stage('Last stage') {
       steps { echo 'Byeee, world!'}
      }
      }
      }
pipeline {
    agent {
   any {}
    }
    environment {
    // Get the maven tool
        // ** NOTE: This 'M3' maven tool must be configured in the global configuration
        def mvnHome = tool 'M3'
        //def dockerHome = tool 'Docker'
   }
   stages {
   stage('First Stage') {
            steps {
                echo 'Hello, world!' 
            }
        }
    
    
        stage('test') {
      steps {
        echo 'testing'
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
            return !openshift.selector("bc", "example-build-jenk").exists();
          }
        }
        }
      }
       steps {
        script {
          openshift.withCluster() {
          openshift.withProject('devops-example') {
           openshift.newBuild("--name=example-build-jenk", "--docker-image=docker-registry.default.svc.cluster.local:5000/devops-example/openjdk18-openshift", "--binary", "--to-docker=true", "--to=docker-registry.default.svc.cluster.local:5000/devops-example/example-build-jenk")
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
           openshift.selector("bc", "example-build-jenk").startBuild("--from-file=target/example-0.0.1-SNAPSHOT.jar", "--wait")
        }
         }
       }
    }
  }
  
//  stage('Tag image as DEV') {
//      steps {
//        script {
//          openshift.withCluster() {
//            openshift.tag("example:latest", "example:dev")
//          }
//        }
//      }
//    }

stage('Create deployment config') {
      when {
        expression {
          openshift.withCluster() {
          openshift.withProject('devops-example') {
            return !openshift.selector('dc', 'example-deploy-jenk').exists();
          }
        }
        }
      }
      steps {
        script {
          openshift.withCluster() {   
                  openshift.withProject('devops-example') {
            openshift.newApp("--docker-image=docker-registry.default.svc.cluster.local:5000/devops-example/example-build-jenk:latest", "--name=example-deploy-jenk").narrow('svc').expose()
          }
          }
        }
      }
    }
    stage('Rollout') {
      steps {
        script {
          openshift.withCluster() {
          openshift.withProject('devops-example') {
            openshift.selector("dc", "example-deploy-jenk").rollout().latest()
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
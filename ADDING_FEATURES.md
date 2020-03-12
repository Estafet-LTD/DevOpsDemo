# Adding features to the SIAB OpenShift cluster

See the files in /home/engineer/ocp/pods for examples of how the existing containers were installed. These can be used as exemplars of how to include new images and templates. Images represent the basis for creating containers while templates will simplify the creation of applications within a project from the Service Catalog

The basic steps follow to add an image to the internal OpenShift docker registry and to create a Service Catalog item for use in projects. 

The files used to create the mongodb example can be found in the folder  _mongo-db-example_ 

The example will add a mongodb image with an ephemeral Service Catalog Item:

## add docker image to the infrastructure VM

The infrastructure VM is connected to the internet and is the only way to get components into the disconnected environment

* Add the image to the infrastructure vm and tag it for the Thales registry

See  _infra-get-images_  for the example

```
docker pull registry.redhat.io/rhscl/mongodb-34-rhel7
docker tag registry.redhat.io/rhscl/mongodb-34-rhel7  repo.thales.com:5000/rhscl/mongodb-34-rhel7
docker push repo.thales.com:5000/rhscl/mongodb-34-rhel7
```

## add the image to the internal OCP registry in the OCP VM in the openshift namespace

See  _load-mongo-images.sh_  for the example file

```
oc login -u developer -p developer

TOKEN=$(oc whoami -t)

docker login -u developer -p $TOKEN docker-registry.default.svc.cluster.local:5000

docker pull repo.thales.com:5000/rhscl/mongodb-34-rhel7


docker tag repo.thales.com:5000/rhscl/mongodb-34-rhel7 docker-registry.default.svc.cluster.local:5000/openshift/mongodb-34-rhel7

docker push docker-registry.default.svc.cluster.local:5000/openshift/mongodb-34-rhel7

oc login -u system:admin

```

## add the template to the openshift project

You must find and modify a suitable template file online or else create your own file. there are examples online e.g. at  _https://github.com/openshift/origin/tree/master/examples/db-templates_

In this case we have chosen mongodb-ephemeral-template.json and placed it in  _/home/engineer/ocp/pods/_

The amended file is in  _mongodb-ephemeral-template.yaml_  as an example

If you wish to create persistent application containers there will be a need to create a persistent volume (pv) - see the example in  _/home/engineer/ocp/pods/jenkins-deployment.sh_  which creates storage folders and permissions in  _/srv/nfs_ and calls  _jenkins-pv.yaml_ to create the persistent volume

* amend the template as required to reflect the image stream tag you have created and the version. E.g. 


```
{
                                "kind": "ImageStreamTag",
                                "name": "mongodb-34-rhel7:${MONGODB_VERSION}",
                                "namespace": "${NAMESPACE}"
                                }
```

and

```
{
            "description": "Version of MongoDB image to be used (3.6 or latest).",
            "displayName": "Version of MongoDB Image",
            "name": "MONGODB_VERSION",
            "required": true,
            "value": "3.4"
        }
```

* switch to the openshift project

```
oc project openshift
```

* apply the template (assumes it is in /home/engineer/ocp/pods/)

```
oc create -f /home/engineer/ocp/pods/mongodb-ephemeral-template.json
```

The template should now appear in the OpenShift service catalog and permit the simple creation of mongo db application pods


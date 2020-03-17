# Securing OpenShift Container Platform

The first part of this document refers to how to secure the OCP features themselves; the second part talks about securing routes to pods that you create.

## Securing OCP 

The following information is taken from (this link will not work in the disconnected environment)

[http://docs.openshift.com/container-platform/3.11/install_config/certificate_customization.html](http://docs.openshift.com/container-platform/3.11/install_config/certificate_customization.html)


Administrators can configure custom serving certificates for the public host names of the OCP API and web console. This can be done during a cluster installation or later.

### During cluster installation

#### Configuring a certificate chain:

If a certificate chain is used, then all certificates must be manually concatenated into a single named certificate file. These certificates must be placed in the following order:

OpenShift Container Platform master host certificate

Intermediate CA certificate

Root CA certificate

Third party certificate

To create this certificate chain, concatenate the certificates into a common file. You must run this command for each certificate and ensure that they are in the previously defined order.

```
$ cat <certificate>.pem >> ca-chain.cert.pem
```

#### Configuring Custom Certificates During Installation

During cluster installations, custom certificates can be configured using the  _openshift_master_named_certificates_  and  _openshift_master_overwrite_named_certificates parameters_ , which are configurable in the inventory file. More details are available about configuring custom certificates in the Ansible documentation.

Custom Certificate Configuration Parameters

```
openshift_master_overwrite_named_certificates=true # If you provide a value for the penshift_master_named_certificates parameter, set this parameter to true.
 
openshift_master_named_certificates=[{"certfile": "/path/on/host/to/crt-file", "keyfile": /path/on/host/to/key-file", "names": ["public-master-host.com"], "cafile": "/path/on/host/to/ca-file"}]  # Provisions a master API certificate.
openshift_hosted_router_certificate={"certfile": "/path/on/host/to/app-crt-file", "keyfile": "/path/on/host/to/app-key-file", "cafile": "/path/on/host/to/app-ca-file"} # Provisions a router wildcard certificate.
```

Example parameters for a master API certificate:

```
openshift_master_overwrite_named_certificates=true
openshift_master_named_certificates=[{"names": ["master.148.251.233.173.nip.io"], "certfile": "/home/cloud-user/master-bundle.cert.pem", "keyfile": "/home/cloud-user/master.148.251.233.173.nip.io.key.pem"}]

```

Example parameters for a router wildcard certificate:

```
openshift_hosted_router_certificate={"certfile": "/home/cloud-user/star-apps.148.251.233.173.nip.io.cert.pem", "keyfile": "/home/cloud-user/star-apps.148.251.233.173.nip.io.key.pem", "cafile": "/home/cloud-user/ca-chain.cert.pem"}
```

### Retrofit Custom Certificates into a Cluster

[see https://docs.openshift.com/container-platform/3.11/install_config/certificate_customization.html#configuring-custom-certificates-retrofit](https://docs.openshift.com/container-platform/3.11/install_config/certificate_customization.html#configuring-custom-certificates-retrofit) (this link will not work in the disconnected environment)

You can retrofit custom master and custom router certificates into an existing OpenShift Container Platform cluster.

#### Retrofit Custom Master Certificates into a Cluster

To retrofit custom certificates:

* Specify the path to the certificate using the openshift_master_named_certificates parameter.

```
openshift_master_overwrite_named_certificates=true
openshift_master_named_certificates=[{"certfile": "/path/on/host/to/crt-file", "keyfile": "/path/on/host/to/key-file", "names": ["public-master-host.com"], "cafile": "/path/on/host/to/ca-file"}] # Path to a master API certificate.
```

* Change to the playbook directory and run the following playbook:

```
$ ansible-playbook /usr/share/ansible/openshift-ansible/playbooks/redeploy-certificates.yml
```

* If you use named certificates:

Update the certificate parameters in the master-config.yaml file on each master node.

Restart the OpenShift Container Platform master service to apply the changes.

```
$ master-restart api
$ master-restart controllers
```

#### Retrofit Custom Router Certificates into a Cluster

To retrofit custom router certificates:

* Edit the Ansible inventory file to set the openshift_master_overwrite_named_certificates=true.

* Specify the path to the certificate using the openshift_hosted_router_certificate parameter.

```
openshift_master_overwrite_named_certificates=true
openshift_hosted_router_certificate={"certfile": "/path/on/host/to/app-crt-file", "keyfile": "/path/on/host/to/app-key-file", "cafile": "/path/on/host/to/app-ca-file"} (Path to a router wildcard certificate.)
```

* Change to the playbook directory and run the following playbook:

```
$ cd /usr/share/ansible/openshift-ansible
$ ansible-playbook playbooks/openshift-hosted/redeploy-router-certificates.yml
```
---------------------------------------------------------------------
## Securing routes

[see https://docs.openshift.com/container-platform/3.11/dev_guide/routes.html](https://docs.openshift.com/container-platform/3.11/dev_guide/routes.html) (this link will not work in the disconnected environment)

Unsecured routes are the default configuration, and are therefore the simplest to set up. 
 
However, secured routes offer security for connections to remain private. Secured routes specify the TLS termination of the route and, optionally, provide a key and certificate(s). 

To create a secured HTTPS route encrypted with a key and certificate (PEM-format files which you must generate and sign separately), you can use the create route command and optionally provide certificates and a key.

```
$ oc create route edge --service=frontend \
    --cert=${MASTER_CONFIG_DIR}/ca.crt \
    --key=${MASTER_CONFIG_DIR}/ca.key \
    --ca-cert=${MASTER_CONFIG_DIR}/ca.crt \
    --hostname=www.example.com
```

Alternative way to create a route is to create a yaml file and use  _oc create -f_    

```
$ oc create -f <yaml file with route definition> # alternative way to create route
```

You can create a secured route without specifying a key and certificate, in which case the router’s default certificate will be used for TLS termination.

TLS termination in OpenShift Container Platform relies on SNI for serving custom certificates. Any non-SNI traffic received on port 443 is handled with TLS termination and a default certificate, which may not match the requested host name, resulting in validation errors.

[From: https://docs.openshift.com/container-platform/3.11/architecture/networking/routes.html#secured-routes](https://docs.openshift.com/container-platform/3.11/architecture/networking/routes.html#secured-routes) (this link will not work in the disconnected environment)

Secured routes can use any of the following three types of secure TLS termination.

### Edge Termination

With edge termination, TLS termination occurs at the router, prior to proxying traffic to its destination. TLS certificates are served by the front end of the router, so they must be configured into the route, otherwise the router’s default certificate will be used for TLS termination.

#### example yaml for edge terminated route

```
apiVersion: v1
kind: Route
metadata:
  name: route-edge-secured 
spec:
  host: www.example.com
  to:
    kind: Service
    name: service-name 
  tls:
    termination: edge            
    key: |-                      
      -----BEGIN PRIVATE KEY-----
      [... <PEM format key file>] 
      -----END PRIVATE KEY-----
    certificate: |-              
      -----BEGIN CERTIFICATE-----
      [... <PEM format certificate file>]
      -----END CERTIFICATE-----
    caCertificate: |-            
      -----BEGIN CERTIFICATE-----
      [... <optional CA certificate to establish chain for validation>]
      -----END CERTIFICATE-----
 ```
   
Because TLS is terminated at the router, connections from the router to the endpoints over the internal network are not encrypted.

Edge-terminated routes can specify an insecureEdgeTerminationPolicy that enables traffic on insecure schemes (HTTP) to be disabled, 
allowed or redirected. The allowed values for insecureEdgeTerminationPolicy are: None or empty (for disabled), Allow or Redirect. 
The default insecureEdgeTerminationPolicy is to disable traffic on the insecure scheme. 
A common use case is to allow content to be served via a secure scheme but serve the assets (example images, stylesheets and javascript) via the insecure scheme.

#### example yaml file for secure route with edge termination allowing HTTP traffic

```
apiVersion: v1
kind: Route
metadata:
  name: route-edge-secured-allow-insecure 
spec:
  host: www.example.com
  to:
    kind: Service
    name: service-name 
  tls:
    termination:                   edge   
    insecureEdgeTerminationPolicy: Allow  
    [ ... ]
```

### Passthrough Termination

With passthrough termination, encrypted traffic is sent straight to the destination without the router providing TLS termination. Therefore no key or certificate is required.

The destination pod is responsible for serving certificates for the traffic at the endpoint. 
This is currently the only method that can support requiring client certificates (also known as two-way authentication).

Passthrough routes can also have an insecureEdgeTerminationPolicy. The only valid values are None (or empty, for disabled) or Redirect.

#### passthrough example yaml

```
apiVersion: v1
kind: Route
metadata:
  name: route-passthrough-secured 
spec:
  host: www.example.com
  to:
    kind: Service
    name: service-name 
  tls:
    termination: passthrough 
 ```

### Re-encryption Termination

Re-encryption is a variation on edge termination where the router terminates TLS with a certificate, 
then re-encrypts its connection to the endpoint which may have a different certificate. 
Therefore the full path of the connection is encrypted, even over the internal network. The router uses health checks to determine the authenticity of the host.

#### re-encrypt example yaml

```
apiVersion: v1
kind: Route
metadata:
  name: route-pt-secured 
spec:
  host: www.example.com
  to:
    kind: Service
    name: service-name 
  tls:
    termination: reencrypt        
    key: [as in edge termination]
    certificate: [as in edge termination]
    caCertificate: [as in edge termination]
    destinationCACertificate: |-  
      -----BEGIN CERTIFICATE-----
      [... <certificate to secure connection from route to destination pods>]
      -----END CERTIFICATE-----
 ```
 
If the destinationCACertificate field is left empty, the router automatically leverages the certificate authority that is generated for service serving certificates, and is injected into every pod as var/run/secrets/kubernetes.io/serviceaccount/service-ca.crt. This allows new routes that leverage end-to-end encryption without having to generate a certificate for the route. This is useful for custom routers or the F5 router, which might not allow the destinationCACertificate unless the administrator has allowed it.

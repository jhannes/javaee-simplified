# Java EE Simplified without Spring

Java web applications often contains a big stack of technologies which
interact in ways that are hard to predict. Many developers revert to
developing by copying and pasting code they don't understand and hoping
that it will work.

This project is meant to be a demonstration of a full Java web application
which demonstrates a straightforward way to solve the most common tasks
of a Java application with the least magical technological choices.

* Responding to HTTP requests with an embedded Jetty servlet container
  This has the advantage of letting you own the `main` method of
  your application, reducing the amount of hard-to-predict behavior
* Routing requests to controllers using the standard Java EE JAX-RS
  stack. Although this requires a bit more injection magic than I would
  like, this is still provides more control than with other approaches
* Building a Docker image with jib-maven-plugin, avoiding the need
  for a boot, assembly or classloading step when building the application
* Defining APIs contract-first with OpenAPI generator and serving
  the API documentation with Swagger UI webjar
* Serving the front-end code from the same Jetty container using
  the frontend-maven-plugin, which means a single deployment for
  releases and avoiding cross-origin API requests
* Implementing authentication with OpenID Connect
* Implementing database logic and transactions with a the
  micro-ORM fluent-jdbc
* Configuration-free logging with MDCs using logevents

## The application

The application is a simple TODO application

## Running for development

Required dependencies:

* JDK 17
* Docker (for running PostgreSQL)
* NodeJS (for frontend build)

Running the application

1. Start the database with `docker-compose up`
2. Start the frontend build with `npm start`
3. Start the application server `com.soprasteria.simplejavaee.ApplicationServer`
4. Access the application on http://localhost:8080
5. If you want the application to authenticate your user, you need to create a
   Azure Active Directory application registration the callback URL
   `http://localhost:8080/api/login/callback` and create a `.env` file with
   the following values:
   ```.env
   OPENID_CLIENT_ID=....
   OPENID_CLIENT_SECRET=....
   ```

## Deployment

This describes how to build a docker image and push it to a docker registry
and how to deploy on a Kubernetes cluster.

### Setup

The application is optimized for deployment with a Docker image
running at GitHub Container Registry (GHCR).

Update your `$HOME/.m2/settings.xml` with your GHCR registry. See
the [Maven Password Encryption guide](https://maven.apache.org/guides/mini/guide-encryption.html)
to avoid storing your passwords in plain text:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings>
    <servers>
        <server>
            <id>ghcr.io</id>
            <username>jhannes</username>
            <password>{...}</password>
        </server>
    </servers>
    <profiles>
        <profile>
            <id>ghcr</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <docker.registry>
                    ghcr.io/jhannes
                </docker.registry>
            </properties>
        </profile>
    </profiles>
</settings>
```

### Building and uploading the Docker image

`mvn install`

### Deploying to a Kubernetes cluster

If you want to test out the application on an ad hoc Kubernetes cluster,
you can use [KinD (Kubernetes in Docker)](https://kind.sigs.k8s.io/)

#### Setting up a Kind cluster

1. Install the necessary dependencies
    * Docker desktop
    * [kind](https://kind.sigs.k8s.io/)
2. Create a cluster on your local machine: `kind create cluster --config setup/create-cluster.yaml`
    * `create-cluster.yaml` contains configuration to forward port 80 and 443 on your local machine to this cluster
    * This configuration includes support for ingress controllers. If you don't need this, you can do `kind create cluster`
    * The `kind create cluster` command updates `~/.kube/config` to make `kubectl` connect to the new cluster
    * You can see the cluster nodes running with `docker ps`
    * You can see the nodes from the perspective of the cluster with `kubectl get nodes`
3. Install an Ingress controller (in our case: Nginx):
    * `kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml`
    * Wait for the pods to start `kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=90s`
4. Install the postgresql operator: `kubectl apply -k github.com/zalando/postgres-operator/manifests`
5. Add the following entry to your hosts-file:
   ```
   127.0.0.1 simplejavaee.test.example.com
   ```


#### Apply the Kubernetes manifest with your docker file to your cluster

1. Create test namespace: `kubectl create namespace simplejavaee`
2. Create a pull-token for the namespace: `kubectl --namespace simplejavaee create secret docker-registry pull-secret --docker-username=<appId> --docker-password=<password> --docker-server=ghcr.io/<username>`
3. Deploy the application: `helm template --set imageRegistry=ghcr.io/<username> src/main/helm | kubectl --namespace simplejavaee apply -f -`

If you have set up your hosts file (or DNS) correct, you can now access the application at https://simplejavaee.test.example.com


## Plan of attack

* [ ] Serve simple http requests with Jetty
* [ ] Serve TODO-list with JAX-RS
* [ ] jib-maven-plugin
* [ ] OpenAPI spec for TODO-list
* [ ] Frontend-maven-plugin
* [ ] OpenID connect
* [ ] Fluent JDBC
* [ ] Kubernetes
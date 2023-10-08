# Java EE Simplified without Spring

Java web applications often contains a big stack of technologies which interact
in ways that are hard to predict. Many developers revert to developing by
copying and pasting code they don't understand and hoping that it will work.

This project is meant to be a demonstration of a full Java web application which
demonstrates a straightforward way to solve the most common tasks of a Java
application with the least magical technological choices.

- Responding to HTTP requests with an embedded Jetty servlet container. This has
  the advantage of letting you own the `main` method of your application,
  reducing the amount of hard-to-predict behavior
- Routing requests to controllers is using
  [Action Controller](https://github.com/jhannes/action-controller), an
  injection-free routing library that I have written myself. As this is a
  central component, I would probably use the standard JAX-RS in production
  settings. However, JAX-RS requires substantial injection magic, and I want to
  show an alternative
- Building a Docker image
  with [jib-maven-plugin](https://github.com/GoogleContainerTools/jib/),
  avoiding the need for boot, assembly or classloading step when building the
  application
- Defining APIs contract-first with
  [OpenAPI generator](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-maven-plugin)
  and serving the API documentation with Swagger UI webjar
    - API spec
    - Swagger UI
    - Action Controller APIs with DTOs generated
      by [openapi-generator-java-annotationfree](https://github.com/jhannes/openapi-generator-java-annotationfree)
    - TypeScript apis and DTOs generated
      by [openapi-generator-typescript-fetch-api](https://github.com/jhannes/openapi-generator-typescript-fetch-api)
- Serving the front-end code from the same Jetty container using
  the [frontend-maven-plugin](https://github.com/eirslett/frontend-maven-plugin),
  which means a single deployment for releases and avoiding cross-origin API
  requests
- Implementing authentication with OpenID Connect
- Implementing database logic and transactions with the micro-ORM fluent-jdbc
- Configuration-free logging with MDCs using logevents

## The application

The application is a simple TODO application. You can list tasks, create new
tasks and update task descriptions. You can update the status of one or more
tasks at the same time. (The reason this is done in bulk is to demonstrate
transactions, as some of the updates may fail, while others succeed)

## Running for development

Required dependencies:

- JDK 17
- Docker (for running PostgreSQL)
- NodeJS (for frontend build)

Running the application

1. Start the database with `docker compose up`
2. Start the frontend build with `npm start`
3. Start the application server `com.soprasteria.simplejavaee.ApplicationServer`
4. Access the application on http://localhost:8080
5. If you want the application to authenticate your user, you need to create a
   Azure Active Directory application registration the callback URL
   `http://localhost:8080/api/login/callback` and create a `.env` file with the
   following values:
   ```.env
   OPENID_CLIENT_ID=....
   OPENID_CLIENT_SECRET=....
   ```

## Deployment

This describes how to build a docker image and push it to a docker registry and
how to deploy on a Kubernetes cluster.

### Configure Maven to upload your docker image

The application is optimized for deployment with a Docker image running at
GitHub Container Registry (GHCR).

You must create a [personal access token](https://github.com/settings/tokens)
with the `write:packages` privilege to use as your password.

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

`mvn clean install`

Running the image locally:

`docker login ghcr.io/jhannes`
`docker run -p 18080:8080 ghcr.io/jhannes/simplejavaee`

### Deploying to a Kubernetes cluster

If you want to test out the application on an ad hoc Kubernetes cluster, you can
use [KinD (Kubernetes in Docker)](https://kind.sigs.k8s.io/). I also
recommend [K9S](https://k9scli.io/) to manage your cluster in style.

#### Setting up a Kind cluster

Kind (Kubernetes in Docker) is a simple to install cluster that can run on a
workstation inside docker. This lets you test full Kubernetes functionality
without access to a full cluster on a cloud provider. Be aware that it may
affect the performance of your workstation.

This example uses Ingresses to route traffic to your deployment and PostgreSQL
as a database. To support this, the following instructions include setting up
Nginx as an ingress controller and the Zalando Postgres operator as base
services in the cluster.

1. Install the necessary dependencies
    - Docker desktop
    - [kind](https://kind.sigs.k8s.io/)
2. Create a cluster on your local
   machine: `kind create cluster --config src/main/cluster/create-cluster.yaml`
    - `create-cluster.yaml` contains configuration to forward port 80 and 443 on
      your local machine to this cluster
    - This configuration includes support for ingress controllers. If you don't
      need this, you can do `kind create cluster`
    - The `kind create cluster` command updates `~/.kube/config` to
      make `kubectl` connect to the new cluster
    - You can see the cluster nodes running with `docker ps`
    - You can see the nodes from the perspective of the cluster
      with `kubectl get nodes`
3. Install an Ingress controller (in our case: Nginx):
    - `kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml`
    - Wait for the pods to
      start `kubectl wait --namespace ingress-nginx --for=condition=ready pod --selector=app.kubernetes.io/component=controller --timeout=90s`
4. Install the postgresql
   operator: `kubectl apply -k github.com/zalando/postgres-operator/manifests`
5. Add the following entry to your hosts-file:
   ```
   127.0.0.1 simplejavaee.test.example.com
   ```

#### Apply the Kubernetes manifest with your docker file to your cluster

1. (First time only) Create test namespace:
   ```sh
   kubectl create namespace simplejavaee
   ```
2. (First time only) Create a pull-token for the namespace:
   ```sh
   kubectl --namespace simplejavaee create secret docker-registry pull-secret --docker-username=<appId> --docker-password=<password> --docker-server=ghcr.io/<username>
   ```
3. (First time only) Create a secret with the OAUTH configuration. (Replace the
   variables with your own. The application will start if the variables are
   wrong, but login will fail)
   ```sh
   kubectl --namespace simplejavaee create secret generic simplejavaee --from-literal=OAUTH_CLIENT_ID=$OAUTH_CLIENT_ID --from-literal=OAUTH_CLIENT_SECRET=$OAUTH_CLIENT_SECRET
   ```
4. Deploy the application (replace `ghcr.io/jhannes/simplejava` with your docker
   repository):
   ```sh
   helm template --set imageRepository=ghcr.io/jhannes/simplejavaee src/main/helm | kubectl --namespace simplejavaee apply -f -
   ```

If you have set up your hosts file (or DNS) correct, you can now access the
application at http://simplejavaee.test.example.com

#### Making OpenID Connect (Active Directory) work

Active Directory requires the redirect URI to be a secure URI. Only https and
localhost-URIs qualify as secure. This means that you will have to set up TLS
for the Ingress for your application. This requires a few steps:

1. Create a self-signed keypair with openssl (replace -subj and -addext values
   as desired):
   ```sh
   openssl req -nodes -x509 -sha256 -newkey rsa:4096 -keyout simplejavaee.test.example.com.key -out simplejavaee.test.example.com.crt -days 356 -subj "/O=Simple Corp/OU=IT Dept/CN=simplejavaee.test.example.com" -addext "subjectAltName = DNS:simplejavaee.test.example.com"
   ```
2. Install the certificate in your namespace
   ```sh
    kubectl --namespace simplejavaee create secret tls tls-secret --key simplejavaee.test.example.com.key --cert simplejavaee.test.example.com.crt
   ```
3. You can now install `simplejavaee.test.example.com.crt` as a Trusted Root
   Certificate Authority on your computer. Instructions vary, but generally, you
   can right-click the file and choose "Install certificate". Make sure you
   select the Root Authorities store
4. You should now be able to go to https://simplejavaee.test.example.com and not
   get an SSL warning. You may have to restart the browser for it to work.
5. In Active Directory create
   an [App registration](https://portal.azure.com/#view/Microsoft_AAD_IAM/ActiveDirectoryMenuBlade/~/Overview)
   for your application.
6. You must setup the Authentication Redirect URI
   to `https://simplejavaee.test.example.com/api/login/callback`
7. You must generate an application secret
8. You must update the application secret to use the App registration
   Application ID as OAUTH_CLIENT_ID and the secret as OAUTH_CLIENT_SECRET:
   ```sh
   kubectl --namespace simplejavaee delete secret simplejavaee
   kubectl --namespace simplejavaee create secret generic simplejavaee --from-literal=OAUTH_CLIENT_ID=$OAUTH_CLIENT_ID --from-literal=OAUTH_CLIENT_SECRET=$OAUTH_CLIENT_SECRET
   ```
9. You should restart the application server to make sure the secrets are in
   place:
   ```sh
   kubectl --namespace simplejavaee rollout restart deployment simplejavaee
   ```
10. You should now be able to log in using the Login link
    at [the web page](https://simplejavaee.test.example.com)

## Still not complete

- [ ] Continuous deployment with Github Actions. Minimally should
  run `mvn clean install` and have image end up at GHCP tagged with commit sha.
  Ideally, should publish reports from `mvn` and `npm`
- [ ] Updating the description doesn't refresh. When updating fails because,
  e.g. because of authentication or authorization error, it would be nice to
  display an error message
- [ ] It should be possible (and easy) to set up an alternative OAUTH provider (
  e.g. Google)
- [ ] Why can't I access the postgres database in KinD?
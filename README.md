Why?

You need to do more! You need to spend more time on the essential complexity and less on accidental complexity.

But what is essential and what is accidental?

A simple example: Leap years and leap seconds are essential. Daylight saving time is accidental

* It's essential that a police system for jailors documents adherence to legal requirements for detainment
* But it is possible to change laws as well
* It's accidental which (modern!) programming language you use
* But what about a responsive web-site or dark mode?
* What about real-time propagation of events with web sockets and Kafka?
* What about continuous deployment to a Kubernetes cluster?
* What about documenting APIs with OpenAPI?

In addition to the essential complexity of the business domain, there is a growing big set of technological expectations from our users and other teams in our organizations. Time spend dealing with a dependencies that won't inject as expected or object that won't map relations as expected is time we won't spend making our users happy.

What you need to do....

* Deploy business logic and assets on Kubernetes
  * Responding to HTTP requests (Jetty)
  * Business logic (Servlets)
  * Static files
  * Packaging as Docker image (jib-maven-plugin)
  * Deploying on a Kubernetes cluster
  * Bonus: Better logging with logevents (*)

* APIs
  * Implementing REST controllers (Action Controller (*))
  * Defining OpenAPI spec (spec first)
  * Serving Swagger-UI from Webjar
  * Using generated code in the controllers (openapi-generator-plugin (*))

* Frontend bundling
  * React application
  * packing into the Docker container (frontend-maven-plugin)
  * Generating frontend code from OpenAPI spec
  * BrowserRouter and default resource

* Authentication with OpenID Connect
  * Configuration with Environment variables

* Implementing database code with a micro ORM
  * Database migration (Flyway)
  * Fluent-jdbc (*)
  * Transactions


Very simple.... 😂

* Serving http content and logic with Jetty
* Docker with jib-maven-plugin
* Kubernetes
* Request routing ActionController (*)
* OpenAPI with openapi-generator and Swagger UI
* React with frontend-maven-plugin
* OpenID Connect
* Micro ORM
* Database migrations (Flyway)
* Configuration

What your application needs to implement:

* Build everything (including generated code, frontend) to a Docker container
* Route requests in an explicit way
* Document and enforce API contracts
* Interaction between requests, internal service objects and environment-dependent configuration
* Data to and from the database

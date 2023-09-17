Java EE Simplified
==================

How do you set up the minimal infrastructure for a Java project that responds to HTTP requests with static and dynamic
content (APIs) and that communicates with a database? Hint: Spring is overcomplicated

The first step of is to create a Docker image with Maven that contains everything needed to serve static files and a
servlet. This requires only one Maven dependency (`org.eclipse.jetty:jetty-servlet`), one Maven plugin
(`com.google.cloud.tools:jib-maven-plugin`) and one Java class in addition to the servlet itself.

The second step is to document and implement APIs. I use OpenAPI to define the APIs and generate the code needed to
implement the API. Using OpenAPI lets me generate code for both the backend and frontend. Generating the code from the
spec instead of the other way around makes API design into a conscious effort and lets me exploit more of OpenAPI. As a
bonus, it requires less typing than creating typical verbose Java classes.

To visualize my API, I include the dependency `org.webjars:swagger-ui`. To implement the API, I use my own small web
framework [Action Controller](https://github.com/jhannes/action-controller/), but you could do the same using JAX-RS or
plain Servlets.

To save data in the database, I'm using [Fluent JDBC](https://github.com/jhannes/fluent-jdbc), my own micro-ORM. You
could of course use plain JDBC or any other ORM.

## Implementation of DAOs



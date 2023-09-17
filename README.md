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
framework Action Controller, but you could do the same using JAX-RS or plain Servlets.

## Implementation of API with Action Controller

As the API gets bigger, it can be difficult to get understand the API code littered through the servlet code. Using a
controller framework can make the API stand out more clearly. In this example, I'm using my own controller framework
called Action Controller to describe the routing and request/response mapping using annotations.

This example could use JAX-RS and Jersey or even Spring, but in these cases, the controllers are instantiated by the
framework. This creates the need for a dependency injection framework, which frequently kicks both novice and
experienced users in the balls. Even so, I use JAX-RS on my current project to avoid depending on self-created code.

### OpenAPI spec

This is a minimal API spec:

```yaml
openapi: "3.0.2"
info:
  title: Task tracker application
  version: "1.0"
servers:
  - url: /api
paths:
  /tasks:
    get:
      operationId: listTasks
      tags: [ Tasks ]
      responses:
        200:
          description: Lists all tasks visible to current user
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Todo"
components:
  schemas:
    Todo:
      properties:
        id:
          type: string
          format: uuid
        title:
          type: string
        status:
          $ref: "#/components/schemas/TaskStatus"
      required:
        - id
        - title
        - status
    TaskStatus:
      enum:
        - TODO
        - DOING
        - DONE
```

In order to visualize the API, we add a <dependency> for `org.webjars:swagger-ui` and use `WebJarServlet` to expose the
files in the dependency jar. `src/main/resources/webapp/api-doc/index.html` sets up Swagger UI to use `todos.yaml`.

### Code generation

openapi-generator-maven-plugin generates DTOs from an OpenAPI spec. To avoid extra dependencies, I have created a plugin
for openapi-generator with a minimal dependency Java generation.

```xml

<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>6.6.0</version>
    <configuration>
        <inputSpec>src/main/resources/webapp/api-doc/todos.yaml</inputSpec>
    </configuration>
    <executions>
        <execution>
            <id>generate-java-api</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <generatorName>java-annotationfree</generatorName>
                <apiPackage>com.soprasteria.generated.javaeesimplified.api</apiPackage>
                <modelPackage>com.soprasteria.generated.javaeesimplified.model</modelPackage>
                <generateApis>false</generateApis>
                <modelNameSuffix>Dto</modelNameSuffix>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>io.github.jhannes.openapi</groupId>
            <artifactId>openapi-generator-java-annotationfree</artifactId>
            <version>0.5.4</version>
        </dependency>
    </dependencies>
</plugin>
```

### API Servlet

TODO
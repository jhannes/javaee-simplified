Steps:

1. Run empty Jetty application
2. Package for Docker
    * `docker run ghcr.io/jhannes/java-simple-server`
3. Run frontend code
4. Run on kind
5. Create OpenAPI spec with Swagger UI
   * Write spec
   * Publish on /api-doc/todo.yaml (update ContentServlet)
   * Display using swagger-ui
6. GET /login fails, forcing user to log in
    * Generate code from spec
    * Generate code from openid-configuration spec ?
    * Frontend gets /api/login
    * On 401, offer user to log in
    * Redirect to /api/login/start -> /api/login/callback
    * Use code flow with client secret. Fix access token using a cookie and verify
7. Implement login with OpenID Connect
8. Implement post and list endpoints in memory
9. Implement post and list endpoint in PostgreSQL

Technologies:

* Jetty
    * Serving static files like a pro!
    * Logging
* frontend-maven-plugin
* jib-maven-plugin
* kind
* openapi-generator
    * typescript + java
* jax-rs
* postgresql + jdbc
    * Zalando postgresql operator
* fluent jdbc

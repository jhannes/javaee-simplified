Java EE Simplified
==================

How do you set up the minimal infrastructure for a Java project that responds to HTTP requests with static and dynamic
content (APIs) and that communicates with a database? Hint: Spring is overcomplicated

The first step of is to create a Docker image with Maven that contains everything needed to serve static files and a
servlet. This requires only one Maven dependency (org.eclipse.jetty:jetty-servlet), one Maven plugin
(com.google.cloud.tools:jib-maven-plugin) and one Java class in addition to the servlet itself.

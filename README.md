
🍂 SPRING? I THINK NOT


# Out biggest enemy is complexity.

## With 💚 by Johannes Brodwall (Sopra Steria/Norwegian Police IT unit)

Code: https://github.com/jhannes/javaee-simplified

Everybody wants to create SIMPLE software, but we have too little
understanding about the nature of COMPLEXITY.

About 40 years ago the book No Silver Bullet described ESSENTIAL
complexity and ACCIDENTAL complexity. With ACCIDENTAL complexity,
we have a choice. With ESSENTIAL complexity, we have to deal.

A simple example: Astrophysics dictate the need for LEAP YEARS and
LEAP SECONDS. But humans decide on the complexity of DAYLIGHT SAVING
TIME.

We are expected to deliver more complex solutions: Integration,
single-sign-on, real-time, responsive software that also
complies with laws regarding privacy. Business is more integrated,
more complex, more regulated.

We are expected to run our systems on cloud native platforms with
a complex set of technologies and integrations.

This we cannot choose. But there is a lot we can choose. As
developers, libraries, frameworks and languages are often within
out power. We better make use of it.

I want to avoid injections. Spring has great doc, but it doesn't 
answer my most pressing question: WHY MY CODE NOT WORK???

In this talk, I will show you MY OWN STACK, but hopefully, there
are a lot of principals that apply to ANY STACK

When delivering software, I wanted to look at how to:
 
1. Respond to HTTP requests with dynamic and static content
2. Deploy to a Kubernetes cluster
3. Route incoming requests to respond to an API specification
4. Document our API
5. Sign on users (OpenID Connect)
6. Include front end code in our deployment
7. Persist data to the database
8. Using transactions
9. Configure services
10. Write useful logs

Not all of this will be covered. What I will do:

* [x] Create a server that responds with dynamic content
* [x] Include static content
* [x] Deploy to Kubernetes (including building a Docker image)
* [x] Document API
* [x] Implement the documented API
* [ ] Persist data to database
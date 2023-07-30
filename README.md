# Goal

The application lets you log in and write, list and update todos that are saved in the database

Parts:

* Frontend with react

Demonstration:

* Go to front page
* Get redirected to log in
* When returned, front page has functionality for creating and updating TODOs


## Missing tasks


* [ ] Open-ID Connect integration


# Notes:

1. To deploy image to ghcr, run `mvn install`. This requires authentication with github
    * (First time only) Create a maven master password: `mvn --encrypt-master-password`. This generates a
      file `~/.m2/settings-security.xml`
    * Generate a token
      under [Settings > Developer Settings > Personal access tokens > Tokens (classic)](https://github.com/settings/tokens).
      The token must have `write:packages`
    * Encrypt the token using `mvn --encrypt-password <github token>`
    * Copy the output into `~/.m2/settings.xml`
      ```xml
       <server>
          <id>ghcr.io</id>
          <username>your github username</username>
          <password>{... password from previous step}</password>
       </server>
      ```
2. To start a local kubernetes cluster, run [`kind create cluster`](https://kind.sigs.k8s.io/). See instructions on the web site
3. To deploy, run `helm template src/main/kubernetes`
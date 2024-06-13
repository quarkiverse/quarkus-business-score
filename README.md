# Quarkus Business Score

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.businessscore/quarkus-business-score?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/io.quarkiverse.businessscore/quarkus-business-score)

This extension offers a convenient way to detect application _zombies_, i.e. applications that live/communicate but **do not produce any business value**.
"business value" means anything that helps to solve a problem of an application user. 
Infrastructure stuff (logs, stats, metrics, health checks) usually does not produce a business value.

### Key concepts

In a nutshell, the extension manages the **business score** which is a numeric representation of the business value. 
An application emits **score records** to increase the business score, either directly with the `BusinessScore` API or with an interceptor binding.
The records are only kept for the specific amount of time - **time window**. 
The sum of all score records in the current time window is the **current business score**. 
The **zombie threshold** defines the point at which the application is considered a _zombie_.
An external system or the application itself can periodically check the business score and react appropriately.

Read the full [documentation](https://docs.quarkiverse.io/quarkus-business-score/dev/index.html).

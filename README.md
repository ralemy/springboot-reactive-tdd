# Test Driven Development of Web Services using Spring Boot

This project explains and demonstrates necessary steps to create Restful 
web services under the TDD methodology using Spring Boot.

The current status of the project follows:

| Technique Demonstrated | Status | Commit ID|
|------------------------|--------|----------|
|[Use of Cucumber for TDD in addition to integration tests][]| Iteration 01|[Iteration 01](../../tree/step01.Cucumber)|
|[H2 repositories for production and test](docs/hibernate.jpa.md)| Iteration 01|[Iteration01](../../tree/step02.hibernateh2)|
|[MongoDB reactive repositories for production and test](docs/mongo.md)|Not ImplementedYet|Not Applicable|
|MVC Restful service|Not ImplementedYet|Not Applicable|
|Reactive Restful Service|Not ImplementedYet|Not Applicable|
|Akka Actor Sytem|Not ImplementedYet|Not Applicable|
|Reactive Web Socket implementation|Not ImplementedYet|Not Applicable|
|Server Side Events| Not ImplementedYet|Not Applicable|


# Development Protocol

Each new technique is created by branching of the master into ``` technique/name``` directory.
The documentation for each technique is in a markdown ```<techniqueName>.md``` file inside the 
```./docs``` directory.

Once the tests pass and the technique is implemented the branch is merged back to master,
and this file is updated to show the commit that contains the implementation, plus a link 
to the markdown documentation for the technique.


[Use of Cucumber for TDD in addition to integration tests]:docs/cucumber.md
[Cucumber01]:f1a9c8d27bb379a898a72bc8a3f0ba257904a1cd

# Test Driven Development of Full Stack applications using Spring Boot

See Github pages (https://ralemy.github.io/springboot-reactive-tdd/)

This project explains and demonstrates necessary steps to create a full stack
application using TDD methodology with Spring Boot.

The current status of the project follows:

| Technique Demonstrated | Stack Position | Technology Focus| iteration|
|------------------------|--------|----------|------|
|[Use of Cucumber for TDD in addition to integration tests](docs/cucumber.md)| Foundation|[Cucumber](../../tree/step01.Cucumber)|01|
|[H2 repositories for production and test](docs/hibernate.jpa.md)| Backend |[Hibernate](../../tree/step02.hibernateh2)|01|
|[Use of Mockito in Cucumber unit tests](docs/mockito.md)| Foundation|[Mockito](../../tree/step04.mockito)|01|
|[MongoDB reactive repositories for production and test](docs/mongo.md)|Backend|[Mongo](../../tree/step03.reactivemongo)|01|
|[MVC Restful service](docs/mvc.md)|Middleware|[SpringMVC](../../tree/step05.spring.mvc)|01|
|Reactive Restful Service|Middleware|Not Applicable|00|
|Akka Actor Sytem|Middleware|Not Applicable|00|
|Reactive Web Socket implementation|Middleware|Not Applicable|00|
|Server Side Events| Middleware|Not Applicable|00|
|Angular integration with MVC services | Frontend | Not Applicable| 00|
|Angular integration with reactive streaming | Frontend | Not Applicable|00|


# Development Protocol

Each new technique is created by branching of the master into ``` technique/name``` directory.
The documentation for each technique is in a markdown ```<techniqueName>.md``` file inside the 
```./docs``` directory.

Once the tests pass and the technique is implemented the branch is merged back to master,
and this file is updated to show the commit that contains the implementation, plus a link 
to the markdown documentation for the technique.



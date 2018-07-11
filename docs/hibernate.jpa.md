# Use of JPA and Hibernate in Spring boot application

Data persistence is a very common requirement in every application. One of the best practices
in implementation of data persistence is the use of ORM, in which a framework will usher data
between objects and database tables automatically. One of the most popular ORM frameworks is
Hibernate, which will be used in this step. 

The Database behind the ORM framework can be any engine, in this example we use H2. H2 is an
embedded engine that can work on file or in memory. The latter is useful in creating transient
data stores for unit tests. In this section we demonstrate the configuration of hibernate to use
one database in production and the other one during tests.

## Configuring hibernate outside test environment

A Spring Boot application has an ```application.properties``` file located in ```src/main/resources```.
here is how this application can be configured to use an on-file database using H2 engine:

```
spring.h2.console.enabled=true
spring.h2.console.path=/h2

spring.datasource.url=jdbc:h2:file:./objects
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true
```

The first line enables the hibernate console, which is very useful in browsing and
examining the data store. the second line instructs that the console should be served
at the ```/h2``` endpoint of web server.

However, since the security-started is included in POM, the ```/h2``` endpoint will not 
be accessible. Web Security is outside the scope of this document but for our purposes all
is needed is to exclude the endpoint from security. 

To achieve that, create a configuration class that is extended from Web

```java
package com.curisprofound.tddwebstack.config;
// imports not shown
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.anonymous()
                .and().authorizeRequests().antMatchers("/h2**").permitAll()
                .and().logout().permitAll();
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }
}
```

Now, each time the application starts, the ```/h2``` endpoint will show the database connection
dialog which can connect with username "sa" and empty password.

### Creating an H2 in memory database for testing

At testing time, Hibernate can use another database, in which case and in memory embedded 
database seems best, given that the repository is transient in nature. To achieve this, 
another application.properties file should be placed in ```src/test/resources``` which 
defines the test environment.

```
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=sa
spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true
```

### Creating Hibernate models

In Spring boot, a POJO annotated with ```@Entity``` will be considered an ORM model, and
a class extending ```JpaRepository<model>``` is used as the DAO (data access object).

Therefore, the inital test will check for a POJO annotated with "Entity":

```gherkin
Feature: As a developer
  I need an ORM to deal with data persistence
  So that I could safely change engines and use migration tools

  @HibernateJPA
  Scenario: Should have a class named Customer annotated with @Entity
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
    When  The annotations of the class are examined
    Then  the "Entity" annotation exists in the class annotations
```

The Steps file for this scenario is implemented as below:

```java
public class HibernateJPASteps extends StepsBase {

    @After("@HibernateJPA")
    public void afterHibernateJPA(){
        tearDown();
    }



    @Given("^There exists a class named \"([^\"]*)\" in \"([^\"]*)\" package$")
    public void thereExistsAClassNamedInPackage(String arg0, String arg1) throws Throwable {
        String name = arg1+"."+arg0;
        Class.forName(name);
        Add(String.class,name, "ClassName");

    }

    @When("^The annotations of the class are examined$")
    public void theAnnotationsOfTheClassAreExamined() throws Throwable {
        Annotation[] annotations = Class.forName(Get(String.class, "ClassName"))
                .getAnnotations();
        Add(Object.class, annotations, "ClassAnnotations");
    }

    @Then("^the \"([^\"]*)\" annotation exists in the class annotations$")
    public void theAnnotationExistsInTheClassAnnotations(String arg0) throws Throwable {
        Annotation[] annotations = (Annotation[])Get(Object.class, "ClassAnnotations");
        Optional<Annotation> actual = Arrays.stream(annotations)
            .filter(a -> a.annotationType()
                            .getName()
                            .contains(arg0))
            .findFirst();
        assertTrue(actual.isPresent());
    }
}
```

Test will fail with ```ClassNotFound``` exception until we actually create the classs,
then it will fail with ```AssertionError``` until we annotate the class as Entity.

If the tests runs while the application is running (for example using Intellij IDEA), one 
can use the console to verify that in fact no transaction is made to the on-file database.

### Unittesting fields

To check for fields, a scenario could asset the existence of a getter and setter
for that field, and the fact that it is a column, and id class, etc.

```gherkin

```
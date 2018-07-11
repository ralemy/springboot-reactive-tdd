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

### Creating 
# Implementing Restful services with MVC

## Open endpoints, not requiring authentication

We will discuss security in the next chapters,for now let's open an endpoint to waive authentication
requirements. edit the WebSecurityConfig file:

```java
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/h2/**").permitAll()
                .antMatchers("/customers").permitAll()
                .anyRequest().authenticated()
                .and().logout().permitAll();
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }
}
```

The first step is to have a RestController object which has mappings for end point. for CRUD services, 
We can test for that:

```gherkin
  @MvcRestful
  Scenario: Should have a controller class for the MVC endpoints
    Given There exists a class named "CustomerController" in "com.curisprofound.tddwebstack.controllers" package
    Then  the "RestController" annotation exists in the class annotations
    And   The class has the following properties: "customerRepository"
    And   The "customerRepository" field is of type "CustomerRepository"
    And   The "getAllCustomers" method of the class is annotated by "GetMapping" with parameter "value" set to "/customers"
```
We now have an endpoint, "/customers" which is glued to the getAllCustomers() method. we now
test that the method will call customerRepository.findAll() to get the list of available customers.

We will mock the findAll() method to return some known value

```gherkin
  @MvcRestful
  Scenario: Should have a mock of customer repository autowired
    Given the autowired customerRepository is a MockBean
    And   the findAll method is masked to return a customer named "customerOne"
    Then  the findAll method will return one customer by name of "customerOne"
```

Now, we can test that if we call the /customer then we will get a list containing one 
customer by the name of "custmer1"

```gherkin
  @MvcRestful
  Scenario: Should return the mocked result from the /customers endpoint
    Given   the findAll method is masked to return a customer named "customerOne"
    When    I "GET" the "/customers" endpoint
    Then    I get a list of Customer objects with one member by the name of "customerOne"
```

In order to simulate calls to endpoints, in the MVC world the MockMVC object is used. 
the mockMVC() method of the base class adds it to the scenario context, so our we will
call that method in the @Before of our scenario:

```java
    @Before("@MvcRestful")
    public void beforeMvcRestful() {
        mockMvc(this.getClass(), "beforeMvcRestful");
    }
```
Now, we can perform a GET to the /customers endpoint and assert that the result is 200.

```java
        ResultActions result = Get(MockMvc.class).perform(
                get("/customers")
        )
                .andExpect(status().isOk());
        Add(ResultActions.class, result);
```

We can then examine the results in next steps:

```java
        String resp = Get(ResultActions.class).andReturn().getResponse().getContentAsString();
        List<Customer> customers = jsonStringToClassArray(resp,Customer.class);
        assertEquals(
                1,
                customers.size()
        );
        assertEquals(
                expectedName,
                customers.get(0).getName()
        );
```

The same technique is used for DELETE, POST, and PUT. basically we mock the repository to be able
to control the responses and prevent actual changes in database, then we call the endpoints on the
controller and check to see how the customerRepository is affected and what is returned to the 
client.


## TDD of authenticated endpoints

The WebSecurityConfig object is controlling the authentication requirements of the endpoints.
at this time, it is allowing "/customer" and "/h2/**", and asking everything else to be authenticated.
Theoretically, it is possible to unit test this class and check the ```FilterChain``` objects to ensure
correct configuration, but this is a lot of work and has huge dependency on the underlying mechanisms
of Spring boot and can change in the future without notice, so it is [not considered the best practice](https://stackoverflow.com/questions/43663688/spring-security-httpsecurity-configuration-testing).

In this case it is easier to lean towards integration testing, just ensuring that the
call will fail and pass properly without and with authentication.

So, let's first check we have an endpoint configured that would behave correctly if 
it is reached. 

```gherkin
  @MvcRestful
  Scenario: Should have a controller class for the MVC endpoints
    Given There exists a class named "CustomerController" in "com.curisprofound.tddwebstack.controllers" package
    And   The "saveCustomer:Customer" method of the class is annotated by "PutMapping" with parameter "value" set to "/customer"
    And   I have mocked save function to trap its input
    When  I call saveCustomer on "customerController" bean with "customerOne"
    Then  I can verify the save function was called with "customerOne"
```
Then we check that if we try to connect to it without authentication,
it will fail.

```gherkin
  @MvcRestful
  Scenario: Should reject call to save customer if not authenticated
    Given I have mocked save function to just return its input
    When   I "PUT" the "/customer" with "customerOne" and no authentication
    Then  I receive a 403 response status
    And   I can verify the save function was not called
```

And if we try to connect to it with an authenticated user, it will 
return 200, and call save on customer repository:

```gherkin
  @MvcRestful
  Scenario: Should accept call to save customer if authenticated
    Given I have mocked save function to just return its input
    And   I have a user "usr1" and password "pass1" configured
    When  I "PUT" the "/customer" with "customerOne" with such credentials
    Then  I receive a 200 response status
    And   I can verify the save function was called with "customerOne"
    And   The endpoint returns a customer object named "customerOne"
```







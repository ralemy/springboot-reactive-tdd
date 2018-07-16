# Implementing Restful services with MVC

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



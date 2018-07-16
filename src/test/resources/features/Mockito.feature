# Feature File for Cucumber Testing
# Created 7/15/18 8:47 PM by rezaalemy

Feature: I need MVC based controllers
  So that I can implement CRUD use cases for JPA based repositories


  @Mockito
  Scenario: Should have a component to override Mock post processor
    Given  There exists a class named "MockPostProcessor" in "com.curisprofound.tddwebstack.cucumber" package
    And    The class has a method "postProcessAfterInitialization" with parameters "Object,String"
    And    the "Component" annotation exists in the class annotations


  @Mockito
  Scenario: Should have a repository class for customer ORM
    Given  There exists a class named "CustomerRepository" in "com.curisprofound.tddwebstack.db" package
    Then   The interface implements the "JpaRepository" with "Customer" and "Long" arguments


  @Mockito
  Scenario: Should have a controller class for the MVC endpoints
    Given There exists a class named "CustomerController" in "com.curisprofound.tddwebstack.controllers" package
    Then  the "RestController" annotation exists in the class annotations
    And   The class has the following properties: "customerRepository"
    And   The "customerRepository" field is of type "CustomerRepository"
    And   The "getAllCustomers" method of the class is annotated by "GetMapping" with parameter "value" set to "/customers"

  @Mockito
  Scenario: Should have a mock of customerRepository injected to the tests
    Given  the autowired customerRepository is a MockBean
    And    the findAll method is masked to return a customer named "customer1"
    Then   the findAll method will return one customer by name of "customer1"
    And    the findAll method was call coundter would be 1
    And    execution of findall test is recorded

  @Mockito
  Scenario: Should reset the mock before the next test
    Given  the findall test has been executed
    And    the findAll method was call coundter would be 0
    Then   the findAll mask is no longer active
    And    the findAll method was call coundter would be 1

  @Mockito
  Scenario: Should be able to capture the input of the mock
    Given I have a customer object by name of "customer1"
    And   I have mocked save function to trap its input
    When  I save it to customer repository
    Then  I can verify the save function was called with "customer1"


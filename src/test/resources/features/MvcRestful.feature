# Feature File for Cucumber Testing
# Created 7/16/18 1:41 PM by rezaalemy

Feature: I need restful interfaces using MVC components
  to implement use cases where MVC is needed.

  @MvcRestful
  Scenario: Should have a controller class for the MVC endpoints
    Given There exists a class named "CustomerController" in "com.curisprofound.tddwebstack.controllers" package
    Then  the "RestController" annotation exists in the class annotations
    And   The class has the following properties: "customerRepository"
    And   The "customerRepository" field is of type "CustomerRepository"
    And   The "getAllCustomers" method of the class is annotated by "GetMapping" with parameter "value" set to "/customers"

  @MvcRestful
  Scenario: Should have a mock of customer repository autowired
    Given the autowired customerRepository is a MockBean
    And   the findAll method is masked to return a customer named "customerOne"
    Then  the findAll method will return one customer by name of "customerOne"

  @MvcRestful
  Scenario: Should return the mocked result from the /customers endpoint
    Given   the findAll method is masked to return a customer named "customerOne"
    When    I "GET" the "/customers" endpoint
    Then    I get a list of Customer objects with one member by the name of "customerOne"

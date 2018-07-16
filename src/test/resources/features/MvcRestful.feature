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

# Feature File for Cucumber Testing
# Created 7/15/18 8:47 PM by rezaalemy

Feature: I need MVC based controllers
  So that I can implement CRUD use cases for JPA based repositories


  @MvcRestful
  Scenario: Should have a repository class for customer ORM
    Given  There exists a class named "CustomerRepository" in "com.curisprofound.tddwebstack.db" package
    Then   The interface implements the "JpaRepository" with "Customer" and "Long" arguments


  @MvcRestful
  Scenario: Should have a controller class for the MVC endpoints
    Given There exists a class named "CustomerController" in "com.curisprofound.tddwebstack.controllers" package
    Then  the "RestController" annotation exists in the class annotations
    And   The class has the following properties: "customerRepository"
    And   The "getAllCustomers" method of the class is annotated by "GetMapping" with parameter "value" set to "/customers"



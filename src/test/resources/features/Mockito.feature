# Feature File for Cucumber Testing
# Created 7/15/18 8:47 PM by rezaalemy

Feature: I need MVC based controllers
  So that I can implement CRUD use cases for JPA based repositories


  @Mockito
  Scenario: Should have a repository class for customer ORM
    Given  There exists a class named "CustomerRepository" in "com.curisprofound.tddwebstack.db" package
    Then   The interface implements the "JpaRepository" with "Customer" and "Long" arguments


  @Mockito
  Scenario: Should have a component to override Mock post processor
    Given  There exists a class named "MockPostProcessor" in "com.curisprofound.tddwebstack.cucumber" package
    And    The class has a method "postProcessAfterInitialization" with parameters "Object,String"
    And    the "Component" annotation exists in the class annotations
    And    The class has a field called "classes" that is of type List of "Class"

  @Mockito
  Scenario: Should inject a bean for Mock post processor which mocks customerRepository
    Given There is a bean for "mockPostProcessor"
    And   The classes list has CustomerRepository in it
    When   I call the post-processor with a general object
    Then   I get the same object without mocking
    When   I add a class to class list of preprocessor
    And    I call the post-processor with a an instance of that class
    Then   I get the mocked version of the class


  @Mockito
  Scenario: Should return the same object for every Id
    When   I get the Customer with id 10
    Then   the customer name is "customerFixed"
    When   I get the Customer with id 20
    Then   the customer name is "customerFixed"

  @Mockito
  Scenario: Should be able to mock the object to return something based on input arguments
    Given  I have mocked customerRepository FindbyId to return a customer with id plus 10
    When   I get the Customer with id 10
    Then   the customer id is 20
    When   I get the Customer with id 25
    Then   the customer id is 35


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


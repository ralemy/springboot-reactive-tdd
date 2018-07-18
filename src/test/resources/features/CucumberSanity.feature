Feature: As a developer
  I need to ensure that Cucumber is installed correctly and can persist state between test steps
  So that I can confidently focus on implementing my features

  @CucumberSanity
  Scenario: Should be able to assert a simple condition
    Given I initialize World variable "p1" to 5 and "p2" to 12
    When  I add "p1" and "p2" and store the result in "p3"
    Then  The World variable "p3" should be equal to 17

  @CucumberSanity
  Scenario: Should be able to use World object across step classes
    Given I initialize World variable "p1" to 4 and "p2" to 7
    When  I multiply "p1" and "p2" and store the result in "p3"
    Then  The World variable "p3" should be equal to 28

  @CucumberSanity
  Scenario: Should clear out the objects between Scenarios
    Given I have initialized "p1" in a previous Scenario
    When  I enter a new Scenario
    Then  The World variable "p1" should be null


  @CucumberSanity
  Scenario: Should be able to correctly interpret method signatures
    Given I have a signature of "Map<String, Class<?> >, Object, java.util.HashSet<String>"
    When  I parse the signature into an object
    Then  I will get a correct presentation of parameters


  @CucumberSanity
  Scenario: Should be able to identify imbalanced
    Given I have a signature of "Map<String,Class<?>, Object, java.util.HashSet<String>"
    Then  I will get an assertion fail on parsing the string: "missing `>`: "
    Given I have a signature of "Map String,Class<?>>, Object, java.util.HashSet<String>"
    Then  I will get an assertion fail on parsing the string: "missing `<`: "

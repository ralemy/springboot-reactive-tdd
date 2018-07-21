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
  Scenario: Should be able to correctly interpret parameter signatures
    Given I have a signature of "Map<String, Class<?> >, Object, java.util.HashSet<String>"
    When  I parse the signature into an object
    Then  I will get a correct presentation of parameters


  @CucumberSanity
  Scenario: Should be able to identify imbalanced signatures
    Given I have a signature of "Map<String,Class<?>, Object, java.util.HashSet<String>"
    Then  I will get an assertion fail on parsing the string: "missing `>`: "
    Given I have a signature of "Map String,Class<?>>, Object, java.util.HashSet<String>"
    Then  I will get an assertion fail on parsing the string: "missing `<`: "

  @CucumberSanity
  Scenario: Should be able to test the existence of a class in a package
    Given There exists a class named "TestMe" in "com.curisprofound.tddwebstack" package

  @CucumberSanity
  Scenario: the test system should be able to say if a class member is accessible
    Given There exists a class named "TestMe" in "com.curisprofound.tddwebstack" package
    And   The class has the following properties: "privateField, publicField, getterField"
    Then  "publicField" is readable
    And   "getterField" is readable
    And   "privateField" is not readable

  @CucumberSanity
  Scenario: should be able to detect a field and its complex type
    Given There exists a class named "TestMe" in "com.curisprofound.tddwebstack" package
    Then  Class has a field "complexField" of Type "Map<String, Map<Integer, List<String>>>"

  @CucumberSanity
  Scenario: Should be able to detect a method on its signature
    Given There exists a class named "TestMe" in "com.curisprofound.tddwebstack" package
    Then  The class has a method with signature "myMethod(Map<String,List<Class<? extends RuntimeException>>>, Object, List<String>):List<String>"

  @CucumberSanity
    Scenario: Should be able to examine all overloaded versions of method
    Given There exists a class named "TestMe" in "com.curisprofound.tddwebstack" package
    And   the parameters for "myMethod" are not "Map<String, List<Class<? super IllegalArgumentException>>>, Object, List<String>"

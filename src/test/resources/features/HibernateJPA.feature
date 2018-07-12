Feature: As a developer
  I need an ORM to deal with data persistence
  So that I could safely change engines and use migration tools

  @HibernateJPA
  Scenario: Should have a class named Customer annotated with @Entity
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
    When  The annotations of the class are examined
    Then  the "Entity" annotation exists in the class annotations

  @HibernateJPA
  Scenario: Customer should have an id field that is annotated as Id (primary key)
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
    Then  The class has a getter for property "Id"
    And   The "id" field is annotated as "Id"

  @HibernateJPA
  Scenario: Should create a column with the name of the field if the field is not annotated
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
    And   The class has an unannotated field called "name"
    When  Hibernate should create a column "name" in table "Customer"
Feature: I need an ORM to deal with data persistence
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
    Then  Hibernate should create a column "name" in table "Customer"

  @HibernateJPA
  Scenario: Should have a column as List of elemental objects that maps to an external table
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
    And   The class has a field called "phoneNumbers" that is of type List of "String"
    When  The annotations of the "phoneNumbers" field are examined
    Then  The "phoneNumbers" field is annotated as "ElementCollection"
    And   Hibernate creates a "customer_phone_numbers" table in the database


  @HibernateJPA
  Scenario: Should have a class named Address which is embeddable
    Given There exists a class named "Address" in "com.curisprofound.tddwebstack.db" package
    And   The class has the following properties: "addressLine1, addressLine2, city, postalCode"
    When  The annotations of the class are examined
    Then  the "Embeddable" annotation exists in the class annotations

  @HibernateJPA
  Scenario: Should have a collection of addresses in Customer, which should map to a one-to-many relationship
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
    And   The class has a field called "addresses" that is of type List of "Address"
    Then  The "addresses" field is annotated as "ElementCollection"
    And   Hibernate creates a "customer_addresses" table in the database

  @HibernateJPA
  Scenario: Should have a collection of meal preferences in Customer, which should map to a one-to-many relationship
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
    And   The class has a field called "mealPreferences" that is of type Map of "String" and "String"
    Then  The "mealPreferences" field is annotated as "ElementCollection"
    And   Hibernate creates a "customer_meal_preferences" table in the database

  @HibernateJPA
  Scenario: Should have a class named HighRiseAddressExtension which has its own table
    Given There exists a class named "HighRiseAddressExtension" in "com.curisprofound.tddwebstack.db" package
    And   The class has the following properties: "suite, floor, buzzerCode"
    When  The annotations of the class are examined
    Then  the "Entity" annotation exists in the class annotations

  @HibernateJPA
  Scenario: the Address class should have a unidirectional one to one relationship with HighRiseAddressExtension
    Given There exists a class named "Address" in "com.curisprofound.tddwebstack.db" package
    And   The class has the following properties: "highRiseExtension"
    And   The "highRiseExtension" field is annotated as "OneToOne"
    When  Hibernate creates a "high_rise_address_extension" table in the database
    Then  the "customer_addresses" table has a foreignKey to "high_rise_address_extension" table
    But   The "high_rise_address_extension" table has no link to "customer_addresses" table

  @HibernateJPA
  Scenario: Should have a ShippingContact class annotated as an entity
    Given There exists a class named "ShippingContact" in "com.curisprofound.tddwebstack.db" package
    And   The class has the following properties: "name, phoneNumber, customer"
    When  The annotations of the class are examined
    Then  the "Entity" annotation exists in the class annotations

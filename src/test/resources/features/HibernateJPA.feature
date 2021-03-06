Feature: I need an ORM to deal with data persistence
  So that I could safely change engines and use migration tools

  @HibernateJPA
  Scenario: Should have a class named Customer annotated with @Entity
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
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
    Then  The "phoneNumbers" field is annotated as "ElementCollection"
    And   Hibernate creates a "customer_phone_numbers" table in the database


  @HibernateJPA
  Scenario: Should have a class named Address which is embeddable
    Given There exists a class named "Address" in "com.curisprofound.tddwebstack.db" package
    And   The class has the following properties: "addressLine1, addressLine2, city, postalCode"
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
    Then  the "Entity" annotation exists in the class annotations

  @HibernateJPA
  Scenario: the Address class should have a unidirectional one to one relationship with HighRiseAddressExtension
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
    And   The class has the following properties: "shippingContact"
    And   The "shippingContact" field is annotated as "OneToOne"
    Then  the "customer" table has a foreignKey to "Shipping_Contact" table
    And  the "shipping_contact" table has a foreignKey to "Customer" table


  @HibernateJPA
  Scenario: Should have an Invoice class annotated as an entity
    Given There exists a class named "Invoice" in "com.curisprofound.tddwebstack.db" package
    And   The class has the following properties: "date, place, customer"
    And   The "customer" field is annotated as "ManyToOne"
    Then  the "Entity" annotation exists in the class annotations

  @HibernateJPA
  Scenario: the Customer class should have a  One to Many relationship with Invoice
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
    And   The class has a field called "invoices" that is of type List of "Invoice"
    And   The "invoices" field is annotated as "OneToMany"
    And   the "invoice" table has a foreignKey to "Customer" table
    But   The "customer" table has no link to "invoice" table

  @HibernateJPA
  Scenario: Should have an Product class annotated as an entity
    Given There exists a class named "Product" in "com.curisprofound.tddwebstack.db" package
    And   The class has the following properties: "name, number"
    Then  the "Entity" annotation exists in the class annotations

  @HibernateJPA
  Scenario: the Invoice class should have a  Many to Many relationship with Product
    Given There exists a class named "Invoice" in "com.curisprofound.tddwebstack.db" package
    And   The class has a field called "products" that is of type List of "Product"
    And   The "products" field is annotated as "ManyToMany"
    Then  Hibernate creates a "invoice_products" table in the database
    And   the "invoice_products" table has a foreignKey to "invoice" table
    And   the "invoice_products" table has a foreignKey to "product" table

  @HibernateJPA
  Scenario: Should have an Product class annotated as an entity
    Given There exists a class named "Product" in "com.curisprofound.tddwebstack.db" package
    And   The class has a field called "invoices" that is of type List of "Invoice"
    Then   The "invoices" field is annotated as "ManyToMany" with parameter "mappedBy" set to "products"

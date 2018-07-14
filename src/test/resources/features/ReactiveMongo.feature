# Feature File for Cucumber Testing
# Created 7/13/18 11:24 PM by rezaalemy

Feature: I need reactive MongoDb tools
  To implement use cases that require streaming and non-blocking requests

  @HibernateJPA  @ReactiveMongo
  Scenario: Should have a Book object marked as Document
    Given There exists a class named "Book" in "com.curisprofound.tddwebstack.db" package
    And   The class has the following properties: "id, title, author, publisher"
    Then  The "id" field is annotated as "Id"
    And   the "Document" annotation exists in the class annotations


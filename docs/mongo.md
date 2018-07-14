# TDD of Reactive MongoDB Documents in Spring Boot

MongoDB is a NOSQL database with ability to run standalone or 
embedded. Documents are stored based on their id and relationships
are modeled as either embedded documents or referenced documents.


Another branch of this repository shows Hibernate ORM and SQL 
data models using H2. This branch focuses on reactive streams,
although MongoDB can be operated using the traditional means 
as well.

## Creating a Document object

The document object is the main independent entity in MongoDB.
So we start by creating a feature file for our tests 

```
Feature: I need to have a reactive NoSQL solution
    so that I can implement non-blocking semi-structured usecases

    Scenario: Should have a document for a book
        Given There is a class "Book" in "com...." package
        And   The class is annoated by "Document"
        Then  The "id" field is annotated as "id"    
```


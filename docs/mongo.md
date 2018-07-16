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
So we start by creating a feature file for our tests. 

The first test is just to see that the Book object exists and 
has the required properties and is annotated correctly.


```gherkin
  @ReactiveMongo
  Scenario: Should have a Book object marked as Document
    Given There exists a class named "Book" in "com.curisprofound.tddwebstack.db" package
    And   The class has the following properties: "id, title, author, publisher"
    Then  The "id" field is annotated as "Id"
    And   The "id" field is of type "String"
    And   the "Document" annotation exists in the class annotations
```

These steps were implemented previously.

## Creating the Repositoy object

To access the Book object in in a reactive manner, we need an interface
that extends ```ReactiveMongoRepository``` So we need to test the 
existance of it.

```gherkin
  @ReactiveMongo
  Scenario: Should have a Book object marked as Document
    Given  there is a "bookRepository" autowired
    Then   The "BookRepository" class implements the "ReactiveMongoRepository" with "Book" and "String" arguments
```

The ReactiveMongoRepository works with Flux and Mono objects. for example, the ```.save()``` function which 
expects a single object will return a ```Mono<?>``` but ```.findAll()```  with returns a collection is the 
form of ```Flux```.

The point of note in reactive streams is that there needs to be an action at the end of the chain, and until
an action is reached, nothing will be done. for example the following statement will actually NOT save anything 
to the database:

```java
bookRepository.save(newBookObject);
```

To actually save, one way is to use a ```block()``` this way:

```java
book savedBook = bookRepository.save(newBookObject).block();
//instructions here will be executed after save is finished.
```

the above will save the book and return the savedBook, but blocks the thread until its done. for most
tests we need to do that, since we want to assert something on the result. In production code, though, it
is best if block() is not used when possible. the better practice is to use subscribe():

```java
bookRepository.save(newBookObject).subscribe(b -> savedBook = b);
//instructions here can be executed before save is finished.
```


So CRUD tests to see that we can read, write, update and delete books would be 

```gherkin
  @ReactiveMongo
  Scenario: Should be able to use repository to save a book
    Given I have instantiated book objects as:
      |id|title|author|publisher|
      |idOne|titleone|authorone|publisherone|
    When  I save the book to the database using the "BookRepository"
    Then  It will be found in the database

  @ReactiveMongo
  Scenario: Should be able to update a book in repository
    Given  I have saved book objects as:
      |id|title|author|publisher|
      |idOne|titleone|authorone,phoneOne|publisherone|
    When I update the book by id "idOne" title to "titleOneUpdated"
    Then the book by id "idOne" will have title "titleOneUpdated"

  @ReactiveMongo
  Scenario: Shpuld be able to delete a book from repository
    Given  I have saved book objects as:
      |id|title|author|publisher|
      |idOne|titleone|authorone,phoneOne|publisherone|
    And   the book by id "idOne" exists in the repository
    When  I delete book by id "idOne"
    Then  the book by id "idOne" does not exist in the repository
```

## Relationships

### Embedded
There are two main ways of implementing relationships for NOSQL documents. one is to have embedded 
documents, i.e. the related object is created, modified, read, and deleted with the parent document.

this one is easy. for example, the author of a book can be a complex object embedded in the book object:

```gherkin
  @ReactiveMongo
  Scenario: Should hava author class as an embedded document
    Given There exists a class named "Author" in "com.curisprofound.tddwebstack.db" package
    Then  The class has the following properties: "name, phone"


  @ReactiveMongo
  Scenario: Should have a Book object marked as Document
    Given There exists a class named "Book" in "com.curisprofound.tddwebstack.db" package
    Then   The "author" field is of type "Author"

  @ReactiveMongo
  Scenario: Should embedd author class inside book class
    Given I have saved book objects as:
      |id|title|author|publisher|
      |idOne|titleone|authorone,phoneOne|publisherone|
    Then  the book by Id "idOne" has an author embedded by name of "authorone"

```

### By Reference
However, in some cases it is best to have the object in its own document collection and reference
it in the other object so it can have its own lifecycle. this is done by annotating the field with
```DBRef```.

```gherkin
  @ReactiveMongo
  Scenario: Should have a publisher class which is stored as document
    Given There exists a class named "Publisher" in "com.curisprofound.tddwebstack.db" package
    Then  The class has the following properties: "name, postalCode"
    And   the "Document" annotation exists in the class annotations

  @ReactiveMongo
  Scenario: Should have a Book object marked as Document
    Given There exists a class named "Book" in "com.curisprofound.tddwebstack.db" package
    Then  The "publisher" field is annotated as "DBRef"
    And   The "publisher" field is of type "Publisher"

  @ReactiveMongo
  Scenario: Should embedd author class inside book class
    Given I have saved book objects as:
      |id|title|author|publisher|
      |idOne|titleone|authorone,phoneOne|publisherone,postalCode1|
    Then  the book by Id "idOne" has a publisher by postalCode of "postalCode1"
```
This shows an interesting fact. first, the saving of the book does not save the publisher object,
it has to be saved separately, and therefore needs its own PublisherRepository object. so let's write
the test for that:

```gherkin
  @ReactiveMongo
  Scenario: Should have a Book object marked as Document
    Given  there is a "publisherRepository" autowired
    Then   The "PublisherRepository" class implements the "ReactiveMongoRepository" with "Publisher" and "String" arguments
```

Now, we need to refactor our save book step to save the publisher object separately.

```java
        Flux.fromIterable(table.asMaps(String.class, String.class))
                .flatMap(b->bookRepository.save(newBook(b)))
                .flatMap(b->publisherRepository.save(b.getPublisher()))
                .collectList().block();
```

notice in the above that because the table may have more than one map to create more than one 
book, flatmap is used to save each book. the first flatmap then has a Flux of maps and from each
map it creates a new book object and registers with the book repository to save. 

the ```bookRepository.save(newBookObject)``` returns a Book, which is then streams to the next flatMap.
the second Flatmap gets a Flux of Book objects and gets the publisher of each book and saves it to the 
```publisherRepository```. finally the collectList() call collects all outputs (by this stage they are 
Publisher objects that are going to be saved), and the block() call actuates the whole chain.

## Bidireactional relationships

If we need a list of books publisher has published inside the publisher
object, we can't simply create a collection of books the way we do for
JPA objects. it will cause a StackOverflowException when the engine
encounters circular references at the time of serialization for saving.

There are two workarounds for this problem:

* Use a list of Id and keep the book id instead of the whole book
object. Remember there is no referential integrity, so if a book is
deleted the references to it should be deleted manually.

* Use a framework such as Kundera which helps add such functionality. a future
iteration of this repo may have examples of this or similar framework.

# Future Work

* Add a section showing how to create streaming repositories for SQL engines
such as H2

* Provide an example of Kundera or similar framework



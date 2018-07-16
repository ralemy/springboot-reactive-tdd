package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.assertions.AssertOnClass;
import com.curisprofound.tddwebstack.assertions.AssertOnDb;
import com.curisprofound.tddwebstack.db.*;
import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ReactiveMongoSteps extends StepsBase{

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    private static boolean databaseFilled=false;

    @Before("@ReactiveMongo")
    public void beforeReactiveMongo(){

    }

    @After("@ReactiveMongo")
    public void afterReactiveMongo(){
        bookRepository.deleteAll().block();
    }

    private List<Book> tableToBooks(DataTable table){
        List<Book> data = new ArrayList<>();
        table.asMaps(String.class, String.class)
                .forEach(map -> data.add(newBook(map)));
        return data;
    }

    public Book newBook(Map<String, String> map) {
        Book b = new Book();
        b.setId(map.getOrDefault("id", ""));
        b.setTitle(map.getOrDefault("title",""));
        String[] authorParams = map.getOrDefault("author","").split(",");
        Author author = new Author();
        if(authorParams.length>1)
            author.setPhone(authorParams[1].trim());
        if(authorParams.length>0)
            author.setName(authorParams[0]);
        b.setAuthor(author);
        Publisher p = new Publisher();
        p.setId(b.getId() + "_publisher");
        String[] publisherParams = map.getOrDefault("publisher", "").split(",");
        if(publisherParams.length>1)
            p.setPostalCode(publisherParams[1].trim());
        if(publisherParams.length>0)
            p.setName(publisherParams[0].trim());
        b.setPublisher(p);
        p.getBooks().add(b.getId());
        return b;
    }

    @Given("^I have instantiated book objects as:$")
    public void iHaveInstantiatedBookObjectsAs(DataTable table) throws Throwable {
        databaseFilled = false;
        Add(List.class, tableToBooks(table),"bookList");
    }

    @SuppressWarnings("unchecked")
    @When("^I save the book to the database using the \"([^\"]*)\"$")
    public void iSaveTheBookToTheDatabaseUsingThe(String arg0) throws Throwable {
        List<Book> data = Get(List.class, "bookList");
        Flux.fromIterable(data)
                .flatMap(b-> bookRepository.save(b))
                .flatMap(b-> publisherRepository.save(b.getPublisher()))
        .then().block();
    }

    @Then("^It will be found in the database$")
    public void itWillBeFoundInTheDatabase() throws Throwable {
        List<?> books = AssertOnDb
                .ForMongo(mongoTemplate)
                .collectionExists(Book.class)
                .getAll()
                .buffer()
                .blockLast();

        assertEquals(
                Get(List.class, "bookList"),
                books
        );
        databaseFilled = true;
    }

    @Given("^previous test has run$")
    public void previousTestHasRun() throws Throwable {
        assertTrue(
                "previous scenario did not pass",
                databaseFilled
        );
    }

    @Then("^The database should not have a collection$")
    public void theDatabaseShouldNotHaveACollection() throws Throwable {
        List<?> books = AssertOnDb
                .ForMongo(mongoTemplate)
                .collectionExists(Book.class)
                .getAll()
                .buffer()
                .blockLast();
        assertNull(
                " There are books in database",
                books
        );

    }

    @When("^I update the book by id \"([^\"]*)\" title to \"([^\"]*)\"$")
    public void iUpdateTheBookByIdTitleTo(String bookId, String newTitle) throws Throwable {
        bookRepository
                .findById(bookId)
                .doOnNext(b->b.setTitle(newTitle))
                .flatMap(b->bookRepository.save(b))
                .block();
    }

    @Then("^the book by id \"([^\"]*)\" will have title \"([^\"]*)\"$")
    public void theBookByIdWillHaveTitle(String bookId, String expected) throws Throwable {
        Book b = bookRepository
                .findById(bookId)
                .block();
        assertEquals(
                expected,
                b.getTitle()
        );
    }

    @Given("^I have saved book objects as:$")
    public void iHaveSavedBookObjectsAs(DataTable table) throws Throwable {
        Flux.fromIterable(table.asMaps(String.class, String.class))
                .flatMap(b->bookRepository.save(newBook(b)))
                .flatMap(b->publisherRepository.save(b.getPublisher()))
                .collectList().block();
    }

    @And("^the book by id \"([^\"]*)\" exists in the repository$")
    public void theBookByIdExistsInTheRepository(String arg0) throws Throwable {
        Book b = bookRepository.findById(arg0).block();
        assertNotNull(
                "book by id " + arg0 + " not found in repo",
                b
        );
    }

    @When("^I delete book by id \"([^\"]*)\"$")
    public void iDeleteBookById(String arg0) throws Throwable {
        bookRepository.deleteById(arg0).block();
    }

    @Then("^the book by id \"([^\"]*)\" does not exist in the repository$")
    public void theBookByIdDoesNotExistInTheRepository(String arg0) throws Throwable {
        Book b = bookRepository.findById(arg0).block();
        assertNull(
                "book by id " + arg0 + " was found in repo",
                b
        );
    }

    @Then("^the book by Id \"([^\"]*)\" has an author embedded by name of \"([^\"]*)\"$")
    public void theBookByIdHasAnAuthorEmbeddedByNameOf(String arg0, String arg1) throws Throwable {
        Book b = bookRepository.findById(arg0).block();
        assertEquals(
                arg1,
                b.getAuthor().getName()
        );
    }

    @Given("^there is a \"([^\"]*)\" autowired$")
    public void thereIsAAutowired(String arg0) throws Throwable {
        Object target  = AssertOnClass
                .For(this.getClass())
                .Field(arg0)
                .exists()
                .getValue(this);
        assertNotNull(
                arg0 + " is null", target
        );
        Add(Object.class, target, arg0.toUpperCase());
    }

    @Then("^The \"([^\"]*)\" class implements the \"([^\"]*)\" with \"([^\"]*)\" and \"([^\"]*)\" arguments$")
    public void theClassImplementsTheWithAndArguments(String className, String root, String type1, String type2) throws Throwable {
        AssertOnClass
                .For(Get(Object.class, className.toUpperCase()).getClass())
                .implementsInterface(className)
                .implementsGenericInterface(root)
                .hasGenericType(type1)
                .hasGenericType(type2);
    }

    @Then("^the book by Id \"([^\"]*)\" has a publisher by postalCode of \"([^\"]*)\"$")
    public void theBookByIdHasAPublisherByPostalCodeOf(String arg0, String arg1) throws Throwable {
        Book b = bookRepository.findById(arg0).block();
        assertEquals(
                arg1,
                b.getPublisher().getPostalCode()
        );
    }
}

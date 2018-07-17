package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.assertions.AssertOnClass;
import com.curisprofound.tddwebstack.controllers.CustomerController;
import com.curisprofound.tddwebstack.db.Address;
import com.curisprofound.tddwebstack.db.Customer;
import com.curisprofound.tddwebstack.db.CustomerRepository;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.stubbing.Answer;
import org.springframework.aop.target.HotSwappableTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestExecutionListeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class MockitoSteps extends StepsBase {

    private static boolean executedFindAll = false;

    @Autowired
    private CustomerRepository customerRepository;


    @Before("@Mockito")
    public void beforeMockito() {
        doReturn(Optional.of(newCustomer("customerFixed")))
                .when(customerRepository)
                .findById(any(Long.class));
    }

    @After("@Mockito")
    public void afterMockito() {
        reset(customerRepository);
        tearDown();
    }


    @Then("^The interface implements the \"([^\"]*)\" with \"([^\"]*)\" and \"([^\"]*)\" arguments$")
    public void theInterfaceImplementsTheWithAndArguments(String root, String type1, String type2) throws Throwable {
        AssertOnClass
                .For(Get("ClassName"))
                .implementsGenericInterface(root)
                .hasGenericType(type1)
                .hasGenericType(type2);
    }

    @Then("^the autowired customerRepository is a MockBean$")
    public void theAutowiredCustomerRepositoryIsAMockBean() throws Throwable {
        Object bean = AssertOnClass.For(this.getClass())
                .Field("customerRepository")
                .getValue(this);
        assertNotNull(
                "customerRepository is null",
                bean
        );
        assertTrue(
                "CustomerRepository is not a mock object",
                MockUtil.isMock(bean)
        );
    }


    @Given("^the findall test has been executed$")
    public void theFindallTestHasBeenExecuted() throws Throwable {
        assertTrue(
                "the find All test was not executed",
                executedFindAll
        );
    }

    @Then("^the findAll mask is no longer active$")
    public void theFindAllMaskIsNoLongerActive() throws Throwable {
        List<Customer> customers = customerRepository.findAll();
        assertEquals(
                0,
                customers.size()
        );
    }

    @And("^the findAll method is masked to return a customer named \"([^\"]*)\"$")
    public void theFindAllMethodIsMaskedToReturnACustomerNamed(String arg0) throws Throwable {
        doReturn(new ArrayList<Customer>() {{
            add(newCustomer(arg0));
        }})
                .when(customerRepository)
                .findAll();
    }

    @Then("^the findAll method will return one customer by name of \"([^\"]*)\"$")
    public void theFindAllMethodWillReturnOneCustomerByNameOf(String arg0) throws Throwable {
        List<Customer> customers = customerRepository.findAll();
        assertEquals(
                1,
                customers.size()
        );
        assertEquals(
                arg0,
                customers.get(0).getName()
        );
    }


    @And("^the findAll method was call coundter would be (\\d+)$")
    public void theFindAllMethodWasCallCoundterWouldBe(int arg0) throws Throwable {
        verify(customerRepository, times(arg0)).findAll();
    }

    @And("^execution of findall test is recorded$")
    public void executionOfFindallTestIsRecorded() throws Throwable {
        executedFindAll = true;
    }

    @Given("^I have a customer object by name of \"([^\"]*)\"$")
    public void iHaveACustomerObjectByNameOf(String arg0) throws Throwable {
        Add(Customer.class, newCustomer(arg0));
    }

    @And("^I have mocked save function to trap its input$")
    public void iHaveMockedSaveFunctionToTrapItsInput() throws Throwable {
        doAnswer((Answer<Customer>) invocationOnMock -> (Customer) invocationOnMock.getArguments()[0])
                .when(customerRepository).save(any(Customer.class));
    }

    @When("^I save it to customer repository$")
    public void iSaveItToCustomerRepository() throws Throwable {
        Customer c = customerRepository.save(Get(Customer.class));
        assertEquals(
                Get(Customer.class).getName(),
                c.getName()
        );
    }

    @Then("^I can verify the save function was called with \"([^\"]*)\"$")
    public void iCanVerifyTheSaveFunctionWasCalledWith(String arg0) throws Throwable {
        verify(customerRepository).save(Get(Customer.class));
    }

    @And("^The class has a method \"([^\"]*)\" with parameters \"([^\"]*)\"$")
    public void theClassHasAMethodWithParameters(String arg0, String arg1) throws Throwable {
        Class<?>[] types = Arrays.stream(arg1.split(","))
                .map(String::trim)
                .filter(c -> !c.equalsIgnoreCase(""))
                .map(this::getClassFromKey)
                .toArray(Class<?>[]::new);
        AssertOnClass
                .For(Get("ClassName"))
                .Method(arg0, types);
    }

    @Given("^There is a bean for \"([^\"]*)\"$")
    public void thereIsABeanFor(String arg0) throws Throwable {
        Object bean = getBean(arg0);
        assertNotNull(
                "Bean doesn't exist with name " + arg0,
                bean
        );
        Add(MockPostProcessor.class, (MockPostProcessor) bean);
    }

    @When("^I call the post-processor with a general object$")
    public void iCallThePostProcessorWithAGeneralObject() throws Throwable {
        Object result = Get(MockPostProcessor.class).postProcessAfterInitialization(new Object(),"");
        Add(Object.class, result, "postProcessorResult");
    }

    @Then("^I get the same object without mocking$")
    public void iGetTheSameObjectWithoutMocking() throws Throwable {
        assertFalse(
                " Object is mocked!",
                MockUtil.isMock(Get(Object.class, "postProcessorResult"))
        );
    }

    @When("^I add a class to class list of preprocessor$")
    public void iAddAnClassToClassListOfPreprocessor() throws Throwable {
        Get(MockPostProcessor.class).classes.add(Address.class);
    }

    @When("^I call the post-processor with a an instance of that class$")
    public void iCallThePostProcessorWithAAnInstanceOfThatClass() throws Throwable {
        Object result = Get(MockPostProcessor.class).postProcessAfterInitialization(new Address(),"");
        Add(Object.class, result, "postProcessorResult");
    }

    @Then("^I get the mocked version of the class$")
    public void iGetTheMockedVersionOfTheClass() throws Throwable {
        assertTrue(
                " Object is not mocked!",
                MockUtil.isMock(Get(Object.class, "postProcessorResult"))
        );
    }

    @And("^The classes list has CustomerRepository in it$")
    public void theClassesListHasCustomerRepositoryInIt() throws Throwable {
        boolean actual = Get(MockPostProcessor.class).classes.stream().anyMatch(c -> c == CustomerRepository.class);
        assertTrue(
                "CustomerRepository is not in the mocked classes",
                actual
        );
    }

    @When("^I get the Customer with id (\\d+)$")
    public void iGetTheCustomerWithId(long arg0) throws Throwable {
        Optional<Customer> c = customerRepository.findById(arg0);
        assertTrue(
                "Customer id " + arg0 + " does not exist",
                c.isPresent()
        );
        Add(Customer.class, c.orElse(null));
    }

    @Then("^the customer name is \"([^\"]*)\"$")
    public void theCustomerNameIs(String arg0) throws Throwable {
        assertEquals(
                arg0,
                Get(Customer.class).getName()
        );
    }

    @Given("^I have mocked customerRepository FindbyId to return a customer with id plus (\\d+)$")
    public void iHaveMockedCustomerRepositoryFindbyIdToReturnACustomerWithIdPlus(int arg0) throws Throwable {
        doAnswer(input -> Optional.of(newCustomer(10 + (Long)input.getArguments()[0])))
                .when(customerRepository)
                .findById(any(Long.class));
    }

    @Then("^the customer id is (\\d+)$")
    public void theCustomerIdIs(int arg0) throws Throwable {
        assertEquals(
                arg0,
                Get(Customer.class).getId()
        );
    }
}

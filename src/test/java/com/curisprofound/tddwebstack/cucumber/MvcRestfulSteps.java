package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.assertions.AssertOnClass;
import com.curisprofound.tddwebstack.controllers.CustomerController;
import com.curisprofound.tddwebstack.db.Customer;
import com.curisprofound.tddwebstack.db.CustomerRepository;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class MvcRestfulSteps extends StepsBase {

    private static boolean executedFindAll = false;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer newCustomer(String name) {
        Customer c = new Customer();
        c.setName(name);
        return c;
    }

    @Before("@MvcRestful")
    public void beforeMvcRestful() {
        getMockMvc(this.getClass(), "beforeMvcRestful");
    }

    @After("@MvcRestful")
    public void afterMvcResult() {
        reset(customerRepository);
        tearDown();
    }

    @And("^The \"([^\"]*)\" method of the class is annotated by \"([^\"]*)\" with parameter \"([^\"]*)\" set to \"([^\"]*)\"$")
    public void theMethodOfTheClassIsAnnotatedByWithParameterSetTo(String method, String annotation, String pname, String pvalue) throws Throwable {
        AssertOnClass
                .For(Get("ClassName"))
                .Method(method)
                .Annotation(annotation)
                .paramHasValue(pname, pvalue);
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
}

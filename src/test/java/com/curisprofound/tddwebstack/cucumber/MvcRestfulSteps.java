package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.assertions.AssertOnClass;
import com.curisprofound.tddwebstack.controllers.CustomerController;
import com.curisprofound.tddwebstack.db.Customer;
import com.curisprofound.tddwebstack.db.CustomerRepository;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MvcRestfulSteps extends StepsBase{

    @Autowired
    private AuthenticationManagerBuilder authManagerBuilder;

    @Autowired
    private CustomerRepository customerRepository;

    @Before("@MvcRestful")
    public void beforeMvcRestful() {
        mockMvc(this.getClass(), "beforeMvcRestful");
    }

    @After("@MvcRestful")
    public void afterMvcResult() {
        reset(customerRepository);
        tearDown();
    }


    @And("^The \"([^\"]*)\" method of the class is annotated by \"([^\"]*)\" with parameter \"([^\"]*)\" set to \"([^\"]*)\"$")
    public void theMethodOfTheClassIsAnnotatedByWithParameterSetTo(String method, String annotation, String pname, String pvalue) throws Throwable {
        String methodName = method.contains(":") ? method.split(":")[0].trim() : method;
        Class<?>[] methodArgs = method.contains(":") ?
                Arrays.stream(method.split(":")[1].split(","))
                .map(String::trim)
                .map(this::getClassFromKey)
                .toArray(Class<?>[]::new) : new Class<?>[0];
        AssertOnClass
                .For(Get("ClassName"))
                .Method(methodName, methodArgs)
                .Annotation(annotation)
                .paramHasValue(pname, pvalue);
    }

    @When("^I \"([^\"]*)\" the \"([^\"]*)\" endpoint$")
    public void iTheEndpoint(String arg0, String arg1) throws Throwable {
        ResultActions result = Get(MockMvc.class).perform(
                get(arg1)
        )
                .andExpect(status().isOk());
        Add(ResultActions.class, result);
    }

    @Then("^I get a list of Customer objects with one member by the name of \"([^\"]*)\"$")
    public void iGetAListOfCustomerObjectsWithOneMemberByTheNameOf(String expectedName) throws Throwable {
        String resp = Get(ResultActions.class).andReturn().getResponse().getContentAsString();
        List<Customer> customers = jsonStringToClassArray(resp,Customer.class);
        assertEquals(
                1,
                customers.size()
        );
        assertEquals(
                expectedName,
                customers.get(0).getName()
        );
    }

    @When("^I call saveCustomer on \"([^\"]*)\" bean with \"([^\"]*)\"$")
    public void iCallSaveCustomerOnBeanWith(String arg0, String arg1) throws Throwable {
        Add(Customer.class, newCustomer(arg1));
        ((CustomerController) getBean(arg0)).saveCustomer(Get(Customer.class));
    }

    @And("^I \"([^\"]*)\" the \"([^\"]*)\" with \"([^\"]*)\" and no authentication$")
    public void iTheWithAndNoAuthentication(String arg0, String arg1, String arg2) throws Throwable {
        Add(Customer.class, newCustomer(arg2));

        ResultActions result = Get(MockMvc.class).perform(
                put(arg1).contentType(MediaType.APPLICATION_JSON)
                .content(jsonObjectToString(Get(Customer.class)))
        );
        Add(ResultActions.class, result);

    }


    @Then("^I receive a (\\d+) response status$")
    public void iReceiveAResponseStatus(int arg0) throws Throwable {
        Get(ResultActions.class).andExpect(status().is(arg0));
    }

    @And("^I have a user \"([^\"]*)\" and password \"([^\"]*)\" configured$")
    public void iHaveAUserAndPasswordConfigured(String user, String pass) throws Throwable {
        Map<String,String> creds = new HashMap<>();
        creds.put("User", user.trim());
        creds.put("Password", pass.trim());
        creds.put("Role", "USER");
        Add(Map.class, creds);
    }

    @When("^I \"([^\"]*)\" the \"([^\"]*)\" with \"([^\"]*)\" with such credentials$")
    public void iTheWithWithSuchCredentials(String arg0, String endpoint, String customerName) throws Throwable {
        Add(Customer.class, newCustomer(customerName));
        Map<String, String> creds = Get(Map.class);

        ResultActions result = Get(MockMvc.class).perform(
                put(endpoint).contentType(MediaType.APPLICATION_JSON)
                        .with(user(creds.get("User"))
                                .password(creds.get("Password"))
                                .roles(creds.get("Role")))
                        .content(jsonObjectToString(Get(Customer.class)))
        );
        Add(ResultActions.class, result);
    }

    @And("^The endpoint returns a customer object named \"([^\"]*)\"$")
    public void theEndpointReturnsACustomerObjectNamed(String arg0) throws Throwable {
        String resp = Get(ResultActions.class).andReturn().getResponse().getContentAsString();
        Customer customer = jsonStringToObject(resp, Customer.class);
        assertEquals(
                arg0,
                customer.getName()
        );
    }
}

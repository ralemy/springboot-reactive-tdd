package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.assertions.AssertOnClass;
import com.curisprofound.tddwebstack.db.Customer;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MvcRestfulSteps extends StepsBase{

    @Before("@MvcRestful")
    public void beforeMvcRestful() {
        mockMvc(this.getClass(), "beforeMvcRestful");
    }

    @After("@MvcRestful")
    public void afterMvcResult() {
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

    @When("^I \"([^\"]*)\" the \"([^\"]*)\" endpoint$")
    public void iTheEndpoint(String arg0, String arg1) throws Throwable {
        ResultActions result = Get(MockMvc.class).perform(
                get("/customers")
        )
                .andExpect(status().isOk());
        Add(ResultActions.class, result);
    }

    @Then("^I get a list of Customer objects with one member by the name of \"([^\"]*)\"$")
    public void iGetAListOfCustomerObjectsWithOneMemberByTheNameOf(String arg0) throws Throwable {
        String resp = Get(ResultActions.class).andReturn().getResponse().getContentAsString();
        List<Customer> customers = jsonStringToClassArray(resp,Customer.class);
        assertEquals(
                1,
                customers.size()
        );
        assertEquals(
                arg0,
                customers.get(0).getName()
        );
    }
}

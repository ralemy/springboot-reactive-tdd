package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.assertions.AssertOnClass;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;

public class MvcRestfulSteps extends StepsBase{

    @Before("@MvcRestful")
    public void beforeMvcRestful() {
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
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I get a list of Customer objects with one member by the name of \"([^\"]*)\"$")
    public void iGetAListOfCustomerObjectsWithOneMemberByTheNameOf(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}

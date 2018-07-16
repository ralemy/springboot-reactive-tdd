package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.db.CustomerRepository;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CucumberSanitySteps01 extends StepsBase{


    private static boolean haveInitializedP1 = false;

    @When("^I multiply \"([^\"]*)\" and \"([^\"]*)\" and store the result in \"([^\"]*)\"$")
    public void iMultiplyAndAndStoreTheResultIn(String p1, String p2, String p3) throws Throwable {
        int k =  Get(Integer.class , p1) * Get(Integer.class, p2);
        Add(Integer.class, k, p3);
        haveInitializedP1 = true;
    }

    @Given("^I have initialized \"([^\"]*)\" in a previous Scenario$")
    public void iHaveInitializedInAPreviousScenario(String arg0) throws Throwable {
        if(arg0.equalsIgnoreCase("p1"))
            assertTrue(haveInitializedP1);
    }

    @When("^I enter a new Scenario$")
    public void iEnterANewScenario() throws Throwable {

    }

    @Then("^The World variable \"([^\"]*)\" should be null$")
    public void theWorldVariableShouldBeNull(String arg0) throws Throwable {
        assertNull(Get(Integer.class,arg0));
    }

}

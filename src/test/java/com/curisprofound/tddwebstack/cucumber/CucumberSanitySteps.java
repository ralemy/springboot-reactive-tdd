package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.db.CustomerRepository;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.Assert.assertEquals;

public class CucumberSanitySteps  extends StepsBase{


    @After("@CucumberSanity")
    public void afterCucumberSanity(){
        tearDown();
    }

    @Given("^I initialize World variable \"([^\"]*)\" to (\\d+) and \"([^\"]*)\" to (\\d+)$")
    public void iInitializeWorldVariableToAndTo(String p1, int i, String p2, int j) throws Throwable {
        Add(Integer.class, i, p1);
        Add(Integer.class, j, p2);
    }


    @When("^I add \"([^\"]*)\" and \"([^\"]*)\" and store the result in \"([^\"]*)\"$")
    public void iAddAndAndStoreTheResultIn(String p1, String p2, String p3) throws Throwable {
       int k =  Get(Integer.class , p1) + Get(Integer.class, p2);
       Add(Integer.class, k, p3);
    }

    @Then("^The World variable \"([^\"]*)\" should be equal to (\\d+)$")
    public void theWorldVariableShouldBeEqualTo(String p3, int k) throws Throwable {
        assertEquals(
                k,
                Get(Integer.class, p3).intValue()
        );
    }
}

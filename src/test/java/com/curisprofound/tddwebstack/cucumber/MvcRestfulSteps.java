package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.assertions.AssertOnClass;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;

public class MvcRestfulSteps extends StepsBase {


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

    @Then("^The interface implements the \"([^\"]*)\" with \"([^\"]*)\" and \"([^\"]*)\" arguments$")
    public void theInterfaceImplementsTheWithAndArguments(String root, String type1, String type2) throws Throwable {
        AssertOnClass
                .For(Get("ClassName"))
                .implementsGenericInterface(root)
                .hasGenericType(type1)
                .hasGenericType(type2);
    }
}

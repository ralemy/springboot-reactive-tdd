package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.assertions.AssertOnClass;
import com.curisprofound.tddwebstack.assertions.TypeDef;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CucumberSanitySteps extends StepsBase {


    @After("@CucumberSanity")
    public void afterCucumberSanity() {
        tearDown();
    }

    @Given("^I initialize World variable \"([^\"]*)\" to (\\d+) and \"([^\"]*)\" to (\\d+)$")
    public void iInitializeWorldVariableToAndTo(String p1, int i, String p2, int j) throws Throwable {
        Add(Integer.class, i, p1);
        Add(Integer.class, j, p2);
    }


    @When("^I add \"([^\"]*)\" and \"([^\"]*)\" and store the result in \"([^\"]*)\"$")
    public void iAddAndAndStoreTheResultIn(String p1, String p2, String p3) throws Throwable {
        int k = Get(Integer.class, p1) + Get(Integer.class, p2);
        Add(Integer.class, k, p3);
    }

    @Then("^The World variable \"([^\"]*)\" should be equal to (\\d+)$")
    public void theWorldVariableShouldBeEqualTo(String p3, int k) throws Throwable {
        assertEquals(
                k,
                Get(Integer.class, p3).intValue()
        );
    }

    @Then("^Class has a field \"([^\"]*)\" of Type \"([^\"]*)\"$")
    public void classHasAFieldOfType(String fieldName, String signature) throws Throwable {
        AssertOnClass.For(Get("ClassName"))
                .Field(fieldName)
                .isOfComplexType(TypeDef.parse(signature).get(0));
    }

    @Then("^The class has a method with signature \"([^\"]*)\"$")
    public void theClassHasAMethodWithSignatureListString(String methodSignature) throws Throwable {
        String returnValue = methodSignature.split(":")[1].trim();
        String methodName = methodSignature.split("\\(")[0].trim();
        String params = methodSignature.split("\\(")[1].split("\\)")[0].trim();
        AssertOnClass
                .For(Get("ClassName"))
                .Method(methodName, TypeDef.parse(params))
                .hasReturnType(TypeDef.parse(returnValue).get(0));

    }

    @And("^the parameters for \"([^\"]*)\" are not \"([^\"]*)\"$")
    public void theParametersForAreNot(String methodName, String params) throws Throwable {
        List<TypeDef> parsedParams = TypeDef.parse(params);
        AssertOnClass
                .For(Get("ClassName"))
                .Methods(methodName)
                .forEach(method->method.Not().hasArguments(parsedParams));
    }

    @Given("^There exists a class named \"([^\"]*)\" in \"([^\"]*)\" package$")
    public void thereExistsAClassNamedInPackage(String className, String packageName) throws Throwable {
        String fullName = packageName + "." + className;
        Class.forName(fullName);
        Add(String.class, fullName, "ClassName");
    }

    @Then("^the \"([^\"]*)\" annotation exists in the class annotations$")
    public void theAnnotationExistsInTheClassAnnotations(String annotationName) throws Throwable {
        AssertOnClass
                .For(Get("ClassName"))
                .hasAnnotations(annotationName);
    }

}

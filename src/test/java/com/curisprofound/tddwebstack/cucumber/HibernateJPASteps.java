package com.curisprofound.tddwebstack.cucumber;

import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public class HibernateJPASteps extends StepsBase {

    @After("@HibernateJPA")
    public void afterHibernateJPA(){
        tearDown();
    }



    @Given("^There exists a class named \"([^\"]*)\" in \"([^\"]*)\" package$")
    public void thereExistsAClassNamedInPackage(String arg0, String arg1) throws Throwable {
        String name = arg1+"."+arg0;
        Class.forName(name);
        Add(String.class,name, "ClassName");

    }

    @When("^The annotations of the class are examined$")
    public void theAnnotationsOfTheClassAreExamined() throws Throwable {
        Annotation[] annotations = Class.forName(Get(String.class, "ClassName"))
                .getAnnotations();
        Add(Object.class, annotations, "ClassAnnotations");
    }

    @Then("^the \"([^\"]*)\" annotation exists in the class annotations$")
    public void theAnnotationExistsInTheClassAnnotations(String arg0) throws Throwable {
        Annotation[] annotations = (Annotation[])Get(Object.class, "ClassAnnotations");
        Optional<Annotation> actual = Arrays.stream(annotations).filter(a -> a.annotationType().getName().contains(arg0)).findFirst();
        assertTrue(actual.isPresent());

    }
}


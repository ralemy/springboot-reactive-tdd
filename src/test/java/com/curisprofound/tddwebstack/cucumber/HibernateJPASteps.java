package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.assertions.AssertOnClass;
import com.curisprofound.tddwebstack.assertions.AssertOnDb;
import com.curisprofound.tddwebstack.db.Address;
import cucumber.api.java.After;
import cucumber.api.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

public class HibernateJPASteps extends StepsBase {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @After("@HibernateJPA")
    public void afterHibernateJPA() {
        tearDown();
    }


    @Given("^There exists a class named \"([^\"]*)\" in \"([^\"]*)\" package$")
    public void thereExistsAClassNamedInPackage(String arg0, String arg1) throws Throwable {
        String name = arg1 + "." + arg0;
        Class.forName(name);
        Add(String.class, name, "ClassName");

    }


    @Then("^the \"([^\"]*)\" annotation exists in the class annotations$")
    public void theAnnotationExistsInTheClassAnnotations(String arg0) throws Throwable {
        AssertOnClass
                .For(Get("ClassName"))
                .hasAnnotations(arg0);
    }

    @Then("^The class has a getter for property \"([^\"]*)\"$")
    public void theClassHasAGetterForProperty(String propertyName) throws Throwable {
        AssertOnClass
                .For(Get("ClassName"))
                .isReadable(propertyName);
    }

    @And("^The \"([^\"]*)\" field is annotated as \"([^\"]*)\"$")
    public void theFieldIsAnnotatedAs(String propertyName, String annotationName) throws Throwable {
        AssertOnClass
                .For(Get("ClassName"))
                .Field(propertyName)
                .hasAnnotations(annotationName);
    }

    @And("^The class has an unannotated field called \"([^\"]*)\"$")
    public void theClassHasAnUnannotatedFieldCalled(String propertyName) throws Throwable {
        AssertOnClass
                .For(Get("ClassName"))
                .Field(propertyName)
                .Not()
                .hasAnnotations();
    }


    @When("^Hibernate should create a column \"([^\"]*)\" in table \"([^\"]*)\"$")
    public void hibernateShouldCreateAColumnInTable(String arg0, String arg1) throws Throwable {

        AssertOnDb
                .ForH2(jdbcTemplate)
                .Table(arg1)
                .hasColumnsByName(arg0);
    }



    @And("^Hibernate creates a \"([^\"]*)\" table in the database$")
    public void hibernateCreatesATableInTheDatabase(String tableName) throws Throwable {
        AssertOnDb
                .ForH2(jdbcTemplate)
                .Table(tableName)
                .exists();
    }

    @And("^The class has the following properties: \"([^\"]*)\"$")
    public void theClassHasTheFollowingProperties(String propertyList) throws Throwable {
        String[] fieldNames = Arrays.stream(propertyList.split(","))
                .map(String::trim).toArray(String[]::new);
        AssertOnClass
                .For(Get("ClassName"))
                .hasFields(fieldNames);
    }

    @And("^The class has a field called \"([^\"]*)\" that is of type List of \"([^\"]*)\"$")
    public void theClassHasAFieldCalledThatIsOfTypeListOf(String propertyName, String type) throws Throwable {
        AssertOnClass
                .For(Get("ClassName"))
                .Field(propertyName)
                .isOfType(List.class)
                .genericArgIsOfType(0, getClassFromKey(type));
    }


    @And("^The class has a field called \"([^\"]*)\" that is of type Map of \"([^\"]*)\" and \"([^\"]*)\"$")
    public void theClassHasAFieldCalledThatIsOfTypeMapOfAnd(String propertyName, String type1, String type2) throws Throwable {
        AssertOnClass
                .For(Get("ClassName"))
                .Field(propertyName)
                .isOfType(Map.class)
                .genericArgIsOfType(0, getClassFromKey(type1))
                .genericArgIsOfType(0, getClassFromKey(type2));
    }


    @Then("^the \"([^\"]*)\" table has a foreignKey to \"([^\"]*)\" table$")
    public void theTableHasAForeignKeyToTable(String source, String target) throws Throwable {
        AssertOnDb
                .ForH2(jdbcTemplate)
                .Table(source)
                .hasForeignKeyTo(target);
    }


    @But("^The \"([^\"]*)\" table has no link to \"([^\"]*)\" table$")
    public void theTableHasNoLinkToTable(String source, String target) throws Throwable {
        AssertOnDb
                .ForH2(jdbcTemplate)
                .Table(source)
                .hasNoForeignKeysTo(target);
    }


    private Class<?> getClassFromKey(String type) {
        if (type.equalsIgnoreCase("String"))
            return String.class;
        if (type.equalsIgnoreCase("Address"))
            return Address.class;
        return Object.class;
    }


    @Then("^\"([^\"]*)\" is readable$")
    public void isReadable(String arg0) throws Throwable {
        AssertOnClass
                .For(Get("ClassName"))
                .isReadable(arg0);
    }

    @And("^\"([^\"]*)\" is not readable$")
    public void isNotReadable(String arg0) throws Throwable {
        AssertOnClass
                .For(Get("ClassName"))
                .Not()
                .isReadable(arg0);
    }

    @Then("^The \"([^\"]*)\" field is annotated as \"([^\"]*)\" with parameter \"([^\"]*)\" set to \"([^\"]*)\"$")
    public void theFieldIsAnnotatedAsWithParameterSetTo(String field, String annotation, String param, String value) throws Throwable {
        AssertOnClass
                .For(Get("ClassName"))
                .Field(field)
                .Annotation(annotation)
                .paramHasValue(param,value);
    }
}


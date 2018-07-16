package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.assertions.AssertOnClass;
import com.curisprofound.tddwebstack.assertions.AssertOnDb;
import com.curisprofound.tddwebstack.db.*;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;


public class HibernateJPASteps extends StepsBase {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @After("@HibernateJPA")
    public void afterHibernateJPA() {
        tearDown();
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
                .Not()
                .hasForeignKeyTo(target);
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

    private Class<?> getClassFromKey(String type) throws Exception {
        if (type.equalsIgnoreCase("String"))
            return String.class;
        if (type.equalsIgnoreCase("Address"))
            return Address.class;
        if(type.equalsIgnoreCase("Invoice"))
            return Invoice.class;
        if(type.equalsIgnoreCase("Product"))
            return Product.class;
        if(type.equalsIgnoreCase("Author"))
            return Author.class;
        if(type.equalsIgnoreCase("Publisher"))
            return Publisher.class;
        if(type.equalsIgnoreCase("CustomerRepository"))
            return CustomerRepository.class;

        throw new Exception("Unknow type: " + type);
    }

    @And("^The \"([^\"]*)\" field is of type \"([^\"]*)\"$")
    public void theFieldIsOfType(String arg0, String arg1) throws Throwable {
        AssertOnClass
                .For(Get("ClassName"))
                .Field(arg0)
                .isOfType(getClassFromKey(arg1));
    }

    @Given("^There exists a class named \"([^\"]*)\" in \"([^\"]*)\" package$")
    public void thereExistsAClassNamedInPackage(String className, String packageName) throws Throwable {
        String fullName = packageName + "." + className;
        Class.forName(fullName);
        Add(String.class, fullName, "ClassName");
    }
}


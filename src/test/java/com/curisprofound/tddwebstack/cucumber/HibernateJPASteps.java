package com.curisprofound.tddwebstack.cucumber;

import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @When("^The annotations of the class are examined$")
    public void theAnnotationsOfTheClassAreExamined() throws Throwable {
        Annotation[] annotations = Class.forName(Get(String.class, "ClassName"))
                .getAnnotations();
        Add(Object.class, annotations, "ClassAnnotations");
    }

    @Then("^the \"([^\"]*)\" annotation exists in the class annotations$")
    public void theAnnotationExistsInTheClassAnnotations(String arg0) throws Throwable {
        Annotation[] annotations = (Annotation[]) Get(Object.class, "ClassAnnotations");
        Optional<Annotation> actual = Arrays.stream(annotations).filter(a -> a.annotationType().getName().contains(arg0)).findFirst();
        assertTrue(actual.isPresent());

    }

    @Then("^The class has a getter for property \"([^\"]*)\"$")
    public void theClassHasAGetterForProperty(String propertyName) throws Throwable {
        propertyName = correctCase(propertyName, "method");
        Method method = Class.forName(Get(String.class, "ClassName")).getDeclaredMethod("get" + propertyName);
        Add(Method.class, method, Get(String.class, "ClassName") + ".get" + propertyName);
    }

    @And("^The \"([^\"]*)\" field is annotated as \"([^\"]*)\"$")
    public void theFieldIsAnnotatedAs(String propertyName, String annotationName) throws Throwable {
        Field f = getFieldByName(propertyName, Get("ClassName"));

        Optional<Annotation> annotation = Arrays.stream(f.getAnnotations()).filter(
                a -> a.annotationType().getName().contains(annotationName)
        ).findAny();


        assertTrue(
                "Should have an annotation for " + annotationName,
                annotation.isPresent()
        );
    }

    @And("^The class has an unannotated field called \"([^\"]*)\"$")
    public void theClassHasAnUnannotatedFieldCalled(String propertyName) throws Throwable {
        propertyName = correctCase(propertyName, "Field");
        Field f = Class.forName(Get("ClassName")).getDeclaredField(propertyName);
        assertEquals(
                0,
                f.getAnnotations().length
        );
    }

    private String correctCase(String propertyName, String target) {
        return
                (target.equalsIgnoreCase("field") ?
                        propertyName.substring(0, 1).toLowerCase() :
                        propertyName.substring(0, 1).toUpperCase()) +
                        propertyName.substring(1);
    }

    @When("^Hibernate should create a column \"([^\"]*)\" in table \"([^\"]*)\"$")
    public void hibernateShouldCreateAColumnInTable(String arg0, String arg1) throws Throwable {

        String sqlQuery = "show columns from " + arg1.toUpperCase();
        Optional<String> target =
                jdbcTemplate.query(sqlQuery, new ColumnMapRowMapper()).stream()
                        .flatMap(c -> c.entrySet().stream())
                        .filter(c -> c.getKey().equalsIgnoreCase("field"))
                        .filter(c -> ((String) c.getValue()).equalsIgnoreCase(arg0))
                        .map(c -> (String) c.getValue())
                        .findAny();
        assertTrue("Should have a column named " + arg0, target.isPresent());
    }

    @And("^The class has a field called \"([^\"]*)\" that is of type List of Strings$")
    public void theClassHasAFieldCalledThatIsOfTypeListOfStrings(String propertyName) throws Throwable {
        Field f = getFieldByName(propertyName, Get("ClassName"));
        assertTrue("phoneNumbers should be a list instead of " + f.getType().getCanonicalName() ,
                List.class.isAssignableFrom(f.getType()));
        Class<?> stringClass = (Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
        assertTrue(
                "Should be a list of String instead of " + stringClass.getCanonicalName(),
                String.class.isAssignableFrom(stringClass)
                );
    }

    private Field getFieldByName(String propertyName, String className) throws ClassNotFoundException, NoSuchFieldException {
        propertyName = correctCase(propertyName, "field");
        Field f = Class.forName(className).getDeclaredField(propertyName);
        f.setAccessible(true);
        return f;
    }

    @When("^The annotations of the \"([^\"]*)\" field are examined$")
    public void theAnnotationsOfTheFieldAreExamined(String propertyName) throws Throwable {
        Field f = getFieldByName(propertyName, Get("ClassName"));
        Add(Object.class, f.getAnnotations(), "FieldAnnotations");
    }

    @And("^Hibernate creates a \"([^\"]*)\" table in the database$")
    public void hibernateCreatesATableInTheDatabase(String tableName) throws Throwable {
        Optional<Map<String, Object>> tableList = jdbcTemplate.query(
                "select * from information_schema.indexes where table_name = ?",
                new Object[]{tableName.toUpperCase()},
                new ColumnMapRowMapper()
        ).stream().findAny();

        assertTrue("should have a table named " + tableName, tableList.isPresent());
    }
}


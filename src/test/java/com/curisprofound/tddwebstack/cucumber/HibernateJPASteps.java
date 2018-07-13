package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.db.Address;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.en.*;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        assertTrue("NO @" + arg0 + " annotation on class " + Get("ClassName"), actual.isPresent());

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

        Optional<String> target = getColumnNameStream(arg1)
                .filter(c -> c.equalsIgnoreCase(arg0))
                .findAny();
        assertTrue("Should have a column named " + arg0, target.isPresent());
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

    @And("^The class has the following properties: \"([^\"]*)\"$")
    public void theClassHasTheFollowingProperties(String propertyList) throws Throwable {
        String[] propertyNames = propertyList.split(",");
        Class<?> target = Class.forName(Get("ClassName"));
        for (String c : propertyNames) {
            try {
                target.getDeclaredField(c.trim());
            } catch (NoSuchFieldException e) {
                Assert.fail("Class doesn't have a field called " + c);
            }
        }
    }

    @And("^The class has a field called \"([^\"]*)\" that is of type List of \"([^\"]*)\"$")
    public void theClassHasAFieldCalledThatIsOfTypeListOf(String propertyName, String type) throws Throwable {
        Field f = getFieldByName(propertyName, Get("ClassName"));
        Class<?> target = getClassFromKey(type);
        assertTrue(propertyName + " should be a list instead of " + f.getType().getCanonicalName(),
                List.class.isAssignableFrom(f.getType()));
        Class<?> actualClass = getFieldGenericType(f, 0);
        assertTrue(
                "Should be a list of " + type + " instead of " + actualClass.getCanonicalName(),
                target.isAssignableFrom(actualClass)
        );
    }


    @And("^The class has a field called \"([^\"]*)\" that is of type Map of \"([^\"]*)\" and \"([^\"]*)\"$")
    public void theClassHasAFieldCalledThatIsOfTypeMapOfAnd(String propertyName, String type1, String type2) throws Throwable {
        Field f = getFieldByName(propertyName, Get("ClassName"));
        assertTrue(propertyName + " should be a Map instead of " + f.getType().getCanonicalName(),
                Map.class.isAssignableFrom(f.getType()));
        Class<?> actualClass = getFieldGenericType(f, 0);
        assertTrue(
                "First Type Should be " + type1 + " instead of " + actualClass.getCanonicalName(),
                getClassFromKey(type1).isAssignableFrom(actualClass)
        );
        actualClass = getFieldGenericType(f, 1);
        assertTrue(
                "Second Type Should be " + type2 + " instead of " + actualClass.getCanonicalName(),
                getClassFromKey(type2).isAssignableFrom(actualClass)
        );
    }

    private Class<?> getFieldGenericType(Field f, int i) {
        return (Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[i];
    }

    private Class<?> getClassFromKey(String type) {
        if (type.equalsIgnoreCase("String"))
            return String.class;
        if (type.equalsIgnoreCase("Address"))
            return Address.class;
        return Object.class;
    }

    @Then("^the \"([^\"]*)\" table has a foreignKey to \"([^\"]*)\" table$")
    public void theTableHasAForeignKeyToTable(String source, String target) throws Throwable {
        Optional<Object> foreignKey = getForeignKey(source,target);

        assertTrue(
                source + " should have a foreign key to " + target,
                foreignKey.isPresent()
        );
    }


    @But("^The \"([^\"]*)\" table has no link to \"([^\"]*)\" table$")
    public void theTableHasNoLinkToTable(String source, String target) throws Throwable {
        Optional<Object> foreignKey = getForeignKey(source,target);

        assertFalse(
                source + " should not have a foreign key to " + target,
                foreignKey.isPresent()
        );
    }

    private Optional<Object> getForeignKey(String source, String target) {
        return jdbcTemplate.query(
                "SELECT * FROM INFORMATION_SCHEMA.CONSTRAINTS where table_name = ?",
                new Object[]{source.toUpperCase()},
                new ColumnMapRowMapper()
        ).stream()
                .filter(c -> c.getOrDefault("CONSTRAINT_TYPE", "").equals("REFERENTIAL"))
                .filter(c -> ((String) c.getOrDefault("SQL", "")).contains(target.toUpperCase()))
                .map(c -> c.getOrDefault("UniqueIndexName", ""))
                .findAny();
    }

    private Stream<String> getColumnNameStream(String tableName) {
        return jdbcTemplate.query(
                "show columns from " + tableName.toUpperCase(), new ColumnMapRowMapper())
                .stream()
                .flatMap(c -> c.entrySet().stream())
                .filter(c -> c.getKey().equalsIgnoreCase("field"))
                .map(c -> (String) c.getValue());
    }

}


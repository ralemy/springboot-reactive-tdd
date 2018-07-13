# Use of JPA and Hibernate in Spring boot application

Data persistence is a very common requirement in every application. One of the best practices
in implementation of data persistence is the use of ORM, in which a framework will usher data
between objects and database tables automatically. One of the most popular ORM frameworks is
Hibernate, which will be used in this step. 

The Database behind the ORM framework can be any engine, in this example we use H2. H2 is an
embedded engine that can work on file or in memory. The latter is useful in creating transient
data stores for unit tests. In this section we demonstrate the configuration of hibernate to use
one database in production and the other one during tests.

## Configuring hibernate outside test environment

A Spring Boot application has an ```application.properties``` file located in ```src/main/resources```.
here is how this application can be configured to use an on-file database using H2 engine:

```
spring.h2.console.enabled=true
spring.h2.console.path=/h2

spring.datasource.url=jdbc:h2:file:./objects
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true
```

The first line enables the hibernate console, which is very useful in browsing and
examining the data store. the second line instructs that the console should be served
at the ```/h2``` endpoint of web server.

However, since the security-started is included in POM, the ```/h2``` endpoint will not 
be accessible. Web Security is outside the scope of this document but for our purposes all
is needed is to exclude the endpoint from security. 

To achieve that, create a configuration class that is extended from Web

```java
package com.curisprofound.tddwebstack.config;
// imports not shown
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.anonymous()
                .and().authorizeRequests().antMatchers("/h2**").permitAll()
                .and().logout().permitAll();
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }
}
```

Now, each time the application starts, the ```/h2``` endpoint will show the database connection
dialog which can connect with username "sa" and empty password.

### Creating an H2 in memory database for testing

At testing time, Hibernate can use another database, in which case and in memory embedded 
database seems best, given that the repository is transient in nature. To achieve this, 
another application.properties file should be placed in ```src/test/resources``` which 
defines the test environment.

```
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=sa
spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true
```

### Creating Hibernate models

In Spring boot, a POJO annotated with ```@Entity``` will be considered an ORM model, and
a class extending ```JpaRepository<model>``` is used as the DAO (data access object).

Therefore, the inital test will check for a POJO annotated with "Entity":

```gherkin
Feature: As a developer
  I need an ORM to deal with data persistence
  So that I could safely change engines and use migration tools

  @HibernateJPA
  Scenario: Should have a class named Customer annotated with @Entity
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
    When  The annotations of the class are examined
    Then  the "Entity" annotation exists in the class annotations
```

The Steps file for this scenario is implemented as below:

```java
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
        Optional<Annotation> actual = Arrays.stream(annotations)
            .filter(a -> a.annotationType()
                            .getName()
                            .contains(arg0))
            .findFirst();
        assertTrue(actual.isPresent());
    }
}
```

Test will fail with ```ClassNotFound``` exception until we actually create the classs,
then it will fail with ```AssertionError``` until we annotate the class as Entity.

If the tests runs while the application is running (for example using Intellij IDEA), one 
can use the console to verify that in fact no transaction is made to the on-file database.

### Unit testing private fields

To check for fields, a scenario could asset the existence of a getter and setter
for that field, and the fact that it is a column, and id class, etc.

```gherkin
  @HibernateJPA
  Scenario: Customer should have an id field that is annotated as Id (primary key)
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
    Then  The class has a getter for property "Id"
    And   The "id" field is annotated as "Id"
```

for a Customer object that has been created like the following:

```java
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Customer {
    @Id
    private long id;
    private String name;
}
```

The steps need to be declared in a way that the annotations of the private variable
```id``` can be examined. this is achieved with the following steps:

```java
@Then("^The class has a getter for property \"([^\"]*)\"$")
    public void theClassHasAGetterForProperty(String propertyName) throws Throwable {
        propertyName = propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
        Method method = Class.forName(Get(String.class, "ClassName")).getDeclaredMethod("get" + propertyName);
        Add(Method.class, method, Get(String.class,"ClassName") + ".get"+propertyName);
    }

    @And("^The \"([^\"]*)\" field is annotated as \"([^\"]*)\"$")
    public void theFieldIsAnnotatedAs(String propertyName, String annotationName) throws Throwable {
        propertyName = propertyName.substring(0,1).toLowerCase() + propertyName.substring(1);

        Field f = Class.forName(Get("ClassName")).getDeclaredField(propertyName);
        f.setAccessible(true);

        Optional<Annotation> annotation = Arrays.stream(f.getAnnotations()).filter(
                a -> a.annotationType().getName().contains(annotationName)
        ).findAny();


        assertTrue(
                "Should have an annotation for " + annotationName,
                annotation.isPresent()
        );
    }
```
Notice the call to ```setAccessible``` method on the declared member.

### unit testing unannotated fields

In the Customer object example above, the ```name``` field is not annotated, which 
means that it will map to a column with the same name. However, it can't be tested 
as the Id field because there is no annotation on the field. So, we need to examine 
the created table in the database directly and ensure that the required field was 
created.

```gherkin
  @HibernateJPA
  Scenario: Should create a column with the name of the field if the field is not annotated
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
    And   The class has an unannotated field called "name"
    When  Hibernate should create a column "name" in table "Customer"
```

the Given statement was implemented previously, however we need to assert that the
field is not annotated:

```java
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
```

To connect directly to the H2 database, we use inject the JdbcTemplate object to our
test harness and run a describtive query on the table:

```java
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
```

The result of the above query will be a table like below

|FIELD|     TYPE|          NULL|   KEY|    DEFAULT |
|-----|---------|--------------|------|------------| 
|ID|BIGINT(19) |   NO|     PRI|     NULL|
|NAME|      VARCHAR(255)|  YES|          |  NULL|

```ColumnMapRowMapper()``` will gather the above information in a 
```List<Map<String,Object>>```. i.e, a list of rows, each row represented as a map,
where the key is the field name and the value is the field content. the functional 
lambdas in the above code findout if there is a field by the expected name in the 
returned result.

# Relationships

## ElementCollection

```@ElementCollection``` is an annotation used when the collection of objects does not
have a lifecycle of its own. it is in essence a One-To-Many relationship, but the 'Many' 
side is created, updated and deleted with the One side.

ElementCollection is studied in this document from three aspects: the collection of simple 
types, the collection of Embeddable objects, and the collection of Maps.

### ElementCollection of Simple types

We can Imagine the customer to have a list of zero to any number of phone numbers.
because the count is not limited, this needs to be modeled in a one-to-many relationship.
however, a single phonenumber doesn't have a meaning without an owner. The test for this
scenario is written as bellow:

```gherkin
 @HibernateJPA
  Scenario: Should have a column as List of elemental objects that maps to an external table
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
    And   The class has a field called "phoneNumbers" that is of type List of Strings
    When  The annotations of the "phoneNumbers" field are examined
    Then  The "phoneNumbers" field is annotated as "ElementCollection"
    And   Hibernate creates a "customer_phone_numbers" table in the database
```

perhaps the only new thing in writing the steps for the above scenario is how to check
that the generic type of a list field is a string, so we can distinguish between 
```List<String>``` and ```List<Integer>```.

```java
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

```
For the tests to pass, the Customer object has to be refactored:

```java
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Customer {
    @Id
    private long id;
    private String name;

    @ElementCollection
    private List<String> phoneNumbers;

    public List<String> getPhoneNumbers(){
        if(phoneNumbers == null)
            phoneNumbers = new ArrayList<>();
        return phoneNumbers;
    }
}
```
### ElementCollection of embedded type

The other use case for ElementCollection is to have a property with complex type, a typical example of which is 
the Address (which consists of address lines, city, country, postal code, etc.). A customer may have multiple 
addresses, but an address by itself doesn't have a lifecycle. 

First, a test scenario for the existance and correct configuration of the Address object.

```gherkin
  @HibernateJPA
  Scenario: Should have a class named Address which is embeddable
    Given There exists a class named "Address" in "com.curisprofound.tddwebstack.db" package
    And   The class has the following properties: "addressLine1, addressLine2, city, postalCode"
    When  The annotations of the class are examined
    Then  the "Embeddable" annotation exists in the class annotations
```

The only new step is to check existence of a list of properties on a class

```java
    @And("^The class has the following properties: \"([^\"]*)\"$")
    public void theClassHasTheFollowingProperties(String propertyList) throws Throwable {
        String[] propertyNames= propertyList.split(",");
        Class<?> target = Class.forName(Get("ClassName"));
        for (String c : propertyNames) {
            try {
                target.getDeclaredField(c.trim());
            } catch (NoSuchFieldException e) {
                Assert.fail("Class doesn't have a field called " + c);
            }
        }
    }
```

Now, a test scenario for the Customer to have a List of addresses annotated 
with ```ElementCollection```.

```gherkin
@HibernateJPA
  Scenario: Should have a collection of addresses in Customer, which should map to a one-to-many relationship
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
    And   The class has a field called "addresses" that is of type List of "Address"
    Then  The "addresses" field is annotated as "ElementCollection"
    And   Hibernate creates a "customer_addresses" table in the database
```

All of the above steps have previously been implemented. 

### ElementCollection of Map types

If the property that is being collected is a Map with keys and values, the 
general usage of the annotation remains the same, and hibernate will create a table
with three columns for it: one for the foreign key, one for the map key, and 
one (or more if the map value is embeddable) for the map value.

Here is a test:

```gherkin
  @HibernateJPA
  Scenario: Should have a collection of meal preferences in Customer, which should map to a one-to-many relationship
    Given There exists a class named "Customer" in "com.curisprofound.tddwebstack.db" package
    And   The class has a field called "mealPreferences" that is of type Map of "String" and "String"
    Then  The "mealPreferences" field is annotated as "ElementCollection"
    And   Hibernate creates a "customer_meal_preferences" table in the database
```

Checking the generic types of Map is the same as List:

```java
    @And("^The class has a field called \"([^\"]*)\" that is of type Map of \"([^\"]*)\" and \"([^\"]*)\"$")
    public void theClassHasAFieldCalledThatIsOfTypeMapOfAnd(String propertyName, String type1, String type2) throws Throwable {
        Field f = getFieldByName(propertyName, Get("ClassName"));
        assertTrue(propertyName + " should be a Map instead of " + f.getType().getCanonicalName(),
                Map.class.isAssignableFrom(f.getType()));
        Class<?> actualClass = getFieldGenericType(f,0);
        assertTrue(
                "First Type Should be " + type1 + " instead of " + actualClass.getCanonicalName(),
                getClassFromKey(type1).isAssignableFrom(actualClass)
        );
        actualClass = getFieldGenericType(f,1);
        assertTrue(
                "Second Type Should be " + type2 + " instead of " + actualClass.getCanonicalName(),
                getClassFromKey(type2).isAssignableFrom(actualClass)
        );
    }

    private Class<?> getFieldGenericType(Field f, int i){
        return (Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[i];
    }

    private Class<?> getClassFromKey(String type) {
        if(type.equalsIgnoreCase("String"))
            return String.class;
        if(type.equalsIgnoreCase("Address"))
            return Address.class;
        return Object.class;
    }
```

## One-to-One relationships

One-to-One relationships are usually employed when the entity has a rare optional complex property.

For example, imagine that 5% of customers are retailers, and retailers have a set of information,
such as national business number, store count, warehouse capacity, etc. 

Normally, there would be a column on the customers table for each of the above. 
however, it is wasteful in this situation because 95% of the time the columns will all
be null as only 5% of customers are retailers.

At times like this, a One-to-One relationship is created to move the 
retailer rows to their own table.

### Unidirectional One-to-one relationships

Unidirectional relationships are those that one can get to the child from parent but not 
to parent from child. these are used when the child table is an extension that doesn't have any
meaning by itself. Imagine that for addresses that show a high rise building, we have
an extension that shows the floor, suite, and buzzer number. 

First, the test to ensure such class exists and has correct properties:

```gherkin
  @HibernateJPA
  Scenario: Should have a class named HighRiseAddressExtension which has its own table
    Given There exists a class named "HighRiseAddressExtension" in "com.curisprofound.tddwebstack.db" package
    And   The class has the following properties: "suite, floor, buzzerCode"
    When  The annotations of the class are examined
    Then  the "Entity" annotation exists in the class annotations
```

Nothing new here. the test will fail and will pass when we have the class:

```java
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HighRiseAddressExtension {
    @Id
    @GeneratedValue
    private long id;
    private String suite;
    private String floor;
    private String buzzerCode;
}
```

Now, the test that would ensure it has a unidirectional one-to-one relationship with
Address:

```gherkin
  @HibernateJPA
  Scenario: the Address class should have a unidirectional one to one relationship with HighRiseAddressExtension
    Given There exists a class named "Address" in "com.curisprofound.tddwebstack.db" package
    And   The class has the following properties: "highRiseExtension"
    And   The "highRiseExtension" field is annotated as "OneToOne"
    When  Hibernate creates a "high_rise_address_extension" table in the database
    Then  the "customer_addresses" table has a foreignKey to "high_rise_address_extension" table
    But   The "high_rise_address_extension" table has no link to "customer_addresses" table
```

Two new steps need to check the foreign keys between two tables to ensure that the
relationship is unidirectional. here is how to implement them:

```java
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
```

## Bidirectional One-to-One relationship

If the child entity has a link to refer back to the parent then the relationship will
be bilateral. For example, The customer may have a Shipping Contact which has its own
properties from whom we could get the customer they represent. 

As ususal, there needs to be a test to ensure the existance of the ShippingContact class
and its correct annotation.


```gherkin

```
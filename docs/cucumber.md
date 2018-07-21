# Using Cucumber for Unit testing 

[Cucumber][] is a tool that runs automated acceptance tests using Behavior-Driven-Development 
style language called Gherkin. As such, most guidelines about using Cucumber is in [integration
testing][], and unit tests are usually performed using other tools such as JUNIT.

Nevertheless, we believe that using Cucumber to write unit tests is also helpful in a few ways,
including having a single testing tool, allowing developers to communicate better with stakeholders
using the plain test approach of BDD, and allowing for reduction of repetitive code in ```Given``` 
and ```Then``` clauses by sharing them between scenarios.


## POM Dependencies:

It is a good idea to include hamcrest-all while we are at it, to provide matchers that would make 
assertions easier. Cucumber java, junit, and spring artifacts are included for obvious reasons.


```xml
		<!-- https://mvnrepository.com/artifact/org.hamcrest/hamcrest-all -->
		<dependency>
			<groupId>org.hamcrest</groupId>
			<artifactId>hamcrest-all</artifactId>
			<version>1.3</version>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>info.cukes</groupId>
			<artifactId>cucumber-java</artifactId>
			<version>1.2.5</version>
			<scope>test</scope>
		</dependency>
		<!-- https://mvnrepository.com/artifact/info.cukes/cucumber-junit -->
		<dependency>
			<groupId>info.cukes</groupId>
			<artifactId>cucumber-junit</artifactId>
			<version>1.2.5</version>
			<scope>test</scope>
		</dependency>
		<!-- https://mvnrepository.com/artifact/info.cukes/cucumber-spring -->
		<dependency>
			<groupId>info.cukes</groupId>
			<artifactId>cucumber-spring</artifactId>
			<version>1.2.5</version>
			<scope>test</scope>
		</dependency>

```

To initialize unit tests to run with Cucumber, one follows the section on using [non-JUnit 
test runners][] in the docs. This requires inclusion of the following in the POM:

```xml
		<dependency>
			<groupId>org.springframework.restdocs</groupId>
			<artifactId>spring-restdocs-mockmvc</artifactId>
			<scope>test</scope>
		</dependency>

```

## The Cucumber Test Runner

In the root test package (```src/test/java/com/.../```) create a ```CucumberTest``` class:

```java
@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        glue = "com.curisprofound.tddwebstack.cucumber",
        features = "src/test/resources")
@ContextConfiguration(classes = TddWebStackApplication.class)
@SpringBootTest
public class CucumberTest {
}
```

This is telling tests to run with Cucumber and where to find features and steps for the tests.

## The Cucumber World Object

To save state between steps of a Scenario, Cucumber in Ruby has the concept of a World object,
which gets recreated between scenarios. The equivalent with Spring boot is to have a component 
that is created and injected at the start of each scenario.

This component is only used in tests so it should be created in ```src/test/java/com/.../cucumber```

to make it reusable with different scenarios, it has a Hash Map to persist objects between steps and 
stores and retrieves them based on their cannonical class name. It also has a few overloaded 
methods to simplify working with the hash map. 

By design, this object is deviating from Java method naming conventions by having the 
method name for Get and Add start with capital letters. This is done to make it easier to distnguish
these methods from other get and add methods which are used frequently in tests (such as
get() method of MockMvc)

```java
@Component
public class World {
    private final Map<String,Object> context;

    public World() {
        context = new HashMap<>();
    }

    public void Clear(){
        context.clear();
    }

    @SuppressWarnings("unchecked")
    public <T> T Get(Class<T> clazz, String key){
        return (T) context.getOrDefault(key,null);
    }

    public <T> T Get(Class<T> clazz){
        return Get(clazz, clazz.getCanonicalName());
    }



    public <T> T Add(Class<T> clazz, T target, String key){
        context.put(key,target);
        return target;
    }
    public <T> T Add(Class<T> clazz, T target){
        return Add(clazz,target, clazz.getCanonicalName());
    }
    public <T> T Add(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        return Add(clazz, clazz.newInstance(),clazz.getCanonicalName());
    }

}
```




## The Cucumber StepsBase class

in the cucumber test package (```src/test/java/com/.../cucumber```) create a StepsBase class

This class will contain helper functions shared between test step classes. for example, it 
receives an instance of the World object and provides shortcut methods to store and retrieve 
objects using the World component.

```java
@SpringBootTest
@ContextConfiguration(classes = TddWebStackApplication.class)
public class StepsBase {
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private World world;

    private final ManualRestDocumentation restDocumentation;
    private final ObjectMapper objectMapper;
    private final TypeFactory typeFactory;

    public StepsBase(){
        restDocumentation = new ManualRestDocumentation();
        objectMapper = new ObjectMapper();
        typeFactory  = objectMapper.getTypeFactory();
    }
    public MockMvc getMockMvc(Class testClass, String testMethod) {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
        restDocumentation.beforeTest(testClass, testMethod);
        return mockMvc;
    }

    public void tearDown() {
        world.Clear();
        restDocumentation.afterTest();
    }

    protected  <T> List<T> jsonStringToClassArray(String content, Class<T> clazz) throws IOException {
        return objectMapper.readValue(
                content,
                typeFactory.constructCollectionType(List.class,clazz));

    }
    
    public <T> T Get(Class<T> clazz, String key){
        return world.Get(clazz,key);        
    }

    public <T> T Get(Class<T> clazz){
        return world.Get(clazz);
    }
    
    public <T> T Add(Class<T> clazz, T target, String key){
        return world.Add(clazz,target,key);
    }
    public <T> T Add(Class<T> clazz, T target){
        return world.Add(clazz,target);
    }
    public <T> T Add(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        return world.Add(clazz);
    }


}
```

As we implement more features these classes will hold abstraction functions that make
tests easier to read and simpler to write.

## Sanity Tests for Cucumber

Cucumber testing has two kinds of files. the features file, which will be put in 
```src/test/reasources/features``` and step files which will be placed in 
```src/test/java/com/.../cucumber```. One feature file may call steps across multiple
step files, and that is where the World object comes in, to ensure the continuity of 
data across steps. to make sure our system works correctly, we create a feature file: 

```gherkin
Feature: As a developer
  I need to ensure that Cucumber is installed correctly and can persist state between test steps
  So that I can confidently focus on implementing my features

  @CucumberSanity
  Scenario: Should be able to assert a simple condition
    Given I initialize World variable "p1" to 5 and "p2" to 12
    When  I add "p1" and "p2" and store the result in "p3"
    Then  The World variable "p3" should be equal to 17

```
The first scenario is simply testing that World can keep variables and restore and 
assert them. for it to work, we need a steps function which will be a subclass of StepsBase
class.

```java
public class CucumberSanitySteps  extends StepsBase{

    @Before("@CucumberSanity")
    public void beforeCucumberSanity(){

    }

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
```
Note that most IDE products, especially Intellij IDEA, have plugins and facilities to
create Cucumber step files and methods automatically.

### Using the World object across Steps files

Let's add another scenario to our feature file:

```gherkin
  @CucumberSanity
  Scenario: Should be able to use World object across step classes
    Given I initialize World variable "p1" to 4 and "p2" to 7
    When  I multiply "p1" and "p2" and store the result in "p3"
    Then  The World variable "p3" should be equal to 28
```

This scenario reuses two steps from the previous scenario, so lets create the middle step
in a new file. when the scenario is running, it has to run two steps from the first file 
and the middle step from the second file

```java
public class CucumberSantiySteps01 extends StepsBase{


    @When("^I multiply \"([^\"]*)\" and \"([^\"]*)\" and store the result in \"([^\"]*)\"$")
    public void iMultiplyAndAndStoreTheResultIn(String p1, String p2, String p3) throws Throwable {
        int k =  Get(Integer.class , p1) * Get(Integer.class, p2);
        Add(Integer.class, k, p3);
    }
}
```

### Resetting the World between Scenarios

The last Scenario in this section ensurs that the world is reset between Scenarios.
The feature for this is rather straight forward:

```gherkin
 @CucumberSanity
  Scenario: Should clear out the objects between Scenarios
    Given I have initialized "p1" in a previous Scenario
    When  I enter a new Scenario
    Then  The World variable "p1" should be null
```

To implement the steps, however, we need to refactor the steps file to keep state between 
Scenarios. this deviation of protocol is achieved by creating a static member in the steps file.

```java
public class CucumberSantiySteps01 extends StepsBase{

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
        // no need to do anything. this step is provided for continuity and clarity.
    }

    @Then("^The World variable \"([^\"]*)\" should be null$")
    public void theWorldVariableShouldBeNull(String arg0) throws Throwable {
        assertNull(Get(Integer.class,arg0));
    }
}
```

# Asserting Classes

The universal first step in TDD methodology is to write the test Before writing the code. Since everything
in Java is a class, work usually begins with creating a class, so the first test is for existence of 
the class:

```gherkin
    Given There exists a class named "TestMe" in "com.curisprofound.tddwebstack" package
``` 

Once the class is created, there needs to be tests to assert a variety of conditions, such as 
existence of fields and methods with certain names, certain method with certain parameters and 
return values, certain annotaions on fields, methods, classes, etc. Also, keep in mind that there
is more often than not a need to negate a test, for example chekcing that a method does not exist or
a field is not of certain type. 

Obviously, the way to write all these tests is through Reflection in Java. However, reflection usually
requires a lot of boilerplate code and casting, so we have encapsulated such tests under the 
```AssertOnClass``` object.

The AssertOnClass object creates a chain of tests. for example, the above condition is implemented as

```java
String fullName = packageName + "." + className;
AssertOnClass
    .For(fullName);
Add(String.class, fullName, "ClassName");
```

The full class name is added to the World object so that it can be used in next steps. for example if the 
scenario continues with the following test:

```gherkin
And the class has a field called "publicField"
```

then we can get the classname from the world object:

```java
AssertOnClass
    .For(Get("ClassName"))
    .Field(fieldName);
```

## Method Signatures

testing that there exists a method under a specific name which accepts certain set of parameters and 
returns a certain type of object is another popular requirement. imagine another step:

```gherkin
    Then  The class has a method with signature "myMethod(Map<String,List<Class<? extends RuntimeException>>>, Object, List<String>):List<String>"
```

This is a shortcut to check everything in one step. the format should be similar to Typescript definitions:

     nameofMethod ( parameters of method) : return type of method

here is who it is implemented:

```java
        String returnValue = methodSignature.split(":")[1].trim();
        String methodName = methodSignature.split("\\(")[0].trim();
        String params = methodSignature.split("\\(")[1].split("\\)")[0].trim();
        AssertOnClass
                .For(Get("ClassName"))
                .Method(methodName, TypeDef.parse(params))
                .hasReturnType(TypeDef.parse(returnValue).get(0));
```

## More examples

The cucumberSanity.feature file in src/test/resources/features has examples of complex types and annotation
checking and other tests.


# Future Direction

There are many things that can be tested with reflection. which exceptions are thrown by a method, 
which annotations are placed on the parameters of a method, class superclass and implemented interfaces, 
etc.

As we encounter these tests we add them the AssertOnClass branches to be reused when encountered again.






[Cucumber]:https://en.wikipedia.org/wiki/Cucumber_(software)
[integration testing]:https://thepracticaldeveloper.com/2018/03/31/cucumber-tests-spring-boot-dependency-injection/
[non-JUnit test runners]: https://docs.spring.io/spring-restdocs/docs/current/reference/html5/#_setting_up_your_tests_without_junit

 
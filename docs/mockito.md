# Mocking in Cucumber steps with Mockito framework in Spring Boot

Mockito is a very useful mocking library. The usual practice when writing unit tests is to mock
all dependencies of a certain class, then interact with the class and assert that dependencies are 
called with correct arguments and order. 

Once the correct behaviour of the class is asserted, it will be mocked for classes that depend on it,
and behaviour of those classes are tested. In short, we first mock the dependencies
and test the class, then mock the class and test the classes for which this class is a dependency.

**Spying** is a special type of mocking where a real instance of the class is intercepted and mocked.
This means that one can keep some methods of the object under observation while the rest would 
function as usual.

## Common usage patterns

Fpllowing is an overview of most common mocking actions. for these operations, imagine we have 
a CustomerController class that depends on CusotmerRepository, and we are testing
CustomerController. Notice that we are mocking the dependency, not the class we are actually 
testing:

```java
// intercept the return value when a method is called 
doReturn(myCustomer).when(customerRepository).findById(any(Long.class));

//change the return value based on the argument sent to the method
doAnswer((Answer<Customer>) input ->  new Customer((Long)input.getArguments()[0]))
                .when(customerRepository).findById(any(Long.class));

//verify that a method has been called once, 5 times, and never
verify(customerRepository).findAll()
verify(customerRepository, times(5)).findAll()
verify(customerRepository, never()).findAll()

//verify that a method has been called with a specific argument
verify(customerRepository).findById(eq(5L));

//extract the argument sent to the method:
ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
verify(customerRepository).save(captor.capture());
assertEquals(
        myCustomer.getName()
        captor.getValue().getName()
);
```

for any of the above to work, the ```customerRepository``` object should be mocked. for usual 
objects, this is accomplished by using ```Mockito.mock()``` or ```Mockito.spy()```.

However, in the case of SpringBoot, the beans are created at configuration time, so the test 
needs to use ```@MockBean``` or ```@SpyBean``` annotations to replace the bean with a mocked
version before Mockito can evaluate the above statements.


## Caveats of usign Mockito when testing with Cucumber

Spring boot has Mockito as a standard dependency of spring-boot-starter-test. However, mocking beans
has caveats that need to be considered when tests are written with Cucumber. 

The first issue is that all Cucumber tests are considered one big suite, unless one uses multiple
test classes or creates a [custom runner][]. Both approaches will be reviewed in next iterations,
but in their absence this means that all of the tests will be run in the same context.

The ramification is that when a bean is mocked with ```@MockBean```, it has to be mocked for all of 
the classes (e.g. in the base class or configuration class), and remains mocked for all of them.
This means we can't have a feature file that tests the class and another one that mocks it when 
it is a dependency for another class.

The simplest approach to workaround the problem is to ```@SpyBean``` on the dependency beans, 
so that the beans will all be actual objects but can be intercepted when necessary to perform mocking.

Most unfortunately there is a [known bug][] in ```@SpyBean``` which throws an exception when trying
to spy on JpaRepositories, with a [workaround][] which includes a Component to override the post-processor
for Mockito.

First, let's test for a JpaRepository that we would later mock and inject to our tests:

```gherkin
  @Mockito
  Scenario: Should have a repository class for customer ORM
    Given  There exists a class named "CustomerRepository" in "com.curisprofound.tddwebstack.db" package
    Then   The interface implements the "JpaRepository" with "Customer" and "Long" arguments
```

Based on the [workaround][], we need a MockPostProcessor class in com.curisprofound.tddwebstack.cucumber, which has a 
```postProcessAfterInitialization()``` method, which is annotated as ```Component``` and 
takes a bean object and a name string. we can write a test for that:

```gherkin
  @Mockito
  Scenario: Should have a component to override Mock post processor
    Given  There exists a class named "MockPostProcessor" in "com.curisprofound.tddwebstack.cucumber" package
    And    The class has a method "postProcessAfterInitialization" with parameters "Object,String"
    And    the "Component" annotation exists in the class annotations
    And    The class has a field called "classes" that is of type List of "Class"
``` 

Once this test passes, we can test that a bean exists and it works correctly:

```gherkin
  @Mockito
  Scenario: Should inject a bean for Mock post processor which mocks customerRepository
    Given There is a bean for "mockPostProcessor"
    And   The classes list has CustomerRepository in it
    When   I call the post-processor with a general object
    Then   I get the same object without mocking
    When   I add a class to class list of preprocessor
    And    I call the post-processor with a an instance of that class
    Then   I get the mocked version of the class
```

here is How the final workaround component looks like:

```java
@Component
public class MockPostProcessor implements BeanPostProcessor {

    public final List<Class> classes = new ArrayList<>();

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        classes.add(CustomerRepository.class);

        return classes.stream().noneMatch(c-> c.isInstance(bean)) ?
                bean :
                Mockito.mock(CustomerRepository.class, AdditionalAnswers.delegatesTo(bean));
    }
}
```

## Mocking Life Cycle

Now that the workaround is implemented, at every feature file we can decide what to mock and 
what to keep. to achieve this we add an annotation to each scenario, then create a @Before and
@After annotated method to control what will be mocked and reset them at the end of the test:

```java
    @Before("@Mockito")
    public void beforeMockito() {
        doReturn(Optional.of(newCustomer("customerFixed")))
                .when(customerRepository)
                .findById(any(Long.class));
    }

    @After("@Mockito")
    public void afterMockito() {
        reset(customerRepository);
        tearDown();
    }
```
It is important to reset the mock at the end of the Scenario so it doesn't carry the masks to
next tests.

## Testing for correct behaviour

We can now test and make sure each of the above usage patterns works correctly:

Since customerRepository is mocked in @Before method to return a customer with the name of 
"customerFixed" no matter what Id is given, the following test should pass if mocking has 
been done correctly:

```gherkin
  @Mockito
  Scenario: Should return the same object for every Id
    When   I get the Customer with id 10
    Then   the customer name is "customerFixed"
    When   I get the Customer with id 20
    Then   the customer name is "customerFixed"
```

We can also set the answer relative to the argument that the mock receives. 

```gherkin
  @Mockito
  Scenario: Should be able to mock the object to return something based on input arguments
    Given  I have mocked customerRepository FindbyId to return a customer with id plus 10
    When   I get the Customer with id 10
    Then   the customer id is 20
    When   I get the Customer with id 25
    Then   the customer id is 35
```

We can see that a function has been called once, specific times, or never

```gherkin
  @Mockito
  Scenario: Should have a mock of customerRepository injected to the tests
    Given  the autowired customerRepository is a MockBean
    And    the findAll method is masked to return a customer named "customer1"
    Then   the findAll method will return one customer by name of "customer1"
    And    the findAll method was call coundter would be 1
    And    execution of findall test is recorded
```

And that the mock is reset between scenarios:

```gherkin
  @Mockito
  Scenario: Should reset the mock before the next test
    Given  the findall test has been executed
    And    the findAll method was call coundter would be 0
    Then   the findAll mask is no longer active
    And    the findAll method was call coundter would be 1
```

Finally, we can capture and examine the invocation arguments of a method:

```gherkin
  @Mockito
  Scenario: Should be able to capture the input of the mock
    Given I have a customer object by name of "customer1"
    And   I have mocked save function to trap its input
    When  I save it to customer repository
    Then  I can verify the save function was called with "customer1"
```

# Next Steps

In the next iteration we will examine the creation of a [custom runner][] or implmentation of 
multiple Cucumber runner classes.


[custom runner]:https://stackoverflow.com/questions/35314463/is-it-possible-to-configure-cucumber-to-run-the-same-test-with-different-spring
[known bug]:https://github.com/spring-projects/spring-boot/issues/7033
[workaround]:https://gist.github.com/olivergierke/979d42f161123af7d3fca2d7bcc4a335

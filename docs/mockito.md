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

Based on the [workaround][], we need a MockPostProcessor class in com.curisprofound.tddwebstack.cucumber, which has a 
```postProcessAfterInitialization()``` method, which is annotated as ```Component``` and 
takes a bean object and a name string. we can write a test for that:

```gherkin
  Scenario: Should have a component to override Mock post processor
    Given  There exists a class named "MockPostProcessor" in "com.curisprofound.tddwebstack.cucumber" package
    And    The class has a method "postProcessAfterInitialization" with parameters "Object,String"
    And    the "Component" annotation exists in the class annotations
``` 

Once this test passes, we can test 






[custom runner]:https://stackoverflow.com/questions/35314463/is-it-possible-to-configure-cucumber-to-run-the-same-test-with-different-spring
[known bug]:https://github.com/spring-projects/spring-boot/issues/7033
[workaround]:https://gist.github.com/olivergierke/979d42f161123af7d3fca2d7bcc4a335

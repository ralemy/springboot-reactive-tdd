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


[Cucumber]:https://en.wikipedia.org/wiki/Cucumber_(software)
[integration testing]:https://thepracticaldeveloper.com/2018/03/31/cucumber-tests-spring-boot-dependency-injection/

 
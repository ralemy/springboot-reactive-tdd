package com.curisprofound.tddwebstack;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        glue = "com.curisprofound.tddwebstack.cucumber",
        features = "src/test/resources")
@ContextConfiguration(classes = TddWebStackApplication.class)
@SpringBootTest
public class CucumberTest {
}

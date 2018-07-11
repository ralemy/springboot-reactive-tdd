package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.TddWebStackApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

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
    private boolean shouldCallAfterTest;

    public StepsBase(){
        restDocumentation = new ManualRestDocumentation();
        objectMapper = new ObjectMapper();
        typeFactory  = objectMapper.getTypeFactory();
        shouldCallAfterTest = false;
    }
    public MockMvc getMockMvc(Class testClass, String testMethod) {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
        restDocumentation.beforeTest(testClass, testMethod);
        shouldCallAfterTest = true;
        return mockMvc;
    }

    public void tearDown() {
        world.Clear();
        if(shouldCallAfterTest)
            restDocumentation.afterTest();
        shouldCallAfterTest = false;
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

    public String Get(String key) { return world.Get(String.class, key);}

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

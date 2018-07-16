package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.TddWebStackApplication;
import com.curisprofound.tddwebstack.db.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Profile;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = TddWebStackApplication.class)
public class StepsBase {



    @Autowired
    private WebApplicationContext context;

    @Autowired
    private World world;

    private  ManualRestDocumentation restDocumentation;
    private  ObjectMapper objectMapper;
    private  TypeFactory typeFactory;
    private boolean shouldCallAfterTest;
    private WebTestClient webTestClient;

    public StepsBase(){
        restDocumentation = new ManualRestDocumentation();
        objectMapper = new ObjectMapper();
        typeFactory = objectMapper.getTypeFactory();
        shouldCallAfterTest = false;
    }

    public MockMvc getMockMvc(Class testClass, String testMethod) {

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(
                        org.springframework.restdocs
                                .mockmvc.MockMvcRestDocumentation
                                .documentationConfiguration(restDocumentation))
                .build();
        restDocumentation.beforeTest(testClass, testMethod);
        shouldCallAfterTest = true;
        Add(MockMvc.class, mockMvc);
        return mockMvc;
    }

    public Object getBean(String beanName){
        return context.getBean(beanName);
    }
    public String[] getAllBeanNames(){
        return context.getBeanDefinitionNames();
    }

    public void tearDown() {
        world.Clear();
        if (shouldCallAfterTest)
            restDocumentation.afterTest();
        shouldCallAfterTest = false;
    }

    protected <T> List<T> jsonStringToClassArray(String content, Class<T> clazz) throws IOException {
        return objectMapper.readValue(
                content,
                typeFactory.constructCollectionType(List.class, clazz));

    }

    public <T> T Get(Class<T> clazz, String key) {
        return world.Get(clazz, key);
    }

    public <T> T Get(Class<T> clazz) {
        return world.Get(clazz);
    }

    public String Get(String key) {
        return world.Get(String.class, key);
    }

    public <T> T Add(Class<T> clazz, T target, String key) {
        return world.Add(clazz, target, key);
    }

    public <T> T Add(Class<T> clazz, T target) {
        return world.Add(clazz, target);
    }

    public <T> T Add(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        return world.Add(clazz);
    }


}

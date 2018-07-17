package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.TddWebStackApplication;
import com.curisprofound.tddwebstack.db.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import java.io.IOException;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

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

    public void mockMvc(Class testClass, String testMethod) {

        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
//                .apply(
//                        org.springframework.restdocs
//                                .mockmvc.MockMvcRestDocumentation
//                                .documentationConfiguration(restDocumentation))
                .build();
//        restDocumentation.beforeTest(testClass, testMethod);
//        shouldCallAfterTest = true;
        Add(MockMvc.class, mockMvc);
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
    protected String jsonObjectToString(Object content) throws IOException {
        return objectMapper.writeValueAsString(content);
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

    public Class<?> getClassFromKey(String type) {
        if (type.equalsIgnoreCase("String"))
            return String.class;
        if (type.equalsIgnoreCase("Address"))
            return Address.class;
        if(type.equalsIgnoreCase("Invoice"))
            return Invoice.class;
        if(type.equalsIgnoreCase("Product"))
            return Product.class;
        if(type.equalsIgnoreCase("Author"))
            return Author.class;
        if(type.equalsIgnoreCase("Publisher"))
            return Publisher.class;
        if(type.equalsIgnoreCase("CustomerRepository"))
            return CustomerRepository.class;
        if(type.equalsIgnoreCase("Object"))
            return Object.class;
        if(type.equalsIgnoreCase("Class"))
            return Class.class;
        if(type.equalsIgnoreCase("Customer"))
            return Customer.class;

        Assert.fail("Unknown Type: " + type );
        return null;
    }

    protected Customer newCustomer(String name) {
        Customer c = new Customer();
        c.setName(name);
        return c;
    }
    protected Customer newCustomer(Long id) {
        Customer c = new Customer();
        c.setId(id);
        return c;
    }


}

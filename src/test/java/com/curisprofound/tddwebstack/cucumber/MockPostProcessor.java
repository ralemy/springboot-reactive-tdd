package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.db.CustomerRepository;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
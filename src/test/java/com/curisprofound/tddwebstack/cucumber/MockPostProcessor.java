package com.curisprofound.tddwebstack.cucumber;

import com.curisprofound.tddwebstack.db.CustomerRepository;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class MockPostProcessor implements BeanPostProcessor {

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (!CustomerRepository.class.isInstance(bean)) {
            return bean;
        }

        return Mockito.mock(CustomerRepository.class, AdditionalAnswers.delegatesTo(bean));
    }
}
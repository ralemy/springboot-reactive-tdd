package com.curisprofound.tddwebstack.assertions;

import org.junit.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AnnotationAssertions extends Assertions<AnnotationAssertions> {

    private final Annotation annotation;
    private final String name;

    public AnnotationAssertions(Annotation annotation) {
        this.annotation = annotation;
        this.name = annotation.annotationType().getCanonicalName();
    }

    public AnnotationAssertions paramHasValue(String param, String value) {
        try {
            Method method = annotation.getClass().getDeclaredMethod(param);

            String actual = (method.getReturnType().isArray()) ?
                    ((String[]) method.invoke(annotation))[0] :
                    (String) method.invoke(annotation);
            if (not)
                assertNotEquals(value, actual);
            else
                assertEquals(value, actual);
        } catch (NoSuchMethodException e) {
            Assert.fail(name + " does not have a parameter called " + param);
        } catch (IllegalAccessException e) {
            Assert.fail(name + " illegal access exception " + param);
        } catch (InvocationTargetException e) {
            Assert.fail(name + "On " + param +
                    " invocation target exception " + e.getMessage());
        }
        return chain();
    }
}

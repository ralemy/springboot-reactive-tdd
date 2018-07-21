package com.curisprofound.tddwebstack.assertions;

import org.junit.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;

public class MethodAssertions extends Assertions<MethodAssertions> {

    private final Method method;

    public MethodAssertions(Method method) {
        this.method = method;
    }

    public AnnotationAssertions Annotation(String name) {
        return getAnnotation(method.getAnnotations(), method.getName(), name);
    }

    public MethodAssertions hasAnnotations(String... annotations) {
        Optional<String> annotation = checkAnnotations(method.getAnnotations(), not, annotations);
        String msg = method.getName() +
                (not ? " is annotated with " : " is not annotated with ") +
                annotation.orElse(" ");
        assertFalse(msg, annotation.isPresent());
        return chain();
    }

    public MethodAssertions hasArguments(List<TypeDef> params) {
        if(compareTypeLists(method.getGenericParameterTypes(), params, method.getName() + " parameter"))
            if(not)
                Assert.fail(method.getName() + " does not have a different signature than that provided");
        return this;
    }

    public MethodAssertions hasReturnType(TypeDef param) {

        if(compareType(method.getGenericReturnType(), param, method.getName() + " return Type ")) {
            if(not)
                Assert.fail(method.getName() + " has return type " + method.getGenericReturnType().getTypeName());
        }
        return this;
    }


    private Optional<Class<?>> checkArguments(Class<?>[] actual, Class<?>[] expected) {
        for (Class<?> e : expected)
            if (not && Arrays.stream(actual).anyMatch(a -> a.isAssignableFrom(e)))
                return Optional.of(e);
            else if (!not && Arrays.stream(actual).noneMatch(a -> a.isAssignableFrom(e)))
                return Optional.of(e);
        return Optional.empty();
    }

}

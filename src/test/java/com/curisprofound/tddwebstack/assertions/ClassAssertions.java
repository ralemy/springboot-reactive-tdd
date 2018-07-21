package com.curisprofound.tddwebstack.assertions;


import org.junit.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClassAssertions extends Assertions<ClassAssertions> {

    private final Class<?> base;
    private final Type type;

    public ClassAssertions(Class<?> aClass) {
        base = aClass;
        type = null;
    }

    public ClassAssertions(Type aType) {
        base = aType.getClass();
        type = aType;
    }

    public FieldAssertions Field(String propertyName) {
        try {
            Field field = base.getDeclaredField(propertyName);
            if (not)
                Assert.fail("field " + propertyName + " exists in class " + base.getCanonicalName());
            return new FieldAssertions(field);
        } catch (NoSuchFieldException e) {
            if (not)
                return null;
            Assert.fail("field " + propertyName + " does not exist in class " + base.getCanonicalName());
        }
        return null;
    }

    public Stream<MethodAssertions> Methods(String methodName) {
        Method[] methods = base.getMethods();
        return Arrays.stream(methods)
                .filter(m -> m.getName().equals(methodName))
                .map(MethodAssertions::new);
    }

    public ClassAssertions hasMethod(String methodName) {
        Optional<Method> method = Arrays.stream(base.getMethods())
                .filter(m -> m.getName().equals(methodName))
                .findAny();
        if (not)
            assertFalse(base.getName() + " has method called " + methodName, method.isPresent());
        else
            assertTrue(base.getName() + " doesn't have a method called " + methodName, method.isPresent());
        return this;
    }

    public MethodAssertions Method(String methodName, List<TypeDef> params) {
        Class<?>[] args = params.stream().map(p->p.parameterClass).toArray(Class<?>[]::new);
        return Method(methodName, args)
                .hasArguments(params);
    }

    public MethodAssertions Method(String methodName, Class<?>... methodArgs) {
        try {
            Method method = base.getDeclaredMethod(methodName, methodArgs);
            if (not)
                Assert.fail("Method " + methodName + " exists in class " + base.getCanonicalName());
            return new MethodAssertions(method);
        } catch (NoSuchMethodException e) {
            if (not)
                return null;
            Assert.fail("Method " + methodName + " does not exist in class " + base.getCanonicalName());
        }
        return null;
    }

    public ClassAssertions implementsInterface(String interfaceName) {
        String msg = base.getCanonicalName() +
                (not ? " implements " : " does not implement ") +
                interfaceName + " interface";
        Optional<Class<?>> interfaceClass = Arrays.stream(base.getInterfaces())
                .filter(i -> i.getName().contains(interfaceName))
                .findAny();
        if (not)
            assertFalse(msg, interfaceClass.isPresent());
        else
            assertTrue(msg, interfaceClass.isPresent());

        return not ? this : new ClassAssertions(interfaceClass.get());
    }

    public ClassAssertions implementsGenericInterface(String interfaceName) {
        String msg = base.getCanonicalName() +
                (not ? " implements " : " does not implement ") +
                interfaceName + " generic interface";
        Optional<Type> interfaceClass = Arrays.stream(base.getGenericInterfaces())
                .filter(i -> i.getTypeName().contains(interfaceName))
                .findAny();
        if (not)
            assertFalse(msg, interfaceClass.isPresent());
        else
            assertTrue(msg, interfaceClass.isPresent());
        return not ? this : new ClassAssertions(interfaceClass.get());
    }

    public ClassAssertions hasGenericType(String typeName) {
        ParameterizedType pType = type == null ?
                (ParameterizedType) base.getGenericSuperclass() : (ParameterizedType) type;
        boolean actual = Arrays.stream(pType.getActualTypeArguments())
                .anyMatch(s -> s.getTypeName().contains(typeName));
        String msg = (type == null ? type.getTypeName() : base.getCanonicalName()) +
                (not ? " has a generic of type " : " does not have a generic of type ") +
                typeName;
        if (not)
            assertFalse(msg, actual);
        else
            assertTrue(msg, actual);
        return this;
    }

    public ClassAssertions isReadable(String propertyName) {
        String msg = propertyName +
                (not ? " is accessible in class " : " is not accessible in class ") +
                base.getCanonicalName();
        boolean actual = checkReadable(propertyName);
        if (not)
            assertFalse(msg, actual);
        else
            assertTrue(msg, actual);
        return chain();
    }

    public ClassAssertions hasFields(String... propertyNames) {
        for (String c : propertyNames) {
            try {
                base.getDeclaredField(c.trim());
                if (not)
                    Assert.fail(base.getCanonicalName() + " has a field called " + c);
            } catch (NoSuchFieldException e) {
                if (not)
                    continue;
                Assert.fail(base.getCanonicalName() + " doesn't have a field called " + c);
            }
        }
        return chain();
    }

    public ClassAssertions hasAnnotations(String... annotations) {
        Optional<String> annotation = checkAnnotations(base.getAnnotations(), not, annotations);
        String msg = base.getCanonicalName() +
                (not ? " is annotated with " : " is not annotated with ") +
                annotation.orElse("  ");
        assertFalse(msg, annotation.isPresent());
        return chain();
    }


    private boolean checkReadable(String propertyName) {
        try {
            base.getDeclaredMethod("get" + correctCase(propertyName, "method"));
        } catch (NoSuchMethodException e) {
            try {
                base.getDeclaredField(correctCase(propertyName, "field")).get(base.newInstance());
            } catch (Exception e1) {
                return false;
            }
        }
        return true;
    }

    private static String correctCase(String propertyName, String target) {
        return
                (target.equalsIgnoreCase("field") ?
                        propertyName.substring(0, 1).toLowerCase() :
                        propertyName.substring(0, 1).toUpperCase()) +
                        propertyName.substring(1);
    }

}

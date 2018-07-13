package com.curisprofound.tddwebstack.assertions;

import org.junit.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssertOnClass {
    public static ClassAssertions For(String className) throws ClassNotFoundException {
        return new ClassAssertions(Class.forName(className));
    }

    private static String correctCase(String propertyName, String target) {
        return
                (target.equalsIgnoreCase("field") ?
                        propertyName.substring(0, 1).toLowerCase() :
                        propertyName.substring(0, 1).toUpperCase()) +
                        propertyName.substring(1);
    }

    static Optional<String> checkAnnotations(Annotation[] actual, String... expected){
        for(String annotation : expected)
            if(!hasAnnotation(actual, annotation))
                return Optional.of(annotation);
        return Optional.empty();
    }

    private static boolean hasAnnotation(Annotation[] actual, String annotation) {
        return Arrays.stream(actual).anyMatch(
                a -> a.annotationType().getName().contains(annotation)
        );
    }

    public static class FieldAsserstions{
        private final Class<?> base;
        private final Field field;


        FieldAsserstions(Class<?> base, String propertyName) throws NoSuchFieldException {
            this.base = base;
            this.field = getFieldByName(propertyName, base);
        }

        public FieldAsserstions hasAnnotations(String... annotations){
            Optional<String> annotation = checkAnnotations(field.getAnnotations(), annotations);
            assertFalse(
                    field.getName() + " is not annotated with " + annotation.orElseGet(() -> ""),
                    annotation.isPresent()
            );
            return this;
        }

        public FieldAsserstions hasNoAnnotations(){
            assertEquals(
                    0,
                    field.getAnnotations().length
            );
            return this;
        }

        public FieldAsserstions isOfType(Class<?> ref){
            assertTrue(
                    field.getName() + " is not a type of " + ref.getName(),
                    ref.isAssignableFrom(field.getType())
            );
            return this;
        }

        public FieldAsserstions genericArgIsOfType(int index, Class<?> ref){
            Class<?> type = getFieldGenericType(index);
            String message = MessageFormat.format(
                    "Generic Type Argument number {0} of field {1} is not of type {2}. it is type {3}",
                    index, field.getName(), ref.getCanonicalName(), type.getCanonicalName());
            assertTrue(
                    message,
                    ref.isAssignableFrom(type)
            );
            return this;

        }

        private Class<?> getFieldGenericType(int i) {
            return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[i];
        }


        public Annotation[] getAnnotations(){
            return field.getAnnotations();
        }


        private boolean hasAnnotation(String annotation) {
           return Arrays.stream(field.getAnnotations()).anyMatch(
                    a -> a.annotationType().getName().contains(annotation)
            );
        }


        private Field getFieldByName(String propertyName, Class<?> base) throws NoSuchFieldException {
            Field f = base.getDeclaredField(correctCase(propertyName, "field"));
            f.setAccessible(true);
            return f;
        }

        private Field getFieldByName(String propertyName, String className) throws ClassNotFoundException, NoSuchFieldException {
            return getFieldByName(propertyName, Class.forName(className));
        }
    }


    public static class ClassAssertions{

        private final Class<?> base;
        ClassAssertions(Class<?> aClass) {
            base = aClass;
        }

        public ClassAssertions isReadable(String propertyName){
            assertTrue(
                    propertyName + " is not accessible in class " + base.getCanonicalName(),
                    checkReadable(propertyName)
            );
            return this;
        }
        public ClassAssertions isNotReadable(String propertyName){
            assertFalse(
                    propertyName + " is accessible in class " + base.getCanonicalName(),
                    checkReadable(propertyName)
            );
            return this;
        }

        public boolean checkReadable(String propertyName){
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

        public FieldAsserstions Field(String propertyName) throws NoSuchFieldException {
            return new FieldAsserstions(base, propertyName);
        }

        public Annotation[] getAnnotations(){
            return base.getAnnotations();
        }

        public ClassAssertions hasFields(String... propertyNames){
            for (String c : propertyNames) {
                try {
                    base.getDeclaredField(c.trim());
                } catch (NoSuchFieldException e) {
                    Assert.fail("Class doesn't have a field called " + c);
                }
            }
            return this;
        }

        public ClassAssertions hasAnnotations(String... annotations){
            Optional<String> annotation = checkAnnotations(base.getAnnotations(), annotations);
            assertFalse(
                    base.getCanonicalName() + " is not annotated with " + annotation.orElseGet(() -> ""),
                    annotation.isPresent()
            );
            return this;
        }

    }

}


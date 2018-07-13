package com.curisprofound.tddwebstack.assertions;

import org.junit.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

public class AssertOnClass {
    public static ClassAssertions For(String className) throws ClassNotFoundException {
        return new ClassAssertions(Class.forName(className));
    }


    public static class ClassAssertions extends Assertions<ClassAssertions> {

        private final Class<?> base;

        ClassAssertions(Class<?> aClass) {
            base = aClass;
        }

        public FieldAssertions Field(String propertyName) throws NoSuchFieldException {

            return new FieldAssertions(getFieldByName(propertyName));
        }


        public ClassAssertions isReadable(String propertyName) {
            String msg = propertyName +
                    (not ? "is accessible in class" : " is not accessible in class ") +
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
                        Assert.fail("Class has a field called " + c);
                } catch (NoSuchFieldException e) {
                    if (not)
                        continue;
                    Assert.fail("Class doesn't have a field called " + c);
                }
            }
            return chain();
        }

        public ClassAssertions hasAnnotations(String... annotations) {
            Optional<String> annotation = checkAnnotations(base.getAnnotations(), not, annotations);
            String msg = base.getCanonicalName() +
                    (not ? "is annotated with" : " is not annotated with ") +
                    annotation.orElse("  ");
            assertFalse(msg, annotation.isPresent());
            return chain();
        }

        public Annotation[] getAnnotations() {
            return base.getAnnotations();
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

        static Optional<String> checkAnnotations(Annotation[] actual, boolean not, String... expected) {
            for (String annotation : expected)
                if (not && hasAnnotation(actual, annotation))
                    return Optional.of(annotation);
                else if (!not && !hasAnnotation(actual, annotation))
                    return Optional.of(annotation);
            return Optional.empty();
        }

        private static boolean hasAnnotation(Annotation[] actual, String annotation) {
            return Arrays.stream(actual).anyMatch(
                    a -> a.annotationType().getName().contains(annotation)
            );
        }

        private Field getFieldByName(String propertyName) throws NoSuchFieldException {
            Field f = base.getDeclaredField(correctCase(propertyName, "field"));
            f.setAccessible(true);
            return f;
        }

        public static class FieldAssertions extends Assertions<FieldAssertions> {
            private final Field field;

            FieldAssertions(Field field) throws NoSuchFieldException {
                this.field = field;
            }

            public AnnotationAssertions Annotation(String name) {
                Optional<Annotation> match = Arrays
                        .stream(field.getAnnotations())
                        .filter(a -> a.annotationType().getName().contains(name))
                        .findAny();
                if (!match.isPresent())
                    Assert.fail(field.getName() + " has no annotation by name of " + name);
                return new AnnotationAssertions(match.get());
            }

            public FieldAssertions hasAnnotations(String... annotations) {

                Optional<String> annotation = checkAnnotations(field.getAnnotations(), not, annotations);
                String msg = field.getName() +
                        (not ? " is annotated with " : " is not annotated with ") +
                        annotation.orElse(" ");
                assertFalse(msg, annotation.isPresent());
                return chain();
            }

            public FieldAssertions hasAnnotations() {
                if (not)
                    assertEquals(0, field.getAnnotations().length);
                else
                    assertThat(
                            field.getAnnotations().length,
                            greaterThan(0)
                    );
                return chain();
            }

            public FieldAssertions isOfType(Class<?> ref) {
                String msg = field.getName() +
                        (not ? " is type of " : " is not a type of ") +
                        ref.getName();

                if (not)
                    assertFalse(msg, ref.isAssignableFrom(field.getType()));
                else
                    assertTrue(msg, ref.isAssignableFrom(field.getType()));
                return chain();
            }

            public FieldAssertions genericArgIsOfType(int index, Class<?> ref) {
                Class<?> type = getFieldGenericType(index);
                String msg = MessageFormat.format(
                        (not ? "Generic Type Argument number {0} of field {1} is of type {2}" :
                                "Generic Type Argument number {0} of field {1} is not of type {2}. it is type {3}"),
                        index, field.getName(), ref.getCanonicalName(), type.getCanonicalName());
                if (not)
                    assertFalse(msg, ref.isAssignableFrom(type));
                else
                    assertTrue(msg, ref.isAssignableFrom(type));
                return chain();

            }

            public Annotation[] getAnnotations() {
                return field.getAnnotations();
            }

            private Class<?> getFieldGenericType(int i) {
                return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[i];
            }


            public static class AnnotationAssertions extends Assertions<AnnotationAssertions> {

                private final Annotation annotation;
                private final String name;

                public AnnotationAssertions(Annotation annotation) {
                    this.annotation = annotation;
                    this.name = annotation.annotationType().getCanonicalName();
                }

                public AnnotationAssertions paramHasValue(String param, String value) {
                    try {
                        Method method = annotation.getClass().getDeclaredMethod(param);
                        String actual = (String) method.invoke(annotation);
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
        }
    }
}


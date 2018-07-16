package com.curisprofound.tddwebstack.assertions;

import org.junit.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

public class AssertOnClass {
    public static ClassAssertions For(String className) throws ClassNotFoundException {
        return new ClassAssertions(Class.forName(className));
    }

    public static ClassAssertions For(Class<?> clazz) throws ClassNotFoundException {
        return new ClassAssertions(clazz);
    }


    public static class ClassAssertions extends Assertions<ClassAssertions> {

        private final Class<?> base;
        private final Type type;

        ClassAssertions(Class<?> aClass) {
            base = aClass;
            type = null;
        }

        ClassAssertions(Type aType) {
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

        public MethodAssertions Method(String methodName, Class<?>... methodArgs) {
            try {
                Method method = base.getDeclaredMethod(methodName,methodArgs);
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

        static AnnotationAssertions getAnnotation(Annotation[] annotations, String className, String name) {
            Optional<Annotation> match = Arrays
                    .stream(annotations)
                    .filter(a -> a.annotationType().getName().contains(name))
                    .findAny();
            if (!match.isPresent())
                Assert.fail(className + " has no annotation by name of " + name);
            return new AnnotationAssertions(match.get());
        }

        private static boolean hasAnnotation(Annotation[] actual, String annotation) {
            return Arrays.stream(actual).anyMatch(
                    a -> a.annotationType().getName().contains(annotation)
            );
        }

        public static class MethodAssertions extends Assertions<MethodAssertions> {

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

            public MethodAssertions hasArguments(Class<?>... methodArgs) {
                Class<?>[] actual = method.getParameterTypes();
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

        public static class FieldAssertions extends Assertions<FieldAssertions> {
            private final Field field;

            FieldAssertions(Field field) {
                this.field = field;
            }

            public FieldAssertions exists() {
                return this;
            }

            public Object getValue(Object instance) throws IllegalAccessException {
                field.setAccessible(true);
                return field.get(instance);
            }

            ;

            public AnnotationAssertions Annotation(String name) {
                return getAnnotation(field.getAnnotations(), field.getName(), name);
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
                    assertTrue(
                            msg + " it is of type " + field.getType().getCanonicalName(),
                            ref.isAssignableFrom(field.getType()));
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

    }
}


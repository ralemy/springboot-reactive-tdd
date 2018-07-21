package com.curisprofound.tddwebstack.assertions;

import org.junit.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class Assertions<T extends Assertions<T>> {
    protected boolean not = false;

    public T Not() {
        not = true;
        return (T) this;
    }

    protected T chain() {
        not = false;
        return (T) this;
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

    static AnnotationAssertions getAnnotation(Annotation[] annotations, String parentName, String name) {
        Optional<Annotation> match = Arrays
                .stream(annotations)
                .filter(a -> a.annotationType().getName().contains(name))
                .findAny();
        if (!match.isPresent())
            Assert.fail(parentName + " has no annotation by name of " + name);
        return new AnnotationAssertions(match.get());
    }

    public void countShouldBeEqual(int length, String targetName, int size) {
        if (length != size)
            if (!not)
                Assert.fail("Wrong Count of Type Arguments for " + targetName);
    }

    public Class<?> getParentClass(Type typeArgument) {
        Class<?> target = typeArgument.getClass();

        if (ParameterizedType.class.isAssignableFrom(target))
            return (Class<?>) ((ParameterizedType) typeArgument).getRawType();
        if (WildcardType.class.isAssignableFrom(target))
            return (Class<?>) (
                    ((WildcardType) typeArgument).getUpperBounds().length > 0 ?
                            ((WildcardType) typeArgument).getUpperBounds()[0] :
                            ((WildcardType) typeArgument).getLowerBounds()[0]
            );
        return (Class<?>) typeArgument;
    }

    public boolean compareBaseType(Type actual, TypeDef expected) {
        if (ParameterizedType.class.isAssignableFrom(actual.getClass()))
            return compareParametrizedBase((ParameterizedType) actual, expected);
        if (WildcardType.class.isAssignableFrom(actual.getClass()))
            return compareWildType((WildcardType) actual, expected);
        if (expected.category.equals("simple"))
            return actual.getTypeName().equals(expected.parameterClass.getTypeName());
        return false;
    }

    private boolean compareWildType(WildcardType actual, TypeDef expected) {
        if (!expected.category.equals("wildcard"))
            return false;
        if (expected.bound.equals("lower"))
            return (actual.getLowerBounds().length > 0 &&
                    actual.getLowerBounds()[0].getTypeName().equals(expected.parameterClass.getTypeName()));
        return (actual.getUpperBounds().length > 0 &&
                actual.getUpperBounds()[0].getTypeName().equals(expected.parameterClass.getTypeName()));
    }

    private boolean compareParametrizedBase(ParameterizedType actual, TypeDef expected) {
        if (expected.category.equals("generic"))
            return (actual.getRawType().getTypeName().startsWith(expected.parameterClass.getTypeName()));
        return false;
    }

    public boolean compareType(Type actual, TypeDef expected, String containerName) {
        if (compareBaseType(actual, expected))
            return sameGenericParams(actual, expected, containerName);
        if (!not)
            Assert.fail(containerName +
                    " is of type " + actual.getTypeName() +
                    " expected: " + expected.typeName());
        return false;
    }

    private boolean sameGenericParams(Type actual, TypeDef expected, String containerName) {
        if (expected.genericTypes.size() > 0)
            if (ParameterizedType.class.isAssignableFrom(actual.getClass()))
                return compareTypeLists(
                        ((ParameterizedType) actual).getActualTypeArguments(),
                        expected.genericTypes,
                        containerName + "==>\n" + actual.getTypeName());
            else
                return expectedNotGeneric(actual, expected, containerName);
        return true;
    }

    private boolean expectedNotGeneric(Type actual, TypeDef expected, String containerName) {
        if (!not)
            Assert.fail(containerName + "." + actual.getTypeName() + " is not a Generic type");
        return false;
    }

    private boolean countsAreSame(int actual, int expected, String containerName) {
        if (actual == expected)
            return true;
        if (!not)
            Assert.fail(containerName + " has " + actual + " argument(s): expected " + expected);
        return false;
    }

    public boolean compareTypeLists(Type[] actual, List<TypeDef> expected, String containerName) {
        if (!countsAreSame(actual.length, expected.size(), containerName))
            return false;
        String[] sections = containerName.split("==>\n");
        containerName = sections[0] + (sections.length > 1 ? "==>\n" +sections[sections.length - 1] : "");
        for (int i = 0; i < actual.length; i++)
            if (!compareType(actual[i], expected.get(i), containerName + "(arg " + (i + 1) + ")"))
                return false;
        return true;
    }


}

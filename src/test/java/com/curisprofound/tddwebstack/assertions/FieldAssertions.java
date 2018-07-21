package com.curisprofound.tddwebstack.assertions;

import org.junit.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FieldAssertions extends Assertions<FieldAssertions> {
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

    public FieldAssertions isOfComplexType(TypeDef p){
        if(compareType(field.getGenericType(), p, field.getName()))
           if(not)
               Assert.fail(field.getName() + " is of type " + field.getGenericType().getTypeName());
        return this;
    }

    private void examineGenerics(List<TypeDef> expectedTypes, Type genericType) {
        shouldBeParameterized(genericType);
        Type[] typeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
        countShouldBeEqual(typeArguments.length, genericType.getTypeName(), expectedTypes.size());

        for(int i=0 ; i<typeArguments.length ;i++)
            examineGenericArg(expectedTypes.get(i), typeArguments[i]);
    }

    private void examineGenericArg(TypeDef typeDef, Type typeArgument) {

        isOfType(typeDef.parameterClass, "Generic Type" ,  getParentClass(typeArgument));

        if(typeDef.genericTypes.size()>0)
            examineGenerics(typeDef.genericTypes, typeArgument);
    }


    private void shouldBeParameterized(Type genericType) {
        if(!ParameterizedType.class.isAssignableFrom(genericType.getClass()))
            if(!not)
                Assert.fail(genericType.getTypeName() + " Does not have any generic types defined");
    }



    public FieldAssertions isOfType(Class<?> ref) {

        isOfType(ref, field.getName(), field.getType());
        return chain();
    }


    public FieldAssertions isOfType(Class<?> ref, String fieldName, Class<?> fieldClass) {
        String msg = fieldName +
                (not ? " is type of " : " is not a type of ") +
                ref.getName();

        if (not)
            assertFalse(msg, ref.isAssignableFrom(fieldClass));
        else
            assertTrue(
                    msg + " it is of type " + fieldClass.getCanonicalName(),
                    ref.isAssignableFrom(fieldClass));
        return this;
    }

    public FieldAssertions genericArgIsOfType(int index, Class<?> ref) {
        Class<?> type = getFieldGenericType(index, field.getGenericType());
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

    private Class<?> getFieldGenericType(int i, Type genericType) {
        Type type = ((ParameterizedType) genericType).getActualTypeArguments()[i];
        return (Class<?>) (ParameterizedType.class.isAssignableFrom(type.getClass()) ?
                        ((ParameterizedType) type).getRawType() : type);
    }

}

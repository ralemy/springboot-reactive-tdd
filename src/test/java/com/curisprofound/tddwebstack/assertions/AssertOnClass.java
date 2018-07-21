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


}


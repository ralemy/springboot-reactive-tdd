package com.curisprofound.tddwebstack.assertions;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeDef {
    public String category = "simple";
    public Class<?> parameterClass;
    public String bound = "exact";
    public List<TypeDef> genericTypes = new ArrayList<>();
    int endCursor;

    public static Map<String, Class<?>> classNames = new HashMap<>();

    public TypeDef(Class<?> clazz) {
        parameterClass = clazz;
    }

    public TypeDef(String className, int cursor, String category) {
        this.category = category;
        parameterClass = nameToClass(className);
        endCursor = cursor;
        setBound(className);

    }

    public TypeDef(String className, List<TypeDef> parameters, int cursor, String category) {
        this.category = category;
        parameterClass = nameToClass(className);
        genericTypes = parameters;
        endCursor = cursor + parameters.stream().map(p -> p.endCursor).reduce(0, (a, p) -> p);
        setBound(className);
    }


    private void setBound(String className) {
        if (className.startsWith("?"))
            category = "wildcard";
        if (className.contains("extends"))
            bound = "upper";
        else if (className.contains("super"))
            bound = "lower";
        else
            bound = "any";
    }


    public static void setClassNames(Map<String, Class<?>> classNames) {
        TypeDef.classNames = classNames;
    }

    public static void addClassNames(Map<String, Class<?>> classNames) {
        TypeDef.classNames.putAll(classNames);
    }

    public static void setClassNames() {

        classNames.put("String", String.class);
        classNames.put("Integer", Integer.class);
        classNames.put("Object", Object.class);
        classNames.put("Class", Class.class);
        classNames.put("?", Class.class);
        classNames.put("Map", Map.class);
        classNames.put("List", List.class);
        classNames.put("IllegalArgumentException", IllegalArgumentException.class);
        classNames.put("RuntimeException", RuntimeException.class);

    }

    private static List<TypeDef> parseParameter(String signature) {
        signature = signature.replaceAll("\\s+", "");
        List<TypeDef> parameters = new ArrayList<>();
        StringBuilder name = new StringBuilder();
        int i = 0;
        for (i = 0; i < signature.length(); i++)
            if (signature.substring(i, i + 1).equalsIgnoreCase(","))
                name = addSibling(parameters, name.toString(), i + 1);
            else if (signature.substring(i, i + 1).equalsIgnoreCase("<")) {
                i = addGenericTypes(parameters, name.toString(), signature.substring(i + 1), i);
                name = new StringBuilder();
            } else if (signature.substring(i, i + 1).equalsIgnoreCase(">")) {
                i += 1;
                break;
            } else
                name.append(signature.substring(i, i + 1));

        addSibling(parameters, name.toString(), i);
        if (parameters.size() > 0)
            parameters.get(parameters.size() - 1).endCursor = i;
        return parameters;
    }


    public static Class<?> nameToClass(String s) {
        try {
            if (s.startsWith("?"))
                if (s.contains("extends"))
                    s = s.split("extends")[1];
                else if (s.contains("super"))
                    s = s.split("super")[1];
            return classNames.containsKey(s) ? classNames.get(s) : Class.forName(s);
        } catch (ClassNotFoundException e) {
            Assert.fail("Class Not found: " + s);
        }
        return null;
    }


    private static int addGenericTypes(List<TypeDef> parameters, String s, String substring, int cursor) {
        if (s.trim().isEmpty()) return cursor;
        TypeDef parent = new TypeDef(s, parseParameter(substring), cursor, "generic");
        parameters.add(parent);
        return parent.endCursor;
    }

    private static StringBuilder addSibling(List<TypeDef> parameters, String s, int cursor) {
        if (s.trim().isEmpty()) return new StringBuilder();
        TypeDef sibling = new TypeDef(s, cursor, "simple");
        parameters.add(sibling);
        return new StringBuilder();
    }

    public static List<TypeDef> parse(String signature) throws Exception {
        String template = "x" + signature + "x";
        int lessThan = template.split("<").length;
        int greaterThan = template.split(">").length;
        if (lessThan < greaterThan)
            throw new Exception("missing `<`: " + signature);
        else if (greaterThan < lessThan)
            throw new Exception("missing `>`: " + signature);
        return parseParameter(signature.replaceAll("\\s+", ""));
    }

    public String typeName() {
        if (category.equals("wildcard"))
            return boundName();
        if (category.equals("generic"))
            return parameterClass.getTypeName() + "<" +
                    genericTypes.stream()
                            .map(TypeDef::typeName)
                            .reduce("", ((a, t) -> a + "," + t))
                            .substring(1) + ">";
        return parameterClass.getTypeName();
    }

    private String boundName() {
        if (bound.equals("any"))
            return "?";
        return (bound.equals("lower") ?
                "? super " : "? extends ")
                + parameterClass.getTypeName();
    }
}

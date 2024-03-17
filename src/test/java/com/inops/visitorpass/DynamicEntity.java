package com.inops.visitorpass;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class DynamicEntity {

    public static void main(String[] args) {
        try {
            // Create a dynamic entity class
            Class<?> dynamicEntityClass = createDynamicEntityClass("DynamicEntity");

            // Create an instance of the dynamic entity
            Object dynamicEntity = dynamicEntityClass.getDeclaredConstructor().newInstance();

            // Set fields dynamically
            setField(dynamicEntity, "id", 1L);
            setField(dynamicEntity, "name", "DynamicEntityName");

            // Use the dynamic entity
            System.out.println("Dynamic Entity ID: " + getField(dynamicEntity, "id"));
            System.out.println("Dynamic Entity Name: " + getField(dynamicEntity, "name"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Class<?> createDynamicEntityClass(String className) throws Exception {
        // Create a simple dynamic entity class with an @Entity annotation
        StringBuilder classSource = new StringBuilder();
        classSource.append("public class ").append(className).append(" {\n");
        classSource.append("    private Long id;\n");
        classSource.append("    private String name;\n");
        classSource.append("}\n");

        // Use a dynamic class loader to load the dynamically generated class
        DynamicClassLoader dynamicClassLoader = new DynamicClassLoader();
        return dynamicClassLoader.loadClass(className, classSource.toString());
    }

    private static void setField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    private static Object getField(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    private static class DynamicClassLoader extends ClassLoader {
        public Class<?> loadClass(String name, String source) {
            byte[] byteCode = source.getBytes();
            return defineClass(name, byteCode, 0, byteCode.length);
        }
    }
}

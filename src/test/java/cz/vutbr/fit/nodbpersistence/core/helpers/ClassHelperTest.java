package cz.vutbr.fit.nodbpersistence.core.helpers;

import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static org.junit.Assert.*;

public class ClassHelperTest {

    @Test
    public void testPrimitiveOrWrapper() {
        assertTrue(ClassHelper.isSimpleValueType(int.class));
        assertTrue(ClassHelper.isSimpleValueType(Integer.class));
        assertTrue(ClassHelper.isSimpleValueType(float.class));
        assertTrue(ClassHelper.isSimpleValueType(Float.class));
        assertTrue(ClassHelper.isSimpleValueType(double.class));
        assertTrue(ClassHelper.isSimpleValueType(Double.class));
        assertTrue(ClassHelper.isSimpleValueType(boolean.class));
        assertTrue(ClassHelper.isSimpleValueType(Boolean.class));
        assertTrue(ClassHelper.isSimpleValueType(short.class));
        assertTrue(ClassHelper.isSimpleValueType(Short.class));
        assertTrue(ClassHelper.isSimpleValueType(byte.class));
        assertTrue(ClassHelper.isSimpleValueType(Byte.class));
        assertTrue(ClassHelper.isSimpleValueType(long.class));
        assertTrue(ClassHelper.isSimpleValueType(Long.class));

        assertFalse(ClassHelper.isPrimitiveOrWrapper(String.class));
    }

    @Test
    public void testSimpleValueType() {
        assertTrue(ClassHelper.isSimpleValueType(String.class));
        assertTrue(ClassHelper.isSimpleValueType(Enum.class));
        assertTrue(ClassHelper.isSimpleValueType(CharSequence.class));
        assertTrue(ClassHelper.isSimpleValueType(Number.class));
        assertTrue(ClassHelper.isSimpleValueType(String.class));
        assertTrue(ClassHelper.isSimpleValueType(Date.class));
        assertTrue(ClassHelper.isSimpleValueType(URI.class));
        assertTrue(ClassHelper.isSimpleValueType(Locale.class));

        assertFalse(ClassHelper.isSimpleValueType(HashMap.class));
    }

    @Test
    public void testSetFieldValueStatic() throws ReflectiveOperationException {
        ClassHelperTestClass testObject = new ClassHelperTestClass();
        Class<?> testClass = ClassHelperTestClass.class;
        Field testStaticField = testClass.getDeclaredField("testStatic");
        ClassHelper.setFieldValue(testStaticField, testObject, "newTestStatic");
        assertEquals("newTestStatic", ClassHelperTestClass.testStatic);
    }

    @Test
    public void testSetFieldValueFinalStatic() throws ReflectiveOperationException {
        ClassHelperTestClass testObject = new ClassHelperTestClass();
        Class<?> testClass = ClassHelperTestClass.class;
        Field testFinalStaticField = testClass.getDeclaredField("testFinalStatic");
        ClassHelper.setFieldValue(testFinalStaticField, testObject, 2L);
        assertEquals(Long.valueOf(2L), ClassHelperTestClass.testFinalStatic);
    }

    @Test
    public void testSetLong() throws ReflectiveOperationException {
        ClassHelperTestClass testObject = new ClassHelperTestClass();
        Class<?> testClass = ClassHelperTestClass.class;
        Field testNumber = testClass.getDeclaredField("number");
        ClassHelper.setFieldValue(testNumber, testObject, 2L);
        assertEquals(Long.valueOf(2L), testObject.getNumber());
    }

    @Test
    public void testGetFieldValueStatic() throws ReflectiveOperationException {
        ClassHelperTestClass testObject = new ClassHelperTestClass();
        Class<?> testClass = ClassHelperTestClass.class;
        Field testStaticField = testClass.getDeclaredField("testStatic");
        String finalStatic = (String) ClassHelper.getFieldValue(testStaticField, testObject);
        assertEquals("testStatic", finalStatic);
    }

    @Test
    public void testGetFieldValueStaticFinal() throws ReflectiveOperationException {
        ClassHelperTestClass testObject = new ClassHelperTestClass();
        Class<?> testClass = ClassHelperTestClass.class;
        Field testStaticField = testClass.getDeclaredField("testFinalStatic");
        Long finalStatic = (Long) ClassHelper.getFieldValue(testStaticField, testObject);
        assertEquals(Long.valueOf(1L), finalStatic);
    }

}
package cz.vutbr.fit.nodbpersistence.core.helpers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConvertStringToTypeTest {

    @Test
    public void testConvertInt() {
        String value = "1";
        int resultint = (int) ConvertStringToType.convertStringToType(int.class, value);
        assertEquals(resultint, 1);
        Integer resultInt = (Integer) ConvertStringToType.convertStringToType(Integer.class, value);
        assertEquals(resultInt, Integer.valueOf(1));
    }

}
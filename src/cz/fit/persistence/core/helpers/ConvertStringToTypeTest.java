package cz.fit.persistence.core.helpers;

import cz.fit.persistence.exceptions.PersistenceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConvertStringToTypeTest {

    @Test
    void convertString() {
        String testString = "hello";
        assertEquals(ConvertStringToType.convertStringToType(String.class,testString),testString);
        assertThrows(PersistenceException.class,() -> ConvertStringToType.convertStringToType(Integer.class,testString));
    }

    @Test
    void convertDouble() {
        double f = 1.0;
        String fString = String.valueOf(f);
        assertEquals((double) ConvertStringToType.convertStringToType(double.class, fString), f);
        assertThrows(PersistenceException.class,() -> ConvertStringToType.convertStringToType(Integer.class,fString));

        Double fWrapped = f;
        String fString1 = fWrapped.toString();

        assertEquals(ConvertStringToType.convertStringToType(Double.class, fString1), fWrapped);
    }

    @Test
    void convertFloat() {
        float f = 1.2323f;
        String fString = String.valueOf(f);
        assertEquals((float) ConvertStringToType.convertStringToType(float.class, fString), f);
        assertThrows(PersistenceException.class,() -> ConvertStringToType.convertStringToType(Integer.class,fString));

        Float fWrapped = f;
        String fString1 = fWrapped.toString();

        assertEquals(ConvertStringToType.convertStringToType(Float.class, fString1), fWrapped);
    }
}
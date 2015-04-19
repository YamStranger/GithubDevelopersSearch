package com.util;

import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;


/**
 * User: YamStranger
 * Date: 4/14/15
 * Time: 10:38 AM
 */
public class ArgumentsParserTest {

    @Test
    public void init_validParameters_proceed() {
        final String[] arguments = {"-key=value", "-key2=value2", "-illa=123"};
        final ArgumentsParser parser = new ArgumentsParser(arguments);
        final Map<String, String> values = parser.arguments();
        assertTrue(values.containsKey("key"));
        assertEquals(values.get("key"), "value");
        assertTrue(values.containsKey("key2"));
        assertEquals(values.get("key2"), "value2");
        assertTrue(values.containsKey("illa"));
        assertEquals(values.get("illa"), "123");
    }

    @Test
    public void init_parameterWithSpaces_proceed() {
        final String[] arguments = {"-key=value -key2=value2 -illa=123"};
        final ArgumentsParser parser = new ArgumentsParser(arguments);
        final Map<String, String> values = parser.arguments();
        assertTrue(values.containsKey("key"));
        assertEquals(values.get("key"), "value");
        assertFalse(values.containsKey("key2"));
        assertNull(values.get("key2"));
        assertFalse(values.containsKey("illa"));
        assertNull(values.get("illa"));
    }
}

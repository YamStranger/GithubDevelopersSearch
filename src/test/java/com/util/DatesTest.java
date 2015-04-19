package com.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Calendar;

/**
 * User: YamStranger
 * Date: 4/15/15
 * Time: 12:59 AM
 */
public class DatesTest {
    @Test
    public void diff_GMT_UPC_proceed() {
        Dates start = new Dates("2011-12-16T10:35:28Z");
        Dates end = new Dates("2011-12-16T11:35:28Z");
        Assert.assertEquals(new Dates(start.calendar()).difference(end.calendar(), Calendar.HOUR), -1);
        start = new Dates("2011-12-16T11:37:28Z");
        end = new Dates("2011-12-16T11:35:28Z");
        Assert.assertEquals(new Dates(start.calendar()).difference(end.calendar(), Calendar.MINUTE), 2);
    }
}

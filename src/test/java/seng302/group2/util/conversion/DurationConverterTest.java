package seng302.group2.util.conversion;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Shinobu on 26/08/2015.
 */
public class DurationConverterTest {
    @Test
    public void testReadDurationToMinutes() throws Exception {
        String test = "1h30min";
        String test2 = "1h30m";
        String test3 = "30min";
        String test4 = "2h";
        String test6 = "2h5min";
        String test7 = "2hours 5min";
        String test8 = "2 hours 10 mins";
        String test9 = "     2 hour     5 m  ";
        String test10 = "5";
        String test11 = "1.1";
        String test12 = "0";
        String test13 = "1.25 hours";

        Assert.assertEquals((Double) 90.0, DurationConverter.readDurationToMinutes(test));
        Assert.assertEquals((Double) 90.0, DurationConverter.readDurationToMinutes(test2));
        Assert.assertEquals((Double) 30.0, DurationConverter.readDurationToMinutes(test3));
        Assert.assertEquals((Double) 120.0, DurationConverter.readDurationToMinutes(test4));
        Assert.assertEquals((Double) 125.0, DurationConverter.readDurationToMinutes(test6));
        Assert.assertEquals((Double) 125.0, DurationConverter.readDurationToMinutes(test7));
        Assert.assertEquals((Double) 130.0, DurationConverter.readDurationToMinutes(test8));
        Assert.assertEquals((Double) 125.0, DurationConverter.readDurationToMinutes(test9));
        Assert.assertEquals((Double) 300.0, DurationConverter.readDurationToMinutes(test10));
        Assert.assertEquals((Double) 66.0, DurationConverter.readDurationToMinutes(test11));
        Assert.assertEquals((Double) 0.0, DurationConverter.readDurationToMinutes(test12));
        Assert.assertEquals((Double) 75.0, DurationConverter.readDurationToMinutes(test13));
    }
}

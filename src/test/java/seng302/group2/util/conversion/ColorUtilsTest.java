package seng302.group2.util.conversion;

import javafx.scene.paint.Color;
import org.junit.Assert;
import org.junit.Test;

public class ColorUtilsTest {

    @Test
    public void testToRGBCode() throws Exception {
        Assert.assertEquals("#FF0000FF", ColorUtils.toRGBCode(Color.RED));
        Assert.assertEquals("#008000FF", ColorUtils.toRGBCode(Color.GREEN));
        Assert.assertEquals("#0000FFFF", ColorUtils.toRGBCode(Color.BLUE));
        Assert.assertEquals("#00000000", ColorUtils.toRGBCode(Color.TRANSPARENT));
    }

    @Test
    public void testToColor() throws Exception {
        Assert.assertEquals(Color.RED, ColorUtils.toColor("#FF0000FF"));
        Assert.assertEquals(Color.GREEN, ColorUtils.toColor("#008000FF"));
        Assert.assertEquals(Color.BLUE, ColorUtils.toColor("#0000FFFF"));
        Assert.assertEquals(Color.TRANSPARENT, ColorUtils.toColor("#00000000"));
    }
}
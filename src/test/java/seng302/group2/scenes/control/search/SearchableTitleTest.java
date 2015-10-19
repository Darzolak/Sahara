package seng302.group2.scenes.control.search;

import javafx.scene.text.Text;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import seng302.group2.scenes.JavaFxTestApp;

/**
 * Created by Bronson on 24/08/2015.
 */
public class SearchableTitleTest extends TestCase {

    @BeforeClass
    public static void initJFX() {
        JavaFxTestApp.initJFX();
    }

    @Test
    public void testSetText() throws Exception {
        SearchableTitle sTitle = new SearchableTitle("title text");

        // Check the first text flow is the text we constructed with.
        Assert.assertEquals("title text", ((Text) sTitle.texts.get(0).getChildren().get(0)).getText());

        sTitle.setText("another title text");
        Assert.assertEquals("another title text", ((Text) sTitle.texts.get(0).getChildren().get(0)).getText());
        Assert.assertEquals("another title text", sTitle.getText());
    }

    @Test
    public void testSetAndUpdateFlow() throws Exception {
        SearchableTitle sTitle = new SearchableTitle("title text");
        // Invalidate our use of the searchable text by adding new stuff that isn't searchable
        //sText.getChildren().addAll(new Label("stuff"), new Text("that shouldn't be here"), new Label());
        sTitle.setText("some other text");

        sTitle.updateFlow();  // Update the text from the actual strings we use through the set

        Assert.assertEquals(1, sTitle.getChildren().size());
        Assert.assertEquals("some other text", ((Text)sTitle.texts.get(0).getChildren().get(0)).getText());
    }

    @Test
    public void testQueryBasic() throws Exception {
        SearchableTitle sTitle = new SearchableTitle("title text");

        Assert.assertFalse(sTitle.query(""));
        Assert.assertFalse(sTitle.query("z"));
        Assert.assertTrue(sTitle.query("TiTLe tEx"));
        Assert.assertTrue(sTitle.query("i"));
    }

    public void testQueryIntermediate() throws Exception {
        SearchableTitle sTitle = new SearchableTitle("title text");
        Assert.assertEquals(1, sTitle.getChildren().size());

        Assert.assertTrue(sTitle.query("text"));
        Assert.assertEquals(3, sTitle.getChildren().size());
        Assert.assertEquals("title ", ((Text) sTitle.texts.get(0).getChildren().get(0)).getText());
        Assert.assertEquals("text", ((Text) sTitle.texts.get(1).getChildren().get(0)).getText());
        Assert.assertEquals("", ((Text) sTitle.texts.get(2).getChildren().get(0)).getText());

        Assert.assertTrue(sTitle.texts.get(1).getStyle().contains(
                "-fx-background-color:" + SearchableControl.highlightColourString));
    }
}
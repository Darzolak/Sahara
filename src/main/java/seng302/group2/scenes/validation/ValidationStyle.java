package seng302.group2.scenes.validation;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver;
import seng302.group2.scenes.control.search.SearchableControl;

/**
 * A small validation style class used for reusable styling on different nodes for consistency
 * Created by Jordane on 24/08/2015.
 */
public class ValidationStyle {

    /**
     * Adds a smooth, red border glow effect on the node, generally used to show an invalid state
     *
     * @param node The node to apply the glow effect to
     */
    public static void borderGlowRed(Node node) {
        node.setStyle(node.getStyle() + " -fx-effect: dropshadow(three-pass-box, rgba(255,0,0,1.0), 8, 0, 0, 0 );");
    }

    /**
     * Adds a smooth, red border glow effect on the node, generally used to show an invalid state
     *
     * @param node The node to apply the glow effect to
     */
    public static void borderGlowSearch(Node node) {
        borderGlow(node, SearchableControl.highlightColour);
    }

    /**
     * Adds a transparent glow effect on the node, generally used to stop showing any other flow effect
     *
     * @param node The node to apply the glow effect to
     */
    public static void borderGlowNone(Node node) {
        node.setStyle(node.getStyle() + " -fx-effect: dropshadow(three-pass-box ,rgba(0,0,0,0.0), 8, 0.0, 0, 1);");
    }

    /**
     * Adds a custom coloured glow effect on the node with the given Color. Red, Green, Blue, and Alpha channels of the
     * Color are used
     *
     * @param node  The node to apply the glow effect to
     * @param color The color of the glow effect
     */
    public static void borderGlow(Node node, Color color) {
        node.setStyle(node.getStyle() + " -fx-effect: dropshadow( three-pass-box , rgba("
                + color.getRed() * 255 + ", "
                + color.getGreen() * 255 + ", "
                + color.getBlue() * 255 + ", "
                + color.getOpacity()
                + ") , 8, 0.0 , 0 , 1 );");
    }


    /**
     * Shows a PopOver message containing the given message on the given node
     *
     * @param message The message to display
     * @param node    The node to show the PopOver on
     */
    public static void showMessage(String message, Node node) {
        PopOver po = new PopOver();
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(8));
        box.getChildren().add(new Text(message));
        po.setContentNode(box);
        po.setDetachable(false);
        po.setAutoHide(true);

        Platform.runLater(() -> po.show(node));

        node.onMouseClickedProperty().addListener((observable, oldValue, newValue) -> {
            po.hide();
        });

        timeoutHide(po);
    }


    /**
     * Shows a PopOver message containing the given message on the given text field
     *
     * @param message The message to display
     * @param node    The text field to show the PopOver on
     */
    public static void showMessage(String message, TextField node) {
        PopOver po = new PopOver();
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(8));
        box.getChildren().add(new Text(message));
        po.setContentNode(box);
        po.setDetachable(false);
        po.setAutoHide(true);
        po.show(node);

        node.onMouseClickedProperty().addListener((observable, oldValue, newValue) -> {
            po.hide();
        });

        node.textProperty().addListener((observable, oldValue, newValue) -> {
            po.hide();
        });

        timeoutHide(po);
    }


    /**
     * Hides the PopOver after a timeout of 4000ms, handled by starting and sleeping a new thread
     *
     * @param po The PopOver to hide
     */
    private static void timeoutHide(PopOver po) {
        Runnable hide = () -> {
            try {
                Thread.sleep(4000);
            }
            catch (InterruptedException ex) {
                po.hide();
            }
            po.hide();
        };
        Thread hideThread = new Thread(hide);
        hideThread.start();
    }
}

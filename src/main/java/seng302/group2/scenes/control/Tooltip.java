package seng302.group2.scenes.control;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.util.Duration;

import java.lang.reflect.Field;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A custom class that allows the setting of custom durations on standard JavaFX Tooltips using reflection techniques.
 * Created by Jordane on 23/08/2015.
 */
public class Tooltip extends javafx.scene.control.Tooltip {

    static Lock staticLock = new ReentrantLock();
    /**
     * The default delay for custom Tooltips, much shorter than original JavaFX Tooltips
     */
    private final int defaultDelay = 200;


    /**
     * Basic constructor for a Sahara Tooltip
     *
     * @param tipText The tooltip text
     */
    public Tooltip(String tipText) {
        super(tipText);
        setDelay(defaultDelay);
    }


    /**
     * Constructor for a Sahara Tooltip with custom show delay
     *
     * @param tipText The tooltip text
     * @param delay   The delay the Tooltip has from hovering to showing
     */
    public Tooltip(String tipText, int delay) {
        super(tipText);
        setDelay(delay);
    }


    /**
     * Constructor for a Sahara Tooltip with a given node to install on, and a custom show delay
     *
     * @param tipText The tooltip text
     * @param node    The node that the Tooltip should be automatically installed onto
     */
    public Tooltip(String tipText, Node node) {
        super(tipText);
        setDelay(defaultDelay);
        Tooltip.install(node, this);
    }


    /**
     * Constructor for a Sahara Tooltip with a given node to install on, and a custom show delay
     *
     * @param tipText The tooltip text
     * @param node    The node that the Tooltip should be automatically installed onto
     * @param delay   The delay the Tooltip has from hovering to showing
     */
    public Tooltip(String tipText, Node node, int delay) {
        super(tipText);
        setDelay(delay);
        Tooltip.install(node, this);
    }


    /**
     * An on-the-fly creation method for a Sahara Tooltip with a given node to install on, and a custom show delay
     *
     * @param tipText The tooltip text
     * @param node    The node that the Tooltip should be automatically installed onto
     */
    public static void create(String tipText, Node node) {
        Tooltip tt = new Tooltip(tipText, node);
    }


    /**
     * An on-the-fly creation method for a Sahara Tooltip with a given node to install on, and a custom show delay
     *
     * @param tipText The tooltip text
     * @param node    The node that the Tooltip should be automatically installed onto
     * @param delay   The delay the Tooltip has from hovering to showing
     */
    public static void create(String tipText, Node node, int delay) {
        Tooltip tt = new Tooltip(tipText, node, delay);
    }

    /**
     * Sets the delay of a JavaFX Tooltip using reflection
     *
     * @param tooltip The Tooltip to set the delay of showing
     * @param delay   The delay in milliseconds
     */
    private static void setDelay(javafx.scene.control.Tooltip tooltip, int delay) {
        staticLock.lock();
        try {
            Field fieldBehavior = tooltip.getClass().getSuperclass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(delay)));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        staticLock.unlock();
    }

    /**
     * Sets the showing delay of the Tooltip
     *
     * @param delay The delay between hovering and showing the Tooltip in milliseconds
     */
    public void setDelay(int delay) {
        setDelay(this, delay);
    }
}

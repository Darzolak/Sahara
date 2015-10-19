package seng302.group2.scenes.control;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.controlsfx.control.PopOver;

/**
 * A custom PopOver that acts as an information ToolTip that's useful for editing and displaying information other than
 * plain string content
 * Created by Jordane on 24/08/2015.
 */
public class PopOverTip extends PopOver {

    private Node parent = null;

    /**
     * Creates a PopOverTip on the given parent/owner
     *
     * @param parentNode The parent of the PopOverTip
     */
    public PopOverTip(Node parentNode) {
        super();
        init(parentNode);
        setContentNode(wrapContent(new Label("No Content")));
    }

    /**
     * Creates a PopOverTip on the given parent/owner with the given content as the inner of the PopOverTip
     *
     * @param parentNode  The parent of the PopOverTip
     * @param contentNode The content of the PopOverTip
     */
    public PopOverTip(Node parentNode, Node contentNode) {
        super(contentNode);
        init(parentNode);
        setContentNode(wrapContent(contentNode));
    }


    public PopOverTip(Node parentNode, String message) {
        super();
        init(parentNode);
        setContentNode(wrapContent(new Label(message)));
    }

    /**
     * Performs basic initialisation of the PopOverTip
     *
     * @param parentNode The parent of the PopOverTip
     */
    private void init(Node parentNode) {
        this.parent = parentNode;
        setDetachable(false);  // We don't want to detach ToolTip/information PopOverTips, use a PopOver instead
        setupHover();
    }

    /**
     * Creates a wrapper of a node with some added padding
     *
     * @param node The node to wrap
     * @return The node wrapped in an HBox with a default padding
     */
    private HBox wrapContent(Node node) {
        HBox wrapper = new HBox();
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(8));
        wrapper.getChildren().add(node);
        return wrapper;
    }

    /**
     * Creates mouse event handlers that trigger the PopOverTip to display and hide itself on a hover over it's parent
     */
    private void setupHover() {
        if (parent != null) {

            parent.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
                if (isShowing()) {
                    this.setX(event.getScreenX() - this.getWidth() / 2.0);
                    this.setY(event.getScreenY() + 4);

                }
            });

            parent.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> show(parent, event.getScreenX(),
                    event.getScreenY() + parent.getBoundsInLocal().getMaxY()));
            parent.addEventHandler(MouseEvent.MOUSE_EXITED, event -> hide());
        }
    }
}

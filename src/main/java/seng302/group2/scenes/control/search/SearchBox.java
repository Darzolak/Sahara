package seng302.group2.scenes.control.search;

import javafx.scene.control.TextField;
import seng302.group2.App;

import java.util.Collection;

/**
 * A stylised TextField that also performs a basic search query on the current scene content through a key listener
 * Created by jml168 on 26/07/15.
 */
public class SearchBox extends TextField {
    /**
     * Creates a search box element for searching text in the current content scene
     */
    public SearchBox() {
        this.setPromptText("Search this pane...");

        // Rounds the edges, adds an icon, and makes it look like a search box!
        this.setStyle("-fx-border-radius: 12 12 12 12;"
                        + "-fx-background-radius: 12 12 12 12; "
                        + "-fx-background-image: url('/icons/search-new.png');"
                        + "-fx-background-repeat: no-repeat; "
                        + "-fx-background-position: right 6px center; "
        );

        this.setPrefWidth(200);

        // Adds some search functionality on the release of a key (so it includes what was entered)
        this.setOnKeyReleased(ke -> {
                if (App.mainPane.getContent() instanceof SearchableScene) {
                    //System.out.println("Querying: " + this.getText());
                    Collection<SearchableTab> tabs =
                            ((SearchableScene) App.mainPane.getContent()).query(this.getText());
                }
            });
    }

    /**
     * Querys all the tabs in the current scene
     * @param query the string to query
     */
    public void search(String query) {
        Collection<SearchableTab> tabs =
                ((SearchableScene) App.mainPane.getContent()).query(this.getText());
    }
}

package seng302.group2.scenes.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Used to show a standard JavaFX dialog.
 * Created by Jordane on 20/05/2015.
 */
public class CustomDialog {
    /**
     * Shows a standard dialog with the given text and severity
     *
     * @param title    The title string (dialog caption)
     * @param message  The dialog content
     * @param severity The dialog severity
     */
    public static void showDialog(String title, String message, Alert.AlertType severity) {
        Alert alert = new Alert(severity);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Shows a confirmation box with the given text
     *
     * @param title    The title string (dialog caption)
     * @param question The confirmation content/question
     * @return The button option that was chosen
     */
    public static ButtonType showConfirmation(String title, String question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(question);

        return alert.showAndWait().get();
    }
}

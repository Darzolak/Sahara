/*
 * SENG302 Group 2
 */
package seng302.group2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import seng302.group2.scenes.MainPane;
import seng302.group2.scenes.menu.MainToolbar;
import seng302.group2.util.config.ConfigLoader;
import seng302.group2.workspace.workspace.Workspace;
import seng302.group2.workspace.workspace.Workspace.SaveLoadResult;

import java.util.Optional;
import java.util.prefs.Preferences;

/**
 * The executable class for Sahara.
 */
public class App extends Application {
    public static SplitPane content;
    public static MainPane mainPane;
    public static double version = 0.6;
    public static Stage mainStage;
    private static Scene mainScene;

    /**
     * Refreshes the main scene GUI.
     */
    public static void refreshMainScene() {
        if (App.mainStage == null) {
            return;  // App is not running (Tests)
        }

        mainPane.refreshAll();

        App.refreshWindowTitle();

        MainToolbar.undoRedoToggle();
    }

    /**
     * Refreshes the title of the window to show the name of the current workspace, if any.
     */
    public static void refreshWindowTitle() {
        if (App.mainStage == null) {
            return;  // App is not running (Tests)
        }
        if (Global.currentWorkspace == null) {
            App.mainStage.titleProperty().set("Sahara");
        }
        else {
            if (Global.currentWorkspace.getHasUnsavedChanges()) {
                App.mainStage.titleProperty().set("Sahara: " + Global.currentWorkspace.getLongName()
                        + "*");
            }
            else {
                App.mainStage.titleProperty().set("Sahara: "
                        + Global.currentWorkspace.getLongName());
            }
        }
    }

    /**
     * The closure and application tear-down method that should be executed on closure of the
     * application from any branch of the project code
     */
    public static void exitApp() {
        ConfigLoader.saveConfig();
        if (!Global.currentWorkspace.getHasUnsavedChanges()) {
            System.exit(0);
            return;  // Clean from a method POV
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save");
        alert.setHeaderText("Unsaved Changes");
        alert.setContentText("Would you like to save your changes to the current workspace?");
        alert.getDialogPane().setStyle(" -fx-max-width:500px; -fx-max-height: 180px; -fx-pref-width: 500px; "
                + "-fx-pref-height: 180px;");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);


        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonTypeYes) {
            SaveLoadResult saved = Workspace.saveWorkspace(Global.currentWorkspace, false);
            if (saved == SaveLoadResult.SUCCESS) {
                // Save configuration again as settings may change on save of workspace
                ConfigLoader.saveConfig();
                System.exit(0);
            }
        }
        else if (result.get() == buttonTypeNo) {
            System.exit(0);
        }
    }


    /**
     * The main entry of the application.
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The GUI setup and launch of the workspace.
     *
     * @param primaryStage primary stage
     */
    @Override
    public void start(Stage primaryStage) {
        Global.currentWorkspace = new Workspace();

        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // The title of the window
        primaryStage.setTitle("Sahara");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(500);

        Preferences userPrefs = Preferences.userNodeForPackage(getClass());
        // get window location from user preferences, second value is default
        boolean maxed = userPrefs.getBoolean("stage.maximized", true);
        double x = userPrefs.getDouble("stage.x", 100);
        double y = userPrefs.getDouble("stage.y", 100);
        double w = userPrefs.getDouble("stage.width", 1060);
        double h = userPrefs.getDouble("stage.height", 640);

        primaryStage.setMaximized(maxed);
        primaryStage.setX(x);
        primaryStage.setY(y);
        primaryStage.setWidth(w);
        primaryStage.setHeight(h);

        //primaryStage.setWidth(0.75 * screenSize.getWidth());
        //primaryStage.setHeight(0.75 * screenSize.getHeight());
        //primaryStage.setMinHeight(0.25 * screenSize.getWidth());
        //primaryStage.setMinWidth(0.25 * screenSize.getWidth());

        primaryStage.getIcons().add(new Image("/icon.png"));

        // Set the scene of the stage to the initial scene
        content = new SplitPane();
        mainPane = new MainPane();
        App.mainScene = new Scene(mainPane);
        primaryStage.setScene(App.mainScene);
        mainStage = primaryStage;

        // Load the config
        ConfigLoader.loadConfig();

        // Exit button handling
        Platform.setImplicitExit(false);
        primaryStage.setOnCloseRequest(event -> {
            // Set user preferences for window
            userPrefs.putBoolean("stage.maximized", primaryStage.isMaximized());
            userPrefs.putDouble("stage.x", primaryStage.getX());
            userPrefs.putDouble("stage.y", primaryStage.getY());
            userPrefs.putDouble("stage.width", primaryStage.getWidth());
            userPrefs.putDouble("stage.height", primaryStage.getHeight());

            // Perform shutdown of application
            exitApp();
            event.consume();
        });

        // Show the stage/window
        App.refreshWindowTitle();
        mainStage.show();

        App.mainPane.getTree().selectItem(Global.currentWorkspace);
        Global.selectedTreeItem.setExpanded(true);
    }
}

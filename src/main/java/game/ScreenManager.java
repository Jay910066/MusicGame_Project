package game;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ScreenManager {
    private Stage stage;
    private VBox root = new VBox();
    private Scene scene = new Scene(root);

    public ScreenManager(Stage stage) {
        this.stage = stage;
        root.setAlignment(Pos.CENTER);
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    }

    public void switchToMainMenu() {
        MainMenu mainMenu = new MainMenu(this);
        root.getChildren().clear();
        root.getChildren().add(mainMenu);
    }

    public void switchToSettings(String previousScreen) {
        Settings settings = new Settings(this, previousScreen);
        root.getChildren().clear();
        root.getChildren().add(settings);
    }

    public void switchToSongListMenu() {
        SongListMenu songListMenu = new SongListMenu(this);
        root.getChildren().clear();
        root.getChildren().add(songListMenu);
    }
}

package game;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

public class ScreenManager {
    private VBox root = new VBox();
    private Scene scene = new Scene(root);

    public ScreenManager(Stage stage) {
        root.setAlignment(Pos.CENTER);
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    }

    public void switchToMainMenu(int previousIndex) {
        MainMenu mainMenu = new MainMenu(this, previousIndex);
        root.getChildren().clear();
        root.getChildren().add(mainMenu);
    }

    public void switchToSettings(String previousScreen, int previousIndex) {
        Settings settings = new Settings(this, previousScreen, previousIndex);
        root.getChildren().clear();
        root.getChildren().add(settings);
    }

    public void switchToSongListMenu(int previousIndex) {
        SongListMenu songListMenu = new SongListMenu(this, previousIndex);
        root.getChildren().clear();
        root.getChildren().add(songListMenu);
    }

    public void switchToGamePlay(File selectedSong, int previousIndex) {
        GamePlay gamePlay = new GamePlay(this, previousIndex, selectedSong);
        root.getChildren().clear();
        root.getChildren().add(gamePlay);
    }
}

package game;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

public class SongListMenu extends VBox {
    public SongListMenu(ScreenManager screenManager) {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> screenManager.switchToMainMenu());
        getChildren().add(backButton);

        this.setOnKeyPressed(e ->{
            if(e.getCode() == KeyCode.ESCAPE)
                screenManager.switchToMainMenu();
        });

        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(e -> screenManager.switchToSettings("SongListMenu"));
        getChildren().add(settingsButton);
    }
}

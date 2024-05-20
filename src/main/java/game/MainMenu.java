package game;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
/**
 * 主畫面
 */
public class MainMenu extends VBox {
    /**
     * 主畫面
     * @param screenManager 畫面管理器
     */
    public MainMenu(ScreenManager screenManager) {
        this.setAlignment(Pos.CENTER);

        ImageView mainTitle = new ImageView("file:Resources/Images/MainTitle.png");
        Button playButton = new Button("Play");
        ImageView optionButton = new ImageView("file:Resources/Images/SettingsButton.png");
        Button exitButton = new Button("Exit");

        playButton.setOnAction(e -> screenManager.switchToSongListMenu());
        optionButton.setOnMouseClicked(e -> screenManager.switchToSettings("MainMenu"));
        exitButton.setOnAction(e -> System.exit(0));

        VBox mainMenu = new VBox();

        mainMenu.setAlignment(Pos.CENTER);
        mainMenu.setSpacing(20);
        mainMenu.getChildren().addAll(mainTitle, playButton, optionButton, exitButton);

        StackPane root = new StackPane();
        this.getChildren().add(root);

        root.getChildren().add(mainMenu);

        HBox dialogBox = new HBox(20);
        dialogBox.setAlignment(Pos.CENTER);
        Label message = new Label("Are you sure you want to exit?");
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(event -> System.exit(0));
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> root.getChildren().remove(dialogBox));

        dialogBox.getChildren().addAll(message, confirmButton, cancelButton);
        dialogBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");


        this.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ESCAPE){
                if(!root.getChildren().contains(dialogBox))
                    root.getChildren().add(dialogBox);
                else
                    root.getChildren().remove(dialogBox);
            }
        });
    }
}
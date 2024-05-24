package game;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
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

        ImageView background = new ImageView("file:Resources/Images/可能是menu的圖.jpg");
        ImageView playButton = new ImageView("file:Resources/Images/PlayButton.png");
        ImageView settingsButton = new ImageView("file:Resources/Images/SettingsButton.png");
        ImageView quitButton = new ImageView("file:Resources/Images/QuitButton.png");

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double maxDimension = Math.max(screenBounds.getWidth(), screenBounds.getHeight());
        background.setPreserveRatio(true);
        if(background.getFitHeight() < background.getFitWidth())
            background.setFitHeight(maxDimension * 1);
        else
            background.setFitWidth(maxDimension * 1);

        playButton.setOnMouseEntered(e -> playButton.setImage(new Image("file:Resources/Images/PlayButton_Selected.png")));
        playButton.setOnMouseExited(e -> playButton.setImage(new Image("file:Resources/Images/PlayButton.png")));

        settingsButton.setOnMouseEntered(e -> settingsButton.setImage(new Image("file:Resources/Images/SettingsButton_Selected.png")));
        settingsButton.setOnMouseExited(e -> settingsButton.setImage(new Image("file:Resources/Images/SettingsButton.png")));

        quitButton.setOnMouseEntered(e -> quitButton.setImage(new Image("file:Resources/Images/QuitButton_Selected.png")));
        quitButton.setOnMouseExited(e -> quitButton.setImage(new Image("file:Resources/Images/QuitButton.png")));

        playButton.setOnMouseClicked(e -> screenManager.switchToSongListMenu());
        settingsButton.setOnMouseClicked(e -> screenManager.switchToSettings("MainMenu"));
        quitButton.setOnMouseClicked(e -> System.exit(0));

        VBox mainMenu = new VBox();

        mainMenu.setAlignment(Pos.CENTER);
        mainMenu.setSpacing(20);
        mainMenu.getChildren().addAll( playButton, settingsButton, quitButton);

        StackPane root = new StackPane();
        this.getChildren().add(root);

        root.getChildren().addAll(background,mainMenu);

        HBox leaveWindow = new HBox(20);
        leaveWindow.setAlignment(Pos.CENTER);
        Label message = new Label("Are you sure you want to exit?");
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(event -> System.exit(0));
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> root.getChildren().remove(leaveWindow));

        leaveWindow.getChildren().addAll(message, confirmButton, cancelButton);
        leaveWindow.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");


        this.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ESCAPE){
                if(!root.getChildren().contains(leaveWindow))
                    root.getChildren().add(leaveWindow);
                else
                    root.getChildren().remove(leaveWindow);
            }
        });
    }
}
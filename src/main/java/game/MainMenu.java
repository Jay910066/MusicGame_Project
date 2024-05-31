package game;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Screen;

/**
 * 主畫面
 */
public class MainMenu extends StackPane {

    /**
     * 主畫面
     *
     * @param screenManager 畫面管理器
     */
    public MainMenu(ScreenManager screenManager) {
        //設定畫面
        this.setAlignment(Pos.CENTER);
        this.setFocusTraversable(true);
        this.requestFocus();

        //設定背景、按鈕、離開視窗
        setBackGround();
        setButtons(screenManager);
        setLeaveWindow();
    }

    /**
     * 設置背景
     */
    private void setBackGround() {
        ImageView background = new ImageView("file:Resources/Images/menu_background.jpg"); //背景圖片

        //設定背景大小，使其符合螢幕大小
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double maxDimension = Math.max(screenBounds.getWidth(), screenBounds.getHeight());
        background.setPreserveRatio(true);
        if(background.getFitHeight() < background.getFitWidth())
            background.setFitHeight(maxDimension * 1);
        else
            background.setFitWidth(maxDimension * 1);

        getChildren().add(background);
    }

    /**
     * 設置按鈕
     *
     * @param screenManager 畫面管理器
     */
    private void setButtons(ScreenManager screenManager) {
        SoundEffect selectsoundEffect = new SoundEffect();

        //進入選歌畫面按鈕
        ImageView playButton = new ImageView("file:Resources/Images/PlayButton.png");
        playButton.setOnMouseEntered(e -> {playButton.setImage(new Image("file:Resources/Images/PlayButton_Selected.png"));selectsoundEffect.playSelectSound();});
        playButton.setOnMouseExited(e -> playButton.setImage(new Image("file:Resources/Images/PlayButton.png")));
        playButton.setOnMouseClicked(e -> screenManager.switchToSongListMenu());

        //進入設定畫面按鈕
        ImageView settingsButton = new ImageView("file:Resources/Images/SettingsButton.png");
        settingsButton.setOnMouseEntered(e -> {settingsButton.setImage(new Image("file:Resources/Images/SettingsButton_Selected.png"));selectsoundEffect.playSelectSound();});
        settingsButton.setOnMouseExited(e -> settingsButton.setImage(new Image("file:Resources/Images/SettingsButton.png")));
        settingsButton.setOnMouseClicked(e -> screenManager.switchToSettings("MainMenu"));

        //離開遊戲按鈕
        ImageView quitButton = new ImageView("file:Resources/Images/QuitButton.png");
        quitButton.setOnMouseEntered(e -> {quitButton.setImage(new Image("file:Resources/Images/QuitButton_Selected.png"));selectsoundEffect.playSelectSound();});
        quitButton.setOnMouseExited(e -> quitButton.setImage(new Image("file:Resources/Images/QuitButton.png")));
        quitButton.setOnMouseClicked(e -> System.exit(0));

        //按鈕容器
        VBox buttonBox = new VBox();
        buttonBox.setTranslateY(250);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(20);
        buttonBox.getChildren().addAll(playButton, settingsButton, quitButton);

        getChildren().add(buttonBox);
    }

    /**
     * 設定離開視窗
     */
    private void setLeaveWindow() {
        //離開提示視窗
        HBox leaveWindow = new HBox(20);
        leaveWindow.setAlignment(Pos.CENTER);
        leaveWindow.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        //離開提示訊息
        Label message = new Label("Are you sure you want to exit?");
        message.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        //確認離開按鈕
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        confirmButton.setOnAction(event -> System.exit(0));

        //取消離開按鈕
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        cancelButton.setOnAction(event -> getChildren().remove(leaveWindow));

        leaveWindow.getChildren().addAll(message, confirmButton, cancelButton);

        this.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ESCAPE) {
                if(!getChildren().contains(leaveWindow))
                    getChildren().add(leaveWindow);
                else
                    getChildren().remove(leaveWindow);
            }
        });
    }
}
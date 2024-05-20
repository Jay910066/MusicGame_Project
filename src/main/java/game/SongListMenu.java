package game;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.io.File;

/**
 * 歌曲列表畫面
 */
public class SongListMenu extends StackPane {
    public static int selectedIndex = 0;

    private VBox selector;
    private int totalItems;
    private HBox[] songBoxes;
    private File[] songList;
    private ReadOsu readOsu;
    private ImageView background;
    private ImageView songCover;
    private Text songNameText;
    private Text artistText;
    private Text creatorText;
    private Media selectedSong;
    private MediaPlayer previewSongPlayer;
    /**
     * 歌曲列表畫面
     * @param screenManager 畫面管理器
     */
    public SongListMenu(ScreenManager screenManager) {
        background = new ImageView();
        getChildren().add(background);

        ColorAdjust brightness = new ColorAdjust();
        brightness.setBrightness(-0.1);
        background.setEffect(brightness);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double maxDimension = Math.max(screenBounds.getWidth(), screenBounds.getHeight());
        background.setPreserveRatio(true);
        if(background.getFitHeight() < background.getFitWidth())
            background.setFitHeight(maxDimension * 1);
        else
            background.setFitWidth(maxDimension * 1);



        BorderPane root = new BorderPane();
        getChildren().add(root);


        // Selector
        selector = new VBox();
        root.setRight(selector);
        selector.setFocusTraversable(true);
        selector.requestFocus();
        selector.setAlignment(Pos.CENTER);
        selector.setPadding(new Insets(100));
        selector.setSpacing(50);

        File songFolder = new File("Resources/Songs");
        songList = songFolder.listFiles();
        totalItems = songList.length;
        songBoxes = new HBox[totalItems];
        readOsu = new ReadOsu();

        for(int i = 0; i < totalItems; i++) {
            readOsu.setSong(songList[i].getPath() + "/Info.osu");
            Label songNameLabel = new Label(readOsu.getTitle());
            songNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 60;");
            HBox songBox = new HBox();
            songBox.setMinHeight(100);
            songBox.setMinWidth(600);
            songBox.setStyle("-fx-background-color: darkgray;");
            songBox.setAlignment(Pos.CENTER);
            songBox.setPadding(new Insets(10));
            songBox.getChildren().add(songNameLabel);
            songBoxes[i] = songBox;
        }

        selector.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                moveUp();
            } else if (event.getCode() == KeyCode.DOWN) {
                moveDown();
            }
        });

        // Detail Box
        VBox detailBox = new VBox();
        root.setLeft(detailBox);
        detailBox.setMinWidth(screenBounds.getWidth() / 4);
        detailBox.setMinHeight(screenBounds.getHeight());
        detailBox.setStyle("-fx-background-color: rgba(100, 100, 100, 0.6);");
        detailBox.setPadding(new Insets(50));
        detailBox.setAlignment(Pos.CENTER);

        songCover = new ImageView("file:" + songList[selectedIndex].getPath() + "/cover.jpg");
        detailBox.getChildren().add(songCover);
        songCover.setPreserveRatio(true);
        if(songCover.getFitHeight() < songCover.getFitWidth())
            songCover.setFitHeight(screenBounds.getHeight() / 5);
        else
            songCover.setFitWidth(screenBounds.getWidth() / 5);

        GridPane songInfo = new GridPane();
        detailBox.getChildren().add(songInfo);
        songInfo.setVgap(10);
        songInfo.setHgap(20);

        Label songNameLabel = new Label("Song Name:");
        songInfo.add(songNameLabel, 0, 0);
        songNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20; -fx-text-fill: white");
        songNameText = new Text(readOsu.getTitle());
        songInfo.add(songNameText, 1, 0);
        songNameText.setStyle("-fx-font-weight: bold; -fx-font-size: 20");
        songNameText.setFill(Color.WHITE);

        Label artistLabel = new Label("Artist:");
        songInfo.add(artistLabel, 0, 1);
        artistLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20; -fx-text-fill: white");
        artistText = new Text(readOsu.getArtist());
        songInfo.add(artistText, 1, 1);
        artistText.setStyle("-fx-font-weight: bold; -fx-font-size: 20");
        artistText.setFill(Color.WHITE);

        Label creatorLabel = new Label("Creator:");
        songInfo.add(creatorLabel, 0, 2);
        creatorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20; -fx-text-fill: white");
        creatorText = new Text(readOsu.getCreator());
        songInfo.add(creatorText, 1, 2);
        creatorText.setStyle("-fx-font-weight: bold; -fx-font-size: 20");
        creatorText.setFill(Color.WHITE);

        HBox buttonBox = new HBox();
        detailBox.getChildren().add(buttonBox);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setSpacing(10);

        ImageView backButton = new ImageView("file:Resources/Images/BackButton.png");
        buttonBox.getChildren().add(backButton);
        backButton.setOnMouseClicked(e -> {
            screenManager.switchToMainMenu();
            previewSongPlayer.stop();
        });

        ImageView settingsButton = new ImageView("file:Resources/Images/SettingsButton.png");
        buttonBox.getChildren().add(settingsButton);
        settingsButton.setOnMouseClicked(e -> {
            screenManager.switchToSettings("SongListMenu");
            previewSongPlayer.stop();
        });

        if(new File("file:" + songList[selectedIndex].getPath() + "/background.jpg").exists()){
            background.setImage(new Image("file:" + songList[selectedIndex].getPath() + "/background.jpg"));
        }else {
            background.setImage(new Image("file:" + songList[selectedIndex].getPath() + "/cover.jpg"));
        }
        selectedSong = new Media(new File(songList[selectedIndex], "song.mp3").toURI().toString());
        previewSongPlayer = new MediaPlayer(selectedSong);
        selectItem(selectedIndex);

        songBoxes[selectedIndex].setOnMouseClicked(e -> {
            screenManager.switchToGamePlay(songList[selectedIndex]);
            previewSongPlayer.stop();
        });

        this.setOnKeyPressed(e ->{
            if(e.getCode() == KeyCode.ESCAPE) {
                screenManager.switchToMainMenu();
                previewSongPlayer.stop();
            }else if(e.getCode() == KeyCode.ENTER) {
                screenManager.switchToGamePlay(songList[selectedIndex]);
                previewSongPlayer.stop();
            }
        });
    }

    private void moveUp() {
        selectedIndex = (selectedIndex - 1 + totalItems) % totalItems;
        selectItem(selectedIndex);
    }

    private void moveDown() {
        selectedIndex = (selectedIndex + 1) % totalItems;
        selectItem(selectedIndex);
    }

    private void selectItem(int index) {
        readOsu.setSong(songList[index].getPath() + "/Info.osu");
        if(new File("file:" + songList[selectedIndex].getPath() + "/background.jpg").exists()){
            background.setImage(new Image("file:" + songList[selectedIndex].getPath() + "/background.jpg"));
        }else {
            background.setImage(new Image("file:" + songList[selectedIndex].getPath() + "/cover.jpg"));
        }
        songCover.setImage(new Image("file:" + songList[index].getPath() + "/cover.jpg"));
        songNameText.setText(readOsu.getTitle());
        artistText.setText(readOsu.getArtist());
        creatorText.setText(readOsu.getCreator());
        previewSongPlayer.stop();
        selectedSong = new Media(new File(songList[index], "song.mp3").toURI().toString());
        previewSongPlayer = new MediaPlayer(selectedSong);
        previewSongPlayer.setStartTime(Duration.millis(readOsu.getPreviewTime()));
        previewSongPlayer.play();

        selector.getChildren().clear();
        HBox songBox1 = songBoxes[(index - 2 + totalItems) % totalItems];
        HBox songBox2 = songBoxes[(index - 1 + totalItems) % totalItems];
        HBox songBox3 = songBoxes[index];
        HBox songBox4 = songBoxes[(index + 1) % totalItems];
        HBox songBox5 = songBoxes[(index + 2) % totalItems];

        songBox1.setStyle("-fx-background-color: rgba(100,100,100,1);");
        songBox1.setScaleX(0.8);
        songBox1.setScaleY(0.8);
        songBox2.setStyle("-fx-background-color: darkgray;");
        songBox2.setScaleX(0.9);
        songBox2.setScaleY(0.9);
        songBox3.setStyle("-fx-background-color: lightblue;");
        songBox3.setScaleX(1);
        songBox3.setScaleY(1);
        songBox4.setStyle("-fx-background-color: darkgray;");
        songBox4.setScaleX(0.9);
        songBox4.setScaleY(0.9);
        songBox5.setStyle("-fx-background-color: rgba(100,100,100,1);");
        songBox5.setScaleX(0.8);
        songBox5.setScaleY(0.8);
        selector.getChildren().addAll(songBox1, songBox2, songBox3, songBox4, songBox5);
    }
}

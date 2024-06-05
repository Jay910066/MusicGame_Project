package game;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Test extends Application {

    private MediaPlayer mediaPlayer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sound Player");

        Button chooseFileButton = new Button("Choose Sound File");
        chooseFileButton.setOnAction(e -> chooseSoundFile(primaryStage));

        VBox layout = new VBox(10);
        layout.getChildren().addAll(chooseFileButton);

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void chooseSoundFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3"));
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            playSound(selectedFile);
        }
    }

    private void playSound(File soundFile) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        Media sound = new Media(soundFile.toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }
}

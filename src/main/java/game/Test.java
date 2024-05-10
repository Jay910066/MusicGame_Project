package game;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.IdentityHashMap;

public class Test extends Application {

    private VBox container;
    private int selectedIndex;
    private int totalItems;
    HBox[] songBoxes;

    @Override
    public void start(Stage primaryStage) {
        container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10));
        container.setSpacing(10);

        File songFolder = new File("Resources/Songs");
        File[] songs = songFolder.listFiles();
        totalItems = songs.length;
        songBoxes = new HBox[totalItems];

        for(int i = 0; i < totalItems; i++) {
            Label label = new Label(songs[i].getName());
            HBox songBox = new HBox();
            songBox.setMaxHeight(50);
            songBox.setMaxWidth(100);
            songBox.setStyle("-fx-background-color: darkgray;");
            songBox.setAlignment(Pos.CENTER);
            songBox.setPadding(new Insets(10));
            songBox.getChildren().add(label);
            songBoxes[i] = songBox;
        }

        System.out.println(songs[0].getPath()+ "/cover.jpg");

        selectedIndex = 0;
        selectItem(selectedIndex, "d");

        Scene scene = new Scene(container, 200, 300);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                moveUp();
            } else if (event.getCode() == KeyCode.DOWN) {
                moveDown();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Infinite Carousel Selector");
        primaryStage.show();
    }

    private void moveUp() {
        selectedIndex = (selectedIndex - 1 + totalItems) % totalItems;
        selectItem(selectedIndex, "Up");
    }

    private void moveDown() {
        selectedIndex = (selectedIndex + 1) % totalItems;
        selectItem(selectedIndex, "Down");
    }

    private PathTransition createTransition(HBox songBox, int index){
        Line path = new Line(songBox.getLayoutX(), 0, songBox.getLayoutX(), 50);
        PathTransition transition = new PathTransition();
        transition.setDuration(Duration.millis(200));
        transition.setPath(path);
        transition.setNode(songBox);
        return transition;
    }

    private void selectItem(int index, String direction) {

        HBox songBox0 = songBoxes[(index - 3 + totalItems) % totalItems];
        PathTransition transition0 = createTransition(songBox0, 0);
        transition0.play();
        HBox songBox1 = songBoxes[(index - 2 + totalItems) % totalItems];
        PathTransition transition1 = createTransition(songBox0, 0);
        transition1.play();
        HBox songBox2 = songBoxes[(index - 1 + totalItems) % totalItems];
        PathTransition transition2 = createTransition(songBox0, 0);
        transition2.play();
        HBox songBox3 = songBoxes[index];
        PathTransition transition3 = createTransition(songBox0, 0);
        transition3.play();
        HBox songBox4 = songBoxes[(index + 1) % totalItems];
        PathTransition transition4 = createTransition(songBox0, 0);
        transition4.play();
        HBox songBox5 = songBoxes[(index + 2) % totalItems];
        PathTransition transition5 = createTransition(songBox0, 0);
        transition5.play();
        HBox songBox6 = songBoxes[(index + 3) % totalItems];
        PathTransition transition6 = createTransition(songBox0, 0);
        transition6.play();

        songBox1.setStyle("-fx-background-color: darkgray;");
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
        songBox5.setStyle("-fx-background-color: darkgray;");
        songBox5.setScaleX(0.8);
        songBox5.setScaleY(0.8);

        container.getChildren().addAll(songBox1, songBox2, songBox3, songBox4, songBox5);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Single extends Note{
    private int track;
    private int hitTime;
    private boolean hit = false;
    private boolean kill = false;
    private ImageView singleNote;

    Single(int track, int hitTime) {
        super(track, hitTime);
        if (track == 0 || track == 3) {
            singleNote = new ImageView(new Image("file:src/main/resources/side.png"));
        } else if (track == 1 || track == 2) {
            singleNote = new ImageView(new Image("file:src/main/resources/middle.png"));
        }
    }
}

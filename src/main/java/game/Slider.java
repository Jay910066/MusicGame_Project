package game;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;


public class Slider extends Note{
    private int endTime;
    private int track;
    private int hitTime;
    private boolean hit = false;
    private boolean kill = false;
    private ImageView slideNote;

    Slider(int track, int hitTime, int endTime) {
        super(track, hitTime);
        this.endTime = endTime;
        if (track == 0 || track == 3) {
            slideNote = new ImageView(new Image("file:src/main/resources/side.png"));
        } else if (track == 1 || track == 2) {
            slideNote = new ImageView(new Image("file:src/main/resources/middle.png"));
        }
    }

    public int getEndTime(){
        return endTime;
    }
}

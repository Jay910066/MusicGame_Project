package game;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;


public class Slider extends Note{
    private int endTime;
    private int track;
    private int hitTime;
    private boolean hit = false;
    private boolean kill = false;

    Slider(int track, int hitTime, int endTime) {
        super(track, hitTime);
        this.endTime = endTime;
        if (track == 0) {
            this.setImage(new Image("file:Resources/Images/Left_side.png"));
        } else if (track == 1) {
            this.setImage(new Image("file:Resources/Images/Left_middle.png"));
        }else if(track == 2){
            this.setImage(new Image("file:Resources/Images/Right_middle.png"));
        }else if(track == 3) {
            this.setImage(new Image("file:Resources/Images/Right_side.png"));
        }
    }

    public int getEndTime(){
        return endTime;
    }
}

package game;

import javafx.scene.image.Image;


public class Hold extends Note {
    private int endTime;
    private int track;
    private int hitTime;
    private boolean hit = false;
    private boolean kill = false;
    private boolean startNote = false;
    private boolean endNote = false;
    private boolean bodyNote = false;

    Hold(int track, int hitTime, int endTime) {
        super(track, hitTime);
        startNote = true;
        this.endTime = endTime;
        if(track == 0) {
            this.setImage(new Image("file:Resources/Images/Left_side.png"));
        }else if(track == 1) {
            this.setImage(new Image("file:Resources/Images/Left_middle.png"));
        }else if(track == 2) {
            this.setImage(new Image("file:Resources/Images/Right_middle.png"));
        }else if(track == 3) {
            this.setImage(new Image("file:Resources/Images/Right_side.png"));
        }
    }

    Hold(int track, int endTime) {
        super(track, endTime);
        endNote = true;
        this.endTime = endTime;
        if(track == 0) {
            this.setImage(new Image("file:Resources/Images/Left_side.png"));
        }else if(track == 1) {
            this.setImage(new Image("file:Resources/Images/Left_middle.png"));
        }else if(track == 2) {
            this.setImage(new Image("file:Resources/Images/Right_middle.png"));
        }else if(track == 3) {
            this.setImage(new Image("file:Resources/Images/Right_side.png"));
        }
    }

    Hold(int track){
        super(track, -1);
        bodyNote = true;
        if(track == 0) {
            this.setImage(new Image("file:Resources/Images/Left_side_hold_body.png"));
        }else if(track == 1) {
            this.setImage(new Image("file:Resources/Images/Left_middle_hold_body.png"));
        }else if(track == 2) {
            this.setImage(new Image("file:Resources/Images/Right_middle_hold_body.png"));
        }else if(track == 3) {
            this.setImage(new Image("file:Resources/Images/Right_side_hold_body.png"));
        }
    }

    public int getEndTime() {
        return endTime;
    }

    public boolean isStartNote() {
        return startNote;
    }

    public boolean isEndNote() {
        return endNote;
    }

    public boolean isBodyNote() {
        return bodyNote;
    }
}

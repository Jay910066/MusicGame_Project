package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Note extends ImageView{
    private int track;
    private int bornTime;
    private double deltaTime;
    private int hitTime;
    private boolean hit = false;
    private boolean kill = false;
    private ImageView noteImage;

    Note(int track, int hitTime) {
        this.track = track;
        this.hitTime = hitTime;
        deltaTime = RhythmGame.defaultFlowTime * 1000;
        bornTime = hitTime - (int)(deltaTime / Settings.flowSpeed);
        this.setScaleX(0.5);
        this.setScaleY(0.5);
    }

    public int getTrack(){
        return track;
    };
    public int getHitTime(){
        return hitTime;
    };

    public int getBornTime(){
        return bornTime;
    };

    public void OnHitCheck(){

    };

    public void hit(){
        hit = true;
    };

    public boolean isHit(){
        return hit;
    };

    public void kill(){
        kill = true;
    };

    public boolean isDead(){
        return kill;
    };

    public ImageView getImageView(){
        return noteImage;
    };
}

package game;

import java.util.ArrayList;

public class BeatMap {
    private ArrayList<Track> tracks;

    public BeatMap() {
        tracks = new ArrayList<Track>(4);
    }

    public void pause(){

    }

    public void resume(){

    }

    public Track getTrack(int index) {
        return tracks.get(index);
    }

    public void addTrack(Track track) {
        tracks.add(track);
    }
}

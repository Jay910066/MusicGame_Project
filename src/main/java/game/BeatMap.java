package game;

import java.util.ArrayList;

public class BeatMap {
    private ArrayList<Track> tracks;

    public BeatMap() {
        tracks = new ArrayList<Track>(4);
        for(int i = 0; i < 4; i++) {
            tracks.add(new Track());
        }
    }

    public Track getTrack(int index) {
        return tracks.get(index);
    }
}

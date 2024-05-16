package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadOsu {
    private String PreviewTime;
    private String Title;
    private String Artist;
    private String Creator;

    ReadOsu() {
        PreviewTime = "";
        Title = "";
        Artist = "";
        Creator = "";
    }

    ReadOsu(String path) {
        setSong(path);
    }

    private void readLine(String line) {
        line = line.replace(" ", "");

        if(line.startsWith("PreviewTime:")) {
            PreviewTime = line.substring(12);
        } else if(line.startsWith("TitleUnicode:")) {
            Title = line.substring(13);
        } else if(line.startsWith("ArtistUnicode:")) {
            Artist = line.substring(14);
        } else if(line.startsWith("Creator:")) {
            Creator = line.substring(8);
        }
    }

    public void setSong(String path) {
        File osuFile = new File(path);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(osuFile));
            String line;
            while(!(line = reader.readLine()).equals("[HitObjects]") ) {
                readLine(line);
            }
            reader.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public int getPreviewTime() {
        return Integer.parseInt(PreviewTime);
    }

    public String getTitle() {
        return Title;
    }

    public String getArtist() {
        return Artist;
    }

    public String getCreator() {
        return Creator;
    }
}

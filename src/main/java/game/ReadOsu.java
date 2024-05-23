package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 讀取.osu檔案
 */
public class ReadOsu {
    private File osuFile;
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

    /**
     * 讀取該.osu檔案
     *
     * @param path .osu檔案路徑
     */
    public void setSong(String path) {
        osuFile = new File(path);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(osuFile));
            String line;
            while(!(line = reader.readLine()).equals("[HitObjects]")) {
                readInfo(line);
            }
            reader.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public BeatMap getBeatMap() {
        BeatMap beatMap = new BeatMap();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(osuFile));
            String line;
            boolean startReading = false;
            while((line = reader.readLine()) != null) {
                if(line.equals("[HitObjects]")) {
                    startReading = true;
                    continue;
                }
                if(startReading) {
                    String[] data = line.split(",");
                    int x = Integer.parseInt(data[0]);
                    int hitTime = Integer.parseInt(data[2]);
                    String type = data[3];

                    int track = (int) Math.floor((double) x * 4 / 512);
                    if(type.equals("1") || type.equals("5")) {
                        beatMap.getTrack(track).addNote(new Single(track, hitTime));
                    }else if(type.equals("128")) {
                        int endTime = Integer.parseInt(data[5].split(":")[0]);
                        beatMap.getTrack(track).addNote(new Hold(track, hitTime, endTime));
                        beatMap.getTrack(track).addNote(new Hold(track, endTime));
                    }
                }
            }
            reader.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
        return beatMap;
    }

    /**
     * 讀取歌曲資訊
     *
     * @param line .osu檔案的每一行
     */
    private void readInfo(String line) {
        line = line.replace(" ", ""); // 移除空白

        //取得歌曲資訊
        if(line.startsWith("PreviewTime:")) {
            PreviewTime = line.substring(12);
        }else if(line.startsWith("TitleUnicode:")) {
            Title = line.substring(13);
        }else if(line.startsWith("ArtistUnicode:")) {
            Artist = line.substring(14);
        }else if(line.startsWith("Creator:")) {
            Creator = line.substring(8);
        }
    }

    /**
     * 取得歌曲預覽時間
     *
     * @return 歌曲預覽時間
     */
    public int getPreviewTime() {
        return Integer.parseInt(PreviewTime);
    }

    /**
     * 取得歌曲標題
     *
     * @return 歌曲標題
     */
    public String getTitle() {
        return Title;
    }

    /**
     * 取得歌曲演出者
     *
     * @return 歌曲演出者
     */
    public String getArtist() {
        return Artist;
    }

    /**
     * 取得譜面創作者
     *
     * @return 譜面創作者
     */
    public String getCreator() {
        return Creator;
    }
}

package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 讀取.osu檔案
 */
public class ReadOsu {
    private File osuFile; //osu檔案
    private String PreviewTime; //歌曲預覽時間
    private int mode; //遊玩模式
    private String Title; //歌曲標題
    private String Artist; //歌曲演出者
    private String Creator; //譜面作者
    private String keyCount; //鍵數

    ReadOsu(String path) {
        setSong(path);
    }

    /**
     * 讀取該.osu檔案
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

    /**
     * 取得譜面
     * @return 譜面
     */
    public BeatMap getBeatMap() {
        BeatMap beatMap = new BeatMap(); //譜面
        try {
            BufferedReader reader = new BufferedReader(new FileReader(osuFile));
            String line; //每一行
            boolean startReading = false; //是否開始讀取譜面
            while((line = reader.readLine()) != null) {
                //讀到[HitObjects]後開始讀取譜面
                if(line.equals("[HitObjects]")) {
                    startReading = true;
                    continue;
                }

                //讀取譜面
                if(startReading) {
                    String[] data = line.split(",");
                    int x = Integer.parseInt(data[0]); //x座標
                    int hitTime = Integer.parseInt(data[2]); //點擊時間
                    String type = data[3]; //類型

                    int track = (int) Math.floor((double) x * 4 / 512); //軌道

                    //根據類型加入音符
                    if(type.equals("1") || type.equals("5")) {
                        //單擊音符
                        beatMap.getTrack(track).addNote(new Single(track, hitTime));
                    }else if(type.equals("128")) {
                        //長按音符
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
     * @param line .osu檔案的每一行
     */
    private void readInfo(String line) {
        line = line.replace(" ", ""); // 移除空白

        //取得歌曲資訊
        if(line.startsWith("PreviewTime:")) {
            PreviewTime = line.substring(12);
        }else if(line.startsWith("Mode:")) {
            mode = Integer.parseInt(line.substring(5));
        }else if(line.startsWith("TitleUnicode:")) {
            Title = line.substring(13);
        }else if(line.startsWith("ArtistUnicode:")) {
            Artist = line.substring(14);
        }else if(line.startsWith("Creator:")) {
            Creator = line.substring(8);
        }else if(line.startsWith("CircleSize:")){
            keyCount = line.substring(11);
        }
    }

    /**
     * 取得歌曲預覽時間
     * @return 歌曲預覽時間
     */
    public int getPreviewTime() {
        return Integer.parseInt(PreviewTime);
    }

    /**
     * 取得歌曲標題
     * @return 歌曲標題
     */
    public String getTitle() {
        return Title;
    }

    /**
     * 取得歌曲演出者
     * @return 歌曲演出者
     */
    public String getArtist() {
        return Artist;
    }

    /**
     * 取得譜面創作者
     * @return 譜面創作者
     */
    public String getCreator() {
        return Creator;
    }

    /**
     * 取得遊玩模式
     * @return 遊玩模式
     */
    public boolean is4K_Mania(){
        return mode == 3 && keyCount.equals("4");
    }
}

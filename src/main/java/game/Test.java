package game;

import java.io.File;

public class Test {
    public static void main(String[] args) {
        File file = new File("Resources/Images");
        for(File f : file.listFiles()) {
            System.out.println(f.getName());
        }
    }
}

package edu.bupt;

import org.apache.tika.Tika;

import java.io.FileInputStream;

class TikaFileDistinguisher {
    private Tika tika = new Tika();

    public boolean IsAudio(FileInputStream fin) {
        try {
            String type = tika.detect(fin);
            return type.substring(0, 4).equals("audio");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

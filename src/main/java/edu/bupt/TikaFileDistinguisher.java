package edu.bupt;

import org.apache.tika.Tika;

class TikaFileDistinguisher {
    private Tika tika = new Tika();

    public boolean IsAudio(String url) {
        try {
            String type = tika.detect(url);
            return type.substring(0, 4).equals("audio");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

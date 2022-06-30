package edu.bupt;

import java.util.ArrayList;
import java.util.Iterator;

public class RequestStruct {
}

//歌曲标签添加请求结构
class SongTokenAddRequest{
    private String song_name, new_token_name;
    private ArrayList<String> singers;
    SongTokenAddRequest(String sname, ArrayList<String> singers, String token_name){
        this.singers = singers;
        new_token_name = token_name;
    }

    public String getNew_token_name() {
        return new_token_name;
    }

    public Iterator<String> getSingerIter(){
        return singers.iterator();
    }

    public String getSong_name() {
        return song_name;
    }
}

//添加音乐的请求结构
class SongAddRequest{
    private String origin_url;
    private String name;
    private String album;
    private ArrayList<String> singers;
    private ArrayList<String> tags;
    SongAddRequest(String url, String name, String album, ArrayList<String> singers, ArrayList<String>tags){
        this.origin_url = url;
        this.name = name;
        this.album = album;
        this.singers = singers;
        this.tags = tags;
    }

    public String getAlbum() {
        return album;
    }

    public String getName() {
        return name;
    }

    public String getOrigin_url() {
        return origin_url;
    }
    public String[] getSingers(){
        return (String[]) singers.toArray();
    }
    public Iterator<String> getSingerIter(){
        return singers.iterator();
    }
    public String[] getTags(){
        return (String[]) tags.toArray();
    }
    public Iterator<String> getTagIter(){
        return tags.iterator();
    }
}
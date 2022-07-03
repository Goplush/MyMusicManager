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

    //添加歌单的请求结构

}
class SongListAddRequest{
    private String list_name;
    private String user_id;
    private ArrayList<String> song_names;
    private ArrayList<String> song_albums;
    public SongListAddRequest(String lname, String UID, ArrayList<String> snames,ArrayList<String> salbums){
        list_name = lname;
        user_id = UID;
        song_names = snames;
        song_albums = salbums;
    }

    public String getList_name() {
        return list_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public ArrayList<String> getSong_names() {
        return song_names;
    }

    public void setSong_albums(ArrayList<String> song_albums) {
        this.song_albums = song_albums;
    }
}
class SongSearchWithAlbum{
    private String name, album;
    public SongSearchWithAlbum(String sname, String salbum){
        this.name = sname;
        this.album = salbum;
    }

    public String getName() {
        return name;
    }

    public String getAlbum() {
        return album;
    }
}
package edu.bupt;

import java.util.ArrayList;
import java.util.Iterator;

public class RequestStruct {
}

//歌曲标签添加请求结构
class SongTokenAddRequest{
    private String song_name, token;
    String song_album;
    SongTokenAddRequest(String sname, String salbum, String token_name){
        this.song_name = sname;
        this.song_album = salbum;
        token = token_name;
    }

    public String getToken() {
        return token;
    }

    public String getSong_album() {
        return song_album;
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
    public ArrayList<String> getSingers(){
        return singers;
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
    private String user_name;
    private ArrayList<String> song_names;
    private ArrayList<String> song_albums;
    public SongListAddRequest(String lname, String User_name, ArrayList<String> snames,ArrayList<String> salbums){
        list_name = lname;
        user_name = User_name;
        song_names = snames;
        song_albums = salbums;
    }

    public String getList_name() {
        return list_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public ArrayList<String> getSong_names() {
        return song_names;
    }

    public ArrayList<String> getSong_albums() {
        return song_albums;
    }
}
class SongSearchWithNameAndAlbum {
    private String name, album;
    public SongSearchWithNameAndAlbum(String sname, String salbum){
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
class AddSingleSongToListRequest {
    private String user_name;
    private String list_name;
    private String song_name;
    private String song_album;
    public AddSingleSongToListRequest(String uname, String lname, String sname, String salbum){
        this.list_name = lname;
        this.song_album = salbum;
        this.song_name = sname;
        this.user_name = uname;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getList_name() {
        return list_name;
    }

    public String getSong_name() {
        return song_name;
    }

    public String getSong_album() {
        return song_album;
    }
}
class AddSongIntroRequest{
    private String intro;
    private String song_name;
    private String song_album;
    public AddSongIntroRequest(String intro, String sname, String salbum){
        this.intro= intro;
        this.song_album = salbum;
        this.song_name= sname;
    }

    public String getIntro() {
        return intro;
    }

    public String getSong_name() {
        return song_name;
    }

    public String getSong_album() {
        return song_album;
    }
}
class CommentAddRequest{
    private String comment;
    private String song_name;
    private String song_album;
    public CommentAddRequest(String comment, String sname, String salbum){
        this.comment = comment;
        this.song_name = sname;
        this.song_album = salbum;
    }

    public String getSong_name() {
        return song_name;
    }

    public String getSong_album() {
        return song_album;
    }

    public String getComment() {
        return comment;
    }
}
class ShowCommentRequest {
    private String song_name;
    private String song_album;
    public ShowCommentRequest(String sname, String salbum){
        this.song_name = sname;
        this.song_album = salbum;
    }

    public String getSong_name() {
        return song_name;
    }

    public String getSong_album() {
        return song_album;
    }
}
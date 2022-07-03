package edu.bupt;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class UserClient {
    private String host = "localhost";
    private String command;
    private int port = 9090;
    private Scanner scanner;
    private Menus menus = new Menus();
    private ObjectOutputStream object_ostream;
    private ObjectInputStream object_istream;
    public UserClient(){
        scanner = new Scanner(System.in);

    }
    //以下为客户端行为

    private  boolean SongSearch(String sname, String salbum){
        SongSearchWithNameAndAlbum search = new SongSearchWithNameAndAlbum(sname, salbum);
        try {
            Socket socket = new Socket(host,port);
            if(socket.isConnected()){
                object_ostream = new ObjectOutputStream(socket.getOutputStream());
                object_istream = new ObjectInputStream(socket.getInputStream());
            }
            object_ostream.flush();
            object_ostream.writeObject(search);
            Boolean have_song = (Boolean) object_istream.readObject();
            socket.close();
            return have_song.booleanValue();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    private boolean AddSongList(String UID){
        String list_name;
        ArrayList<String> song_names = new ArrayList<String>();
        ArrayList<String> song_albums = new ArrayList<String>();
        Socket socket = null;
        try {
            socket = new Socket(host,port);
            if(socket.isConnected()){
                object_ostream = new ObjectOutputStream(socket.getOutputStream());
                object_istream = new ObjectInputStream(socket.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("请输入歌单名");
        list_name = scanner.nextLine();
        String tmp_name = "\0";
        String tmp_album = "\0";
        SongListAddRequest list_request = null;
        while (true){
            System.out.println("请输入歌名，若结束请输入分号");
            tmp_name = scanner.nextLine();
            if(tmp_name.equals(";")){break;}
            System.out.println("请输入歌名对应的专辑");
            tmp_album = scanner.nextLine();
            if(!SongSearch(tmp_name,tmp_album)){
                System.out.println("We don't have this song!");
                continue;
            }
            song_names.add(tmp_name);
            song_albums.add(tmp_album);
        }
        list_request = new SongListAddRequest(list_name, UID, song_names, song_albums);
        try {
            object_ostream.writeObject(list_request);
            Boolean result = (Boolean) object_istream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }



    //以下为各级菜单
    private void IntoMainMenu(String Uname){
        while (true){
            System.out.println(menus.main_menu+"请输入您的指令");
            command = scanner.next();
            if(command.equalsIgnoreCase("QUIT")){
                return;
            }
            if(command.equals("1")){
                IntoRecommendMenu(Uname);
            }


        }
    }


    /***
     *已登录用户的歌曲推荐选单
     * @param Uname: username
     */
    private void IntoRecommendMenu(String Uname){
        while (true){
            System.out.println(menus.recommend_menu_logged_in+"请输入您的指令");
            command = scanner.nextLine();
        }


    }
    private void IntoRecommendMenu(){
        while(true){
            System.out.println(menus.recommend_menu_unlogged_in+"请输入您的指令");
            command = scanner.nextLine();
        }
    }
}

class Menus{
    public final String main_menu = "------------主菜单------------------\n1.歌曲推荐\n2.歌单管理\n3.歌曲搜索\nQUIT.退出\n";
    public final String recommend_menu_logged_in = "------------歌曲推荐----------------\n1.昨日热度推荐\n2.个性推荐\nBACK.返回上一级\n";
    public final String recommend_menu_unlogged_in = "------------歌曲推荐----------------\n1.昨日热度推荐\n2.个性推荐（未登陆，不可用）\nBACK.返回上一级\n";
}

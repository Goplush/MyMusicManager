package edu.bupt;

import redis.clients.jedis.Jedis;

public class MusicManageServer {
    private Jedis redis_link;

    public MusicManageServer(){
        redis_link = new Jedis("localhost", 6379);
        redis_link.auth("K5nCjD$vwj8k^DeW");


    }
}
class SongTokenAddRequest{
    private String song_name, singer, new_token_name;
    SongTokenAddRequest(String sname, String singer, String token_name){
        song_name = sname;
        singer = singer;
        new_token_name = token_name;
    }

    public String getNew_token_name() {
        return new_token_name;
    }

    public String getSinger() {
        return singer;
    }

    public String getSong_name() {
        return song_name;
    }
}

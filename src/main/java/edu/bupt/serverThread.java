package edu.bupt;

//类serverThread
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.management.Query;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

class serverThread extends Thread
{
    Socket clientRequest;// 用户连接的通信套接字
    ObjectInputStream input;// 输入流
    ObjectOutputStream output;// 输出流

    // serverThread的构造器
    public serverThread(Socket s)
    {
        this.clientRequest = s;
        // 接收receiveServer传来的套接字

        InputStream istream;
        OutputStream ostream;
        try
        { // 初始化输入、输出流
            istream = clientRequest.getInputStream();
            ostream = clientRequest.getOutputStream();
            input = new ObjectInputStream(istream);
            output = new ObjectOutputStream(ostream);
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

    }

    /********
     * 以下为服务器支持的指令
     *
     *
     */
//存放用户歌曲标签请求的向量
    private Vector<SongTokenAddRequest> token_request_vec = new Vector<SongTokenAddRequest>();
    //以下为MongoDB初始化信息
    private ServerAddress serverAddress = new ServerAddress("123.56.121.72",27017);
    private MongoCredential credential;




    /*****
     * 用户请求新增标签，直接添加到管理员的请求向量中
     */
    class TokenAddToVecTask{
        private SongTokenAddRequest token_request;
        TokenAddToVecTask(SongTokenAddRequest token_request){
            this.token_request = token_request;
        }
        public void task() {
            token_request_vec.add(token_request);
        }
    }
    /***
     * 添加歌曲的任务，应当只能由管理员调用
     */
    class SongAddTask {

        private SongAddRequest song_request;
        SongAddTask (SongAddRequest sar){
            this.song_request = sar;
        }

        String MusicStore2Local(String ori_url){
            //此处为将外链的音乐转存到本地
            return ori_url;
        }
        public void Task() {
            //将歌曲保存到服务器后返回本地服务器的url
            String local = MusicStore2Local(song_request.getOrigin_url());
            List<ServerAddress> addrs = new ArrayList<ServerAddress>();
            addrs.add(serverAddress);
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credential = MongoCredential.createScramSha1Credential(
                    "musicmanageadmin", "MusicManageDB", "qBS42Luz$s7FU&J8".toCharArray());
            credentials.add(credential);
            try {
                //通过连接认证获取MongoDB连接
                MongoClient mongoClient = new MongoClient(addrs,credentials);
                MongoDatabase mongoDatabase = mongoClient.getDatabase("MusicManageDB");
                System.out.println("Connected to MongoDB");

                //选择Songs表（文档）
                MongoCollection<Document> songs_table = mongoDatabase.getCollection("Songs");
                System.out.println("collection Songs selected");

                //执行插入动作
                Document doc = new Document("title", song_request.getName()).append("singers", song_request.getSingers())
                        .append("album", song_request.getAlbum()).append("tags", song_request.getTags()).append("url", local);
                songs_table.insertOne(doc);
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("\n"+e.getMessage());
                //插入音乐插入失败的代码
            }

        }
    }

    /********
     * 用歌名和专辑搜索歌曲
     */
    class AlbumAndNameSongSearchTask{
        private SongSearchWithNameAndAlbum search_struct;
        public AlbumAndNameSongSearchTask(SongSearchWithNameAndAlbum sswa){
            this.search_struct = sswa;
        }
        public void task(){

            String sname = search_struct.getName();
            String salbum = search_struct.getAlbum();
            List<ServerAddress> addrs = new ArrayList<ServerAddress>();
            addrs.add(serverAddress);
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credential = MongoCredential.createScramSha1Credential(
                    "musicmanageadmin", "MusicManageDB", "qBS42Luz$s7FU&J8".toCharArray());
            credentials.add(credential);
            try {
                //通过连接认证获取MongoDB连接
                MongoClient mongoClient = new MongoClient(addrs, credentials);
                MongoDatabase mongoDatabase = mongoClient.getDatabase("MusicManageDB");
                System.out.println("Connected to MongoDB");

                //选择Songs表（集合）
                MongoCollection<Document> songs_table = mongoDatabase.getCollection("Songs");
                System.out.println("collection Songs selected");

                //按要求查询音乐
                BasicDBObject query = new BasicDBObject();
                query.put("name",sname);
                query.put("album",salbum);
                FindIterable<Document> result_set = songs_table.find(query).skip(0);
                output.writeObject(result_set);


            }catch (Exception e){
                e.printStackTrace();
                System.out.println("\n"+e.getMessage());
                //插入音乐失败后执行的代码
            }
        }

    }

    /******
     * 用户新建歌单
     */
    class SongListAddTask{
        private SongListAddRequest request;
        public SongListAddTask(SongListAddRequest slar){
            this.request = slar;
        }
        public void task(){
            List<ServerAddress> addrs = new ArrayList<ServerAddress>();
            addrs.add(serverAddress);
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credential = MongoCredential.createScramSha1Credential(
                    "musicmanageadmin", "MusicManageDB", "qBS42Luz$s7FU&J8".toCharArray());
            credentials.add(credential);
            try {
                //通过连接认证获取MongoDB连接
                MongoClient mongoClient = new MongoClient(addrs, credentials);
                MongoDatabase mongoDatabase = mongoClient.getDatabase("MusicManageDB");
                System.out.println("Connected to MongoDB");

                //选择musiclists表（集合）
                MongoCollection<Document> lists = mongoDatabase.getCollection("musiclists");
                System.out.println("collection musiclists selected");

                //按要求插入表格
                //新建歌单
                Document newlist = new Document("name",request.getList_name()).append("user_name", request.getUser_name());
                lists.insertOne(newlist);
                BasicDBObject query = new BasicDBObject();
                //向歌单中插入单首歌曲
                Iterator<String> snames_to_be_added_iter = request.getSong_names().iterator();
                Iterator<String> albums_to_be_added_iter = request.getSong_albums().iterator();
                String tmp_sname, tmp_salbum;
                while (snames_to_be_added_iter.hasNext()){
                    tmp_salbum = albums_to_be_added_iter.next();
                    tmp_sname = snames_to_be_added_iter.next();
                    AddSingleSongToListRequest request1 = new AddSingleSongToListRequest(request.getUser_name(),
                            request.getList_name(),tmp_sname,tmp_salbum);
                    AddSingleSongToListTask task = new AddSingleSongToListTask(request1);
                    task.task();
                }

            }catch (Exception e){
                e.printStackTrace();
                System.out.println("\n"+e.getMessage());
                //歌单创建失败后执行的代码
            }
        }


    }

    /*****
     * 向特定歌单中加入特定一首歌曲
     */
    class AddSingleSongToListTask{
        private AddSingleSongToListRequest request;
        public AddSingleSongToListTask(AddSingleSongToListRequest req){
            this.request = req;
        }
        public void task(){
            List<ServerAddress> addrs = new ArrayList<ServerAddress>();
            addrs.add(serverAddress);
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credential = MongoCredential.createScramSha1Credential(
                    "musicmanageadmin", "MusicManageDB", "qBS42Luz$s7FU&J8".toCharArray());
            credentials.add(credential);
            try {
                //通过连接认证获取MongoDB连接
                MongoClient mongoClient = new MongoClient(addrs, credentials);
                MongoDatabase mongoDatabase = mongoClient.getDatabase("MusicManageDB");
                System.out.println("Connected to MongoDB");

                //选择musiclists表（集合）
                MongoCollection<Document> lists = mongoDatabase.getCollection("musiclists");
                System.out.println("collection musiclists selected");

                //按要求插入
                //以下语句用于定位文档
                BasicDBObject query = new BasicDBObject();
                query.put("name", request.getList_name());
                query.put("user_name", request.getUser_name());
                //以下语句用于新建歌曲文档
                BasicDBObject song = new BasicDBObject();
                song.put("name",request.getSong_name());
                song.put("album",request.getSong_album());
                //以下语句用于将歌曲文档插入歌单
                BasicDBObject update = new BasicDBObject();
                update.put("$push",new BasicDBObject("songs",song));
                lists.updateOne(query, update);
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("\n"+e.getMessage());
                //歌单创建失败后执行的代码
            }
        }
    }


    @Override
    public void run()
    { // 线程的执行方法

        Object recv_obj = null;
        boolean done = false;
        while (!done)
        {
            try
            {
                recv_obj = input.readObject(); // 接收客户机指令
            } catch (IOException e)
            {
                System.out.println(e.getMessage());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (String.class.isInstance(recv_obj))
            {
                if(((String)recv_obj).equals("QUIT")){
                    done=true;
                    break;
                }
            } else if (SongAddRequest.class.isInstance(recv_obj))
            {
                SongAddTask task = new SongAddTask((SongAddRequest) recv_obj);
                task.Task();
                done = true;
            } else if (SongTokenAddRequest.class.isInstance(recv_obj))
            { // 命令query
                TokenAddToVecTask task = new TokenAddToVecTask((SongTokenAddRequest) recv_obj);
                task.task();
                done = true;
            }else if (SongSearchWithNameAndAlbum.class.isInstance(recv_obj)){
                AlbumAndNameSongSearchTask task = new AlbumAndNameSongSearchTask((SongSearchWithNameAndAlbum) recv_obj);
                task.task();
                done = true;
            }
            // else if …….. //在此可加入服务器的其他指令
            else
            {
                try {
                    output.writeObject("Command not Found! Please refer to the HELP!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }// end of while

        try
        {
            clientRequest.close(); // 关闭套接字
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        recv_obj =null;
    }// end of run
}


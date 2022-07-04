package edu.bupt;

//类serverThread
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

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
        public void task() {
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
                        .append("album", song_request.getAlbum()).append("url", local).append("heat",0);
                songs_table.insertOne(doc);
                Iterator<String> iter = song_request.getTagIter();
                while (iter.hasNext()){
                    AddSingleTokenToSongTask token_task = new AddSingleTokenToSongTask(new SongTokenAddRequest(
                            song_request.getName(), song_request.getAlbum(), iter.next()
                    ));
                    token_task.task();
                }
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
                BasicDBObject field = new BasicDBObject();
                field.put("name","123");
                field.put("singers",new Document());
                FindIterable<Document> result_set = songs_table.find(query).skip(0);
                output.writeObject(result_set);


            }catch (Exception e){
                e.printStackTrace();
                System.out.println("\n"+e.getMessage());
                //插入音乐失败后执行的代码
            }
        }
        public FindIterable<Document> server_search(){
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
                BasicDBObject field = new BasicDBObject();
                field.put("name","123");
                field.put("singers",new Document());
                FindIterable<Document> result_set = songs_table.find(query).skip(0);
                return result_set;
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("\n"+e.getMessage());
                return null;
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
                    SongSearchWithNameAndAlbum search_request = new SongSearchWithNameAndAlbum(
                            tmp_sname,tmp_salbum
                    );
                    AlbumAndNameSongSearchTask task0 = new AlbumAndNameSongSearchTask(search_request);
                    FindIterable<Document>res_set =  task0.server_search();
                    if(!res_set.iterator().hasNext()){
                        return;
                    }
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

    /*******
     * 将标签赋给歌曲
     */
    class AddSingleTokenToSongTask{
        private SongTokenAddRequest request;
        public AddSingleTokenToSongTask(SongTokenAddRequest star){
            this.request = star;
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
                MongoCollection<Document> lists = mongoDatabase.getCollection("Songs");
                System.out.println("collection musiclists selected");

                //按要求插入
                //以下语句用于定位歌曲
                BasicDBObject query = new BasicDBObject();
                query.put("name", request.getSong_name());
                query.put("album", request.getSong_album());
                //以下语句用于将Token插入对应的歌曲文档中
                BasicDBObject update = new BasicDBObject();
                update.put("$push",new BasicDBObject("tags",request.getToken()));
                lists.updateOne(query, update);


            }catch (Exception e){
                e.printStackTrace();
                System.out.println("\n"+e.getMessage());
                //歌单创建失败后执行的代码

            }
        }

    }

    /*******
     * 为歌曲添加/编辑简介的功能实现
     */
    class AddSongIntroTask{
        private AddSongIntroRequest request=null;
        public AddSongIntroTask(AddSongIntroRequest asir){
            this.request = asir;
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
                MongoCollection<Document> lists = mongoDatabase.getCollection("Songs");
                System.out.println("collection musiclists selected");

                //按要求插入
                //以下语句用于定位歌曲
                BasicDBObject query = new BasicDBObject();
                query.put("name", request.getSong_name());
                query.put("album", request.getSong_album());
                //以下语句用于将Token插入对应的歌曲文档中
                BasicDBObject update = new BasicDBObject();
                update.put("$set",new BasicDBObject("intro",request.getIntro()));
                lists.updateOne(query, update);
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("\n"+e.getMessage());
                //歌单创建失败后执行的代码

            }
        }
    }

    /******
     * 对歌曲的评论的保存
     */
    class CommentAddTask {
        private CommentAddRequest request = null;
        public CommentAddTask(CommentAddRequest car){
            this.request = car;
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

                //选择comments表（集合）
                MongoCollection<Document> lists = mongoDatabase.getCollection("comments");
                System.out.println("collection comments selected");

                //按要求插入

                Document doc = new Document("song_name",request.getSong_name()).append("song_album",request.getSong_album()).append("comment",request.getComment());
                lists.insertOne(doc);
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("\n"+e.getMessage());
                //歌单创建失败后执行的代码

            }
        }
    }

    /****
     * 请求歌曲的评价
     */
    class ShowCommentTask{
        private ShowCommentRequest request;
        public ShowCommentTask(ShowCommentRequest scr){
            this.request = scr;
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

                //选择comments表（集合）
                MongoCollection<Document> lists = mongoDatabase.getCollection("comments");
                System.out.println("collection musiclists selected");


                BasicDBObject query = new BasicDBObject();
                query.put("name", request.getSong_name());
                query.put("album", request.getSong_album());

                FindIterable result_set =  lists.find(query);
                System.out.println(result_set.toString());

            }catch (Exception e){
                e.printStackTrace();
                System.out.println("\n"+e.getMessage());
                //歌单创建失败后执行的代码

            }
        }
    }

    class SongHeatRecommand{
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

                //选择Songs表（集合）
                MongoCollection<Document> lists = mongoDatabase.getCollection("Songs");
                System.out.println("collection musiclists selected");

                FindIterable<Document> res_set = lists.find().sort(Sorts.descending("heat")) .batchSize(15)
                        .projection(Projections.fields(Projections.include("name")));
                Iterator<Document> doc_iter = res_set.iterator();
                while (doc_iter.hasNext()){
                    System.out.println(doc_iter.next().toJson());
                }
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
                task.task();
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
            }else if(AddSingleSongToListRequest.class.isInstance(recv_obj)){
                AddSingleSongToListTask task = new AddSingleSongToListTask((AddSingleSongToListRequest) recv_obj);
                task.task();
                done= true;
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

    public void main() {
        /***
        AddSongIntroRequest request = new AddSongIntroRequest(
                "Ketty", "exp","funny mud pee"
        );
        AddSongIntroTask task = new AddSongIntroTask(request);
         ****/
        ArrayList<String>singers = new ArrayList<String>();
        singers.add("三木");
        ArrayList<String> tags = new ArrayList<>();
        tags.add("情歌");
        tags.add("看开");
        SongAddRequest song_request = new SongAddRequest("http://mpge.5nd.com/2022/2022-3-3/3277460/1.mp3", "笑人生"
            , "笑人生", singers,tags );
        SongAddTask song_task = new SongAddTask(song_request);
        song_task.task();
    }
}


package edu.bupt;

//类serverThread
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

class serverThread extends Thread
{
    Socket clientRequest;// 用户连接的通信套接字
    ObjectInputStream input;// 输入流
    PrintWriter output;// 输出流

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
            output = new PrintWriter(ostream, true);
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        output.println("Welcome to the server!");
        // 客户机连接欢迎词
        output.println("Now is: " + new java.util.Date() + " " + "Port:"
                + clientRequest.getLocalPort());
        output.println("What can I do for you?");
    }

    /********
     * 以下为服务器支持的指令
     *
     *
     */
//存放用户歌曲请求的向量
    private Vector<SongTokenAddRequest> token_request_vec = new Vector<SongTokenAddRequest>();
    //以下为MongoDB初始化信息
    private ServerAddress serverAddress = new ServerAddress("123.56.121.72",27017);
    private MongoCredential credential;




    //以下是服务器支持的动作
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
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("\n"+e.getMessage());
                //插入音乐插入失败的代码
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
                    output.println("bye");
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
            }
            // else if …….. //在此可加入服务器的其他指令
            else
            {
                output.println("Command not Found! Please refer to the HELP!");
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


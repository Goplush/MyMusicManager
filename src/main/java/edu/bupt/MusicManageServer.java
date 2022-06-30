package edu.bupt;


import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import edu.bupt.RequestStruct;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import static com.mongodb.client.model.Filters.eq;

public class MusicManageServer {
    //存放用户歌曲请求的向量
    private Vector<SongTokenAddRequest> token_request_vec = new Vector<SongTokenAddRequest>();
    //以下为MongoDB初始化信息
    private ServerAddress serverAddress = new ServerAddress("123.56.121.72",27017);
    private MongoCredential credential;
    class TokenAddToVecTask implements Runnable{
        private SongTokenAddRequest request;
        TokenAddToVecTask(SongTokenAddRequest request){
            this.request = request;
        }
        @Override
        public void run() {
            token_request_vec.add(request);
        }
    }
    //添加歌曲的任务
    class SongAddTask implements Callable<Boolean>{

        private SongAddRequest request;
        SongAddTask (SongAddRequest sar){
            this.request = sar;
        }
        private TikaFileDistinguisher distinguisher = new TikaFileDistinguisher();

        String MusicStore2Local(String ori_url){
            //此处为将外链的音乐转存到本地
            return ori_url;
        }
        @Override
        public Boolean call() throws Exception {
            if(!distinguisher.IsAudio(request.getOrigin_url())){
                //此处为导入的url不是音频时的异常处理代码
                return Boolean.FALSE;
            }
            //将歌曲保存到服务器后返回本地服务器的url
            String local = MusicStore2Local(request.getOrigin_url());
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
                Document doc = new Document("title",request.getName()).append("singers",request.getSingers())
                        .append("album",request.getAlbum()).append("tags",request.getTags()).append("url", local);
                return Boolean.TRUE;
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("\n"+e.getMessage());
                return Boolean.FALSE;
            }

        }
    }
}





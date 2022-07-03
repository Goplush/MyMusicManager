package edu.bupt.login_related.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {

    //数据库连接驱动
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    //数据库连接地址
    private static final String URL = "jdbc:mysql://http://123.56.121.72:888/phpmyadmin_cc515fda64452b78/db_structure.php?db=java_test&table=&server=1&target=&token=61bb9b24735886056575f1f8b6add6de#PMAURL-1:sql.php?db=java_test&table=USER&server=1&target=&token=61bb9b24735886056575f1f8b6add6de";

    // 数据库用户名
    private static final String USERNAME ="root";

    // 数据库密码
    private static final String PASSWORD ="a0eff179f26a6786";

    private Connection conn;

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER);
        conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        return conn;
    }

    // 关闭数据库连接
    public void close() throws Exception{
        if(this.conn != null) {
            try{
                this.conn.close();
            }catch(Exception e) {
                throw e;
            }
        }
    }
}

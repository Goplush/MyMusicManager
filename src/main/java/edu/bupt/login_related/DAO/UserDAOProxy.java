package edu.bupt.login_related.DAO;

import edu.bupt.login_related.VO.User;
import java.sql.SQLException;

public class UserDAOProxy {

    private UserDAO dao;

    public UserDAOProxy() throws Exception {
        this.dao = (UserDAO) new UserDAOlmpl();
    }


    // 根据username查找
    public User findByUsername(String username) throws Exception {
        User user = new User();
        try {
            //在此dao代理类,调用dao实现类的同名方法实现物理数据库的操作
            user = this.dao.findByUsername(username);
        }catch(SQLException e) {
            throw e;
        }finally {
            // 关闭数据库
            //this.conn.close();
        }
        return user;
    }


    // 修改用户信息
    public boolean doUpdate(User user) throws Exception {
        boolean flag = false;
        try {
            if(this.dao.findByUsername(user.getUsername()) != null) {
                flag = this.dao.doUpdate(user);
            }
        }catch(Exception e) {
            throw e;
        }finally {
            //this.conn.close();
        }
        return flag;
    }

    // 增加用户

    public boolean doCreate(User user) throws Exception {
        boolean flag = false;
        try {
            flag = this.dao.doCreate(user);
        }catch(Exception e) {
            throw e;
        }finally {
            //this.conn.close();
        }
        return flag;
    }
}

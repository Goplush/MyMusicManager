package edu.bupt.login_related.FACTORY;

import edu.bupt.DAO.UserDAO;
import edu.bupt.DAO.UserDAOProxy;

public class UserFactory {

    // 静态方法,返回值类型为Dao接口类型,实际返回Dao的实现类
    public static UserDAO getUserDAOInstance() throws Exception {
        return (UserDAO) new UserDAOProxy();
    }
}

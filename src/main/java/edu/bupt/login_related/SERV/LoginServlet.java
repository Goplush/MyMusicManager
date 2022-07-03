package edu.bupt.login_related.SERV;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bupt.DAO.UserDAO;
import edu.bupt.login_related.FACTORY.UserFactory;
import edu.bupt.VO.User;

import javax.script.ScriptContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/loginServlet")
public class LoginServlet {

    private DataFlavor request;
    // 获取前端请求数据
    String username = request.getParameter("username");
    String password = request.getParameter("password");


    Map<String,Object> map = new HashMap<String,Object>();

    UserDAO ud = null;
      try{
        try {
            ud = UserFactory.getUserDAOInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        User user = null;
        try {
            user = ud.findByUsername(username);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        // 判断用户名是否存在
        ScriptContext response = null;
        if (user.getUsername() == null) {
            map.put("userFlag", false);
            map.put("loginFlag",false);
            ObjectMapper mapper = new ObjectMapper();
            try {
                mapper.writeValue(response.getWriter(),map);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            // 判断密码是否正确
            if (!user.getPassword().equals(password)) {
                map.put("userFlag",true);
                map.put("loginFlag",false);
                ObjectMapper mapper = new ObjectMapper();
                try {
                    mapper.writeValue(response.getWriter(),map);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                map.put("userFlag",true);
                map.put("loginFlag",true);
                map.put("user",user);
                ObjectMapper mapper = new ObjectMapper();
                try {
                    mapper.writeValue(response.getWriter(),map);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

        }
    } catch (Exception e)
    {
        e.printStackTrace();
    }
}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    private void doPost(HttpServletRequest request, HttpServletResponse response) {

    }
}

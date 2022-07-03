package edu.bupt.SERV;

import edu.bupt.DAO.UserDAO;
import edu.bupt.FACTORY.UserFactory;
import edu.bupt.VO.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = "/loginToMainServlet")
public class LoginToMainServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 拿到登录的用户名
        String username = request.getParameter("username");

        UserDAO ud= null;
        try {
            ud = UserFactory.getUserDAOInstance();

            // 根据用户名找到该用户的全部信息
            User user = ud.findByUsername(username);

            // 把对象拆分成集合
            Map map = new HashMap();
            map.put("username",user.getUsername());
            map.put("name",user.getName());
            map.put("sex",user.getSex());
            map.put("age",user.getAge());
            map.put("password",user.getPassword());

            // 设置登录状态认证，并知道本次会话属于谁（在需要登录才能进行操作的地方就要进行认证）
            request.getSession().setAttribute("auth",user.getUsername());

            if (request.getSession().getAttribute("auth") != null) {
                // 把拿到的信息存入session中，重定向到主页，即可通过自定义标签拿到并输出
                request.getSession().setAttribute("map",map);
                response.sendRedirect(request.getContextPath()+"/main.jsp");
            } else {
                response.sendRedirect(request.getContextPath()+"/index.jsp");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}

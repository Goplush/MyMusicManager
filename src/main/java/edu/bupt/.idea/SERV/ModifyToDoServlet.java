package edu.bupt.SERV;

import edu.bupt.DAO.UserDAO;
import edu.bupt.FACTORY.UserFactory;
import edu.bupt.VO.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/modifyToDoServlet")
public class ModifyToDoServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 拿到用户名
        String u = request.getParameter("u");

        // 拿到用户输入的修改信息
        String pw = request.getParameter("pw");
        String n = request.getParameter("n");
        String s = request.getParameter("s");
        int a = Integer.parseInt(request.getParameter("a"));

        // 把信息封装成User对象
        User userModify = new User();
        userModify.setUsername(u);
        userModify.setPassword(pw);
        userModify.setName(n);
        userModify.setSex(s);
        userModify.setAge(a);

        // 对数据库中的用户信息作出修改
        UserDAO ud= null;
        try {

            ud = UserFactory.getUserDAOInstance();
            boolean flag  = ud.doUpdate(userModify);

            if (request.getSession().getAttribute("auth") != null) {
                // 把信息存入request中，转发到doModify.jsp页面用Bean对信息作出修改并打输出到页面上
                request.setAttribute("userModify", userModify);
                request.getRequestDispatcher("/doModify.jsp").forward(request, response);
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

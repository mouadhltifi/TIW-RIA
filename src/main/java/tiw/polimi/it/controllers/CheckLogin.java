package tiw.polimi.it.controllers;

import org.thymeleaf.context.WebContext;
import tiw.polimi.it.beans.User;
import tiw.polimi.it.dao.UserDAO;
import tiw.polimi.it.stuffToDelete.MyColors;
import tiw.polimi.it.utils.Const;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ResourceBundle;

@WebServlet("/checkLogin")
@MultipartConfig
public class CheckLogin extends HttpServletDBConnected {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(MyColors.ANSI_RED +"______________________________________________________");
        System.out.println("dentro a checkLogin");
        System.out.println(MyColors.ANSI_RED +"______________________________________________________"+MyColors.ANSI_RESET);



        ResourceBundle lang = HttpServletThymeleaf.findLanguage(req);
        ServletContext context = getServletContext();
        WebContext webContext = new WebContext(req, resp, context);
        HttpSession session;

        String loginPage = "loginPage";
        String homePage = getServletContext().getContextPath() + "/GoToHome";
        String indexPage = getServletContext().getContextPath() + "/index";
        String uMail = req.getParameter("email");
        String uPassword = req.getParameter("password");
        UserDAO userDAO = new UserDAO(conn, lang.getLocale().getLanguage(), lang.getLocale().getCountry());
        User user;
        boolean loginError = false, generalError = false;


        // request processed only if the parameters are sent correctly
        if (uMail != null && !uMail.isEmpty() && uPassword != null && !uPassword.isEmpty()) {

            try {
                user = userDAO.findUser(uMail, uPassword);

                if (user != null) {
                    session = req.getSession(true);
                    session.setMaxInactiveInterval(Const.sessionExpireTime);

                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    session.setAttribute("user", user);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(user.getName());

                    System.out.println(MyColors.ANSI_GREEN + "succesfully logged" +MyColors.ANSI_RESET);
                    return;
                } else {
                    loginError = true;
                }
            } catch (SQLException sqlException) {
                loginError = true;
                generalError = true;
                sqlException.printStackTrace();
            }


            // if there has been an error the user is sent back to its initial page
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("missing credentials");

        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect("/index");
    }
}

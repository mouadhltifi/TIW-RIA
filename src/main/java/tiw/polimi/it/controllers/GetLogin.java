package tiw.polimi.it.controllers;

import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ResourceBundle;

@MultipartConfig

@WebServlet("/index")
public class GetLogin extends HttpServletThymeleaf {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResourceBundle lang = findLanguage(req);
        ServletContext context = getServletContext();
        WebContext webContext = new WebContext(req,resp,context);
        String page = "loginPage";

        webContext.setVariable("lang",lang);
        webContext.setVariable("loginError",false);
        webContext.setVariable("registerError",false);
        thymeleaf.process(page,webContext,resp.getWriter());
    }
}

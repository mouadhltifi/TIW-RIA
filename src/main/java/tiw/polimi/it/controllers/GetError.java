package tiw.polimi.it.controllers;

import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ResourceBundle;

@MultipartConfig

@WebServlet("/error")
public class GetError extends HttpServletThymeleaf {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ResourceBundle lang = findLanguage(req);
        ServletContext context = getServletContext();
        WebContext webContext = new WebContext(req, resp, context);
        String page = "errorPage";
        String errorMessage = req.getParameter("errorMessage");
        HttpSession session = req.getSession(false);

        String code = req.getParameter("code");
        int codeInt;

        if (code == null) {
            resp.sendRedirect("/index");
        } else {
            try {
                codeInt = Integer.parseInt(code);

                //translated errors
                if (codeInt >= 400 && (codeInt <= 431 || codeInt >= 451) && (codeInt <= 451 || codeInt >= 500) && codeInt <= 511) {
                    webContext.setVariable("lang", lang);
                    webContext.setVariable("errorCode", codeInt);
                    webContext.setVariable("logged", session != null);
                    thymeleaf.process(page, webContext, resp.getWriter());
                } else if(errorMessage!=null &&!errorMessage.isEmpty()) {
                    webContext.setVariable("errorMessage",errorMessage);

                }else {
                        //altro tipo di errore
                        resp.sendRedirect("/index");
                    }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                resp.sendRedirect("/index");
            }
            thymeleaf.process(page, webContext, resp.getWriter());


        }
    }
}

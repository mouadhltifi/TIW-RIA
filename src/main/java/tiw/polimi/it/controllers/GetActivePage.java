package tiw.polimi.it.controllers;

import org.thymeleaf.context.WebContext;
import tiw.polimi.it.beans.User;
import tiw.polimi.it.dao.UserDAO;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

@WebServlet("/activePage")
@MultipartConfig

public class GetActivePage extends HttpServletThymeleaf {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ResourceBundle lang = findLanguage(req);


		ServletContext context = getServletContext();
		WebContext webContext = new WebContext(req,resp,context);
		String page = "activePage";
		User user = (User) req.getSession().getAttribute("user");

		webContext.setVariable("lang",lang);
		webContext.setVariable("user",user);
		thymeleaf.process(page,webContext,resp.getWriter());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}

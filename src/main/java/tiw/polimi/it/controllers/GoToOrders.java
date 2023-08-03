package tiw.polimi.it.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.thymeleaf.context.WebContext;
import tiw.polimi.it.beans.Order;
import tiw.polimi.it.beans.User;
import tiw.polimi.it.dao.OrderDAO;
import tiw.polimi.it.dao.UserDAO;
import tiw.polimi.it.stuffToDelete.MyColors;

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

@WebServlet("/GotoOrders")
@MultipartConfig

public class GoToOrders extends HttpServletDBConnected {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        OrderDAO orderDAO = new OrderDAO(conn);
        User user = (User) req.getSession().getAttribute("user");
        List<Order> orderList;

        System.out.println(MyColors.ANSI_RED + "dentro a : " + getClass().getName() +MyColors.ANSI_RESET);
        System.out.println("_____________________________________________________________");
        System.out.println(MyColors.ANSI_RED + "_____________________________________________________________" +MyColors.ANSI_RESET);


        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");


        try {
            orderList = orderDAO.getOrdersByUserID(user.getUserId());

            System.out.println("_____________________________________________________________");
            System.out.println(MyColors.ANSI_RED + "orderList : " +MyColors.ANSI_RESET);
            System.out.println(orderList);
            System.out.println(MyColors.ANSI_RED + "_____________________________________________________________" +MyColors.ANSI_RESET);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(gson.toJson(orderList));


        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"sql exception , error getting orders");
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}

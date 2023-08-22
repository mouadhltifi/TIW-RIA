package tiw.polimi.it.controllers;

import com.google.gson.Gson;
import org.thymeleaf.context.WebContext;
import tiw.polimi.it.beans.OnSale;
import tiw.polimi.it.beans.User;
import tiw.polimi.it.dao.OnSaleDAO;
import tiw.polimi.it.utils.SearchItemsMessage;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

@WebServlet("/searchItems")
@MultipartConfig

public class SearchItems extends HttpServletDBConnected{

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String keySearch = req.getParameter("key");
        String resultPage = "risultati";
        Gson gson = new Gson();
        List<SearchItemsMessage> messageList;


        User user = (User) req.getSession().getAttribute("user");
        OnSale onSale;

        OnSaleDAO onSaleDAO = new OnSaleDAO(conn);




        //check if the key search is correct
        if(keySearch == null || keySearch.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            //resp.getWriter().println("key parameter is null");
            return;
        }

        try {

            //itemList = itemDAO.findItemsByKey(keySearch);

            //onSale = onSaleDAO.findElementsByKey(keySearch);

            messageList = onSaleDAO.getMessageListFromKey(keySearch);


            System.out.println(messageList);



            if(messageList.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print(gson.toJson("not found"));
                return;
            }else {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(messageList));
            }
//            webContext.setVariable("id_in_vendita",onSale.getIds_inVendita());
//            webContext.setVariable("firstVersionPage",firstVersionPage);
//            webContext.setVariable("secondVersionPage",secondVersionPage);
//            webContext.setVariable("lang",lang);
//            thymeleaf.process(resultPage,webContext,resp.getWriter());


        } catch (SQLException sqlException) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("internal error ");
            return;
        }


    }
}

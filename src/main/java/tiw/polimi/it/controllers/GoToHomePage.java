package tiw.polimi.it.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.thymeleaf.context.WebContext;
import tiw.polimi.it.beans.Item;
import tiw.polimi.it.beans.User;
import tiw.polimi.it.dao.ItemDAO;
import tiw.polimi.it.dao.UserDAO;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

@MultipartConfig
@WebServlet("/GoToHome")
public class GoToHomePage extends HttpServletThymeleaf {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        ResourceBundle lang = HttpServletThymeleaf.findLanguage(req);
        ServletContext servletContext = getServletContext();
        WebContext webContext = new WebContext(req, resp, servletContext);
        String path = getServletContext().getContextPath();
        String page = "homePage";
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        UserDAO userDAO = new UserDAO(conn);
        boolean isCookieValid = true;
        LinkedList<Double> itemIds = null;


        //versione con lista cookie
        List<Cookie> cookieList = new LinkedList<>();

        ItemDAO itemDAO;
        User user = (User) req.getSession().getAttribute("user");
        LinkedList<Item> itemsList = null;

        //get user info
        try {
            user = userDAO.findUser(user.getEmail(), user.getPassword());
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "resource not found");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        /* check if there are items stored in cookies */
        Cookie[] cookies = req.getCookies();
        itemDAO = new ItemDAO(conn);

        for (Cookie c : cookies
        ) {
            if (c.getName().equalsIgnoreCase("itemList")) {

                itemsList = new LinkedList<>();
                //c.setMaxAge(0);
                String jsonCookie = URLDecoder.decode(c.getValue(),StandardCharsets.UTF_8);
                itemIds= gson.fromJson(jsonCookie, (Type) LinkedList.class);

                System.out.println("values of item ids \n" + itemIds);

                for (Double item : itemIds
                     ) {
                    try {
                        itemsList.push(itemDAO.getItemById(item));
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                        resp.sendRedirect("/error?code=500");
                        return;                    }
                }
                System.out.println("values of item ids \n" + itemIds);

            }
        }

        /*
            no items id stored in cookies yet
         */
        if (itemIds == null) {
            try {

                itemsList = itemDAO.getRandomItems();
                //itemIds = itemDAO.getRandomItemsIds();
                itemIds = new LinkedList<>();
                for (Item item: itemsList
                     ) {
                    itemIds.push(item.getId_item()-0.0);
                }

               //LinkedList<Integer> itemID = new LinkedList(1,3,4,5)

                //Cookie myCookie = new Cookie("itemList", URLEncoder.encode(gson.toJson(itemIds), StandardCharsets.UTF_8));
                String jsonString = gson.toJson(itemIds,LinkedList.class);
                System.out.println(jsonString);
                Cookie myCookie = new Cookie("itemList",URLEncoder.encode(jsonString,StandardCharsets.UTF_8));
                System.out.println(myCookie);
                resp.addCookie(myCookie);

            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                resp.sendRedirect("/error?code=500");
                return;
            }catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }


        try {
//            webContext.setVariable("user", user);
//            webContext.setVariable("itemList", itemsList);
//            webContext.setVariable("lang",lang);
//            thymeleaf.process(page, webContext, resp.getWriter());


            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(itemsList));

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


    }
}

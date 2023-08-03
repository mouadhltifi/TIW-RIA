package tiw.polimi.it.controllers;


import com.google.gson.*;
import org.thymeleaf.context.WebContext;
import tiw.polimi.it.beans.*;
import tiw.polimi.it.dao.ItemDAO;
import tiw.polimi.it.dao.OnSaleDAO;
import tiw.polimi.it.stuffToDelete.MyColors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

@WebServlet("/updateShoppingCart")
@MultipartConfig
public class UpdateShoppingCart extends HttpServletThymeleaf{

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        Seller seller;
        Item itemAdded;
        System.out.println("eccoci in update cart : "

                + req.getParameter("cartList"));


        String cartString = URLDecoder.decode (req.getParameter("cartList"), StandardCharsets.UTF_8);
        List<ShoppingCart> cartList =  (ArrayList<ShoppingCart>) gson.fromJson(cartString,(Type) List.class);


        if(cartString != null && !cartList.isEmpty()){

            cartList = createCartListFromJson(cartString);


        }else {
            System.out.println("cartList `è nullo : " +cartList);

            //send redirect
        }

    }


    private List<ShoppingCart> createCartListFromJson(String cartString) {
        Gson gson = new Gson();
        List<ShoppingCart> list = new LinkedList<>();
        Gson gson2 = new Gson();
        JsonArray arry = new JsonParser().parse(cartString).getAsJsonArray();
        for (JsonElement jsonElement : arry) {
            list.add(gson.fromJson(jsonElement, ShoppingCart.class));
        }



        return list;
    }
}

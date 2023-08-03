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
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

@MultipartConfig
@WebServlet("/addToCart")


public class AddToCart extends HttpServletDBConnected{

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        ResourceBundle lang = HttpServletThymeleaf.findLanguage(req);
        ServletContext servletContext = getServletContext();
        WebContext webContext = new WebContext(req, resp, servletContext);
        String path = getServletContext().getContextPath();
        String page = "cartPage";
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        User user = (User) req.getSession().getAttribute("user");
        ItemDAO itemDAO = new ItemDAO(conn);
        OnSaleDAO onSaleDAO = new OnSaleDAO(conn);
        OnSale onSale;
        Seller seller;
        Item itemAdded;
        System.out.println("eccoci in addtocart id in vendita : "
                + req.getParameter("id_in_vendita")+ " qty : "
                + req.getParameter("qty") + "itemId : "
                + req.getParameter("itemId") + " cartList :"
                + req.getParameter("cartList"));

        boolean dataError = false;
        String quantityString = req.getParameter("qty");
        String idInVenditaString = req.getParameter("id_in_vendita");
        String itemIdString = req.getParameter("itemId");
        String cartString = URLDecoder.decode (req.getParameter("cartList"),StandardCharsets.UTF_8);
        String cartString2 = req.getParameter("cartList");

        System.out.println(MyColors.ANSI_PURPLE + "String cart zero :" +cartString + MyColors.ANSI_RESET);
        System.out.println(MyColors.ANSI_CYAN + "string cart :" +cartString2 + MyColors.ANSI_RESET);


        //List<ShoppingCart> cartList = (List<ShoppingCart>) req.getSession(false).getAttribute("cart");
        List<ShoppingCart> cartList =  (ArrayList<ShoppingCart>) gson.fromJson(cartString,(Type) List.class);


       // Array cart1List =  gson.fromJson(cartString,(Type) Array.class);
        System.out.println(MyColors.ANSI_PURPLE + "cartList zero :" +cartList + MyColors.ANSI_RESET);
        System.out.println(MyColors.ANSI_PURPLE + "cartList zero :" +cartList + MyColors.ANSI_RESET);


       // List<ShoppingCart> cartList1 = gson.fromJson(cartString2, ArrayList.class);
       // System.out.println(MyColors.ANSI_CYAN + "cartList1 :" +cartList1 + MyColors.ANSI_RESET);
        int quantity = 0;



        //ShoppingCart shoppingCart = new ShoppingCart();

        if(quantityString != null && idInVenditaString !=null && itemIdString!= null){
            try {

                System.out.println("prima di conversione");


                onSale = onSaleDAO.getFromIdOnSale(Integer.parseInt(idInVenditaString));
                System.out.println(onSale);
                itemAdded = onSale.getItems().get(0);
                seller = onSale.getSellers().get(0);
                quantity = Integer.parseInt(quantityString);

                if(onSale.getItems()!=null && quantity >0){
                    //itemId is not corrupted
                    if(cartList == null) {
                        System.out.println("carrello vuoto");
                        //cart is empty
                        cartList = new LinkedList<>();
                        addToCartList(cartList,itemAdded,onSale,quantity,seller);
                    }
                    else {
                        System.out.println(MyColors.ANSI_PURPLE +"cartList not null : " + cartList);


                        cartList = createCartListFromJson(cartString);

                        System.out.println(cartString);

                        findFromCartList(cartList,quantity,onSale);

                    }
                    String json = gson.toJson(cartList);

                    //store into session
                    req.getSession().setAttribute("hashCode",json.hashCode());

                    //staff to delte
                    System.out.println("cartList hashCode: " + cartList.hashCode());


                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println(json);
//                    webContext.setVariable("lang", lang);
//                    webContext.setVariable("user", user);
//                    webContext.setVariable("cartList",cartList);
//                    webContext.setVariable("fromAddToCart",true);
//                    HttpServletThymeleaf.thymeleaf.process(page, webContext, resp.getWriter());
                    return;


                    //prepare WebContext

                }else {
                    //data corrupted
                    System.out.println("errore nei file di input");
                    dataError = true;
                    resp.sendRedirect("/error?code=500");

                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                resp.sendRedirect("/error?code=500");
            }
        }else {
            System.out.println("qualcosa è a null : "+ quantityString + idInVenditaString + itemIdString);

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


    private void findFromCartList(List<ShoppingCart> cartList, int quantity, OnSale onSale) {

        for (ShoppingCart cart : cartList
        ) {
            if(cart.getSeller().getSellerId() == onSale.getSellers().get(0).getSellerId()) {
                //buying other items from the same seller
                System.out.println("compro da stesso venditore");
                cart.setShippingPrice();
                cart.updateSellerCart(onSale.getSellers().get(0),quantity,onSale.getItems().get(0),onSale.getPrices().get(0));
                return;
        }
        }
            //buying for the first time to this seller
            System.out.println("acquisto da nuovo venditore");

            addToCartList(cartList,onSale.getItems().get(0),onSale,quantity,onSale.getSellers().get(0));

    }


    private void addToCartList(List<ShoppingCart> cartList, Item itemAdded, OnSale onSale, int quantity, Seller seller) {
        List<LightItem> tempList = new LinkedList<>();
        tempList.add(new LightItem(itemAdded.getName(),itemAdded.getId_item(),quantity));
        cartList.add(new ShoppingCart(
                tempList,
                onSale.getPrices().get(0)*quantity,
                quantity,
                seller
                ));
    }


}

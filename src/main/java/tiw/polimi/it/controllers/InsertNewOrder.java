package tiw.polimi.it.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.thymeleaf.context.WebContext;
import tiw.polimi.it.beans.*;
import tiw.polimi.it.dao.ItemDAO;
import tiw.polimi.it.dao.OnSaleDAO;
import tiw.polimi.it.dao.OrderDAO;
import tiw.polimi.it.dao.SellerDAO;
import tiw.polimi.it.stuffToDelete.MyColors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

@WebServlet("/insertNewOrder")
@MultipartConfig
public class InsertNewOrder extends HttpServletThymeleaf {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        String cartString = URLDecoder.decode(req.getParameter("cartList"), StandardCharsets.UTF_8);
        User user = (User) req.getSession().getAttribute("user");

        String sellerIdString = req.getParameter("sellerdID");
        //String cartString = req.getParameter("cartId"); //forse non serve
        OrderDAO orderDAO = new OrderDAO(conn);


        List<ShoppingCart> cartList = (ArrayList<ShoppingCart>) gson.fromJson(cartString, (Type) List.class);
        int sellerdId = -1;
        int hashCode = (int) req.getSession().getAttribute("hashCode");
        ShoppingCart purchasedCart;


        System.out.println(MyColors.ANSI_RED + "dentro a : " + getClass().getName() + MyColors.ANSI_RESET);
        System.out.println("_____________________________________________________________");
        System.out.println(MyColors.ANSI_RED + "_____________________________________________________________" + MyColors.ANSI_RESET);


        //check input
        if (sellerIdString != null && !sellerIdString.isEmpty() &&
                cartString != null && !cartString.isEmpty()) {


            if (cartList != null) {


                System.out.println(MyColors.ANSI_BLUE + "______________________________________________________");
                System.out.println("session hashCode : " + hashCode);
                System.out.println("cartString hashCode : " + cartString.hashCode());
                System.out.println(MyColors.ANSI_RESET + "______________________________________________________");

                //hashCode = Integer.parseInt(givenHashCode);

                try {
                    sellerdId = Integer.parseInt(sellerIdString);
                    cartList = createCartListFromJson(cartString);

                    if(!checkValidity(cartList, sellerdId)) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"shopping cart not consistent");
                    }


                    purchasedCart = removeCartFromCartList(cartList, sellerdId);

                    //occhio al possibile manual commit
                    if (orderDAO.insertNewOrder(purchasedCart, user)) {
                        //insert ok

                        System.out.println(MyColors.ANSI_GREEN + "ordine inserito correttamente" + MyColors.ANSI_RESET);

//                        RequestDispatcher dispatcher = req.getRequestDispatcher(page);
//                        dispatcher.forward(req, resp);
                        resp.setStatus(HttpServletResponse.SC_OK);
                        resp.getWriter().write("ok");


                    } else {
                        //insert ko

                        //restore previous conditions
                        cartList.add(purchasedCart);


                        resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "error purchasing new order");

                    }

                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("mysql insert error");

//                    sqlException.printStackTrace();
//                    resp.sendRedirect("/error?code=500");
                }

            }else
            {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "cartList is null!!!");

            }

        } else {
            System.out.println(MyColors.ANSI_RED + "qualche input a null !!" + MyColors.ANSI_RESET);

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "something wrong with the input in HandleCartUpdate");

        }


    }

    private boolean checkValidity(List<ShoppingCart> cartList, int sellerdId) throws SQLException {
        OnSaleDAO onSaleDAO = new OnSaleDAO(conn);
        OnSale onSale = null;
        SellerDAO sellerDAO = new SellerDAO(conn);
        ItemDAO itemDAO = new ItemDAO(conn);
        Double totalPriceOnlyItems = 0.0;

        for (ShoppingCart cart : cartList
        ) {
            if (sellerdId == cart.getSeller().getSellerId()) {

                //checking all items and totalPrice
                for (LightItem item : cart.getItem()
                ) {

                    if (item.getItemId() < 0 || item.getQty() < 1) {

                        System.out.println(MyColors.ANSI_RED + "dentro a : " + getClass().getName() + MyColors.ANSI_RESET);
                        System.out.println("_____________________________________________________________");
                        System.out.println("item id or qty are bad ");
                        System.out.println(MyColors.ANSI_RED + "_____________________________________________________________" + MyColors.ANSI_RESET);

                        return false;
                    }


                    onSale = onSaleDAO.getInfoByItemId(item.getItemId());

                    if (onSale == null || !item.myEquals(onSale.getItems().get(0))) {
                        System.out.println("not same items");
                        return false;
                    }

                    totalPriceOnlyItems += item.getQty() * onSale.getPrices().get(0);

                }

                if(totalPriceOnlyItems != cart.getTotalPrice()) {
                    System.out.println("total price is diffent!!!");
                    return false;
                }

                //since all items are ok , double check : calculate again Shipping Price
                cart.setShippingPrice();
                return true;
            }
        }

        return false;
    }


    private ShoppingCart removeCartFromCartList(List<ShoppingCart> cartList, int sellerdId) {
        //cartList.removeIf(cart -> cart.getSeller().getSellerId() == sellerdId);


        for (ShoppingCart cart : cartList
        ) {
            if (cart.getSeller().getSellerId() == sellerdId) {
                cartList.remove(cart);
                return cart;
            }
        }

        throw new IllegalArgumentException("no item found to remove");

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


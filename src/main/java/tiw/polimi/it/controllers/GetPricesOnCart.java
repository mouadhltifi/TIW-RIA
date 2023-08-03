package tiw.polimi.it.controllers;

import com.google.gson.Gson;
import org.thymeleaf.context.WebContext;
import tiw.polimi.it.beans.OnSale;
import tiw.polimi.it.beans.Seller;
import tiw.polimi.it.beans.ShoppingCart;
import tiw.polimi.it.beans.User;
import tiw.polimi.it.dao.ItemDAO;
import tiw.polimi.it.dao.OnSaleDAO;
import tiw.polimi.it.stuffToDelete.MyColors;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@MultipartConfig
@WebServlet("/getPricesOnCart")

public class GetPricesOnCart extends HttpServletThymeleaf {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        OnSaleDAO onSaleDAO = new OnSaleDAO(conn);

        User user = (User) req.getSession().getAttribute("user");
        int userItemId;
        OnSale onSale;
        WebContext webContext = new WebContext(req, resp, getServletContext());
        boolean firstVersionPage = false;
        boolean secondVersionPage = true;
        String page = "risultati";
        Gson gson = new Gson();
        LinkedList<Double> itemIdsFromCookies = null;

        String cartString = req.getParameter("cartList");



        List<ShoppingCart> cartList = (List<ShoppingCart>) gson.fromJson(cartString,(Type) LinkedList.class);


        try {
            userItemId = Integer.parseInt(req.getParameter("itemId"));
            onSale = onSaleDAO.getInfoByItemId(userItemId);
            HashMap<Integer, List<Double>> pricesOnCart = prepareHashMap_SellerId_TotalPrice(cartList, onSale.getSellers());

            resp.setStatus(HttpServletResponse.SC_OK);

            resp.getWriter().println(gson.toJson(pricesOnCart));

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("sql exception");
        }







}


    private HashMap<Integer, List<Double>> prepareHashMap_SellerId_TotalPrice(List<ShoppingCart> cartList, List<Seller> sellers) {
        HashMap<Integer, List<Double>> result = new HashMap<>();

        List<Double> qty_totalPrice = new ArrayList<>();
        double truncatedDouble;


        System.out.println("creo HaskMap \n " + cartList + "\n" + sellers);
        if (cartList == null || cartList.isEmpty()) return null;
        for (ShoppingCart cart : cartList
        ) {
            for (Seller seller : sellers
            ) {
                if (seller.getSellerId() == cart.getSeller().getSellerId()) {
                    System.out.println("trovato da stesso venditore");

                    truncatedDouble = BigDecimal.valueOf(cart.getTotalPrice())
                            .setScale(3, RoundingMode.HALF_UP)
                            .doubleValue();

                    qty_totalPrice.add(truncatedDouble);
                    qty_totalPrice.add(cart.getQuantity() - 0.0);
                    result.put(seller.getSellerId(), qty_totalPrice);
                    break;
                }
            }
        }
        System.out.println("resultSet : " + result);
        return result;
    }

}

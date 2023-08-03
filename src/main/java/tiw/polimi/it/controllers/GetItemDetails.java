package tiw.polimi.it.controllers;

import com.google.gson.*;
import org.thymeleaf.context.WebContext;
import tiw.polimi.it.beans.*;
import tiw.polimi.it.dao.ItemDAO;
import tiw.polimi.it.dao.OnSaleDAO;
import tiw.polimi.it.stuffToDelete.MyColors;
import tiw.polimi.it.utils.itemDetailsMessage;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
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
import java.util.*;

@WebServlet("/ItemDetails")
@MultipartConfig

public class GetItemDetails extends HttpServletDBConnected {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        OnSaleDAO onSaleDAO = new OnSaleDAO(conn);

        User user = (User) req.getSession().getAttribute("user");
        int userItemId;
        OnSale onSale;
        WebContext webContext = new WebContext(req, resp, getServletContext());
        boolean firstVersionPage = false;
        boolean secondVersionPage = true;
        String page = "risultati";
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        LinkedList<Double> itemIdsFromCookies = null;
        String cartString = URLDecoder.decode (req.getParameter("cartList"),StandardCharsets.UTF_8);

        List<ShoppingCart> cartList =  (ArrayList<ShoppingCart>) gson.fromJson(cartString,(Type) List.class);




        Cookie[] cookies = req.getCookies();
        for (Cookie c : cookies
        ) {
            if (c.getName().equalsIgnoreCase("itemList")) {

                String jsonCookie = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                itemIdsFromCookies = gson.fromJson(jsonCookie, (Type) LinkedList.class);

                if (itemIdsFromCookies == null) {

                    System.out.println("itemId  null");
                    throw new IllegalArgumentException("errorCookies");
                }
            }

        }

        //get the itemId
        try {
            userItemId = Integer.parseInt(req.getParameter("itemId"));

            if (userItemId < 0) {

                System.out.println("userItemId < 0");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("wrong item id");
            }

            //segnalo elemento come visualizzato
            if (itemIdsFromCookies == null) {
                ItemDAO itemDAO = new ItemDAO(conn);
                itemIdsFromCookies = itemDAO.getRandomItemsIds();
            }
            if (!itemIdsFromCookies.contains(userItemId - 0.0)) {
                itemIdsFromCookies.poll();
                itemIdsFromCookies.addLast(userItemId - 0.0);
            }

            String jsonString = gson.toJson(itemIdsFromCookies, LinkedList.class);
            System.out.println(jsonString);
            Cookie myCookie = new Cookie("itemList", URLEncoder.encode(jsonString, StandardCharsets.UTF_8));
            System.out.println(myCookie);
            resp.addCookie(myCookie);



            HashMap<Integer,List<Double>> pricesOnCart = null;


            onSale = onSaleDAO.getInfoByItemId(userItemId);


            if(cartString != null && !cartString.isEmpty()&& cartList!=null)
            {
                cartList = createCartListFromJson(cartString);

                pricesOnCart = prepareHashMap_SellerId_TotalPrice(cartList,onSale.getSellers());
            }



            System.out.println(MyColors.ANSI_GREEN + " getItemDetails corretto " + MyColors.ANSI_RESET);




            resp.setStatus(HttpServletResponse.SC_OK);

            resp.getWriter().println(gson.toJson(new itemDetailsMessage(pricesOnCart,onSale)));





//            webContext.setVariable("lang", lang);
//            webContext.setVariable("user", user);
//            webContext.setVariable("selectedItem", onSale.getItems().get(0));
//            webContext.setVariable("sellers", onSale.getSellers());
//            webContext.setVariable("prices", onSale.getPrices());
//            webContext.setVariable("pricesOnCart", pricesOnCart);
//            webContext.setVariable("hashCode",cartList== null ? null : cartList.hashCode());
//            webContext.setVariable("id_in_vendita",onSale.getIds_inVendita());
//            webContext.setVariable("firstVersionPage", firstVersionPage);
//            webContext.setVariable("secondVersionPage", secondVersionPage);
//            thymeleaf.process(page, webContext, resp.getWriter());

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("bad credentials");
            return;
        }catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("internal server error");
            return;
        }


    }

  

    private HashMap<Integer, List<Double>> prepareHashMap_SellerId_TotalPrice(List<ShoppingCart> cartList, List<Seller> sellers) {
        HashMap<Integer,List<Double>> result = new HashMap<>();

        List<Double> qty_totalPrice = new ArrayList<>();
        double truncatedDouble;


        System.out.println("creo HaskMap \n " + cartList +"\n" + sellers );
        if(cartList== null || cartList.isEmpty()) return null;
        for (ShoppingCart cart:cartList
        ) {
            for (Seller seller: sellers
            ) {
                if(seller.getSellerId()==cart.getSeller().getSellerId()) {
                    System.out.println("trovato da stesso venditore");

                    truncatedDouble = BigDecimal.valueOf(cart.getTotalPrice())
                            .setScale(3, RoundingMode.HALF_UP)
                            .doubleValue();

                    qty_totalPrice.add(truncatedDouble);
                    qty_totalPrice.add(cart.getQuantity()-0.0);
                    result.put(seller.getSellerId(),qty_totalPrice);
                    break;
                }
            }
        }
        System.out.println("resultSet : "+result);
        return result;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
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

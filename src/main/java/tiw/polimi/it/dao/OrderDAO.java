package tiw.polimi.it.dao;

import tiw.polimi.it.beans.*;
import tiw.polimi.it.stuffToDelete.MyColors;

import java.sql.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class OrderDAO extends GeneralDAO {
    public OrderDAO(Connection conn) {
        super(conn);
    }

    public OrderDAO(Connection conn, String language, String country) {
        super(conn, language, country);
    }


    public boolean insertNewOrder(ShoppingCart purchasedCart, User user) throws SQLException {
        /* used variables */
        String query;
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        /* exception handling */


//        query = "begin ;" +
//                "insert into ordine (`data spedizione`, `costo totale`, `indirizzo spedizione`, fornitore, utente) VALUES (?,?,?,?,?); " +
//                "insert into contenimento (id_contenimento, articolo, quantita, ordine) VALUES (LAST_INSERT_ID(),?,?,?);" +
//                "commit ";
//                                          1                   2               3                   4       5
        query = "insert into ordine (`data spedizione`, `costo totale`, `indirizzo spedizione`, fornitore, utente) VALUES (?,?,?,?,?)";

        try {
            conn.setAutoCommit(false);
            preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setDate(1, calculateShippingDate());
            preparedStatement.setDouble(2, purchasedCart.getTotalPrice());
            preparedStatement.setString(3, user.getAddress());
            preparedStatement.setInt(4, purchasedCart.getSeller().getSellerId());
            preparedStatement.setInt(5, user.getUserId());

            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();

            if(resultSet.next()) {
                long id = resultSet.getLong(1);
                System.out.println("key value " + id);

                //2nd insert

                for (LightItem item :purchasedCart.getItem()
                ) {
                    insertItems(item, user, (int) id);
                }
            }else throw new IllegalArgumentException("resulset key is null");

            conn.commit();
            return true;


        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }finally {
            conn.rollback();
            conn.setAutoCommit(true);
        }

//        preparedStatement = conn.prepareStatement(query);
//        preparedStatement.setString(1, email);
//        preparedStatement.setString(2, password);
//        resultSet = preparedStatement.executeQuery();
        return false;
    }

    private Date calculateShippingDate() {
        //get a random date between 1 to 7 days delivery
        java.util.Date date = new java.util.Date();
        Calendar calendar = Calendar.getInstance();
        date = calendar.getTime();
        return  Date.valueOf(LocalDate.now().plusDays(Math.round((Math.random()*7 +1))));
    }

    private void insertItems(LightItem lightItem, User user, int lastKey) throws SQLException {

        String query;
        PreparedStatement preparedStatement;

        System.out.println(MyColors.ANSI_GREEN + "dentro insertItems , last incremented key : " + lastKey + MyColors.ANSI_RESET);

        //query = "insert into contenimento (ordine, articolo, quantita) VALUES (?,?,LAST_INSERT_ID())";
        query = "insert into contenimento (articolo, quantita,ordine ) VALUES (?,?,?)";

        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1,lightItem.getItemId());
        preparedStatement.setInt(2,lightItem.getQty());
        preparedStatement.setInt(3,lastKey);

        preparedStatement.executeUpdate();

    }


    public List<Order> getOrdersByUserID(int userId) throws SQLException {

        String query;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        UserDAO userDAO = new UserDAO(conn);
        List<Order> orderList = new LinkedList<>();
        List<LightItem> lightItems = new LinkedList<>();

        if (userId < 0) {
            throw new IllegalArgumentException("userId corrotto");
        }

//    public Order(String sellerName, String address, Date shippingDate, List<LightItem> itemList, int orderId, double totalPrice) {
//        query = "SELECT DISTINCT f.nome, o.`indirizzo spedizione`, o.`data spedizione`, c.id_contenimento, o.id_ordine, o.`costo totale`" +
//                " FROM progettotiw.ordine AS O join contenimento C " +
//                "on O.id_ordine = C.ordine  join articolo A on A.id_articolo = C.articolo " +
//                "join fornitore f on f.id_fornitore = O.fornitore " +
//                "WHERE O.utente = ? " +
//                "ORDER BY O.`data spedizione` DESC ";

        query = "SELECT  O.* ,f.nome " +
                "FROM ordine as O Join fornitore f on f.id_fornitore = O.fornitore " +
                "WHERE utente = ? " +
                "ORDER BY `data spedizione` DESC ";

        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, userId);
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Order tempOrder = new Order(
                    resultSet.getString("nome"),
                    resultSet.getString("indirizzo spedizione"),
                    resultSet.getDate("data spedizione"),

                    //get list of lighitems from id
                    userDAO.getLightItemsFromID(resultSet.getInt("id_ordine")),
                    resultSet.getInt("id_ordine"),
                    resultSet.getDouble("costo totale"));

            orderList.add(tempOrder);
        }
        return orderList;
    }
}

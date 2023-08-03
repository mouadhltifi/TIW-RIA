package tiw.polimi.it.dao;

import tiw.polimi.it.beans.Item;
import tiw.polimi.it.beans.OnSale;
import tiw.polimi.it.beans.Seller;
import tiw.polimi.it.utils.SearchItemsMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class OnSaleDAO extends GeneralDAO {
    public OnSaleDAO(Connection conn) {
        super(conn);
    }

    public OnSaleDAO(Connection conn, String language, String country) {
        super(conn, language, country);
    }


    public OnSale getInfoByItemId(int itemId) throws SQLException {
        String query;
        PreparedStatement preparedStatemen;
        ResultSet resultSet;
        Item itemOnSale;
        Seller seller;
        ItemDAO itemDAO = new ItemDAO(conn);
        SellerDAO sellerDAO = new SellerDAO(conn);
        ShippingPolicyDAO shippingPolicyDAO = new ShippingPolicyDAO(conn);
        OnSale onsale;
        LinkedList<Seller> sellers;
        LinkedList<Double> prices;
        LinkedList<Item> items;
        LinkedList<Integer> idInVendita;
        int sellerId;

        /*handling excpetion*/
        if (itemId < 0) {
            throw new IllegalArgumentException(selectedLanguage.getString("dbItemIdInvalidCode"));
        }

        query = "SELECT DISTINCT id_in_vendita,id_fornitore,spedizione_gratuita,nome,valutazione,prezzo " +
                "FROM PROGETTOTIW.FORNITORE A\n" +
                "JOIN PROGETTOTIW.INVENDITA  B\n" +
                "ON A.ID_FORNITORE =B.FORNITORE\n" +
                "WHERE B.ARTICOLO = ? " +
                "ORDER BY prezzo ";

        preparedStatemen = conn.prepareStatement(query);
        preparedStatemen.setInt(1, itemId);

        resultSet = preparedStatemen.executeQuery();

        itemOnSale = itemDAO.getItemById(itemId);

        sellers = new LinkedList<>();
        prices = new LinkedList<>();
        items = new LinkedList<>();
        idInVendita = new LinkedList<>();
        items.push(itemOnSale);


        //creating seller bean
        while (resultSet.next()) {

            //adding seller price
            prices.add(resultSet.getDouble("prezzo"));


            sellers.add(new Seller(
                    resultSet.getInt("id_fornitore"),
                    resultSet.getInt("spedizione_gratuita"),
                    resultSet.getString("nome"),
                    resultSet.getInt("valutazione"),
                    shippingPolicyDAO.getShippingPolicyById(resultSet.getInt("id_fornitore"))
            ));

            idInVendita.add(resultSet.getInt("id_in_vendita"));

        }

        System.out.println("\n dentro onSaleDAo stampo dati trovati");
        System.out.println("venditori" + sellers);
        System.out.println("prezzi" + prices);
        System.out.println("items " + items);
        System.out.println("idInVendita" + idInVendita);

        return new OnSale(sellers, prices, items, idInVendita);

    }

    public OnSale findElementsByKey(String keySearch) throws SQLException {

        String query;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        List<Item> itemList;
        List<Double> prices;
        List<Integer> id_inVendita;

        if (keySearch == null || keySearch.isEmpty()) {
            throw new IllegalArgumentException(selectedLanguage.getString("dbInvalidSearch"));
        }


        query = "SELECT DISTINCT id_in_vendita,id_articolo,A.nome,prezzo,descrizione,categoria,foto FROM PROGETTOTIW.ARTICOLO A\n" +
                "JOIN PROGETTOTIW.INVENDITA  B\n" +
                "ON A.ID_ARTICOLO =B.ARTICOLO\n" +
                "WHERE concat(nome,descrizione, categoria) LIKE ?\n" +
                "ORDER BY PREZZO ";

        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, '%' + keySearch + '%');
        resultSet = preparedStatement.executeQuery();


        itemList = new LinkedList<>();
        prices = new LinkedList<>();
        id_inVendita = new LinkedList<>();


        while (resultSet.next()) {
            Item tempItem = new Item(
                    resultSet.getInt("id_articolo"),
                    resultSet.getString("nome"),
                    resultSet.getString("descrizione"),
                    resultSet.getString("categoria"),
                    Base64.getEncoder().encodeToString(resultSet.getBytes("foto"))
            );
            itemList.add(tempItem);

            prices.add(resultSet.getDouble("prezzo"));
            id_inVendita.add(resultSet.getInt("id_in_vendita"));
        }

        return new OnSale(null, prices, itemList, id_inVendita);
    }

    public OnSale getFromIdOnSale(int onSaleID) throws SQLException {

        /*
        da modificare ed unire agli altri 2
         */

        String query;
        PreparedStatement preparedStatemen;
        ResultSet resultSet;
        Item itemOnSale;
        Seller seller;
        ItemDAO itemDAO = new ItemDAO(conn);
        SellerDAO sellerDAO = new SellerDAO(conn);
        ShippingPolicyDAO shippingPolicyDAO = new ShippingPolicyDAO(conn);
        OnSale onsale;
        LinkedList<Seller> sellers;
        LinkedList<Double> prices;
        LinkedList<Item> items;
        LinkedList<Integer> idInVendita;
        int itemId = -1;

        /*handling excpetion*/

        query = "SELECT DISTINCT * " +
                "FROM PROGETTOTIW.FORNITORE A\n" +
                "JOIN PROGETTOTIW.INVENDITA  B\n" +
                "ON A.ID_FORNITORE =B.FORNITORE\n" +
                "WHERE B.id_in_vendita = ? " +
                "ORDER BY prezzo ";


        preparedStatemen = conn.prepareStatement(query);
        preparedStatemen.setInt(1, onSaleID);

        resultSet = preparedStatemen.executeQuery();

        sellers = new LinkedList<>();
        prices = new LinkedList<>();
        items = new LinkedList<>();
        idInVendita = new LinkedList<>();


        //creating seller bean
        while (resultSet.next()) {

            //adding seller price
            prices.add(resultSet.getDouble("prezzo"));


            sellers.add(new Seller(
                    resultSet.getInt("id_fornitore"),
                    resultSet.getInt("spedizione_gratuita"),
                    resultSet.getString("nome"),
                    resultSet.getInt("valutazione"),
                    shippingPolicyDAO.getShippingPolicyById(resultSet.getInt("id_fornitore"))

            ));

            idInVendita.add(resultSet.getInt("id_in_vendita"));
            itemId = resultSet.getInt("articolo");
        }
        itemOnSale = itemDAO.getItemById(itemId);
        items.push(itemOnSale);


        System.out.println("\n dentro onSaleDAo stampo dati trovati");
        System.out.println("venditori" + sellers);
        System.out.println("prezzi" + prices);
        System.out.println("items " + items);
        System.out.println("idInVendita" + idInVendita);

        return new OnSale(sellers, prices, items, idInVendita);


    }

    public List<SearchItemsMessage> getMessageListFromKey(String keySearch) throws SQLException {


        String query;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        List<SearchItemsMessage> messageList;

        if (keySearch == null || keySearch.isEmpty()) {
            throw new IllegalArgumentException(selectedLanguage.getString("dbInvalidSearch"));
        }


        query = "SELECT DISTINCT id_in_vendita,id_articolo,A.nome,prezzo,descrizione,categoria,foto FROM PROGETTOTIW.ARTICOLO A\n" +
                "JOIN PROGETTOTIW.INVENDITA  B\n" +
                "ON A.ID_ARTICOLO =B.ARTICOLO\n" +
                "WHERE concat(nome,descrizione, categoria) LIKE ?\n" +
                "ORDER BY PREZZO ";

        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, '%' + keySearch + '%');
        resultSet = preparedStatement.executeQuery();


        messageList = new LinkedList<>();


        while (resultSet.next()) {
            SearchItemsMessage message = new SearchItemsMessage(
                    new Item(
                            resultSet.getInt("id_articolo"),
                            resultSet.getString("nome"),
                            resultSet.getString("descrizione"),
                            resultSet.getString("categoria"),
                            Base64.getEncoder().encodeToString(resultSet.getBytes("foto"))),
                    resultSet.getDouble("prezzo")
            );
            messageList.add(message);
        }

        return messageList;

    }

}

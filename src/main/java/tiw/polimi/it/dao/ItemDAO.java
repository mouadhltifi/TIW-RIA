package tiw.polimi.it.dao;

import tiw.polimi.it.beans.Item;
import tiw.polimi.it.beans.LightItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class ItemDAO extends GeneralDAO {

    public ItemDAO(Connection conn) {
        super(conn);
    }

    public ItemDAO(Connection conn, String language, String country) {
        super(conn, language, country);
    }

    public LinkedList<Item> getRandomItems() throws SQLException {
        //variables
        String query = " SELECT * FROM articolo ORDER BY RAND() LIMIT 5";
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        LinkedList<Item> lastItemsId;

        preparedStatement = conn.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();

        lastItemsId = new LinkedList<>();
        while (resultSet.next()) {
            Item tempItem = new Item(
                    resultSet.getInt("id_articolo"),
                    resultSet.getString("nome"),
                    resultSet.getString("descrizione"),
                    resultSet.getString("categoria"),
                    Base64.getEncoder().encodeToString(resultSet.getBytes("foto"))
            );
            lastItemsId.push(tempItem);
        }

        return lastItemsId;
    }

    public List<Item> findItemsByKey(String keySearch) throws SQLException {
        //variables
        //String query = "";
        String query = "SELECT DISTINCT id_articolo,A.nome,prezzo,descrizione,categoria,foto FROM PROGETTOTIW.ARTICOLO A\n" +
                "JOIN PROGETTOTIW.INVENDITA  B\n" +
                "ON A.ID_ARTICOLO =B.ARTICOLO\n" +
                "WHERE concat(nome,descrizione, categoria) LIKE ?\n" +
                "ORDER BY PREZZO ";
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        List<Item> itemList = null;


        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, '%' + keySearch + '%');
        resultSet = preparedStatement.executeQuery();


        System.out.println("item dao da eliminare");

        itemList = new ArrayList<>();
        while (resultSet.next()) {
            Item tempItem = new Item(
                    resultSet.getInt("id_articolo"),
                    resultSet.getString("nome"),
                    resultSet.getString("descrizione"),
                    resultSet.getString("categoria"),
                    Base64.getEncoder().encodeToString(resultSet.getBytes("foto"))
            );
            itemList.add(tempItem);
        }

        return itemList;
    }

    public Item getItemById(double itemId) throws SQLException {
        String query = "SELECT DISTINCT * FROM articolo WHERE id_articolo = ?";
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        int myItemId= (int) (itemId -0);
        Item selectedItem = null;

        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1,myItemId);
        resultSet = preparedStatement.executeQuery();

        if(resultSet.next()) {
            selectedItem = new Item(
                    resultSet.getInt("id_articolo"),
                    resultSet.getString("nome"),
                    resultSet.getString("descrizione"),
                    resultSet.getString("categoria"),
                    Base64.getEncoder().encodeToString(resultSet.getBytes("foto"))
            );
            }


        return selectedItem;
    }

    public LinkedList<Double> getRandomItemsIds() throws SQLException{
        //variables
        String query = " SELECT DISTINCT id_articolo FROM articolo ORDER BY RAND() LIMIT 5";
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        LinkedList<Double> lastItemsId;

        preparedStatement = conn.prepareStatement(query);
        resultSet = preparedStatement.executeQuery();

        lastItemsId = new LinkedList<>();
        while (resultSet.next()) {

            lastItemsId.push(resultSet.getInt("id_articolo")*1.0) ;
        }

        return lastItemsId;

    }

    public LightItem getLightItemById(int itemId,int qty) throws SQLException {
        String query = "SELECT DISTINCT * FROM articolo WHERE id_articolo = ?";
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        LightItem selectedItem = null;

        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1,itemId);
        resultSet = preparedStatement.executeQuery();

        if(resultSet.next()) {


            selectedItem = new LightItem(
                    resultSet.getString("nome"),
                    resultSet.getInt("id_articolo"),
                    qty
            );
        }


        return selectedItem;

    }
}

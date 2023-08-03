package tiw.polimi.it.dao;

import tiw.polimi.it.beans.LightItem;
import tiw.polimi.it.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class UserDAO extends GeneralDAO {

    public UserDAO(Connection conn) {
        super(conn);
    }

    public UserDAO(Connection conn, String language, String country) {
        super(conn, language, country);
    }

    public User findUser(String email, String password) throws SQLException {
        /* used variables */
        String query;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        User user;

        /* exception handling */


        query = "SELECT * FROM  utente WHERE email = ? AND password = ?";

        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, email);
        preparedStatement.setString(2, password);
        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            user = new User(
                    resultSet.getInt("id_utente"),
                    resultSet.getString("nome"),
                    resultSet.getString("cognome"),
                    resultSet.getString("email"),
                    resultSet.getString("password"),
                    resultSet.getString("indirizzo spedizione")

            );
        } else return null;
        return user;
    }

    public List<LightItem> getLightItemsFromID(int id_contenimento) throws SQLException {
        String query;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        List<LightItem> lightItems = new LinkedList<>();

        if(id_contenimento < 0) {
            throw new IllegalArgumentException("id_contenimento non valido");
        }


        query = "SELECT DISTINCT A.nome, A.id_articolo ,c.quantita FROM articolo AS A join contenimento c on A.id_articolo = c.articolo " +
                "WHERE c.ordine = ?";

        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id_contenimento);
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            LightItem tempItem = new LightItem(
                    resultSet.getString("nome"),
                    resultSet.getInt("id_articolo"),
                    resultSet.getInt("quantita")
            );
            lightItems.add(tempItem);
        }


        return lightItems;
    }
}

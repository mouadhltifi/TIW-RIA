package tiw.polimi.it.dao;

import tiw.polimi.it.beans.ShippingPolicy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ShippingPolicyDAO extends GeneralDAO{
    public ShippingPolicyDAO(Connection conn) {
        super(conn);
    }

    public ShippingPolicyDAO(Connection conn, String language, String country) {
        super(conn, language, country);
    }

    public List<ShippingPolicy> getShippingPolicyById(int id_fornitore) throws SQLException {
        String query;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        LinkedList<ShippingPolicy> shippingPolicies;

        if(id_fornitore < 0) {
            throw new IllegalArgumentException(selectedLanguage.getString("dbInvalidShippingPolicyId"));
        }

        query = "SELECT DISTINCT * FROM `fascia di spesa` WHERE fornitore = ?";
        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1,id_fornitore);
        resultSet = preparedStatement.executeQuery();

        shippingPolicies = new LinkedList<>();
        while (resultSet.next()) {
            ShippingPolicy temp = new ShippingPolicy(
                    resultSet.getInt("min"),
                    resultSet.getInt("max"),
                    resultSet.getDouble("prezzo")
            );

            shippingPolicies.add(temp);
        }

        System.out.println("politica di spedizione : ");
        for (ShippingPolicy s: shippingPolicies
             ) {
            s.toString();
        }

        return shippingPolicies;
    }
}

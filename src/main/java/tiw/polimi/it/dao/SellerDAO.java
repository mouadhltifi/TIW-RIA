package tiw.polimi.it.dao;

import java.sql.Connection;

public class SellerDAO extends GeneralDAO{
    public SellerDAO(Connection conn) {
        super(conn);
    }

    public SellerDAO(Connection conn, String language, String country) {
        super(conn, language, country);
    }



}

package tiw.polimi.it.controllers;

import tiw.polimi.it.filter.HttpServletFilter;
import tiw.polimi.it.utils.Const;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import java.sql.Connection;
import java.sql.SQLException;


public class HttpServletDBConnected extends HttpServlet {
	protected Connection conn;

	@Override
	public void init() throws ServletException {
		try {
			// getting the connection
			ServletContext context = getServletContext();

			// tutte le connessioni passano da questo filtro
			// include lo user filter che controlla se sei loggato
			// passano da qui tutte le richieste DAO
			conn = HttpServletFilter.applyConnection(context);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException(Const.unavailableException);
		} catch (SQLException e) {
			throw new UnavailableException(Const.sqlException);
		}
	}

	@Override
	public void destroy() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException ignored) {}
	}
}
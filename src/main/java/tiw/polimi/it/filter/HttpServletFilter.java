package tiw.polimi.it.filter;

import tiw.polimi.it.utils.Const;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class HttpServletFilter extends HttpFilter {
	protected Connection conn;
	//protected String path = getServletContext().getContextPath();

	@Override
	public void init(FilterConfig config) throws ServletException {
		try {
			super.init();
			ServletContext context = config.getServletContext();
			conn = applyConnection(context);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException(Const.unavailableException);
		} catch (SQLException e) {
			throw new UnavailableException(Const.sqlException);
		}
	}

	public static Connection applyConnection(ServletContext context) throws ClassNotFoundException, SQLException {
		String username = context.getInitParameter("dbUser");
		String password = context.getInitParameter("dbPassword");
		String database = context.getInitParameter("dbUrl");
		String driver = context.getInitParameter("dbDriver");
		Class.forName(driver);
		return DriverManager.getConnection(database,username,password);
	}
}

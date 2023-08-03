package tiw.polimi.it.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class UserFilter extends HttpServletFilter {

    /* check if user is already stored in Session */
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpSession session = req.getSession();

        if(session.isNew() || session.getAttribute("user")==null) {
            String path = req.getPathInfo();
            res.sendRedirect(path + "/index");
        }else {
            chain.doFilter(req,res);
        }
    }
}

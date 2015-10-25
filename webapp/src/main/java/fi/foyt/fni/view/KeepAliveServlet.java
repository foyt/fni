package fi.foyt.fni.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/keepalive")
public class KeepAliveServlet extends HttpServlet {

  private static final long serialVersionUID = -2289974998522276978L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.getSession(false);
    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
  }
  
}

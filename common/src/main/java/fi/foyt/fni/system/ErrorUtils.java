package fi.foyt.fni.system;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import fi.foyt.fni.utils.mail.MailUtils;

public class ErrorUtils {

  public static void mailError(String recipient, ServletRequest request, ServletResponse response, Throwable t) {
    try {
      String subject = "Error occurred on Forge & Illusion";
      StringWriter contentWriter = new StringWriter();
      
      contentWriter.append("Error details:\n\n");
      contentWriter.append("When: ").append(new Date().toString()).append('\n');

      if (request instanceof HttpServletRequest) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        contentWriter.append("Where: " + httpRequest.getRequestURL().toString()).append('\n');
        
        contentWriter.append("Request Headers:\n");
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
          String headerName = headerNames.nextElement();
          String headerValue = httpRequest.getHeader(headerName);
          contentWriter.append("  ").append(headerName).append(": ").append(headerValue).append('\n');
        }
        
        HttpSession session = httpRequest.getSession();
        if (session != null) {
          contentWriter.append("Session id:").append(session.getId()).append('\n');
          contentWriter.append("Session attributes:\n");
          Enumeration<String> attributeNames = session.getAttributeNames();
          while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attributeValue = session.getAttribute(attributeName);
            contentWriter.append("  ").append(attributeName).append(": ").append(String.valueOf(attributeValue)).append('\n');
          }
        }
      }
      
      contentWriter.append('\n');
      contentWriter.append("Stack trace: \n");
      contentWriter.append('\n');
      
      PrintWriter stackTraceWriter = new PrintWriter(contentWriter, false);
      try {
        t.printStackTrace(stackTraceWriter);
      } finally {
        stackTraceWriter.flush();
        stackTraceWriter.close();
      }
      
      MailUtils.sendMail(recipient, recipient, recipient, recipient, subject, contentWriter.toString(), "text/plain");
    } catch (Throwable e) {
      try {
        Logger.getGlobal().log(Level.SEVERE, "Could not mail error", e);
      } catch (Throwable e2) {
      }
    }
  }
  
}

package fi.foyt.fni.mail;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Dependent
public class Mailer {
  
  @Resource(mappedName = "java:jboss/mail/Default")
  private Session mailSession;

  public void sendMail(String fromMail, String fromName, String toMail, String toName, String subject, String content, String contentType) throws MessagingException {
    try {
      MimeMessage message = new MimeMessage(mailSession);

      message.setSubject(subject, "UTF-8");
      message.setContent(content, contentType + ";charset=\"utf-8\"");

      InternetAddress addressFrom = new InternetAddress(fromMail, fromName, "UTF-8");
      message.setFrom(addressFrom);
      message.setReplyTo(new InternetAddress[] { addressFrom });

      InternetAddress[] addressTo = new InternetAddress[1];
      addressTo[0] = new InternetAddress(toMail, toName, "UTF-8");
      message.setRecipients(Message.RecipientType.TO, addressTo);

      Transport.send(message);
    } catch (UnsupportedEncodingException e) {
      throw new MessagingException("Error occured while trying to encode internet address", e);
    }
  }
}

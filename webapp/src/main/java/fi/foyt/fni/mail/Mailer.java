package fi.foyt.fni.mail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
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
  
  public MailBuilder getBuilder() {
    return new MailBuilder();
  }
  
  public class MailBuilder {
    
    public MailBuilder() {
      this.to = new ArrayList<>();
      this.cc = new ArrayList<>();
      this.bcc = new ArrayList<>();
      this.contentType = "text/plain;charset=\"utf-8\"";
    }
    
    public MailBuilder setContentType(String contentType) {
      this.contentType = contentType;
      return this;
    }
    
    public MailBuilder setFrom(String address) throws AddressException {
      fromAddress = new InternetAddress(address);
      return this;
    }
    
    public MailBuilder setFrom(String address, String personal) throws AddressException, UnsupportedEncodingException {
      fromAddress = new InternetAddress(address, personal,  "UTF-8");
      return this;
    }
    
    public MailBuilder setReplyTo(String address) throws AddressException {
      replyToAddress = new InternetAddress(address);
      return this;
    }
    
    public MailBuilder setReplyTo(String address, String personal) throws AddressException, UnsupportedEncodingException {
      replyToAddress = new InternetAddress(address, personal,  "UTF-8");
      return this;
    }
    
    public MailBuilder addTo(String address) throws AddressException {
      to.add(new InternetAddress(address));
      return this;
    }
    
    public MailBuilder addCc(String address) throws AddressException {
      cc.add(new InternetAddress(address));
      return this;
    }
    
    public MailBuilder addBcc(String address) throws AddressException {
      bcc.add(new InternetAddress(address));
      return this;
    }
    
    public MailBuilder addTo(String address, String personal) throws AddressException, UnsupportedEncodingException {
      to.add(new InternetAddress(address, personal,  "UTF-8"));
      return this;
    }
    
    public MailBuilder addCc(String address, String personal) throws AddressException, UnsupportedEncodingException {
      cc.add(new InternetAddress(address, personal,  "UTF-8"));
      return this;
    }
    
    public MailBuilder addBcc(String address, String personal) throws AddressException, UnsupportedEncodingException {
      bcc.add(new InternetAddress(address, personal,  "UTF-8"));
      return this;
    }
    
    public MailBuilder setContent(String content) {
      this.content = content;
      return this;
    }
    
    public MailBuilder setSubject(String subject) {
      this.subject = subject;
      return this;
    }
    
    public void send() throws MessagingException {
      MimeMessage message = new MimeMessage(mailSession);

      message.setSubject(subject, "UTF-8");
      message.setContent(content, contentType);
      message.setFrom(fromAddress);
      
      if (replyToAddress != null) {
        message.setReplyTo(new InternetAddress[] { replyToAddress });
      }
      
      if (!to.isEmpty()) {
        message.setRecipients(Message.RecipientType.TO, to.toArray(new InternetAddress[0]));
      }
      
      if (!cc.isEmpty()) {
        message.setRecipients(Message.RecipientType.CC, cc.toArray(new InternetAddress[0]));
      }
      
      if (!bcc.isEmpty()) {
        message.setRecipients(Message.RecipientType.BCC, bcc.toArray(new InternetAddress[0]));
      }
      
      Transport.send(message);
    }
    
    private InternetAddress fromAddress;
    private InternetAddress replyToAddress;
    private List<InternetAddress> to;
    private List<InternetAddress> cc;
    private List<InternetAddress> bcc;
    private String subject;
    private String content;
    private String contentType;
  }
}

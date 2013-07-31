package fi.foyt.fni.utils.mail;

import java.io.UnsupportedEncodingException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MailUtils {

	private static Session getMailSession() {
		try {
	    InitialContext initialContext = new InitialContext();
	    return (Session) initialContext.lookup("java:jboss/mail/Default");
    } catch (NamingException e) {
	    return null;
    }
	}

	private static void sendMessage(Message message, String fromMail, String fromName, String toMail, String toName) throws MessagingException,
	    UnsupportedEncodingException {

		InternetAddress addressFrom = new InternetAddress(fromMail, fromName, "UTF-8");
		message.setFrom(addressFrom);
		message.setReplyTo(new InternetAddress[] { addressFrom });

		InternetAddress[] addressTo = new InternetAddress[1];
		addressTo[0] = new InternetAddress(toMail, toName, "UTF-8");
		message.setRecipients(Message.RecipientType.TO, addressTo);

		Transport.send(message);
	}

	public static void sendMail(String fromMail, String fromName, String toMail, String toName, String subject, String content, String contentType)
	    throws MessagingException {
		try {
			Session session = getMailSession();

			MimeMessage message = new MimeMessage(session);

			message.setSubject(subject, "UTF-8");
			message.setContent(content, contentType + ";charset=\"utf-8\"");

			sendMessage(message, fromMail, fromName, toMail, toName);
		} catch (UnsupportedEncodingException e) {
			throw new MessagingException("Error occured while trying to encode internet address", e);
		}
	}

}

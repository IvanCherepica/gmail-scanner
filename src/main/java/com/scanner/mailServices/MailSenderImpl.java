package com.scanner.mailServices;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class MailSenderImpl implements MailSender {
	private String username = null;
	private String password = null;

	public MailSenderImpl(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public void sendMessage(String recipient, String content) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("ussr211114@gmail.com"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
			message.setSubject("Test results");
			message.setText(content);
			Transport.send(message);
			System.out.println("Email Sent Successfully");
		} catch (MessagingException e) {
			throw new RuntimeException(e);

		}
	}
}

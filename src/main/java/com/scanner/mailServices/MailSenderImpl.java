package com.scanner.mailServices;

import com.scanner.DTO.Letter;
import com.scanner.properties.MailProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MailSenderImpl implements MailSender {
	@Autowired
	private MailProperties mailProp;
	private String username;
	private String password;
	private boolean launched;

	public MailSenderImpl(String username, String password) {
		this.username = username;
		this.password = password;
	}
	@Override
	public boolean isLaunched() {
		return launched;
	}

	@Override
	public void sendMessage(List<Letter> letters) {
		launched = true;
		Properties props = new Properties();
		props.put("mail.smtp.auth", mailProp.getOutboxAuth());
		props.put("mail.smtp.starttls.enable", mailProp.getOutboxStartTls());
		props.put("mail.smtp.host", mailProp.getOutboxHost());
		props.put("mail.smtp.port", mailProp.getOutboxPort());

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Transport transport = session.getTransport("smtp");
			transport.connect(mailProp.getOutboxHost(), username, password);

			for (Letter letter : letters) {
				String str = letter.getRecipients().toString();
				String addresses = str.substring(1, str.length()-1);
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(username));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(addresses));
				message.setSubject(getSendSubject());
				message.setText(letter.getContent());
				transport.send(message);
				System.out.println("Email Sent Successfully");
			}

			transport.close();
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		launched = false;
	}

	private String getSendSubject() {
		StringBuilder subject = new StringBuilder();
		try (Scanner scan = new Scanner(new FileInputStream("caption.txt"))) {
			while (scan.hasNextLine()) {
				subject.append(scan.nextLine());
			}
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
		return subject.toString();
	}
}

package com.scanner.mailServices;

import com.scanner.properties.MailProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Scanner;
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
	public void sendMessage(String recipient, String content) {
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
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
			message.setSubject(getSendSubject());
			message.setText(content);
			Transport.send(message);
			System.out.println("Email Sent Successfully");
		} catch (MessagingException e) {
			throw new RuntimeException(e);

		}
		launched = false;
	}

	private String getSendSubject() {
		String subject = "";
		try (Scanner scan = new Scanner(new FileInputStream("explanations.txt"))) {
			while (scan.hasNextLine()) {
				String s = scan.nextLine();
				if (s.matches("Заголовок ответа: \"[А-Яа-я\\s*]+\""))
					subject = s.replaceAll("Заголовок ответа: \"", "")
							.replaceAll("\"(.+)?", "");
			}

		}catch(IOException e) {
			throw new RuntimeException(e);
		}
		return subject;
	}
}

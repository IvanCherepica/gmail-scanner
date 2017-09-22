package com.scanner.configs;

import com.scanner.mailServices.MailChecker;
import com.scanner.mailServices.MailCheckerImpl;
import com.scanner.mailServices.MailSender;
import com.scanner.mailServices.MailSenderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class ApplicationContextConfig {
	private String username = null;
	private String password = null;
	private int answersAmount = 0;
	private String sender = null;
	private String rightAnswers = null;

	@Bean
	public MailChecker checkMails() {
		FileInputStream fis;
		Properties property = new Properties();
		try {
			fis = new FileInputStream("src/main/resources/application.properties");
			property.load(fis);
			sender = property.getProperty("sender");
			username = property.getProperty("username");
			password = property.getProperty("password");
			answersAmount = Integer.parseInt(property.getProperty("answers.amount"));
			rightAnswers = property.getProperty("right.answers");
		} catch (IOException e) {
			System.err.println("ОШИБКА: Файл свойств отсуствует!");
		}

		return new MailCheckerImpl(username, password, answersAmount, sender, rightAnswers);
	}

	@Bean
	public MailSender sender() {
		return new MailSenderImpl(username, password);
	}
}

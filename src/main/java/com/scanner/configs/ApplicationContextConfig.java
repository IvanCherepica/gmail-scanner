package com.scanner.configs;

import com.scanner.mailServices.MailChecker;
import com.scanner.mailServices.MailCheckerImpl;
import com.scanner.mailServices.MailSender;
import com.scanner.mailServices.MailSenderImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Configuration
@PropertySource("classpath:application.properties")
public class ApplicationContextConfig {
	@Value("${own.addres}")
	private String username;
	@Value("${password}")
	private String password;
	@Value("${answers.amount}")
	private int answersAmount;
	@Value("${sender}")
	private String sender;
	@Value("${right.answers}")
	private String rightAnswers;

	@Bean
	public MailChecker checkMails() {
		return new MailCheckerImpl(username, password, answersAmount, sender, rightAnswers);
	}

	@Bean
	public MailSender sender() {
		return new MailSenderImpl(username, password);
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}

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
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

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
		ArrayList<String> explanations = getExplanations();
		return new MailCheckerImpl(username, password, answersAmount, sender, explanations, rightAnswers);
	}

	@Bean
	public MailSender sender() {
		return new MailSenderImpl(username, password);
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	private ArrayList<String> getExplanations() {
		ArrayList<String> explanations = new ArrayList<>();
		String fileName = "explanations.txt";
		try (Scanner scan = new Scanner(new FileInputStream(fileName))
				.useDelimiter("\\d\\)\\s")) {
			while (scan.hasNext()) {
				explanations.add(scan.next().replaceAll("\\r\\n", ""));
			}
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
		return explanations;
	}
}

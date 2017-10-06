package com.scanner.mailServices;

/**
 * Created by User on 17.09.2017.
 */
public interface MailSender {
	boolean isLaunched();
	void sendMessage(String recipient, String content);
}

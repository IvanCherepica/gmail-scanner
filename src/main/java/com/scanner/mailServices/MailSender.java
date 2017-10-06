package com.scanner.mailServices;

import com.scanner.DTO.Letter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 17.09.2017.
 */
public interface MailSender {
	boolean isLaunched();
	void sendMessage(List<Letter> letters);
}

package com.scanner.timer;

import com.scanner.mailServices.MailChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class Timer {
	@Autowired
	private MailChecker mailChecker;

	@Scheduled(fixedRate = 10000)
	public void run() {
		mailChecker.check();
	}
}

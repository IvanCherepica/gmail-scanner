package com.scanner.timer;

import com.scanner.commandCatcher.Catcher;
import com.scanner.mailServices.MailChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Timer implements Runnable {
	@Autowired
	private MailChecker mailChecker;
	@Autowired
	private Catcher catcher;

	@Scheduled(fixedRate = 10000)
	public void run() {
		checkCommand();
		mailChecker.check();
		checkCommand();
	}

	private void checkCommand() {
		if (catcher.getCommand().equals("stop")) {
			System.exit(0);
		}
	}
}

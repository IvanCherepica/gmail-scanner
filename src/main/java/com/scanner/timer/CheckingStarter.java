package com.scanner.timer;

import com.scanner.commandCatcher.Catcher;
import com.scanner.mailServices.MailChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
public class CheckingStarter implements Runnable {
	@Autowired
	private MailChecker mailChecker;

	@Autowired
	private Catcher catcher;

	@Scheduled(fixedRate = 10000)
	public void run() {
		if (!catcher.isStopped())
			mailChecker.check();

	}

}

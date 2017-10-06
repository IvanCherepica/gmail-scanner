package com.scanner.commandCatcher;

import com.scanner.mailServices.MailChecker;
import com.scanner.mailServices.MailSender;
import com.scanner.sheetExecutor.SheetExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class Catcher implements Runnable {
	@Autowired
	private MailSender mailSender;
	@Autowired
	private MailChecker mailChecker;
	@Autowired
	private SheetExecutor sheetExecutor;

	private String command = "";

	private boolean stopped = false;

	Catcher() {
		Thread thread = new Thread(this, "Command listener");
		thread.start();
	}

	public boolean isStopped() {
		return stopped;
	}

	@Override
	public void run() {
		Scanner in = new Scanner(System.in);
		while (in.hasNext()) {
			command = in.next();
		}
	}

	@Scheduled(fixedRate = 1000)
	private void stopApp() {
		if (command.equals("stop")) {
			stopped = true;
			if (!mailChecker.isLaunched() && !mailSender.isLaunched() && !sheetExecutor.isLaunched())
				System.exit(0);
		}
	}
}

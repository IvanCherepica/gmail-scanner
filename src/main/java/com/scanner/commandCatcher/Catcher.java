package com.scanner.commandCatcher;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class Catcher implements Runnable {

	private String command = "";

	Catcher() {
		Thread thread = new Thread(this, "Command listener");
		thread.start();
	}

	@Override
	public void run() {
		Scanner in = new Scanner(System.in);

		while (in.hasNext()) {
			command = in.next();
		}
	}

	public String getCommand() {
		return command;
	}

}

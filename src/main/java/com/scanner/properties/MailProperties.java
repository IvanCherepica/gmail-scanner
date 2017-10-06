package com.scanner.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Scanner;


@Component
@PropertySource("classpath:application.properties")
public class MailProperties {
	@Value("${mail.store.type.in}")
	private String inboxStoreType;
	@Value("${mail.host.in}")
	private String inboxHost;
	@Value("${mail.port.in}")
	private String inboxPort;
	@Value("${mail.starttls.enable.in}")
	private String inboxStartTls;
	@Value("${mail.store.folder.in}")
	private String inboxStoreFolder;
	@Value("${mail.auth.out}")
	private String outboxAuth;
	@Value("${mail.starttls.enable.out}")
	private String outboxStartTls;
	@Value("${mail.host.out}")
	private String outboxHost;
	@Value("${mail.port.out}")
	private String outboxPort;

	public MailProperties() {
	}

	public String getInboxStoreType() {
		return inboxStoreType;
	}

	public String getInboxHost() {
		return inboxHost;
	}

	public String getInboxPort() {
		return inboxPort;
	}

	public String getInboxStartTls() {
		return inboxStartTls;
	}

	public String getInboxStoreFolder() {
		return inboxStoreFolder;
	}

	public String getOutboxAuth() {
		return outboxAuth;
	}

	public String getOutboxStartTls() {
		return outboxStartTls;
	}

	public String getOutboxHost() {
		return outboxHost;
	}

	public String getOutboxPort() {
		return outboxPort;
	}
}

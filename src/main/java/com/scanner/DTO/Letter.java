package com.scanner.DTO;

import java.util.List;

public class Letter {
	private List<String> recipients;
	private String content;

	public Letter(List<String> recipients, String content) {
		this.recipients = recipients;
		this.content = content;
	}

	public List<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<String> recipient) {
		this.recipients = recipient;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}

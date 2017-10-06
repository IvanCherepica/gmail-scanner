package com.scanner.DTO;

public class Letter {
	private String recipient;
	private String content;

	public Letter(String recipient, String content) {
		this.recipient = recipient;
		this.content = content;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}

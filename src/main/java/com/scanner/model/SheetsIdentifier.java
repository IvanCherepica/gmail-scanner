package com.scanner.model;

import javax.persistence.*;

@Entity
@Table(name = "sheet_id")
public class SheetsIdentifier {
	@Id
	private final int id = 1;

	@Column(name = "identifier",  nullable = false)
	private String identifier;

	public SheetsIdentifier() {}

	public SheetsIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}

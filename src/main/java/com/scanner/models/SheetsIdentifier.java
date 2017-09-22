package com.scanner.models;

import javax.persistence.*;

@Entity
@Table(name = "sheet_id")
public class SheetsIdentifier {
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private int id;

	@Column(name = "identifier",  nullable = false)
	private String identifier;

	public SheetsIdentifier() {}

	public SheetsIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}

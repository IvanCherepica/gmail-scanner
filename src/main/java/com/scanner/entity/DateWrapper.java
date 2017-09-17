package com.scanner.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "date")
public class DateWrapper {
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private int id;

	@Column(name = "date",  nullable = false)
	private long date;

	public DateWrapper() {}

	public DateWrapper(Date date) {

		this.date = date.getTime();
	}

	public Date getCurrentDate() {
		return new Date(date);
	}

	public void setCurrentDate(Date date) {
		this.date = date.getTime();
	}

	public void setId(int id) {
		this.id = id;
	}

}
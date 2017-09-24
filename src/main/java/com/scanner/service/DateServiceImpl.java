package com.scanner.service;

import com.scanner.dao.DateDao;
import com.scanner.model.DateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DateServiceImpl implements DateService {
	@Autowired
	private DateDao dateRepository;

	public DateWrapper LastDate() {
		return dateRepository.LastDate();
	}

	public void rewriteLastDate(DateWrapper date) {
		dateRepository.rewriteLastDate(date);
	}
}
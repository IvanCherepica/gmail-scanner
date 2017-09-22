package com.scanner.service;

import com.scanner.dao.DateDao;
import com.scanner.models.DateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DateServiceImpl implements DateService {
	@Autowired
	private DateDao dateRepository;

	public DateWrapper getLastDate() {
		return dateRepository.getLastDate();
	}

	public void addLastDate(DateWrapper date) {
		dateRepository.addDate(date);
	}
}
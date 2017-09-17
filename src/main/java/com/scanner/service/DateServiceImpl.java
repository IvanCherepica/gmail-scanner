package com.scanner.service;

import com.scanner.dao.DateDao;
import com.scanner.entity.DateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DateServiceImpl implements DateService {
	@Autowired
	private DateDao repository;

	public DateWrapper getLastDate() {
		return repository.getLastDate();
	}

	@Transactional
	public void addLastDate(DateWrapper date) {
		repository.addDate(date);
	}
}

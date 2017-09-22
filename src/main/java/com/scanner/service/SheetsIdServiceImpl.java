package com.scanner.service;

import com.scanner.dao.SheetsIdDao;
import com.scanner.models.SheetsIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SheetsIdServiceImpl implements SheetsIdService {
	@Autowired
	private SheetsIdDao sheetsIdRepository;

	@Override
	public SheetsIdentifier getLastIdentifier() {
		return sheetsIdRepository.getLastIdentifier();
	}

	@Override
	public void addIdentifier(SheetsIdentifier sheetsIdentifier) {
		sheetsIdRepository.addIdentifier(sheetsIdentifier);
	}
}

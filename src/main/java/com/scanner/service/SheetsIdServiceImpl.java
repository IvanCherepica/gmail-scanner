package com.scanner.service;

import com.scanner.dao.SheetsIdDao;
import com.scanner.model.SheetsIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SheetsIdServiceImpl implements SheetsIdService {
	@Autowired
	private SheetsIdDao sheetsIdRepository;

	@Override
	public SheetsIdentifier LastIdentifier() {
		return sheetsIdRepository.LastIdentifier();
	}

	@Override
	public void rewriteIdentifier(SheetsIdentifier sheetsIdentifier) {
		sheetsIdRepository.rewriteIdentifier(sheetsIdentifier);
	}
}

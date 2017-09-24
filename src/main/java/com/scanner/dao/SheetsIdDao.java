package com.scanner.dao;

import com.scanner.model.SheetsIdentifier;


public interface SheetsIdDao {
	SheetsIdentifier LastIdentifier();

	void rewriteIdentifier(SheetsIdentifier sheetsIdentifier);
}

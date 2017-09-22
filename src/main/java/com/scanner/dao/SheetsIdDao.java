package com.scanner.dao;

import com.scanner.models.SheetsIdentifier;

/**
 * Created by User on 20.09.2017.
 */
public interface SheetsIdDao {
	SheetsIdentifier getLastIdentifier();

	void addIdentifier(SheetsIdentifier sheetsIdentifier);
}

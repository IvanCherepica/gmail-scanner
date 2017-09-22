package com.scanner.service;

import com.scanner.models.SheetsIdentifier;

/**
 * Created by User on 20.09.2017.
 */
public interface SheetsIdService {
	SheetsIdentifier getLastIdentifier();

	void addIdentifier(SheetsIdentifier sheetsIdentifier);
}

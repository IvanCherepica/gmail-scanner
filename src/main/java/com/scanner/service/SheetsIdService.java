package com.scanner.service;

import com.scanner.model.SheetsIdentifier;

/**
 * Created by User on 20.09.2017.
 */
public interface SheetsIdService {
	SheetsIdentifier LastIdentifier();

	void rewriteIdentifier(SheetsIdentifier sheetsIdentifier);
}

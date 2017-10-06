package com.scanner.sheetExecutor;

import java.io.IOException;
import java.util.Date;

/**
 * Created by User on 17.09.2017.
 */
public interface SheetExecutor {
	void appendData(Date sentDate, String name, String phone, String email);
}
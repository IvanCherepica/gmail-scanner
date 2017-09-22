package com.scanner.sheetExecutor;

import java.util.Date;

/**
 * Created by User on 17.09.2017.
 */
public interface SheetExecutor {
	String executeSheet(Date sentDate, String name, String phone, String email);
}
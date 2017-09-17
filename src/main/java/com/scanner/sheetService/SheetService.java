package com.scanner.sheetService;

import java.util.Date;

/**
 * Created by User on 17.09.2017.
 */
public interface SheetService {
	String createSheet(Date sentDate, String name, String phone, String email);
}

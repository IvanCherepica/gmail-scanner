package com.scanner.service;

import com.scanner.model.DateWrapper;

/**
 * Created by User on 17.09.2017.
 */
public interface DateService {
	DateWrapper LastDate();

	void rewriteLastDate(DateWrapper date);
}

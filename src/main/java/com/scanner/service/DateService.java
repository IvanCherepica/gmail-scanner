package com.scanner.service;

import com.scanner.entity.DateWrapper;

/**
 * Created by User on 17.09.2017.
 */
public interface DateService {
	DateWrapper getLastDate();

	void addLastDate(DateWrapper date);
}

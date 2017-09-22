package com.scanner.dao;

import com.scanner.models.DateWrapper;

/**
 * Created by User on 17.09.2017.
 */
public interface DateDao {
	DateWrapper getLastDate();

	void addDate(DateWrapper dateWrapper);
}

package com.scanner.dao;

import com.scanner.model.DateWrapper;

/**
 * Created by User on 17.09.2017.
 */
public interface DateDao {
	DateWrapper LastDate();

	void rewriteLastDate(DateWrapper dateWrapper);
}

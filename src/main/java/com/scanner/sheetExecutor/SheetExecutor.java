package com.scanner.sheetExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 17.09.2017.
 */
public interface SheetExecutor {
	boolean isLaunched();
	void appendData(List<List<Object>> userDetailsList);
}
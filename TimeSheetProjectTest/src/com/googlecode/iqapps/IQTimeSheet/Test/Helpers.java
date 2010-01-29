/*
 * Copyright 2010 TimeSheet authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author      (classes and interfaces only, required)
 * @version     (classes and interfaces only, required. See footnote 1)
 * @param       (methods and constructors only)
 * @return      (methods only)
 * @exception   (@throws is a synonym added in Javadoc 1.2)
 * @see         
 */
package com.googlecode.iqapps.IQTimeSheet.Test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author kronenpj
 * 
 */
public class Helpers {
	private static Log log = LogFactory.getLog(Helpers.class);
	private static final String TAG = "Helpers";
	public static final int alignments = 12;
	public static final String DATABASE_NAME = "TimeSheetDB.db";
	public static final String text1 = "Task 1";
	public static final String text2 = "Task 2";
	public static final String text3 = "Task 3";
	public static final String text4 = "Task 4";
	public static final String text5 = "Renamed task";

	public static void sleep() {
		sleep(5000);
	}

	public static void sleep(long time) {
		if (time > 1000 && log.isInfoEnabled())
			log.info(TAG + ": sleeping for " + time + " ms.");
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

	// Keep the test runner happy.
	// @Ignore
	@Test
	public void empty() {
	}
}

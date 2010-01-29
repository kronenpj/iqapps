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

import static com.googlecode.autoandroid.positron.PositronAPI.Key.DOWN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.autoandroid.positron.PositronAPI;
import com.googlecode.autoandroid.positron.junit4.TestCase;
import com.googlecode.iqapps.TimeHelpers;

/**
 * @author kronenpj
 * 
 */
public class WeekReportTest extends TestCase {
	private Log log = LogFactory.getLog(WeekReportTest.class);
	private static final String TAG = "WeekReportTest";
	private static int test = 0;
	private static final String insertIntoTasks = "INSERT INTO tasks (task, active, usage) "
			+ "VALUES ('";
	private static final String insertIntoTimeSheet = "INSERT INTO timesheet (chargeno, timein, timeout) "
			+ "VALUES (";

	@Before
	public void setUp() {
		test++;
		log.info("value of test: " + test);
		backup();
		prefBackup();
		sql(Helpers.DATABASE_NAME, "DELETE FROM tasks;");
		sql(Helpers.DATABASE_NAME, "DELETE FROM timesheet; ");
		sql(Helpers.DATABASE_NAME, insertIntoTasks + Helpers.text1
				+ "', 1, 40);");
		sql(Helpers.DATABASE_NAME, insertIntoTasks + Helpers.text2
				+ "', 0, 30);");
		sql(Helpers.DATABASE_NAME, insertIntoTasks + Helpers.text3
				+ "', 1, 50);");
		sql(Helpers.DATABASE_NAME, insertIntoTasks + Helpers.text4
				+ "', 1, 20);");
		setupTimeSheetDB();
		startActivity("com.googlecode.iqapps.IQTimeSheet",
				"com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity");
		pause();
	}

	/**
	 * 
	 */
	private void setupTimeSheetDB() {
		long now = TimeHelpers.millisNow();
		long monday = TimeHelpers.millisToStartOfWeek(now) + 8 * 3600000; //8am.
		long tuesday = monday + 24 * 3600000;
		long wednesday = tuesday + 24 * 3600000;
		long thursday = wednesday + 24 * 3600000;
		long friday = thursday + 24 * 3600000;

		switch (test) {
		case 1:
			sql(Helpers.DATABASE_NAME, insertIntoTimeSheet + "2, " + monday
					+ ", " + (monday + 8 * 3600000) + ");");
			sql(Helpers.DATABASE_NAME, insertIntoTimeSheet + "4, " + tuesday
					+ ", " + (tuesday + 8 * 3600000) + ");");
			sql(Helpers.DATABASE_NAME, insertIntoTimeSheet + "5, " + wednesday
					+ ", " + (wednesday + 8 * 3600000) + ");");
			sql(Helpers.DATABASE_NAME, insertIntoTimeSheet + "4, " + thursday
					+ ", " + (thursday + 8 * 3600000) + ");");
			sql(Helpers.DATABASE_NAME, insertIntoTimeSheet + "2, " + friday
					+ ", " + (friday + 8 * 3600000) + ");");
			break;
		case 2:
			sql(Helpers.DATABASE_NAME, insertIntoTimeSheet + "2, " + monday
					+ ", " + (monday + 3 * 3600000) + ");");
			sql(Helpers.DATABASE_NAME, insertIntoTimeSheet + "4, "
					+ (monday + 4 * 3600000) + ", " + (monday + 9 * 3600000)
					+ ");");
			sql(Helpers.DATABASE_NAME, insertIntoTimeSheet + "4, " + tuesday
					+ ", " + (tuesday + 5 * 3600000) + ");");
			sql(Helpers.DATABASE_NAME, insertIntoTimeSheet + "2, "
					+ (tuesday + 6 * 3600000) + ", " + (tuesday + 9 * 3600000)
					+ ");");
			sql(Helpers.DATABASE_NAME, insertIntoTimeSheet + "5, " + wednesday
					+ ", " + (wednesday + 8 * 3600000) + ");");
			sql(Helpers.DATABASE_NAME, insertIntoTimeSheet + "4, " + thursday
					+ ", " + (thursday + 8 * 3600000) + ");");
			sql(Helpers.DATABASE_NAME, insertIntoTimeSheet + "2, " + friday
					+ ", " + (friday + 8 * 3600000) + ");");
			break;
		}
	}

	@After
	public void tearDown() {
		finishAll();
		prefRestore();
		restore();
	}

	// @Ignore
	@Test
	public void weeklyReportTest1() {
		press(PositronAPI.Key.MENU);
		press(DOWN, DOWN, PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "WeekReport", 500));
		assertEquals(Helpers.text1, stringAt("#reportlist.0.0.text"));
		assertEquals("16.00 hours", stringAt("#reportlist.0.1.text"));
		assertEquals(Helpers.text3, stringAt("#reportlist.1.0.text"));
		assertEquals("16.00 hours", stringAt("#reportlist.1.1.text"));
		assertEquals(Helpers.text4, stringAt("#reportlist.2.0.text"));
		assertEquals("8.00 hours", stringAt("#reportlist.2.1.text"));
		assertEquals("Hours worked this week: 40.00",
				stringAt("#reportlist.3.text"));
		press(PositronAPI.Key.BACK);
	}

	// @Ignore
	@Test
	public void weeklyReportTest2() {
		press(PositronAPI.Key.MENU);
		press(DOWN, DOWN, PositronAPI.Key.RIGHT);
		click();
		assertTrue(waitFor("class.simpleName", "WeekReport", 500));
		assertEquals(Helpers.text3, stringAt("#reportlist.0.0.text"));
		assertEquals("18.00 hours", stringAt("#reportlist.0.1.text"));
		assertEquals(Helpers.text1, stringAt("#reportlist.1.0.text"));
		assertEquals("14.00 hours", stringAt("#reportlist.1.1.text"));
		assertEquals(Helpers.text4, stringAt("#reportlist.2.0.text"));
		assertEquals("8.00 hours", stringAt("#reportlist.2.1.text"));
		assertEquals("Hours worked this week: 40.00",
				stringAt("#reportlist.3.text"));
		press(PositronAPI.Key.BACK);
	}
}

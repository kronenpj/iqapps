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

package com.googlecode.iqapps.IQTimeSheet.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

import com.googlecode.autoandroid.positron.junit4.TestCase;
import com.googlecode.iqapps.TimeHelpers;

/**
 * Class that tests the TimeHelpers class. This is relatively complete.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class TimeHelpersTest extends TestCase {
	private Log log = LogFactory.getLog(TimeHelpersTest.class);
	private static final String TAG = "TimeHelpersTest";

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToNearestMinute(long)}.
	 */
	// @Ignore
	@Test
	public void testMillisToNearestMinute() {
		long result = TimeHelpers.millisToNearestMinute(1262626212345l);
		assertEquals(1262626200000l, result);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToNearestTenth(long)}.
	 */
	// @Ignore
	@Test
	public void testMillisToNearestTenth() {
		long result = TimeHelpers.millisToNearestTenth(1262626212345l);
		assertEquals(1262626200000l, result);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToStartOfDay(long)}.
	 */
	// @Ignore
	@Test
	public void testMillisToStartOfDay() {
		long result = TimeHelpers.millisToStartOfDay(1262626212345l);
		assertEquals(1262581200000l, result);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToEndOfDay(long)}.
	 */
	// @Ignore
	@Test
	public void testMillisToEndOfDay() {
		long result = TimeHelpers.millisToEndOfDay(1262626212345l);
		assertEquals(1262667599999l, result);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToStartOfWeek(long)}.
	 */
	// @Ignore
	@Test
	public void testMillisToStartOfWeek() {
		long result = TimeHelpers.millisToStartOfWeek(1262626212345l);
		assertEquals(1262581200000l, result);
		assertEquals("Jan 4, 2010", TimeHelpers.millisToDate(result));
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToEndOfWeek(long)}.
	 */
	// @Ignore
	@Test
	public void testMillisToEndOfWeek() {
		long result = TimeHelpers.millisToEndOfWeek(1262626212345l);
		assertEquals(1263185999999l, result);
		assertEquals("Jan 10, 2010", TimeHelpers.millisToDate(result));
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#calculateDuration(long, long)}.
	 */
	// @Ignore
	@Test
	public void testCalculateDuration() {
		float result = TimeHelpers.calculateDuration(1262581200000l,
				1262624400000l);
		assertTrue(12.0 == result);
	}

	/**
	 * Test method for {@link com.googlecode.iqapps.TimeHelpers#millisToMinute(long)}
	 * .
	 */
	// @Ignore
	@Test
	public void testMillisToMinute() {
		// Be careful here. The method adjusts for offset from UTC, or the local
		// time zone. Some time zones have 30-minute offsets.
		int result = TimeHelpers.millisToMinute(1262626212345l);
		assertEquals(30, result);
		result = TimeHelpers.millisToMinute(1262581200000l);
		assertEquals(0, result);
		result = TimeHelpers.millisToMinute(1262667540000l);
		assertEquals(59, result);
		result = TimeHelpers.millisToMinute(0l);
		assertEquals(0, result);
		result = TimeHelpers.millisToMinute(1262581260000l);
		assertEquals(1, result);
	}

	/**
	 * Test method for {@link com.googlecode.iqapps.TimeHelpers#millisToHour(long)}.
	 */
	// @Ignore
	@Test
	public void testMillisToHour() {
		// Be careful here. The method adjusts for offset from UTC, or the local
		// time zone.
		int result = TimeHelpers.millisToHour(1262626212345l);
		assertEquals(12, result);
		result = TimeHelpers.millisToHour(1262667600001l);
		assertEquals(0, result);
		result = TimeHelpers.millisToHour(1262667540000l);
		assertEquals(23, result);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToDayOfMonth(long)}.
	 */
	// @Ignore
	@Test
	public void testMillisToDayOfMonth() {
		// Be careful here. The method adjusts for offset from UTC, or the local
		// time zone.
		int result = TimeHelpers.millisToDayOfMonth(1262626212345l);
		assertEquals(4, result);
		result = TimeHelpers.millisToDayOfMonth(1262667600001l);
		assertEquals(5, result);
		result = TimeHelpers.millisToDayOfMonth(1261501260000l);
		assertEquals(22, result);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisSetTime(long, int, int)}.
	 */
	// @Ignore
	@Test
	public void testMillisSetTime() {
		// Be careful here. The method adjusts for offset from UTC, or the local
		// time zone.
		long result = TimeHelpers.millisSetTime(1262626212345l, 12, 0);
		assertEquals(1262624400000l, result);
		result = TimeHelpers.millisSetTime(1262626212345l, 0, 0);
		assertEquals(1262581200000l, result);
		result = TimeHelpers.millisSetTime(1262626212345l, 23, 59);
		assertEquals(1262667540000l, result);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisSetTime(long, int, int)}.
	 */
	// @Ignore
	@Test
	public void testMillisSetDate() {
		// Be careful here. The method adjusts for offset from UTC, or the local
		// time zone.
		long result;
		result = TimeHelpers.millisSetDate(2010, 1, 1);
		assertEquals(1262322000000l, result);
		result = TimeHelpers.millisSetDate(2010, 7, 1);
		assertEquals(1277956800000l, result);
		result = TimeHelpers.millisSetDate(2009, 12, 31);
		assertEquals(1262235600000l, result);
	}

	/**
	 * Test method for {@link com.googlecode.iqapps.TimeHelpers#millisToDate(long)}.
	 */
	// @Ignore
	@Test
	public void testMillisToDate() {
		// Be careful here. The method adjusts for offset from UTC, or the local
		// time zone.
		String result = TimeHelpers.millisToDate(1262235600000l);
		assertEquals("Dec 31, 2009", result);
		result = TimeHelpers.millisToDate(1262626212345l);
		assertEquals("Jan 4, 2010", result);
		result = TimeHelpers.millisToDate(1262667600000l);
		assertEquals("Jan 5, 2010", result);
	}

	/**
	 * Test method for {@link com.googlecode.iqapps.TimeHelpers#formatHours(int)}.
	 */
	// @Ignore
	@Test
	public void testFormatHours() {
		String result = TimeHelpers.formatHours(9);
		assertEquals("09", result);
		result = TimeHelpers.formatHours(10);
		assertEquals("10", result);
	}

	/**
	 * Test method for {@link com.googlecode.iqapps.TimeHelpers#formatMinutes(int)}.
	 */
	// @Ignore
	@Test
	public void testFormatMinutes() {
		String result = TimeHelpers.formatMinutes(9);
		assertEquals("09", result);
		result = TimeHelpers.formatMinutes(10);
		assertEquals("10", result);
	}

	/**
	 * Test method for {@link com.googlecode.iqapps.TimeHelpers#millisNow()}.
	 */
	// @Ignore
	@Test
	public void testMillisNow() {
		long now = System.currentTimeMillis();
		long result = TimeHelpers.millisNow();
		long difference = (result - now) * (Long.signum(result - now));
		assertTrue(difference < 3);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#convertToCalendarMonth()}.
	 */
	// @Ignore
	@Test
	public void testConvertToCalendarMonth() {
		int month = 1; // January.
		int result = TimeHelpers.convertToCalendarMonth(month);
		assertEquals(java.util.Calendar.JANUARY, result);

		month = 7;
		result = TimeHelpers.convertToCalendarMonth(month);
		assertEquals(java.util.Calendar.JULY, result);

		month = 12;
		result = TimeHelpers.convertToCalendarMonth(month);
		assertEquals(java.util.Calendar.DECEMBER, result);
	}

	/**
	 * Test method for {@link com.googlecode.iqapps.TimeHelpers#convertToMonth()}.
	 */
	// @Ignore
	@Test
	public void testConvertToMonth() {
		String month = new String("Feb");
		int result = TimeHelpers.convertToMonth(month);
		assertEquals(2, result);

		month = new String("Jun");
		result = TimeHelpers.convertToMonth(month);
		assertEquals(6, result);

		month = new String("November");
		result = TimeHelpers.convertToMonth(month);
		assertEquals(11, result);

		month = new String("September");
		result = TimeHelpers.convertToMonth(month);
		assertEquals(9, result);
	}
}

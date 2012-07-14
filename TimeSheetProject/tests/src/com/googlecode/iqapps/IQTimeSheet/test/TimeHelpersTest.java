package com.googlecode.iqapps.IQTimeSheet.test;

import java.util.Calendar;
import java.util.TimeZone;

import android.test.AndroidTestCase;
import android.test.FlakyTest;
import android.test.suitebuilder.annotation.Suppress;

import com.googlecode.iqapps.TimeHelpers;

//@Suppress //#$##
public class TimeHelpersTest extends AndroidTestCase {

	/*
	 * NOTE: All time calculations are performed in Local time on the device
	 * utilized for testing.
	 */
	// May 18, 2012 11:24:00 -0400 (US/Eastern) ==
	// May 18, 2012 15:24:00 +0000 (UTC) ==
	private static final long MAY18_152400 = 1337354640000L;
	private static final long JAN04_173012 = 1262626212345L;
	private TimeZone tZone;

	@Override
	protected void setUp() throws Exception {
		tZone = TimeZone.getDefault();
		super.setUp();
	}

	/**
	 * Test method to understand what the timezone offset is. For development.
	 */
	@Suppress
	public void testCheckTimezone() {
		long timeInMillis = JAN04_173012;
		long tzOffset = tZone.getOffset(timeInMillis);
		assertTrue(tzOffset == 0.0);

		timeInMillis = MAY18_152400;
		tzOffset = tZone.getOffset(timeInMillis);
		assertTrue(tzOffset == 0.0);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToNearestMinute(long)}.
	 */
	public void testmillisToNearestMinute() {
		long timeInMillis = MAY18_152400;
		assertEquals(1337354640000L,
				TimeHelpers.millisToNearestMinute(timeInMillis));

		timeInMillis = TimeHelpers.millisToNearestMinute(JAN04_173012);
		assertEquals(1262626200000L, timeInMillis);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToAlignMinutes(long, int)}
	 * .
	 */
	public void testmillisToAlignMinutes() {
		long timeInMillis = MAY18_152400;
		assertEquals(1337355000000L,
				TimeHelpers.millisToAlignMinutes(timeInMillis, 30));

		// Move reference time back by 12 minutes and check again.
		timeInMillis -= (12 * 60 * 1000);
		assertEquals(1337353200000L,
				TimeHelpers.millisToAlignMinutes(timeInMillis, 30));

		timeInMillis = TimeHelpers.millisToAlignMinutes(JAN04_173012, 6);
		assertEquals(1262626200000L, timeInMillis);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToStartOfDay(long)}.
	 */
	public void testmillisToStartOfDay() {
		// This is start-of-day in the local time zone, so we must add the
		// offset to get a consistent result.
		long timeInMillis = MAY18_152400;
		timeInMillis = TimeHelpers.millisToStartOfDay(timeInMillis);
		long tzOffset = tZone.getOffset(timeInMillis);
		timeInMillis += tzOffset;
		assertEquals(1337299200000L, timeInMillis);

		timeInMillis = JAN04_173012;
		timeInMillis = TimeHelpers.millisToStartOfDay(timeInMillis);
		tzOffset = tZone.getOffset(timeInMillis);
		timeInMillis += tzOffset;
		assertEquals(1262563200000L, timeInMillis);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToEndOfDay(long)}.
	 */
	public void testmillisToEndOfDay() {
		// This is end-of-day in the local time zone, so we must add the offset
		// to get a consistent result.
		long timeInMillis = MAY18_152400;
		timeInMillis = TimeHelpers.millisToEndOfDay(timeInMillis);
		long tzOffset = tZone.getOffset(timeInMillis);
		timeInMillis += tzOffset;
		assertEquals(1337385600000L, timeInMillis);

		timeInMillis = JAN04_173012;
		timeInMillis = TimeHelpers.millisToEndOfDay(timeInMillis);
		tzOffset = tZone.getOffset(timeInMillis);
		timeInMillis += tzOffset;
		assertEquals(1262649600000L, timeInMillis);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToStartOfWeek(long)}.
	 */
	public void testmillisToStartOfWeek() {
		// This is start-of-week in the local time zone, so we must add the
		// offset to get a consistent result.
		long timeInMillis = MAY18_152400;
		timeInMillis = TimeHelpers.millisToStartOfWeek(timeInMillis);
		long tzOffset = tZone.getOffset(timeInMillis);
		timeInMillis += tzOffset;
		assertEquals(1336953600000L, timeInMillis);

		timeInMillis = JAN04_173012;
		timeInMillis = TimeHelpers.millisToStartOfWeek(timeInMillis);
		// Check the reported date BEFORE converting to UTC for comparison with
		// a raw number...
		assertEquals("Mon, Jan 4, 2010", TimeHelpers.millisToDate(timeInMillis));
		tzOffset = tZone.getOffset(timeInMillis);
		timeInMillis += tzOffset;
		assertEquals(1262563200000L, timeInMillis);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToStartOfWeek(long, int)}.
	 */
	public void testmillisToStartOfWeekWithDay() {
		// This is start-of-week in the local time zone, so we must add the
		// offset to get a consistent result.
		long timeInMillis = MAY18_152400;
		timeInMillis = TimeHelpers.millisToStartOfWeek(timeInMillis,
				Calendar.SUNDAY);
		long tzOffset = tZone.getOffset(timeInMillis);
		timeInMillis += tzOffset;
		assertEquals(1336867200000L, timeInMillis);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToEndOfWeek(long)}.
	 */
	public void testmillisToEndOfWeek() {
		// This is end-of-week in the local time zone, so we must add the offset
		// to get a consistent result.
		long timeInMillis = MAY18_152400;
		timeInMillis = TimeHelpers.millisToEndOfWeek(timeInMillis);
		long tzOffset = tZone.getOffset(timeInMillis);
		timeInMillis += tzOffset;
		assertEquals(1337558399999L, timeInMillis);

		timeInMillis = TimeHelpers.millisToEndOfWeek(JAN04_173012);
		assertEquals("Sun, Jan 10, 2010",
				TimeHelpers.millisToDate(timeInMillis));
		tzOffset = tZone.getOffset(timeInMillis);
		timeInMillis += tzOffset;
		assertEquals(1263167999999L, timeInMillis);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToEndOfWeek(long, int)}.
	 */
	public void testmillisToEndOfWeekWithDay() {
		// This is end-of-week in the local time zone, so we must add the offset
		// to get a consistent result.
		long timeInMillis = MAY18_152400;
		timeInMillis = TimeHelpers.millisToEndOfWeek(timeInMillis,
				Calendar.SATURDAY);
		long tzOffset = tZone.getOffset(timeInMillis);
		timeInMillis += tzOffset;
		assertEquals(1337471999999L, timeInMillis);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToMinute(long)}.
	 */
	public void testmillisToMinute() {
		// Be careful here. The method tested works in the local time zone. Some
		// time zones have 30-minute offsets.
		int result = TimeHelpers.millisToMinute(JAN04_173012);
		assertEquals(30, result);
		result = TimeHelpers.millisToMinute(1262581200000L);
		assertEquals(0, result);
		result = TimeHelpers.millisToMinute(1262667540000L);
		assertEquals(59, result);
		result = TimeHelpers.millisToMinute(0L);
		assertEquals(0, result);
		result = TimeHelpers.millisToMinute(1262581260000L);
		assertEquals(1, result);
		result = TimeHelpers.millisToMinute(MAY18_152400);
		assertEquals(24, result);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToHour(long)}.
	 */
	public void testmillisToHour() {
		// Be careful here. The method tested works in the local time zone.

		long timeInMillis = MAY18_152400;
		long tzOffset = tZone.getOffset(timeInMillis);
		// Make timeInMillis UTC for consistency of this check.
		timeInMillis -= tzOffset;
		assertEquals(15, TimeHelpers.millisToHour(timeInMillis));

		timeInMillis = JAN04_173012;
		tzOffset = tZone.getOffset(timeInMillis);
		timeInMillis -= tzOffset;
		assertEquals(17, TimeHelpers.millisToHour(timeInMillis));

		timeInMillis = 1262667600001L;
		tzOffset = tZone.getOffset(timeInMillis);
		timeInMillis -= tzOffset;
		assertEquals(5, TimeHelpers.millisToHour(timeInMillis));

		timeInMillis = 1262667540000L;
		tzOffset = tZone.getOffset(timeInMillis);
		timeInMillis -= tzOffset;
		assertEquals(4, TimeHelpers.millisToHour(timeInMillis));
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToDayOfMonth(long)}.
	 */
	public void testmillisToDayOfMonth() {
		// Be careful here. The method tested works in the local time zone.

		assertEquals(18, TimeHelpers.millisToDayOfMonth(MAY18_152400));
		assertEquals(4, TimeHelpers.millisToDayOfMonth(JAN04_173012));

		long temp = 1262667600001L;
		temp -= tZone.getOffset(temp);
		assertEquals(5, TimeHelpers.millisToDayOfMonth(temp));

		temp = 1261501260000L;
		temp -= tZone.getOffset(temp);
		assertEquals(22, TimeHelpers.millisToDayOfMonth(temp));
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToDayOfYear(long)}.
	 */
	public void testmillisToDayOfYear() {
		long timeInMillis = MAY18_152400;
		assertEquals(139, TimeHelpers.millisToDayOfYear(timeInMillis));
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToMonthOfYear(long)}.
	 */
	public void testmillisToMonthOfYear() {
		// Zero-based months
		long timeInMillis = MAY18_152400;
		assertEquals(4, TimeHelpers.millisToMonthOfYear(timeInMillis));
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToYear(long)}.
	 */
	public void testmillisToYear() {
		long timeInMillis = MAY18_152400;
		assertEquals(2012, TimeHelpers.millisToYear(timeInMillis));
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisSetTime(long, int, int)}.
	 */
	public void testmillisSetTime() {
		// Be careful here. The method tested works in the local time zone.

		// TODO: Explain why this works...
		// The time set here is interpreted as being in the local time zone, so
		// we add the offset to obtain UTC.
		long result = TimeHelpers.millisSetTime(MAY18_152400, 10, 56);
		long tzOffset = tZone.getOffset(result);
		result += tzOffset;
		assertEquals(1337338560000L, result);

		result = TimeHelpers.millisSetTime(JAN04_173012, 12, 0);
		tzOffset = tZone.getOffset(result);
		result += tzOffset;
		assertEquals(1262606400000L, result);

		result = TimeHelpers.millisSetTime(JAN04_173012, 0, 0);
		tzOffset = tZone.getOffset(result);
		result += tzOffset;
		assertEquals(1262563200000L, result);

		result = TimeHelpers.millisSetTime(JAN04_173012, 23, 59);
		tzOffset = tZone.getOffset(result);
		result += tzOffset;
		assertEquals(1262649540000L, result);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisSetDate(int, int, int)}.
	 */
	public void testmillisSetDate() {
		// Be careful here. The method tested works in the local time zone.

		long timeInMillis = TimeHelpers.millisSetDate(2012, 9, 16);
		// long tzOffset = tZone.getOffset(timeInMillis);
		// Make timeInMillis UTC for consistency of this check.
		// timeInMillis -= tzOffset;
		String result = TimeHelpers.millisToDate(timeInMillis);
		assertEquals("Wanted: Sun, Sep 16, 2012, Was: " + result,
				"Sun, Sep 16, 2012", result);
		// assertEquals(1347753600000L, timeInMillis);

		timeInMillis = TimeHelpers.millisSetDate(2010, 1, 1);
		// tzOffset = tZone.getOffset(timeInMillis);
		// timeInMillis -= tzOffset;
		result = TimeHelpers.millisToDate(timeInMillis);
		assertEquals("Wanted: Fri, Jan 1, 2010, Was: " + result,
				"Fri, Jan 1, 2010", result);
		// assertEquals(1262304000000L, timeInMillis);

		timeInMillis = TimeHelpers.millisSetDate(2010, 7, 1);
		// tzOffset = tZone.getOffset(timeInMillis);
		// timeInMillis -= tzOffset;
		result = TimeHelpers.millisToDate(timeInMillis);
		assertEquals("Wanted: Thu, Jul 1, 2010, Was: " + result,
				"Thu, Jul 1, 2010", result);
		// assertEquals(1277942400000L, timeInMillis);

		timeInMillis = TimeHelpers.millisSetDate(2009, 12, 31);
		// tzOffset = tZone.getOffset(timeInMillis);
		// timeInMillis -= tzOffset;
		result = TimeHelpers.millisToDate(timeInMillis);
		assertEquals("Wanted: Thu, Dec 31, 2009, Was: " + result,
				"Thu, Dec 31, 2009", result);
		// assertEquals(1262217600000L, timeInMillis);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToDate(long)}.
	 */
	public void testmillisToDate() {
		// Be careful here. The method tested works in the local time zone.
		String result = TimeHelpers.millisToDate(MAY18_152400);
		assertEquals("Fri, May 18, 2012", result);

		long temp = 1262235600000L;
		temp -= tZone.getOffset(temp);
		result = TimeHelpers.millisToDate(temp);
		assertEquals("Thu, Dec 31, 2009", result);

		result = TimeHelpers.millisToDate(JAN04_173012);
		assertEquals("Mon, Jan 4, 2010", result);

		temp = 1262667600000L;
		temp -= tZone.getOffset(temp);
		result = TimeHelpers.millisToDate(temp);
		assertEquals("Tue, Jan 5, 2010", result);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#millisToTimeDate(long)}.
	 */
	public void testmillisToTimeDate() {
		long timeInMillis = MAY18_152400;

		long tzOffset = tZone.getOffset(timeInMillis);
		// Make timeInMillis UTC for consistency of this check.
		timeInMillis -= tzOffset;
		String date = TimeHelpers.millisToTimeDate(timeInMillis);
		assertEquals("Want to see: May 18, 2012 15:24:00, but saw instead: "
				+ date, "May 18, 2012 15:24:00", date);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#calculateDuration(long, long)}.
	 */
	public void testCalculateDuration() {
		float result = TimeHelpers.calculateDuration(1262581200000L,
				1262624400000L);
		assertEquals((float) 12.0, result);

		long timeInMillis = MAY18_152400;
		assertEquals((float) 12.6,
				TimeHelpers.calculateDuration(timeInMillis, 1337486400000L));
	}

	/**
	 * Test method for {@link com.googlecode.iqapps.TimeHelpers#millisNow()}.
	 */
	@FlakyTest
	public void testMillisNow() {
		long now = System.currentTimeMillis();
		long result = TimeHelpers.millisNow();
		long difference = (result - now) * (Long.signum(result - now));
		assertTrue(difference < 3);
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#formatHours(int)}.
	 */
	public void testformatHours() {
		assertEquals("00", TimeHelpers.formatHours(0));
		assertEquals("05", TimeHelpers.formatHours(5));
		assertEquals("10", TimeHelpers.formatHours(10));
		assertEquals("15", TimeHelpers.formatHours(15));
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#formatMinutes(int)}.
	 */
	public void testformatMinutes() {
		assertEquals("00", TimeHelpers.formatMinutes(0));
		assertEquals("05", TimeHelpers.formatMinutes(5));
		assertEquals("10", TimeHelpers.formatMinutes(10));
		assertEquals("15", TimeHelpers.formatMinutes(15));
	}

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#convertToMonth()}.
	 */
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

	/**
	 * Test method for
	 * {@link com.googlecode.iqapps.TimeHelpers#convertToCalendarMonth()}.
	 */
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
}

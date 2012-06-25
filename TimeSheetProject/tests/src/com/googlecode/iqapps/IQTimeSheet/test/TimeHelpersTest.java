package com.googlecode.iqapps.IQTimeSheet.test;

import junit.framework.Assert;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Suppress;

import com.googlecode.iqapps.TimeHelpers;

public class TimeHelpersTest extends AndroidTestCase {

	/* NOTE: All time calculations are performed in UTC */
	// May 18, 2012 11:24:00 -0400 (Eastern)
	// May 18, 2012 15:24:00 +0000 (UTC)
	final static long may18 = 1337354640000L;

	public void testmillisToNearestMinute() {
		long timeInMillis = may18;
		Assert.assertEquals(1337354640000L,
				TimeHelpers.millisToNearestMinute(timeInMillis));
	}

	public void testmillisToAlignMinutes() {
		long timeInMillis = may18;
		Assert.assertEquals(1337355000000L,
				TimeHelpers.millisToAlignMinutes(timeInMillis, 30));

		// Move reference time back by 12 minutes and check again.
		timeInMillis -= (12 * 60 * 1000);
		Assert.assertEquals(1337353200000L,
				TimeHelpers.millisToAlignMinutes(timeInMillis, 30));
	}

	public void testmillisToStartOfDay() {
		long timeInMillis = may18;
		Assert.assertEquals(1337299200000L,
				TimeHelpers.millisToStartOfDay(timeInMillis));
	}

	public void testmillisToEndOfDay() {
		long timeInMillis = may18;
		Assert.assertEquals(1337385600000L,
				TimeHelpers.millisToEndOfDay(timeInMillis));
	}

	public void testmillisToStartOfWeek() {
		long timeInMillis = may18;
		Assert.assertEquals(1337385600000L,
				TimeHelpers.millisToEndOfDay(timeInMillis));
	}

	public void testmillisToEndOfWeek() {
		long timeInMillis = may18;
		Assert.assertEquals(1337385600000L,
				TimeHelpers.millisToEndOfDay(timeInMillis));
	}

	public void testcalculateDuration() {
		long timeInMillis = may18;
		Assert.assertEquals((float) 12.6,
				TimeHelpers.calculateDuration(timeInMillis, 1337486400000L));
	}

	public void testmillisToMinute() {
		long timeInMillis = may18;
		Assert.assertEquals(24, TimeHelpers.millisToMinute(timeInMillis));
	}

	public void testmillisToHour() {
		long timeInMillis = may18;
		Assert.assertEquals(15, TimeHelpers.millisToHour(timeInMillis));
	}

	public void testmillisToDayOfMonth() {
		long timeInMillis = may18;
		Assert.assertEquals(18, TimeHelpers.millisToDayOfMonth(timeInMillis));
	}

	public void testmillisToDayOfYear() {
		long timeInMillis = may18;
		Assert.assertEquals(139, TimeHelpers.millisToDayOfYear(timeInMillis));
	}

	public void testmillisToMonthOfYear() {
		// Zero-based months
		long timeInMillis = may18;
		Assert.assertEquals(4, TimeHelpers.millisToMonthOfYear(timeInMillis));
	}

	public void testmillisToYear() {
		long timeInMillis = may18;
		Assert.assertEquals(2012, TimeHelpers.millisToYear(timeInMillis));
	}

	@Suppress
	public void testmillisSetTime() {

	}

	@Suppress
	public void testmillisSetDate() {

	}

	@Suppress
	public void testmillisToDate() {

	}

	@Suppress
	public void testmillisToTimeDate() {

	}

	@Suppress
	public void testminutesBetweenMillis() {

	}

	@Suppress
	public void testformatHours() {

	}

	@Suppress
	public void testformatMinutes() {

	}
}

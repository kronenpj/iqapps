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

package com.googlecode.iqapps;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Collection of routines to help deal with millisecond times and dates. Mostly
 * just wrappers for the Gregorian calendar routines.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class TimeHelpers {
	@SuppressWarnings("unused")
	private static String TAG = "TimeHelpers";

	/*
	 * Method to parse information from milliseconds. This is intended to be an
	 * example, and is not used in this application.
	 * 
	 * @param millis Milliseconds to parse.
	 * 
	 * @return The supplied value trimmed to the minute boundary.
	 */
	@SuppressWarnings("unused")
	private long parseMillis(long timeInMillis) {
		final int days = (int) (timeInMillis / (24L * 60 * 60 * 1000));
		int remainder = (int) (timeInMillis % (24L * 60 * 60 * 1000));
		final int hours = remainder / (60 * 60 * 1000);
		remainder %= 60 * 60 * 1000;
		final int minutes = remainder / (60 * 1000);

		final long minInMillis = days * (24L * 60 * 60 * 1000) + hours
				* (60 * 60 * 1000) + minutes * (60 * 1000);

		final Date date = new Date(minInMillis);

		return minInMillis;
	}

	/*
	 * Method to trim millisecond resolution down to minute resolution. The
	 * user-interface deals with minute intervals, not milliseconds.
	 * 
	 * @param millis Milliseconds to trim to minutes.
	 * 
	 * @return The supplied value trimmed to the one-minute boundary.
	 */
	public static long millisToNearestMinute(long timeInMillis) {
		final int days = (int) (timeInMillis / (24L * 60 * 60 * 1000));
		int remainder = (int) (timeInMillis % (24L * 60 * 60 * 1000));
		final int hours = remainder / (60 * 60 * 1000);
		remainder %= 60 * 60 * 1000;
		final int minutes = remainder / (60 * 1000);

		final long minInMillis = days * (24L * 60 * 60 * 1000) + hours
				* (60 * 60 * 1000) + minutes * (60 * 1000);

		return minInMillis;
	}

	/*
	 * Method to trim millisecond resolution down to six-minute resolution. The
	 * user-interface deals with minute intervals, not milliseconds.
	 * 
	 * @param millis Milliseconds to trim to minutes.
	 * 
	 * @return The supplied value trimmed to the six-minute boundary.
	 */
	public static long millisToNearestTenth(long timeInMillis) {
		return millisToAlignMinutes(timeInMillis, 6);
	}

	/*
	 * Method to trim millisecond resolution down to six-minute resolution. The
	 * user-interface deals with minute intervals, not milliseconds.
	 * 
	 * TODO: This is not accurate when alignTo is odd, eg: 5. Figure out what to
	 * do there.
	 * 
	 * @param millis Milliseconds to trim to minutes.
	 * 
	 * @return The supplied value trimmed to the six-minute boundary.
	 */
	public static long millisToAlignMinutes(long timeInMillis, int alignTo) {
		final int days = (int) (timeInMillis / (24L * 60 * 60 * 1000));
		int remainder = (int) (timeInMillis % (24L * 60 * 60 * 1000));
		int hours = remainder / (60 * 60 * 1000);
		remainder %= 60 * 60 * 1000;
		int minutes = remainder / (60 * 1000);
		int modulo = minutes % alignTo;

		if (modulo < (alignTo / 2.0)) {
			minutes = minutes - modulo;
		} else {
			minutes = minutes + alignTo - modulo;
			if (minutes == 0)
				hours = hours + 1;
		}

		final long minInMillis = days * (24L * 60 * 60 * 1000) + hours
				* (60 * 60 * 1000) + minutes * (60 * 1000);

		return minInMillis;
	}

	/*
	 * Method to calculate the millisecond time for the start of the day.
	 * 
	 * @param millis Milliseconds to trim to the start of the day.
	 * 
	 * @return The supplied value trimmed back to the start of the day.
	 */
	public static long millisToStartOfDay(long timeInMillis) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(timeInMillis);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/*
	 * Method to calculate the millisecond time for the end of the day.
	 * 
	 * @param millis Milliseconds to extend to the end of the day.
	 * 
	 * @return The supplied value extended to the end of the day.
	 */
	public static long millisToEndOfDay(long timeInMillis) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(timeInMillis);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 0);
		// Flip to the next day by adding a second. This forces the calendar to
		// do most of the work :).
		calendar.add(Calendar.SECOND, 1);

		// Then take away one millisecond so that we're still bounding it by the
		// day.
		return calendar.getTimeInMillis() - 1;
	}

	/*
	 * Method to calculate the millisecond time for the start of the week.
	 * 
	 * @param millis Milliseconds to trim to the start of the week.
	 * 
	 * @return The supplied value trimmed back to the start of the week.
	 */
	public static long millisToStartOfWeek(long timeInMillis) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(timeInMillis);
		// TODO: Make this configurable.
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		// Make sure we actually went backward in time and adjust it if not.
		if (calendar.getTimeInMillis() > timeInMillis)
			calendar.add(Calendar.DAY_OF_MONTH, -7);

		return calendar.getTimeInMillis();
	}

	/*
	 * Method to calculate the millisecond time for the end of the week.
	 * 
	 * @param millis Milliseconds to extend to the end of the week.
	 * 
	 * @return The supplied value extended to the end of the week.
	 */
	public static long millisToEndOfWeek(long timeInMillis) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(timeInMillis);
		// TODO: Make this configurable.
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 0);
		// Flip to the next day by adding a second. This forces the calendar to
		// do most of the work :).
		calendar.add(Calendar.SECOND, 1);

		// Make sure we actually went forward in time and adjust it if not.
		if (calendar.getTimeInMillis() < timeInMillis)
			calendar.add(Calendar.DAY_OF_MONTH, 7);

		// Then take away one millisecond so that we're still bounding it by the
		// day.
		return calendar.getTimeInMillis() - 1;
	}

	/*
	 * Method to calculate the fractional number of hours between two time
	 * periods.
	 * 
	 * @param timeIn The start time of the calculation.
	 * 
	 * @param timeOut The end time of the calculation.
	 * 
	 * @return The fractional number of hours between the supplied times.
	 */
	public static float calculateDuration(long timeIn, long timeOut) {
		final long timeInMillis = timeOut - timeIn;
		int remainder = (int) (timeInMillis % (24L * 60 * 60 * 1000));
		int hours = remainder / (60 * 60 * 1000);
		remainder %= 60 * 60 * 1000;
		int minutes = remainder / (60 * 1000);

		final float hourFraction = (hours) + (float) (minutes / 60.0);

		return hourFraction;
	}

	/*
	 * Method to extract the minute represented by the supplied number in
	 * milliseconds.
	 * 
	 * @param millis Milliseconds to use for calculations.
	 * 
	 * @return The minute of the hour of the supplied time.
	 */
	public static int millisToMinute(long timeInMillis) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(timeInMillis);
		int minute = calendar.get(Calendar.MINUTE);

		return minute;
	}

	/*
	 * Method to trim millisecond resolution down to six-minute resolution. The
	 * user-interface deals with minute intervals, not milliseconds.
	 * 
	 * @param millis Milliseconds to use for calculations.
	 * 
	 * @return Hour of the day of the supplied time.
	 */
	public static int millisToHour(long timeInMillis) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(timeInMillis);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);

		return hour;
	}

	/*
	 * Method to return the day of month for a given time in milliseconds.
	 * 
	 * @param millis Milliseconds to use for calculations.
	 * 
	 * @return Day of month of supplied time.
	 */
	public static int millisToDayOfMonth(long timeInMillis) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(timeInMillis);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return day;
	}

	/*
	 * Method to return the month for a given time in milliseconds.
	 * 
	 * @param millis Milliseconds to use for calculations.
	 * 
	 * @return Month of supplied time.
	 */
	public static int millisToMonthOfYear(long timeInMillis) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(timeInMillis);
		int day = calendar.get(Calendar.MONTH);
		return day;
	}

	/*
	 * Method to return the year for a given time in milliseconds.
	 * 
	 * @param millis Milliseconds to use for calculations.
	 * 
	 * @return Year of supplied time.
	 */
	public static int millisToYear(long timeInMillis) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(timeInMillis);
		int day = calendar.get(Calendar.YEAR);
		return day;
	}

	/*
	 * Method to set time to supplied hours and minutes from an initial
	 * millisecond time.
	 * 
	 * @param millis Milliseconds to start from.
	 * 
	 * @param hour Hours to add to the start time.
	 * 
	 * @param minute Hours to add to the start time.
	 * 
	 * @return The supplied value trimmed back to the start of the day.
	 */
	public static long millisSetTime(long timeInMillis, int hours, int minutes) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(timeInMillis);
		calendar.set(Calendar.HOUR_OF_DAY, hours);
		calendar.set(Calendar.MINUTE, minutes);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/*
	 * Method to set time to supplied year, month and day.
	 * 
	 * @param year Year of the date to be set
	 * 
	 * @param month Month of the date to be set
	 * 
	 * @param date Day of month of the date to be set
	 * 
	 * @return The supplied value of the start of the day supplied.
	 */
	public static long millisSetDate(int year, int month, int date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, TimeHelpers
				.convertToCalendarMonth(month));
		calendar.set(Calendar.DAY_OF_MONTH, date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/*
	 * Method to return a string representation of the date.
	 * 
	 * @param millis Milliseconds to convert.
	 * 
	 * @return Localized date for the supplied time.
	 */
	public static String millisToDate(long timeInMillis) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(timeInMillis);
		SimpleDateFormat simpleDate = new SimpleDateFormat("MMM d, yyyy");
		String date = simpleDate.format(calendar.getTime());
		return date;
	}

	/*
	 * Method to compute the difference between two millisecond times in
	 * minutes.
	 * 
	 * @param firstTimeMillis Milliseconds as the minuend.
	 * 
	 * @param secondTimeMillis Milliseconds as the subtrahend.
	 * 
	 * @return The difference between first and second time in minutes.
	 */
	public static int minutesBetweenMillis(long firstTimeMillis,
			long secondTimeMillis) {
		long timeInMillis = firstTimeMillis - secondTimeMillis;
		int remainder = (int) (timeInMillis % (24L * 60 * 60 * 1000));
		remainder %= 60 * 60 * 1000;
		final int minutes = remainder / (60 * 1000);

		return minutes;
	}

	/*
	 * Method to return the day of month for a given time in milliseconds.
	 * 
	 * @param millis Milliseconds to use for calculations.
	 * 
	 * @return Day of month of supplied time.
	 */
	public static String formatHours(int in) {
		return (in <= 9 ? "0" : "") + in;
	}

	/*
	 * Method to return the day of month for a given time in milliseconds.
	 * 
	 * @param millis Milliseconds to use for calculations.
	 * 
	 * @return Day of month of supplied time.
	 */
	public static String formatMinutes(int in) {
		return (in <= 9 ? "0" : "") + in;
	}

	/*
	 * Method to return the current time in milliseconds.
	 * 
	 * @return The current time in milliseconds.
	 */
	public static long millisNow() {
		final long now = System.currentTimeMillis();

		return now;
	}

	/**
	 * Method to convert a "traditional" month to a java.util.Calendar month
	 * constant. They're offset by one if you're interested. January = 0,
	 * December = 11, etc..
	 * 
	 * @param month
	 *            Traditional month integer to convert. 1-January, 6-June,
	 *            12-December.
	 * @return The java.util.Calendar constant value for the supplied month
	 *         integer.
	 */
	public static int convertToCalendarMonth(int month) {
		int retMonth = -1;
		switch (month) {
		case 1:
			retMonth = java.util.Calendar.JANUARY;
			break;
		case 2:
			retMonth = java.util.Calendar.FEBRUARY;
			break;
		case 3:
			retMonth = java.util.Calendar.MARCH;
			break;
		case 4:
			retMonth = java.util.Calendar.APRIL;
			break;
		case 5:
			retMonth = java.util.Calendar.MAY;
			break;
		case 6:
			retMonth = java.util.Calendar.JUNE;
			break;
		case 7:
			retMonth = java.util.Calendar.JULY;
			break;
		case 8:
			retMonth = java.util.Calendar.AUGUST;
			break;
		case 9:
			retMonth = java.util.Calendar.SEPTEMBER;
			break;
		case 10:
			retMonth = java.util.Calendar.OCTOBER;
			break;
		case 11:
			retMonth = java.util.Calendar.NOVEMBER;
			break;
		case 12:
			retMonth = java.util.Calendar.DECEMBER;
			break;
		}

		return retMonth;
	}

	/**
	 * Method to convert a traditional month String to the corresponding
	 * integer.
	 * 
	 * @param month
	 *            Traditional month string to convert. January, June, Dec, Apr,
	 *            etc...
	 * @return The traditional "human" value for the supplied month string.
	 */
	public static int convertToMonth(String month) {
		int retMonth = -1;
		if (month.equalsIgnoreCase("january") || month.equalsIgnoreCase("jan"))
			retMonth = 1;
		if (month.equalsIgnoreCase("february") || month.equalsIgnoreCase("feb"))
			retMonth = 2;
		if (month.equalsIgnoreCase("march") || month.equalsIgnoreCase("mar"))
			retMonth = 3;
		if (month.equalsIgnoreCase("april") || month.equalsIgnoreCase("apr"))
			retMonth = 4;
		if (month.equalsIgnoreCase("may"))
			retMonth = 5;
		if (month.equalsIgnoreCase("june") || month.equalsIgnoreCase("jun"))
			retMonth = 6;
		if (month.equalsIgnoreCase("july") || month.equalsIgnoreCase("jul"))
			retMonth = 7;
		if (month.equalsIgnoreCase("august") || month.equalsIgnoreCase("aug"))
			retMonth = 8;
		if (month.equalsIgnoreCase("september")
				|| month.equalsIgnoreCase("sep"))
			retMonth = 9;
		if (month.equalsIgnoreCase("october") || month.equalsIgnoreCase("oct"))
			retMonth = 10;
		if (month.equalsIgnoreCase("november") || month.equalsIgnoreCase("nov"))
			retMonth = 11;
		if (month.equalsIgnoreCase("december") || month.equalsIgnoreCase("dec"))
			retMonth = 12;

		return retMonth;
	}
}

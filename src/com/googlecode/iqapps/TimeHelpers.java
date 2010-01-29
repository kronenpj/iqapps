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
		final int days = (int) (timeInMillis / (24L * 60 * 60 * 1000));
		int remainder = (int) (timeInMillis % (24L * 60 * 60 * 1000));
		int hours = remainder / (60 * 60 * 1000);
		remainder %= 60 * 60 * 1000;
		int minutes = remainder / (60 * 1000);
		int modulo = minutes % 6;

		if (modulo <= 2) {
			minutes = minutes - modulo;
		} else {
			minutes = minutes + 6 - modulo;
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
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		calendar.set(GregorianCalendar.MILLISECOND, 0);
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
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		calendar.set(GregorianCalendar.MINUTE, 59);
		calendar.set(GregorianCalendar.SECOND, 59);
		calendar.set(GregorianCalendar.MILLISECOND, 0);
		// Flip to the next day by adding a second. This forces the calendar to
		// do most of the work :).
		calendar.add(GregorianCalendar.SECOND, 1);

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
		calendar.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		calendar.set(GregorianCalendar.MILLISECOND, 0);

		// Make sure we actually went backward in time and adjust it if not.
		if (calendar.getTimeInMillis() > timeInMillis)
			calendar.add(GregorianCalendar.DAY_OF_MONTH, -7);

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
		calendar.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SUNDAY);
		calendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		calendar.set(GregorianCalendar.MINUTE, 59);
		calendar.set(GregorianCalendar.SECOND, 59);
		calendar.set(GregorianCalendar.MILLISECOND, 0);
		// Flip to the next day by adding a second. This forces the calendar to
		// do most of the work :).
		calendar.add(GregorianCalendar.SECOND, 1);

		// Make sure we actually went forward in time and adjust it if not.
		if (calendar.getTimeInMillis() < timeInMillis)
			calendar.add(GregorianCalendar.DAY_OF_MONTH, 7);

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

		final float hourFraction = (float) (hours) + (float) (minutes / 60.0);

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
		int minute = calendar.get(GregorianCalendar.MINUTE);

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
		int hour = calendar.get(GregorianCalendar.HOUR_OF_DAY);

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
		int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);
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
		int day = calendar.get(GregorianCalendar.MONTH);
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
		int day = calendar.get(GregorianCalendar.YEAR);
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
		calendar.set(GregorianCalendar.HOUR_OF_DAY, hours);
		calendar.set(GregorianCalendar.MINUTE, minutes);
		calendar.set(GregorianCalendar.SECOND, 0);
		calendar.set(GregorianCalendar.MILLISECOND, 0);
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
		calendar.set(GregorianCalendar.YEAR, year);
		calendar.set(GregorianCalendar.MONTH, month);
		calendar.set(GregorianCalendar.DAY_OF_MONTH, date);
		calendar.set(GregorianCalendar.HOUR, 0);
		calendar.set(GregorianCalendar.MINUTE, 0);
		calendar.set(GregorianCalendar.SECOND, 0);
		calendar.set(GregorianCalendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/*
	 * Method to return a slightly mangled locale string of the date. The locale
	 * date will most likely be mangled badly in non-English locales.
	 * 
	 * @param millis Milliseconds to convert.
	 * 
	 * @return Localized date for the supplied time.
	 */
	public static String millisToDate(long timeInMillis) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.setTimeInMillis(timeInMillis);
		String date = calendar.getTime().toLocaleString();
		int comma = date.indexOf(',');
		return date.substring(0, date.indexOf(' ', comma + 2));
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
}

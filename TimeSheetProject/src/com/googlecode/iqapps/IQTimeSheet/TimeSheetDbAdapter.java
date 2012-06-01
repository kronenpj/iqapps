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

package com.googlecode.iqapps.IQTimeSheet;

import java.sql.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.iqapps.TimeHelpers;

/**
 * Simple time sheet database helper class. Defines the basic CRUD operations
 * for the time sheet application, and gives the ability to list all entries as
 * well as retrieve or modify a specific entry.
 *
 * Graciously stolen from the Android Notepad example.
 *
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class TimeSheetDbAdapter {
	public static final String KEY_VERSION = "version";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_CHARGENO = "chargeno";
	public static final String KEY_TIMEIN = "timein";
	public static final String KEY_TIMEOUT = "timeout";
	public static final String KEY_RANGE = "range";
	public static final String KEY_TOTAL = "total";
	public static final String MAX_ROW = "max(" + KEY_ROWID + ")";
	public static final String MAX_COUNT = "max(" + KEY_VERSION + ")";
	public static final String KEY_TASK = "task";
	public static final String KEY_ACTIVE = "active";
	public static final String KEY_USAGE = "usage";
	public static final String KEY_OLDUSAGE = "oldusage";
	public static final String KEY_LASTUSED = "lastused";
	public static final String DB_FALSE = "0";
	public static final String DB_TRUE = "1";

	private static final String TAG = "TimeSheetDbAdapter";
	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	protected static final String DATABASE_NAME = "TimeSheetDB.db";
	private static final String TASKS_DATABASE_TABLE = "Tasks";
	private static final String CLOCK_DATABASE_TABLE = "TimeSheet";
	private static final String ENTRYITEMS_VIEW = "EntryItems";
	private static final String ENTRYREPORT_VIEW = "EntryReport";
	private static final String DATABASE_METADATA = "TimeSheetMeta";
	private static final int DATABASE_VERSION = 2;

	/**
	 * Database creation SQL statements
	 */
	private static final String CLOCK_TABLE_CREATE = "CREATE TABLE TimeSheet("
			+ KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "chargeno INTEGER NOT NULL, " + "timein INTEGER NOT NULL, "
			+ "timeout INTEGER NOT NULL DEFAULT 0" + ");";
	private static final String TASK_TABLE_CREATE = "CREATE TABLE Tasks("
			+ KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "task TEXT NOT NULL, " + "active BOOLEAN NOT NULL DEFAULT '"
			+ DB_TRUE + "', " + "usage INTEGER NOT NULL DEFAULT 0, "
			+ "oldusage INTEGER NOT NULL DEFAULT 0, "
			+ "lastused INTEGER NOT NULL DEFAULT 0" + ");";
	private static final String ENTRYITEMS_VIEW_CREATE = "CREATE VIEW "
			+ ENTRYITEMS_VIEW + " AS SELECT " + CLOCK_DATABASE_TABLE + "."
			+ KEY_ROWID + " as " + KEY_ROWID + "," + TASKS_DATABASE_TABLE + "."
			+ KEY_TASK + " as " + KEY_TASK + "," + CLOCK_DATABASE_TABLE + "."
			+ KEY_TIMEIN + " as " + KEY_TIMEIN + "," + CLOCK_DATABASE_TABLE
			+ "." + KEY_TIMEOUT + " as " + KEY_TIMEOUT + " FROM "
			+ CLOCK_DATABASE_TABLE + "," + TASKS_DATABASE_TABLE + " WHERE "
			+ CLOCK_DATABASE_TABLE + "." + KEY_CHARGENO + "="
			+ TASKS_DATABASE_TABLE + "." + KEY_ROWID + ";";
	private static final String ENTRYREPORT_VIEW_CREATE = "CREATE VIEW "
			+ ENTRYREPORT_VIEW + " AS SELECT " + CLOCK_DATABASE_TABLE + "."
			+ KEY_ROWID + " as " + KEY_ROWID + "," + TASKS_DATABASE_TABLE + "."
			+ KEY_TASK + " as " + KEY_TASK + "," + CLOCK_DATABASE_TABLE + "."
			+ KEY_TIMEIN + " as " + KEY_TIMEIN + "," + CLOCK_DATABASE_TABLE
			+ "." + KEY_TIMEOUT + " as " + KEY_TIMEOUT + ", strftime('%H:%M',"
			+ KEY_TIMEIN
			+ "/1000,'unixepoch','localtime') || ' to ' || CASE WHEN "
			+ KEY_TIMEOUT + " = 0 THEN 'now' ELSE strftime('%H:%M',"
			+ KEY_TIMEOUT + "/1000,'unixepoch','localtime') END as "
			+ KEY_RANGE + " FROM " + CLOCK_DATABASE_TABLE + ","
			+ TASKS_DATABASE_TABLE + " WHERE " + CLOCK_DATABASE_TABLE + "."
			+ KEY_CHARGENO + "=" + TASKS_DATABASE_TABLE + "." + KEY_ROWID + ";";
	private static final String TASKS_INDEX = "CREATE UNIQUE INDEX "
			+ TASKS_DATABASE_TABLE + "_index ON " + TASKS_DATABASE_TABLE + " ("
			+ KEY_TASK + ");";
	private static final String CHARGENO_INDEX = "CREATE INDEX "
			+ CLOCK_DATABASE_TABLE + "_chargeno_index ON "
			+ CLOCK_DATABASE_TABLE + " (" + KEY_CHARGENO + ");";
	private static final String TIMEIN_INDEX = "CREATE INDEX "
			+ CLOCK_DATABASE_TABLE + "_timein_index ON " + CLOCK_DATABASE_TABLE
			+ " (" + KEY_TIMEIN + ");";
	private static final String TIMEOUT_INDEX = "CREATE INDEX "
			+ CLOCK_DATABASE_TABLE + "_timeout_index ON "
			+ CLOCK_DATABASE_TABLE + " (" + KEY_TIMEOUT + ");";

	private static final String METADATA_CREATE = "create table "
			+ "TimeSheetMeta(version integer primary key);";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TASK_TABLE_CREATE);
			db.execSQL(CLOCK_TABLE_CREATE);
			db.execSQL(METADATA_CREATE);
			db.execSQL(ENTRYITEMS_VIEW_CREATE);
			db.execSQL(ENTRYREPORT_VIEW_CREATE);
			db.execSQL(TASKS_INDEX);
			db.execSQL(CHARGENO_INDEX);
			db.execSQL(TIMEIN_INDEX);
			db.execSQL(TIMEOUT_INDEX);

			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_VERSION, DATABASE_VERSION);

			db.insert(DATABASE_METADATA, null, initialValues);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ".");
			// db.execSQL("ALTER TABLE TimeSheet ADD...");
			// db.execSQL("UPDATE TimeSheet SET ");
			switch (oldVersion) {
			case 1:
				db.execSQL(CHARGENO_INDEX);
				db.execSQL(TIMEIN_INDEX);
				db.execSQL(TIMEOUT_INDEX);
				db.execSQL("UPDATE " + DATABASE_METADATA + " SET "
						+ KEY_VERSION + "=" + newVersion);
			}
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 */
	public TimeSheetDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the time sheet database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 *
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public TimeSheetDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		try {
			mDb = mDbHelper.getWritableDatabase();
		} catch (NullPointerException e) {
			mDb = mCtx.openOrCreateDatabase(DATABASE_NAME, 0, null);
		}
		return this;
	}

	/**
	 * Close the time sheet database.
	 */
	public void close() {
		mDbHelper.close();
	}

	/**
	 * Create a new time entry using the charge number provided. If the entry is
	 * successfully created return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 *
	 * @param chargeno
	 *            the charge number for the entry
	 *
	 * @return rowId or -1 if failed
	 */
	public long createEntry(String task) {
		long chargeno = getTaskIDByName(task);
		return createEntry(chargeno);
	}

	/**
	 * Create a new time entry using the charge number provided. If the entry is
	 * successfully created return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 *
	 * @param chargeno
	 *            the charge number for the entry
	 *
	 * @return rowId or -1 if failed
	 */
	public long createEntry(long chargeno) {
		return createEntry(chargeno, System.currentTimeMillis());
	}

	/**
	 * Create a new time entry using the charge number provided. If the entry is
	 * successfully created return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 *
	 * @param chargeno
	 *            the charge number for the entry
	 * @param timeIn
	 *            the time in milliseconds of the clock-in
	 *
	 * @return rowId or -1 if failed
	 */
	public long createEntry(String task, long timeIn) {
		long chargeno = getTaskIDByName(task);
		return createEntry(chargeno, timeIn);
	}

	/**
	 * Create a new time entry using the charge number provided. If the entry is
	 * successfully created return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 *
	 * @param chargeno
	 *            the charge number for the entry
	 * @param timeIn
	 *            the time in milliseconds of the clock-in
	 *
	 * @return rowId or -1 if failed
	 */
	public long createEntry(long chargeno, long timeIn) {
		if (TimeSheetActivity.prefs.getAlignMinutesAuto()) {
			timeIn = TimeHelpers.millisToAlignMinutes(timeIn,
					TimeSheetActivity.prefs.getAlignMinutes());
		}

		incrementTaskUsage(chargeno);

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CHARGENO, chargeno);
		initialValues.put(KEY_TIMEIN, timeIn);

		Log.d(TAG, "createEntry: " + chargeno);
		return mDb.insert(CLOCK_DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Create a new time entry using the charge number provided. If the entry is
	 * successfully created return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 *
	 * @param chargeno
	 *            the charge number for the entry
	 *
	 * @return rowId or -1 if failed
	 */
	public long closeEntry() {
		long rowID = lastClockEntry();
		Cursor c = fetchEntry(rowID, KEY_CHARGENO);

		c.moveToFirst();

		long chargeno = c.getLong(0);
		c.close();
		return closeEntry(chargeno);
	}

	/**
	 * Create a new time entry using the charge number provided. If the entry is
	 * successfully created return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 *
	 * @param chargeno
	 *            the charge number for the entry
	 *
	 * @return rowId or -1 if failed
	 */
	public long closeEntry(String task) {
		long chargeno = getTaskIDByName(task);
		return closeEntry(chargeno);
	}

	/**
	 * Create a new time entry using the charge number ID provided. If the entry
	 * is successfully created return the new rowId for that note, otherwise
	 * return a -1 to indicate failure.
	 *
	 * @param chargeno
	 *            the charge number for the entry
	 *
	 * @return rowId or -1 if failed
	 */
	public long closeEntry(long chargeno) {
		return closeEntry(chargeno, System.currentTimeMillis());
	}

	/**
	 * Create a new time entry using the charge number ID provided. If the entry
	 * is successfully created return the new rowId for that note, otherwise
	 * return a -1 to indicate failure.
	 *
	 * @param chargeno
	 *            the charge number for the entry
	 *
	 * @return rowId or -1 if failed
	 */
	public long closeEntry(String task, long timeOut) {
		long chargeno = getTaskIDByName(task);
		return closeEntry(chargeno, timeOut);
	}

	/**
	 * Close an existing time entry using the charge number provided. If the
	 * entry is successfully created return the new rowId for that note,
	 * otherwise return a -1 to indicate failure.
	 *
	 * @param chargeno
	 *            the charge number for the entry
	 * @param timeOut
	 *            the time in milliseconds of the clock-out
	 *
	 * @return rowId or -1 if failed
	 */
	public long closeEntry(long chargeno, long timeOut) {
		final long origTimeOut = timeOut;
		if (TimeSheetActivity.prefs.getAlignMinutesAuto()) {
			timeOut = TimeHelpers.millisToAlignMinutes(timeOut,
					TimeSheetActivity.prefs.getAlignMinutes());
			// TODO: Fix in a more sensible way.
			// Hack to account for a cross-day automatic clock out.
			// if (timeOut - origTimeOut == 1)
			// timeOut = origTimeOut;
		}
		ContentValues updateValues = new ContentValues();
		updateValues.put(KEY_TIMEOUT, timeOut);

		Log.d(TAG, "closeEntry: " + chargeno);
		return mDb.update(CLOCK_DATABASE_TABLE, updateValues, KEY_ROWID
				+ "= ? and " + KEY_CHARGENO + " = ?", new String[] {
				Long.toString(lastClockEntry()), Long.toString(chargeno) });
	}

	/**
	 * Delete the entry with the given rowId
	 *
	 * @param rowId
	 *            code id of note to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteEntry(long rowId) {
		Log.i("Delete called", "value__" + rowId);
		return mDb.delete(CLOCK_DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all entries in the database
	 *
	 * @return Cursor over all database entries
	 */
	public Cursor fetchAllTimeEntries() {
		return mDb.query(CLOCK_DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_CHARGENO, KEY_TIMEIN, KEY_TIMEOUT }, null, null, null,
				null, null);
	}

	/**
	 * Return a Cursor positioned at the entry that matches the given rowId
	 *
	 * @param rowId
	 *            id of entry to retrieve
	 * @return Cursor positioned to matching entry, if found
	 * @throws SQLException
	 *             if entry could not be found/retrieved
	 */
	public Cursor fetchEntry(long rowId) throws SQLException {
		Cursor mCursor = mDb.query(true, CLOCK_DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_CHARGENO, KEY_TIMEIN, KEY_TIMEOUT }, KEY_ROWID
				+ "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * Return a Cursor positioned at the entry that matches the given rowId
	 *
	 * @param rowId
	 *            id of entry to retrieve
	 * @return Cursor positioned to matching entry, if found
	 * @throws SQLException
	 *             if entry could not be found/retrieved
	 */
	public Cursor fetchEntry(long rowId, String column) throws SQLException {
		Cursor mCursor = mDb.query(true, CLOCK_DATABASE_TABLE,
				new String[] { column }, KEY_ROWID + "=" + rowId, null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * Update the note using the details provided. The entry to be updated is
	 * specified using the rowId, and it is altered to use the title and body
	 * values passed in
	 *
	 * @param rowId
	 *            id of entry to update
	 * @param title
	 *            value to set entry title to
	 * @param body
	 *            value to set note body to
	 * @return true if the note was successfully updated, false otherwise
	 */
	public boolean updateEntry(long rowId, long chargeno, String date,
			long timein, long timeout) {
		ContentValues args = new ContentValues();
		// Only change items that aren't null or -1.
		if (chargeno != -1)
			args.put(KEY_CHARGENO, chargeno);
		if (timein != -1)
			args.put(KEY_TIMEIN, timein);
		if (timeout != -1)
			args.put(KEY_TIMEOUT, timeout);

		if (rowId == -1)
			rowId = lastClockEntry();

		return mDb.update(CLOCK_DATABASE_TABLE, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	/**
	 * Retrieve the last entry in the table. Hopefully this will be deprecated
	 * in favor of something a little more robust in the future.
	 *
	 * @return rowId or -1 if failed
	 */
	public long taskIDForLastClockEntry() {
		long lastClockID = lastClockEntry();

		Cursor mCursor = mDb.query(true, CLOCK_DATABASE_TABLE,
				new String[] { KEY_CHARGENO }, KEY_ROWID + " = " + lastClockID,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		if (mCursor.isAfterLast())
			return -1;

		long response = mCursor.getLong(0);
		mCursor.close();

		return response;
	}

	/**
	 * Retrieve the last entry in the table. Hopefully this will be deprecated
	 * in favor of something a little more robust in the future.
	 *
	 * @return rowId or -1 if failed
	 */
	public long lastTaskEntry() {
		Cursor mCursor = mDb.query(true, TASKS_DATABASE_TABLE,
				new String[] { MAX_ROW }, null, null, null, null, null, null);
		mCursor.moveToFirst();
		long response = mCursor.getLong(0);
		mCursor.close();
		return response;
	}

	/**
	 * Return a Cursor positioned at the entry that matches the given rowId
	 *
	 * @param rowId
	 *            id of entry to retrieve
	 * @return Cursor positioned to matching entry, if found
	 * @throws SQLException
	 *             if entry could not be found/retrieved
	 */
	public Cursor getTimeEntryTuple(long rowId) throws SQLException {
		Cursor mCursor = mDb.query(true, ENTRYITEMS_VIEW, new String[] {
				KEY_ROWID, KEY_TASK, KEY_TIMEIN, KEY_TIMEOUT }, KEY_ROWID + "="
				+ rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * Retrieve the entry in the timesheet table immediately prior to the
	 * supplied entry.
	 *
	 * @return rowId or -1 if failed
	 */
	public long getPreviousClocking(long rowID) {
		long thisTimeIn = -1;
		long prevTimeOut = -1;

		Log.d(TAG, "getPreviousClocking for row: " + rowID);

		// Get the tuple from the provided row
		Cursor mCurrent = getTimeEntryTuple(rowID);

		// KEY_ROWID, KEY_TASK, KEY_TIMEIN, KEY_TIMEOUT
		if (mCurrent != null) {
			try {
				mCurrent.moveToFirst();
				String response = mCurrent.getString(2);
				thisTimeIn = Long.parseLong(response);
				Log.d(TAG, "timeIn for current: " + thisTimeIn);
			} catch (IllegalStateException e) {
			} finally {
				mCurrent.close();
			}
		}

		// Query to discover the immediately previous row ID.
		Cursor mCursor = mDb.query(true, CLOCK_DATABASE_TABLE,
				new String[] { KEY_ROWID }, KEY_ROWID + " < '" + rowID + "'",
				null, null, null, KEY_ROWID + " desc", "1");
		if (mCursor != null) {
			mCursor.moveToFirst();
		} else {
			return -1;
		}
		String response = mCursor.getString(0);
		long prevRowID = Long.parseLong(response);
		mCursor.close();
		Log.d(TAG, "rowID for previous: " + prevRowID);

		// Get the tuple from the just-retrieved row
		mCurrent = getTimeEntryTuple(prevRowID);
		// KEY_ROWID, KEY_TASK, KEY_TIMEIN, KEY_TIMEOUT
		if (mCurrent != null) {
			try {
				mCurrent.moveToFirst();
				String response1 = mCurrent.getString(3);
				prevTimeOut = Long.parseLong(response1);
				Log.d(TAG, "timeOut for previous: " + prevTimeOut);
			} catch (IllegalStateException e) {
			} finally {
				mCurrent.close();
			}
			// If the two tasks don't flow from one to another, don't allow the
			// entry to be adjusted.
			if (thisTimeIn != prevTimeOut)
				prevRowID = -1;
		}

		return prevRowID;
	}

	/**
	 * Retrieve the entry in the timesheet table immediately following to the
	 * supplied entry.
	 *
	 * @return rowId or -1 if failed
	 */
	// TODO: Should this be chronological or ordered by _id? as it is now?
	// And, if it should be chronological by time in or time out or both... :(
	public long getNextClocking(long rowID) {
		long thisTimeOut = -1;
		long nextTimeIn = -1;

		Log.d(TAG, "getNextClocking for row: " + rowID);

		// Get the tuple from the provided row
		Cursor mCurrent = getTimeEntryTuple(rowID);
		// KEY_ROWID, KEY_TASK, KEY_TIMEIN, KEY_TIMEOUT
		if (mCurrent != null) {
			try {
				mCurrent.moveToFirst();
				String response = mCurrent.getString(3);
				thisTimeOut = Long.parseLong(response);
				Log.d(TAG, "timeOut for current: " + thisTimeOut);
			} catch (IllegalStateException e) {
			} finally {
				mCurrent.close();
			}
		}

		// Query to discover the immediately following row ID.
		Cursor mCursor = null;
		try {
			mCursor = mDb.query(true, CLOCK_DATABASE_TABLE,
					new String[] { KEY_ROWID }, KEY_ROWID + " > '" + rowID
							+ "'", null, null, null, KEY_ROWID, "1");
		} catch (RuntimeException e) {
			Log.i(TAG, "Caught exception finding next clocking.");
			Log.i(TAG, e.toString());
			return -1;
		}
		if (mCursor != null) {
			mCursor.moveToFirst();
		} else {
			return -1;
		}

		long nextRowID = -1;
		try {
			String response = mCursor.getString(0);
			nextRowID = Long.parseLong(response);
		} catch (CursorIndexOutOfBoundsException e) {
			Log.i(TAG, "Caught exception retrieving row.");
			Log.i(TAG, e.toString());
			return -1;
		} catch (RuntimeException e) {
			Log.i(TAG, "Caught exception retrieving row.");
			Log.i(TAG, e.toString());
			return -1;
		} finally {
			mCursor.close();
		}
		Log.d(TAG, "rowID for next: " + nextRowID);

		// Get the tuple from the just-retrieved row
		mCurrent = getTimeEntryTuple(nextRowID);
		// KEY_ROWID, KEY_TASK, KEY_TIMEIN, KEY_TIMEOUT
		if (mCurrent != null) {
			try {
				mCurrent.moveToFirst();
				String response1 = mCurrent.getString(2);
				nextTimeIn = Long.parseLong(response1);
				Log.d(TAG, "timeIn for next: " + nextTimeIn);
			} catch (IllegalStateException e) {
			} finally {
				mCurrent.close();
			}
			// If the two tasks don't flow from one to another, don't allow the
			// entry to be adjusted.
			if (thisTimeOut != nextTimeIn)
				nextRowID = -1;
		}

		return nextRowID;
	}

	/**
	 * Retrieve the last entry in the table. Hopefully this will be deprecated
	 * in favor of something a little more robust in the future.
	 *
	 * @return rowId or -1 if failed
	 */
	public long lastClockEntry() {
		Cursor mCursor = mDb.query(true, CLOCK_DATABASE_TABLE,
				new String[] { MAX_ROW }, null, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		long response = mCursor.getLong(0);
		mCursor.close();
		return response;
	}

	/**
	 * Retrieve the last entry in the table. Hopefully this will be deprecated
	 * in favor of something a little more robust in the future.
	 *
	 * @return rowId or -1 if failed
	 */
	public long[] todaysEntries() {
		long now = TimeHelpers.millisNow();
		long todayStart = TimeHelpers.millisToStartOfDay(now);
		long todayEnd = TimeHelpers.millisToEndOfDay(now);
		long[] rows = null;

		// public Cursor query(boolean distinct, String table, String[] columns,
		// String selection, String[] selectionArgs, String groupBy, String
		// having, String orderBy) {

		Cursor mCursor = mDb.query(false, CLOCK_DATABASE_TABLE,
				new String[] { KEY_ROWID }, KEY_TIMEIN + ">=? and ("
						+ KEY_TIMEOUT + "<=? or " + KEY_TIMEOUT + "= 0)",
				new String[] { String.valueOf(todayStart).toString(),
						String.valueOf(todayEnd).toString() }, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToLast();
		} else {
			Log.e(TAG, "todaysEntried mCursor is null.");
		}

		if (mCursor.isAfterLast()) {
			Toast.makeText(mCtx, "No entries in the database for today.",
					Toast.LENGTH_SHORT);
			return null;
		}

		rows = new long[mCursor.getCount()];

		mCursor.moveToFirst();
		while (!mCursor.isAfterLast()) {
			rows[mCursor.getPosition()] = mCursor.getLong(0);
			mCursor.moveToNext();
		}
		mCursor.close();
		return rows;
	}

	/**
	 * Retrieve list of entries for the day surrounding the supplied time.
	 *
	 * @return rowId or -1 if failed
	 */
	public Cursor getEntryReportCursor(boolean distinct, String[] columns,
			long start, long end) {
		return getEntryReportCursor(distinct, columns, null, null, start, end);
	}

	/**
	 * Retrieve list of entries for the day surrounding the supplied time.
	 *
	 * @return rowId or -1 if failed
	 */
	protected Cursor getEntryReportCursor(boolean distinct, String[] columns,
			String groupBy, String orderBy, long start, long end) {
		// public Cursor query(boolean distinct, String table, String[] columns,
		// String selection, String[] selectionArgs, String groupBy, String
		// having, String orderBy, String limit) {

		String selection;
		final int endDay = TimeHelpers.millisToDayOfMonth(end - 1000);
		final long now = TimeHelpers.millisNow();
		if (TimeHelpers.millisToDayOfMonth(now - 1000) == endDay
				|| TimeHelpers.millisToDayOfMonth(TimeHelpers
						.millisToEndOfWeek(now - 1000)) == endDay) {
			Log.d(TAG, "Allowing selection of zero-end-hour entries.");
			selection = KEY_TIMEIN + " >=? and " + KEY_TIMEOUT + " <= ? and ("
					+ KEY_TIMEOUT + " >= " + KEY_TIMEIN + " or " + KEY_TIMEOUT
					+ " = 0)";
		} else {
			selection = KEY_TIMEIN + " >=? and " + KEY_TIMEOUT + " <= ? and "
					+ KEY_TIMEOUT + " >= " + KEY_TIMEIN;
		}

		Log.d(TAG, "getEntryCursorReport: Selection criteria: " + selection);
		Log.d(TAG, "getEntryCursorReport: Selection arguments: " + start + ", "
				+ end);
		Cursor mCursor = mDb.query(distinct, ENTRYREPORT_VIEW, columns,
				selection, new String[] { String.valueOf(start).toString(),
						String.valueOf(end).toString() }, groupBy, null,
				orderBy, null);
		if (mCursor != null) {
			mCursor.moveToLast();
		} else {
			Log.e(TAG, "entryReport mCursor for range is null.");
		}

		if (mCursor.isAfterLast()) {
			Log.d(TAG, "entryReport mCursor for range is empty.");
			Toast.makeText(mCtx,
					"No entries in the database for supplied range.",
					Toast.LENGTH_SHORT);
			return null;
		}

		return mCursor;
	}

	/**
	 * Retrieve list of entries for the day surrounding the current time.
	 *
	 * @return rowId or -1 if failed
	 */
	public Cursor dayEntryReport() {
		return dayEntryReport(TimeHelpers.millisNow());
	}

	/**
	 * Retrieve list of entries for the day surrounding the supplied time.
	 *
	 * @return rowId or -1 if failed
	 */
	public Cursor dayEntryReport(long time) {
		if (time < 0)
			dayEntryReport();

		long todayStart = TimeHelpers.millisToStartOfDay(time);
		long todayEnd = TimeHelpers.millisToEndOfDay(time);

		Log.d(TAG, "dayEntryReport start: "
				+ TimeHelpers.millisToDate(todayStart));
		Log.d(TAG, "dayEntryReport end: " + TimeHelpers.millisToDate(todayEnd));

		String[] columns = new String[] { KEY_ROWID, KEY_TASK, KEY_RANGE,
				KEY_TIMEIN, KEY_TIMEOUT };
		return getEntryReportCursor(false, columns, null, KEY_TIMEIN,
				todayStart, todayEnd);
	}

	/**
	 * Method that retrieves the entries for today from the entry view.
	 *
	 * @return Cursor over the results.
	 */
	public Cursor daySummary() {
		return daySummary(TimeHelpers.millisNow());
	}

	/**
	 * Method that retrieves the entries for a single specified day from the
	 * entry view.
	 *
	 * @return Cursor over the results.
	 */
	public Cursor daySummary(long time) {
		if (time < 0)
			daySummary();

		long todayStart = TimeHelpers.millisToStartOfDay(time);
		long todayEnd = TimeHelpers.millisToEndOfDay(time);

		Log.d(TAG, "daySummary start: " + TimeHelpers.millisToDate(todayStart));
		Log.d(TAG, "daySummary end: " + TimeHelpers.millisToDate(todayEnd));

		String[] columns = {
				KEY_ROWID,
				KEY_TASK,
				KEY_TIMEIN,
				"sum((CASE WHEN " + KEY_TIMEOUT + " = 0 THEN " + time
						+ " ELSE " + KEY_TIMEOUT + " END - " + KEY_TIMEIN
						+ ")/3600000.0) as " + KEY_TOTAL };
		String groupBy = KEY_TASK;
		String orderBy = KEY_TOTAL + " DESC";
		return getEntryReportCursor(true, columns, groupBy, orderBy,
				todayStart, todayEnd);
	}

	/**
	 * Retrieve list of entries for the day surrounding the current time.
	 *
	 * @return rowId or -1 if failed
	 */
	public Cursor weekEntryReport() {
		return weekEntryReport(TimeHelpers.millisNow());
	}

	/**
	 * Retrieve list of entries for the day surrounding the supplied time.
	 *
	 * @return rowId or -1 if failed
	 */
	public Cursor weekEntryReport(long time) {
		if (time < 0)
			weekEntryReport();

		long todayStart = TimeHelpers.millisToStartOfWeek(time);
		long todayEnd = TimeHelpers.millisToEndOfWeek(time);

		// public Cursor query(boolean distinct, String table, String[] columns,
		// String selection, String[] selectionArgs, String groupBy, String
		// having, String orderBy) {

		Log.d(TAG, "weekEntryReport start: "
				+ TimeHelpers.millisToDate(todayStart));
		Log
				.d(TAG, "weekEntryReport end: "
						+ TimeHelpers.millisToDate(todayEnd));

		String[] columns = new String[] { KEY_ROWID, KEY_TASK, KEY_RANGE,
				KEY_TIMEIN, KEY_TIMEOUT };
		return getEntryReportCursor(false, columns, todayStart, todayEnd);
	}

	/**
	 * Method that retrieves the entries for today from the entry view.
	 *
	 * @return Cursor over the results.
	 */
	public Cursor weekSummary() {
		return weekSummary(TimeHelpers.millisNow());
	}

	/**
	 * Method that retrieves the entries for a single specified day from the
	 * entry view.
	 *
	 * @return Cursor over the results.
	 */
	public Cursor weekSummary(long time) {
		if (time < 0)
			weekSummary();

		long todayStart = TimeHelpers.millisToStartOfWeek(time);
		long todayEnd = TimeHelpers.millisToEndOfWeek(time);

		Log
				.d(TAG, "weekSummary start: "
						+ TimeHelpers.millisToDate(todayStart));
		Log.d(TAG, "weekSummary end: " + TimeHelpers.millisToDate(todayEnd));

		// select _id, task, timein, sum((CASE WHEN timeout = 0 THEN **time**
		// ELSE timeout END - timein)/3600000.0) as total where timein>=0 and
		// timeout<=1300000000 group by task order by total desc;

		String[] columns = {
				KEY_ROWID,
				KEY_TASK,
				KEY_TIMEIN,
				"sum((CASE WHEN " + KEY_TIMEOUT + " = 0 THEN " + time
						+ " ELSE " + KEY_TIMEOUT + " END - " + KEY_TIMEIN
						+ ")/3600000.0) as " + KEY_TOTAL };
		String groupBy = KEY_TASK;
		String orderBy = KEY_TOTAL + " DESC";
		return getEntryReportCursor(true, columns, groupBy, orderBy,
				todayStart, todayEnd);
	}

	/**
	 * Create a new time entry using the charge number provided. If the entry is
	 * successfully created return the new rowId for that note, otherwise return
	 * a -1 to indicate failure.
	 *
	 * @param task
	 *            the charge number for the entry
	 *
	 * @return rowId or -1 if failed
	 */
	public long createTask(String task) {
		long tempDate = System.currentTimeMillis(); // Local time...
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TASK, task);
		initialValues.put(KEY_LASTUSED, tempDate);

		return mDb.insert(TASKS_DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Return a Cursor over the list of all entries in the database
	 *
	 * @return Cursor over all database entries
	 */
	public Cursor fetchAllTaskEntries() {
		return mDb.query(TASKS_DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_TASK, KEY_ACTIVE, KEY_USAGE, KEY_OLDUSAGE, KEY_LASTUSED },
				"active='" + DB_TRUE + "'", null, null, null,
				"usage + (oldusage / 2) DESC");
	}

	/**
	 * Return a Cursor over the list of all entries in the database
	 *
	 * @return Cursor over all database entries
	 */
	public Cursor fetchAllDisabledTasks() {
		return mDb.query(TASKS_DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_TASK, KEY_ACTIVE, KEY_USAGE, KEY_OLDUSAGE, KEY_LASTUSED },
				"active='" + DB_FALSE + "'", null, null, null, KEY_TASK);
	}

	/**
	 * Return a Cursor positioned at the entry that matches the given rowId
	 *
	 * @param rowId
	 *            id of entry to retrieve
	 * @return Cursor positioned to matching entry, if found
	 * @throws SQLException
	 *             if entry could not be found/retrieved
	 */
	public Cursor fetchTask(long rowId) throws SQLException {
		Cursor mCursor = mDb.query(true, TASKS_DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_TASK, KEY_ACTIVE, KEY_USAGE, KEY_OLDUSAGE,
				KEY_LASTUSED }, KEY_ROWID + "=" + rowId, null, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * Retrieve the last entry in the table. Hopefully this will be deprecated
	 * in favor of something a little more robust in the future.
	 *
	 * @return rowId or -1 if failed
	 */
	public long getTaskIDByName(String name) {
		Cursor mCursor = mDb.query(true, TASKS_DATABASE_TABLE,
				new String[] { KEY_ROWID }, KEY_TASK + " = '" + name + "'",
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		} else {
			return -1;
		}
		long response = mCursor.getLong(0);
		mCursor.close();
		return response;
	}

	/**
	 * Retrieve the last entry in the table. Hopefully this will be deprecated
	 * in favor of something a little more robust in the future.
	 *
	 * @return rowId or -1 if failed
	 */
	public String getTaskNameByID(long taskID) {
		Cursor mCursor = mDb.query(true, TASKS_DATABASE_TABLE,
				new String[] { KEY_TASK }, KEY_ROWID + " = '" + taskID + "'",
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		} else {
			return null;
		}
		String response = mCursor.getString(0);
		mCursor.close();
		return response;
	}

	/**
	 * Retrieve the last entry in the table. Hopefully this will be deprecated
	 * in favor of something a little more robust in the future.
	 *
	 * @return rowId or -1 if failed
	 */
	public void renameTask(String origName, String newName) {
		long taskID = getTaskIDByName(origName);
		ContentValues newData = new ContentValues(1);
		newData.put(KEY_TASK, newName);
		try {
			// update(String table, ContentValues values, String whereClause,
			// String[] whereArgs)
			mDb.update(TASKS_DATABASE_TABLE, newData, KEY_ROWID + "=?",
					new String[] { String.valueOf(taskID).toString() });
		} catch (RuntimeException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
	}

	/**
	 * Retrieve the last entry in the table. Hopefully this will be deprecated
	 * in favor of something a little more robust in the future.
	 *
	 * @return rowId or -1 if failed
	 */
	public void deactivateTask(String taskName) {
		long taskID = getTaskIDByName(taskName);
		deactivateTask(taskID);
	}

	/**
	 * Retrieve the last entry in the table. Hopefully this will be deprecated
	 * in favor of something a little more robust in the future.
	 *
	 * @return rowId or -1 if failed
	 */
	public void deactivateTask(long taskID) {
		ContentValues newData = new ContentValues(1);
		newData.put(KEY_ACTIVE, DB_FALSE);
		try {
			mDb.update(TASKS_DATABASE_TABLE, newData, KEY_ROWID + "=?",
					new String[] { String.valueOf(taskID).toString() });
		} catch (RuntimeException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
	}

	/**
	 * Retrieve the last entry in the table. Hopefully this will be deprecated
	 * in favor of something a little more robust in the future.
	 *
	 * @return rowId or -1 if failed
	 */
	public void activateTask(String taskName) {
		long taskID = getTaskIDByName(taskName);
		activateTask(taskID);
	}

	/**
	 * Retrieve the last entry in the table. Hopefully this will be deprecated
	 * in favor of something a little more robust in the future.
	 *
	 * @return rowId or -1 if failed
	 */
	public void activateTask(long taskID) {
		ContentValues newData = new ContentValues(1);
		newData.put(KEY_ACTIVE, DB_TRUE);
		try {
			mDb.update(TASKS_DATABASE_TABLE, newData, KEY_ROWID + "=?",
					new String[] { String.valueOf(taskID).toString() });
		} catch (RuntimeException e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
	}

	/**
	 * Retrieve the last entry in the table. Hopefully this will be deprecated
	 * in favor of something a little more robust in the future.
	 *
	 * @return rowId or -1 if failed
	 */
	private void incrementTaskUsage(long taskID) {
		Cursor mCursor = mDb.query(true, TASKS_DATABASE_TABLE, new String[] {
				KEY_USAGE, KEY_OLDUSAGE, KEY_LASTUSED }, KEY_ROWID + " = "
				+ taskID, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		long usage = mCursor.getLong(0);
		long oldUsage = mCursor.getLong(1);
		long lastUsed = mCursor.getLong(2);
		ContentValues updateValues = new ContentValues();

		long now = System.currentTimeMillis();
		Date today = new Date(now);
		Date dateLastUsed = new Date(lastUsed);

		// Roll-over the old usage when transitioning into a new month.
		if (today.getMonth() != dateLastUsed.getMonth()) {
			oldUsage = usage;
			usage = 0;
			updateValues.put(KEY_OLDUSAGE, oldUsage);
		}

		updateValues.put(KEY_LASTUSED, now);
		updateValues.put(KEY_USAGE, usage + 1);
		mDb.update(TASKS_DATABASE_TABLE, updateValues,
				KEY_ROWID + "=" + taskID, null);

		mCursor.close();
	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 *
	 * @param rowId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public int fetchVersion() throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_METADATA,
				new String[] { MAX_COUNT }, null, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		int response = mCursor.getInt(0);
		mCursor.close();
		return response;
	}
}

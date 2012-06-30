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
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
	public static final String KEY_PERCENTAGE = "percentage";
	public static final String KEY_SPLIT = "split";
	public static final String KEY_HOURS = "hours";
	public static final String KEY_PARENTTASK = "parenttask";
	public static final String DB_FALSE = "0";
	public static final String DB_TRUE = "1";

	private static final String TAG = "TimeSheetDbAdapter";
	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	protected static final String DATABASE_NAME = "TimeSheetDB.db";
	private static final String TASKS_DATABASE_TABLE = "Tasks";
	private static final String TASKSPLIT_DATABASE_TABLE = "TaskSplit";
	private static final String CLOCK_DATABASE_TABLE = "TimeSheet";
	private static final String SUMMARY_DATABASE_TABLE = "Summary";
	private static final String ENTRYITEMS_VIEW = "EntryItems";
	private static final String ENTRYREPORT_VIEW = "EntryReport";
	private static final String TASKSPLITREPORT_VIEW = "TaskSplitReport";
	private static final String DATABASE_METADATA = "TimeSheetMeta";
	private static final int DATABASE_VERSION = 3;

	/**
	 * Database creation SQL statements
	 */
	private static final String CLOCK_TABLE_CREATE = "CREATE TABLE "
			+ CLOCK_DATABASE_TABLE + "(" + KEY_ROWID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CHARGENO
			+ " INTEGER NOT NULL REFERENCES " + TASKS_DATABASE_TABLE + "("
			+ KEY_ROWID + "), " + KEY_TIMEIN + " INTEGER NOT NULL, "
			+ KEY_TIMEOUT + " INTEGER NOT NULL DEFAULT 0" + ");";
	private static final String TASK_TABLE_CREATE = "CREATE TABLE "
			+ TASKS_DATABASE_TABLE + "(" + KEY_ROWID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TASK
			+ " TEXT NOT NULL, " + KEY_ACTIVE + " BOOLEAN NOT NULL DEFAULT '"
			+ DB_TRUE + "', " + KEY_USAGE + " INTEGER NOT NULL DEFAULT 0, "
			+ KEY_OLDUSAGE + " INTEGER NOT NULL DEFAULT 0, " + KEY_LASTUSED
			+ " INTEGER NOT NULL DEFAULT 0, " + KEY_SPLIT
			+ " INTEGER DEFAULT 0);";
	private static final String TASKSPLIT_TABLE_CREATE = "CREATE TABLE "
			+ TASKSPLIT_DATABASE_TABLE + "(" + KEY_ROWID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_CHARGENO
			+ " INTEGER NOT NULL REFERENCES " + TASKS_DATABASE_TABLE + "("
			+ KEY_ROWID + "), " + KEY_TASK + " INTEGER NOT NULL REFERENCES "
			+ TASKS_DATABASE_TABLE + "(" + KEY_ROWID + "), " + KEY_PERCENTAGE
			+ " REAL NOT NULL DEFAULT 100 CHECK(" + KEY_PERCENTAGE + ">=0 AND "
			+ KEY_PERCENTAGE + "<=100)" + ");";
	private static final String TASK_TABLE_ALTER3 = "ALTER TABLE "
			+ TASKS_DATABASE_TABLE + " ADD COLUMN " + KEY_SPLIT
			+ " INTEGER DEFAULT 0;";
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
	private static final String TASKSPLITREPORT_VIEW_CREATE = "CREATE VIEW TaskSplitReport AS "
			+ "SELECT Tasks._id as _id, "
			+ "TaskSplit.chargeno as parenttask, "
			+ "Tasks.task as taskdesc, "
			// + "TaskSplit.task as task, "
			+ "TaskSplit.percentage as percentage "
			+ "FROM Tasks, TaskSplit WHERE Tasks._id = TaskSplit.task";

	private static final String SUMMARY_TABLE_CREATE = "CREATE TEMP TABLE"
			+ " IF NOT EXISTS " + SUMMARY_DATABASE_TABLE + " (" + KEY_ROWID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TASK
			+ " TEXT NOT NULL, " + KEY_TOTAL + " REAL DEFAULT 0);";
	private static final String SUMMARY_TABLE_CLEAN = "DELETE FROM "
			+ SUMMARY_DATABASE_TABLE + ";";

	private static final String TASKS_INDEX = "CREATE UNIQUE INDEX "
			+ TASKS_DATABASE_TABLE + "_index ON " + TASKS_DATABASE_TABLE + " ("
			+ KEY_TASK + ");";
	private static final String CHARGENO_INDEX = "CREATE INDEX "
			+ CLOCK_DATABASE_TABLE + "_chargeno_index ON "
			+ CLOCK_DATABASE_TABLE + " (" + KEY_CHARGENO + ");";
	private static final String SPLIT_INDEX = "CREATE INDEX "
			+ TASKSPLIT_DATABASE_TABLE + "_chargeno_index ON "
			+ TASKSPLIT_DATABASE_TABLE + " (" + KEY_CHARGENO + ");";
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
			db.execSQL(TASKSPLIT_TABLE_CREATE);
			db.execSQL(ENTRYITEMS_VIEW_CREATE);
			db.execSQL(ENTRYREPORT_VIEW_CREATE);
			db.execSQL(TASKSPLITREPORT_VIEW_CREATE);
			db.execSQL(TASKS_INDEX);
			db.execSQL(CHARGENO_INDEX);
			db.execSQL(TIMEIN_INDEX);
			db.execSQL(TIMEOUT_INDEX);
			db.execSQL(SPLIT_INDEX);

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
				Log.d(TAG, "Old DB version <= 1");
				Log.d(TAG, "Running: " + CHARGENO_INDEX);
				db.execSQL(CHARGENO_INDEX);
				Log.d(TAG, "Running: " + TIMEIN_INDEX);
				db.execSQL(TIMEIN_INDEX);
				Log.d(TAG, "Running: " + TIMEOUT_INDEX);
				db.execSQL(TIMEOUT_INDEX);
			case 2:
				Log.d(TAG, "Old DB version <= 2");
				Log.d(TAG, "Running: " + TASK_TABLE_ALTER3);
				db.execSQL(TASK_TABLE_ALTER3);
				Log.d(TAG, "Running: " + TASKSPLIT_TABLE_CREATE);
				db.execSQL(TASKSPLIT_TABLE_CREATE);
				Log.d(TAG, "Running: " + TASKSPLITREPORT_VIEW_CREATE);
				db.execSQL(TASKSPLITREPORT_VIEW_CREATE);
				Log.d(TAG, "Running: " + SPLIT_INDEX);
				db.execSQL(SPLIT_INDEX);
			default:
				if (newVersion != oldVersion)
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
	 * Close the database.
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
		return mDb.update(
				CLOCK_DATABASE_TABLE,
				updateValues,
				KEY_ROWID + "= ? and " + KEY_CHARGENO + " = ?",
				new String[] { Long.toString(lastClockEntry()),
						Long.toString(chargeno) });
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
	 * Retrieve list of entries for the day surrounding the supplied time.
	 * 
	 * @return rowId or -1 if failed
	 */
	public Cursor getSummaryCursor(boolean distinct, String[] columns,
			long start, long end) {
		return getSummaryCursor(distinct, columns, null, null, start, end);
	}

	/**
	 * Retrieve list of entries for the day surrounding the supplied time.
	 * 
	 * @return rowId or -1 if failed
	 */
	protected Cursor getSummaryCursor(boolean distinct, String[] columns,
			String groupBy, String orderBy, long start, long end) {
		// public Cursor query(boolean distinct, String table, String[] columns,
		// String selection, String[] selectionArgs, String groupBy, String
		// having, String orderBy, String limit) {

		// String selection;

		// Log.d(TAG, "getSummaryReportCursor: Selection criteria: " +
		// selection);
		try {
			Log.d(TAG, "getSummaryReportCursor: Columns: " + columns[0] + ", "
					+ columns[1] + " and " + columns[2]);
		} catch (Exception e) {
			Log.d(TAG, "getSummaryReportCursor has fewer than 3 columns.");
		}
		Log.d(TAG, "getSummaryReportCursor: Selection arguments: " + start
				+ ", " + end);
		// Cursor mCursor = mDb.query(distinct, SUMMARY_DATABASE_TABLE, columns,
		// selection, new String[] { String.valueOf(start).toString(),
		// String.valueOf(end).toString() }, groupBy, null,
		// orderBy, null);

		// TODO: Below KEY_STOTAL was KEY_TOTAL
		// Cursor mCursor = mDb.query(distinct, SUMMARY_DATABASE_TABLE,
		// columns,
		// KEY_STOTAL + " > 0",
		// new String[] { String.valueOf(0).toString(),
		// String.valueOf(end).toString() }, groupBy, null,
		// orderBy, null);

		String select = new String("SELECT ");
		if (distinct)
			select = select.concat("DISTINCT ");
		for (int c = 0; c < columns.length; c++) {
			if (c == 0) {
				select = select.concat(columns[c]);
			} else {
				select = select.concat(", " + columns[c]);
			}
		}
		select = select.concat(" FROM " + SUMMARY_DATABASE_TABLE);
		select = select.concat(" WHERE " + KEY_TOTAL + " > 0");
		select = select.concat(" GROUP BY " + groupBy);
		select = select.concat(" ORDER BY " + orderBy);
		Log.d(TAG, "getSummaryCursor: query: " + select);
		Cursor mCursor = mDb.rawQuery(select, null);

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

		mCursor.moveToFirst();
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
		if (time <= 0)
			time = TimeHelpers.millisNow();

		long todayStart = TimeHelpers.millisToStartOfDay(time);
		long todayEnd = TimeHelpers.millisToEndOfDay(time);

		Log.d(TAG,
				"dayEntryReport start: " + TimeHelpers.millisToDate(todayStart));
		Log.d(TAG, "dayEntryReport end: " + TimeHelpers.millisToDate(todayEnd));

		String[] columns = new String[] { KEY_ROWID, KEY_TASK, KEY_RANGE,
				KEY_TIMEIN, KEY_TIMEOUT };
		return getEntryReportCursor(false, columns, null, KEY_TIMEIN,
				todayStart, todayEnd);
	}

	/**
	 * Method that retrieves the entries for a single specified day from the
	 * entry view.
	 * 
	 * @param time
	 *            Time, in milliseconds, within the day to be summarized.
	 * 
	 * @return Cursor over the results.
	 */
	public Cursor daySummaryOld(long time) {
		if (time <= 0)
			time = TimeHelpers.millisNow();

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
	 * Method that retrieves the entries for today from the entry view.
	 * 
	 * @return Cursor over the results.
	 */
	public Cursor daySummary() {
		return daySummary(TimeHelpers.millisNow(), true);
	}

	/**
	 * Method that retrieves the entries for today from the entry view.
	 * 
	 * @param omitOpen
	 *            Omit an open task in the result
	 * @return Cursor over the results.
	 */
	public Cursor daySummary(boolean omitOpen) {
		return daySummary(TimeHelpers.millisNow(), omitOpen);
	}

	/**
	 * Method that retrieves the entries for today from the entry view.
	 * 
	 * @return Cursor over the results.
	 */
	public Cursor daySummary(long time) {
		return daySummary(time, true);
	}

	/**
	 * Method that populates a temporary table for a single specified day from
	 * the entry view.
	 * 
	 * @param time
	 *            The time around which to report
	 * @param omitOpen
	 *            Include an open task in the result
	 * 
	 * @return Cursor over the results.
	 */
	// TODO: Finish and replace the other routines with it.
	public Cursor daySummary(long time, boolean omitOpen) {
		if (time <= 0)
			time = TimeHelpers.millisNow();

		long todayStart = TimeHelpers.millisToStartOfDay(time);
		long todayEnd = TimeHelpers.millisToEndOfDay(time);

		Log.d(TAG, "daySummary start: " + TimeHelpers.millisToDate(todayStart));
		Log.d(TAG, "daySummary end: " + TimeHelpers.millisToDate(todayEnd));

		populateSummary(todayStart, todayEnd, omitOpen);

		// String[] columns = { KEY_TASK, KEY_HOURS };
		// String groupBy = KEY_TASK;
		// String orderBy = KEY_TASK;
		String[] columns = { KEY_ROWID, KEY_TASK, KEY_TOTAL };
		String groupBy = KEY_TASK;
		String orderBy = KEY_TOTAL + " DESC";
		try {
			return getSummaryCursor(true, columns, groupBy, orderBy,
					todayStart, todayEnd);
		} catch (SQLiteException e) {
			Log.e(TAG, "getSummaryCursor: " + e.toString());
			return null;
		}
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
	 * Retrieve list of entries for the week surrounding the supplied time.
	 * 
	 * @return Cursor over the entries
	 */
	public Cursor weekEntryReport(long time) {
		if (time <= 0)
			time = TimeHelpers.millisNow();

		long todayStart = TimeHelpers.millisToStartOfWeek(time);
		long todayEnd = TimeHelpers.millisToEndOfWeek(time);

		// public Cursor query(boolean distinct, String table, String[] columns,
		// String selection, String[] selectionArgs, String groupBy, String
		// having, String orderBy) {

		Log.d(TAG,
				"weekEntryReport start: "
						+ TimeHelpers.millisToDate(todayStart));
		Log.d(TAG, "weekEntryReport end: " + TimeHelpers.millisToDate(todayEnd));

		String[] columns = new String[] { KEY_ROWID, KEY_TASK, KEY_RANGE,
				KEY_TIMEIN, KEY_TIMEOUT };
		return getEntryReportCursor(false, columns, todayStart, todayEnd);
	}

	/**
	 * Method that retrieves the entries for a single specified day from the
	 * entry view.
	 * 
	 * @return Cursor over the results.
	 */
	public Cursor weekSummaryOld(long time) {
		if (time <= 0)
			time = TimeHelpers.millisNow();

		long todayStart = TimeHelpers.millisToStartOfWeek(time);
		long todayEnd = TimeHelpers.millisToEndOfWeek(time);

		Log.d(TAG, "weekSummary start: " + TimeHelpers.millisToDate(todayStart));
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
	 * Method that populates a temporary table for a single specified day from
	 * the entry view.
	 * 
	 * @return Cursor over the results.
	 */
	// TODO: Finish and replace the other routines with it.
	public Cursor weekSummary(long time, boolean omitOpen) {
		if (time <= 0)
			time = TimeHelpers.millisNow();

		long weekStart = TimeHelpers.millisToStartOfWeek(time);
		long weekEnd = TimeHelpers.millisToEndOfWeek(time);

		Log.d(TAG, "weekSummary start: " + TimeHelpers.millisToDate(weekStart));
		Log.d(TAG, "weekSummary end: " + TimeHelpers.millisToDate(weekEnd));

		populateSummary(weekStart, weekEnd, omitOpen);

		// String[] columns = { KEY_TASK, KEY_HOURS };
		// String groupBy = KEY_TASK;
		// String orderBy = KEY_TASK;
		String[] columns = { KEY_ROWID, KEY_TASK, KEY_TOTAL };
		String groupBy = KEY_TASK;
		String orderBy = KEY_TOTAL + " DESC";
		try {
			return getSummaryCursor(true, columns, groupBy, orderBy, weekStart,
					weekEnd);
		} catch (SQLiteException e) {
			Log.e(TAG, "getSummaryCursor: " + e.getLocalizedMessage());
			return null;
		}
	}

	/**
	 * @param summaryStart
	 *            The start time for the summary
	 * @param summaryEnd
	 *            The end time for the summary
	 */
	private void populateSummary(long summaryStart, long summaryEnd) {
		populateSummary(summaryStart, summaryEnd, true);
	}

	/**
	 * @param summaryStart
	 *            The start time for the summary
	 * @param summaryEnd
	 *            The end time for the summary
	 * @param omitOpen
	 *            Whether the summary should omit an open task
	 */
	private void populateSummary(long summaryStart, long summaryEnd,
			boolean omitOpen) {
		try {
			Log.d(TAG, "populateSummary: Creating summary table.");
			mDb.execSQL(SUMMARY_TABLE_CREATE);
		} catch (SQLException e) {
			// This shouldn't occur, but hope for the best.
			Log.d(TAG, "populateSummary: SUMMARY_TABLE_CREATE: " + e.toString());
		}
		Log.d(TAG, "populateSummary: Cleaning summary table.");
		mDb.execSQL(SUMMARY_TABLE_CLEAN);

		String omitOpenQuery = new String("");
		if (omitOpen) {
			omitOpenQuery = new String(CLOCK_DATABASE_TABLE + "." + KEY_TIMEOUT
					+ " > 0 " + " AND ");
		}
		// TODO: Figure out autoalign
		final String populateTemp1 = "INSERT INTO " + SUMMARY_DATABASE_TABLE
				+ " (" + KEY_TASK + "," + KEY_TOTAL + ") SELECT "
				+ TASKS_DATABASE_TABLE + "." + KEY_TASK + ", "
				+ "SUM((CASE WHEN " + CLOCK_DATABASE_TABLE + "." + KEY_TIMEOUT
				+ " = 0 THEN " + TimeHelpers.millisNow() + " ELSE "
				+ CLOCK_DATABASE_TABLE + "." + KEY_TIMEOUT + " END - "
				+ CLOCK_DATABASE_TABLE + "." + KEY_TIMEIN + ")/3600000.0) AS "
				+ KEY_TOTAL + " FROM " + CLOCK_DATABASE_TABLE + ","
				+ TASKS_DATABASE_TABLE + " WHERE " + CLOCK_DATABASE_TABLE + "."
				+ KEY_TIMEOUT + " <= " + summaryEnd + " AND " + omitOpenQuery
				+ CLOCK_DATABASE_TABLE + "." + KEY_TIMEIN + " >= "
				+ summaryStart + " AND " + CLOCK_DATABASE_TABLE + "."
				+ KEY_CHARGENO + "=" + TASKS_DATABASE_TABLE + "." + KEY_ROWID
				+ " AND " + TASKS_DATABASE_TABLE + "." + KEY_SPLIT
				+ "=0 GROUP BY " + KEY_TASK;
		Log.d(TAG, "populateTemp1\n" + populateTemp1);
		mDb.execSQL(populateTemp1);

		final String populateTemp2 = "INSERT INTO " + SUMMARY_DATABASE_TABLE
				+ " (" + KEY_TASK + "," + KEY_TOTAL + ") SELECT "
				+ TASKSPLITREPORT_VIEW + ".taskdesc, " + "(sum((CASE WHEN "
				+ CLOCK_DATABASE_TABLE + "." + KEY_TIMEOUT + " = 0 THEN "
				+ TimeHelpers.millisNow() + " ELSE " + CLOCK_DATABASE_TABLE
				+ "." + KEY_TIMEOUT + " END - " + CLOCK_DATABASE_TABLE + "."
				+ KEY_TIMEIN + ")/3600000.0) * (" + TASKSPLIT_DATABASE_TABLE
				+ "." + KEY_PERCENTAGE + "/100.0)) AS " + KEY_TOTAL + " FROM "
				+ CLOCK_DATABASE_TABLE + ", " + TASKSPLIT_DATABASE_TABLE + ", "
				+ TASKS_DATABASE_TABLE + ", " + TASKSPLITREPORT_VIEW
				+ " WHERE " + CLOCK_DATABASE_TABLE + "." + KEY_TIMEOUT + " <= "
				+ summaryEnd + " AND " + omitOpenQuery + CLOCK_DATABASE_TABLE
				+ "." + KEY_TIMEIN + " >= " + summaryStart + " AND "
				+ CLOCK_DATABASE_TABLE + "." + KEY_CHARGENO + "="
				+ TASKS_DATABASE_TABLE + "." + KEY_ROWID + " AND "
				+ TASKS_DATABASE_TABLE + "." + KEY_ROWID + "="
				+ TASKSPLIT_DATABASE_TABLE + "." + KEY_CHARGENO + " AND "
				+ TASKS_DATABASE_TABLE + "." + KEY_ROWID + "="
				+ TASKSPLITREPORT_VIEW + "." + KEY_PARENTTASK + " AND "
				+ TASKSPLIT_DATABASE_TABLE + "." + KEY_PERCENTAGE + "="
				+ TASKSPLITREPORT_VIEW + "." + KEY_PERCENTAGE + " GROUP BY "
				+ TASKSPLIT_DATABASE_TABLE + "." + KEY_TASK;
		Log.d(TAG, "populateTemp2\n" + populateTemp2);
		mDb.execSQL(populateTemp2);
	}

	/**
	 * Create a new time entry using the charge number provided. If the entry is
	 * successfully created return the new rowId for that number, otherwise
	 * return a -1 to indicate failure.
	 * 
	 * @param task
	 *            the charge number text for the entry
	 * 
	 * @return rowId or -1 if failed
	 */
	public long createTask(String task) {
		Log.d(TAG, "createTask: " + task);
		long tempDate = System.currentTimeMillis(); // Local time...
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TASK, task);
		initialValues.put(KEY_LASTUSED, tempDate);

		return mDb.insert(TASKS_DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Create a new split time entry using the information provided. If the
	 * entry is successfully created return the new rowId for that number,
	 * otherwise return a -1 to indicate failure.
	 * 
	 * @param task
	 *            the charge number text for the entry
	 * @param parent
	 *            The parent for the split task
	 * @param percentage
	 *            The amount this task contributes to the task.
	 * 
	 * @return rowId or -1 if failed
	 */
	public long createTask(String task, String parent, int percentage) {
		Log.d(TAG, "createTask: " + task);
		Log.d(TAG, "    parent: " + parent);
		Log.d(TAG, "percentage: " + percentage);
		long tempDate = System.currentTimeMillis(); // Local time...
		long parentId = getTaskIDByName(parent);

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TASK, task);
		initialValues.put(KEY_LASTUSED, tempDate);
		initialValues.put(KEY_SPLIT, 1);
		long newRow = mDb.insert(TASKS_DATABASE_TABLE, null, initialValues);

		initialValues = new ContentValues();
		initialValues.put(KEY_SPLIT, 2);
		try {
			mDb.update(TASKS_DATABASE_TABLE, initialValues, KEY_ROWID + "=?",
					new String[] { String.valueOf(parentId) });
		} catch (SQLiteConstraintException e) {
			Log.d(TAG, "createTask: " + e.toString());
		}

		Log.d(TAG, "new row   : " + newRow);
		initialValues = new ContentValues();
		initialValues.put(KEY_TASK, newRow);
		initialValues.put(KEY_CHARGENO, parentId);
		initialValues.put(KEY_PERCENTAGE, percentage);

		return mDb.insert(TASKSPLIT_DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Return a Cursor over the list of all tasks in the database eligible to be
	 * split task parents.
	 * 
	 * @return Cursor over all database entries
	 */
	public Cursor fetchParentTasks() {
		Log.d(TAG, "fetchParentTasks: Issuing DB query.");
		return mDb.query(TASKS_DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_TASK, KEY_ACTIVE, KEY_SPLIT }, KEY_ACTIVE + "='" + DB_TRUE
				+ "' and " + KEY_SPLIT + "!=1", null, null, null, KEY_TASK);
	}

	/**
	 * Return a Cursor over the list of all entries in the database
	 * 
	 * @return Cursor over all database entries
	 */
	public Cursor fetchAllTaskEntries() {
		Log.d(TAG, "fetchAllTaskEntries: Issuing DB query.");
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
		Log.d(TAG, "fetchAllDisabledTasks: Issuing DB query.");
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
		Log.d(TAG, "fetchTask: Issuing DB query.");
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
		Log.d(TAG, "getTaskIDByName: Issuing DB query.");
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
		Log.d(TAG, "getTaskIDByName: " + response);
		return response;
	}

	/**
	 * Retrieve the last entry in the table. Hopefully this will be deprecated
	 * in favor of something a little more robust in the future.
	 * 
	 * @return rowId or -1 if failed
	 */
	public String getTaskNameByID(long taskID) {
		Log.d(TAG, "getTaskNameByID: Issuing DB query for ID: " + taskID);
		Cursor mCursor = mDb.query(true, TASKS_DATABASE_TABLE,
				new String[] { KEY_TASK }, KEY_ROWID + " = '" + taskID + "'",
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		} else {
			return null;
		}

		String response = new String("");
		if (mCursor.getCount() < 1) {
			Log.d(TAG, "getCount result was < 1");
		} else {
			try {
				response = mCursor.getString(0);
				mCursor.close();
			} catch (SQLException e) {
				Log.i(TAG, "getTaskNameByID: " + e.toString());
			} catch (CursorIndexOutOfBoundsException e) {
				Log.i(TAG, "getTaskNameByID: " + e.toString());
			}
			Log.d(TAG, "getTaskNameByID: " + response);
		}
		return response;
	}

	/**
	 * Return the entry that matches the given rowId
	 * 
	 * @param rowId
	 *            id of task to retrieve
	 * @return parent's task ID, if found, 0 if not
	 */
	public long getSplitTaskParent(String splitTask) {
		Log.d(TAG, "getSplitTaskParent: " + splitTask);
		return getSplitTaskParent(getTaskIDByName(splitTask));
	}

	/**
	 * Return the entry that matches the given rowId
	 * 
	 * @param rowId
	 *            id of task to retrieve
	 * @return parent's task ID, if found, 0 if not
	 */
	public long getSplitTaskParent(long rowId) {
		Log.d(TAG, "getSplitTaskParent: Issuing DB query.");
		long ret;
		Cursor mCursor = null;
		String query = new String("SELECT " + KEY_CHARGENO + " FROM "
				+ TASKSPLIT_DATABASE_TABLE + " WHERE " + KEY_TASK + " = ?");
		Log.d(TAG, "getSplitTaskParent: query: " + query + ", " + rowId);
		try {
			// mCursor = mDb.query(true, TASKSPLIT_DATABASE_TABLE,
			// new String[] { KEY_CHARGENO }, KEY_TASK + "=" + rowId,
			// null, null, null, null, null);
			mCursor = mDb.rawQuery(query,
					new String[] { String.valueOf(rowId) });
		} catch (SQLException e) {
			Log.i(TAG, "getSplitTaskParent: " + e.toString());
			ret = 0;
		}
		try {
			if (mCursor != null) {
				mCursor.moveToFirst();
			}
			ret = mCursor.getLong(0);
			mCursor.close();
		} catch (CursorIndexOutOfBoundsException e) {
			Log.i(TAG, "getSplitTaskParent: " + e.toString());
			ret = 0;
		}
		Log.d(TAG, "getSplitTaskParent: " + ret + " / " + getTaskNameByID(ret));
		return ret;
	}

	/**
	 * Return the entry that matches the given rowId
	 * 
	 * @param rowId
	 *            id of task to retrieve
	 * @return parent's task ID, if found, 0 if not
	 */
	public int getSplitTaskPercentage(String splitTask) {
		Log.d(TAG, "getSplitTaskPercentage: " + splitTask);
		return getSplitTaskPercentage(getTaskIDByName(splitTask));
	}

	/**
	 * Return the entry that matches the given rowId
	 * 
	 * @param rowId
	 *            id of task to retrieve
	 * @return parent's task ID, if found, 0 if not
	 */
	public int getSplitTaskPercentage(long rowId) {
		Log.d(TAG, "getSplitTaskPercentage: Issuing DB query.");
		int ret;
		try {
			Cursor mCursor = mDb.query(true, TASKSPLIT_DATABASE_TABLE,
					new String[] { KEY_PERCENTAGE }, KEY_TASK + "=" + rowId,
					null, null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			}
			ret = mCursor.getInt(0);
			mCursor.close();
		} catch (SQLException e) {
			Log.i(TAG, "getSplitTaskPercentage: " + e.toString());
			ret = 0;
		}
		Log.d(TAG, "getSplitTaskPercentage: " + ret);
		return ret;
	}

	/**
	 * Return the flag whether the task that matches the given rowId is a split
	 * task.
	 * 
	 * @param splitTask
	 *            name of task to retrieve
	 * @return parent's task ID, if found, 0 if not
	 */
	public int getSplitTaskFlag(String splitTask) {
		Log.d(TAG, "getSplitTaskFlag: " + splitTask);
		return getSplitTaskFlag(getTaskIDByName(splitTask));
	}

	/**
	 * Return the flag whether the task that matches the given rowId is a split
	 * task.
	 * 
	 * @param rowId
	 *            id of task to retrieve
	 * @return parent's task ID, if found, 0 if not
	 */
	public int getSplitTaskFlag(long rowId) {
		Log.d(TAG, "getSplitTaskFlag: Issuing DB query.");
		int ret;
		try {
			Cursor mCursor = mDb.query(true, TASKS_DATABASE_TABLE,
					new String[] { KEY_SPLIT }, KEY_ROWID + "=" + rowId, null,
					null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			}
			ret = mCursor.getInt(0);
			Log.i(TAG, "getSplitTaskFlag: " + mCursor.getInt(0));
			mCursor.close();
		} catch (SQLException e) {
			Log.i(TAG, "getSplitTaskPercentage: " + e.toString());
			ret = 0;
		}
		Log.d(TAG, "getSplitTaskFlag: " + ret);
		return ret;
	}

	/**
	 * Return the number of children whose parent matches the given rowId
	 * 
	 * @param rowId
	 *            id of task to retrieve
	 * @return Number of "children" of this task, 0 if none.
	 */
	public long getQuantityOfSplits(long rowId) {
		Log.d(TAG, "getQuantityOfSplits: Issuing DB query.");
		long ret;
		try {
			Cursor mCursor = mDb.query(true, TASKSPLIT_DATABASE_TABLE,
					new String[] { "count(" + KEY_TASK + ")" }, KEY_CHARGENO
							+ "=" + rowId, null, null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			}
			ret = mCursor.getLong(0);
			mCursor.close();
		} catch (SQLException e) {
			Log.i(TAG, "getQuantityOfSplits: " + e.toString());
			ret = 0;
		}
		Log.d(TAG, "getQuantityOfSplits: " + ret);
		return ret;
	}

	/**
	 * Rename specified task
	 * 
	 * @param origName
	 *            Old task name
	 * @param newName
	 *            New task name
	 */
	public void renameTask(String origName, String newName) {
		Log.d(TAG, "renameTask: Issuing DB query.");
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
	 * Alter specified split task
	 * 
	 * @param rowID
	 *            Task ID to change
	 * @param parentID
	 *            New parent ID
	 * @param percentage
	 *            New percentage
	 * @param split
	 *            New split flag state
	 */
	public void alterSplitTask(long rowID, long parentID, int percentage,
			int split) {
		Log.d(TAG, "alterSplitTask: Issuing DB query.");
		long currentParent = getSplitTaskParent(rowID);
		int currentSplit = getSplitTaskFlag(rowID);

		if (split == 0 && currentSplit == 1)
			parentID = -1;

		// If the number of sub-splits under the parent task is 1 (<2) and we
		// are changing the parent, set the split flag to 0.
		if (getQuantityOfSplits(currentParent) < 2 && currentParent != parentID) {
			ContentValues initialValues = new ContentValues(1);
			initialValues.put(KEY_SPLIT, 0);
			int i = mDb.update(TASKS_DATABASE_TABLE, initialValues, KEY_ROWID
					+ "=?", new String[] { String.valueOf(currentParent) });
			Log.d(TAG, "Reverting task " + currentParent
					+ " to standard task returned " + i);
		}

		// Set the flag on the new parent
		if (currentParent != parentID && parentID > 0) {
			ContentValues initialValues = new ContentValues(1);
			initialValues.put(KEY_SPLIT, 2);
			int i = mDb.update(TASKS_DATABASE_TABLE, initialValues, KEY_ROWID
					+ "=?", new String[] { String.valueOf(parentID) });
			Log.d(TAG, "Converting task " + parentID
					+ " to parent task returned: " + i);
		}

		// If the new split state is 1, a child, set the appropriate values
		if (split == 1) {
			Log.d(TAG, "alterSplitTask: Setting up child");
			ContentValues newData = new ContentValues(3);
			newData.put(KEY_CHARGENO, parentID);
			newData.put(KEY_PERCENTAGE, percentage);
			try {
				// update(String table, ContentValues values, String
				// whereClause,
				// String[] whereArgs)
				int i = mDb.update(TASKSPLIT_DATABASE_TABLE, newData, KEY_TASK
						+ "=?",
						new String[] { String.valueOf(rowID).toString() });
				Log.d(TAG, "Setting child task " + rowID
						+ " details returned: " + i);
				if (i == 0) {
					newData.put(KEY_TASK, rowID);
					long j = mDb
							.insert(TASKSPLIT_DATABASE_TABLE, null, newData);
					Log.d(TAG, "Inserting child task " + rowID
							+ " details returned: " + j);
				}
			} catch (RuntimeException e) {
				Log.e(TAG, e.getLocalizedMessage());
			}

			newData = new ContentValues(1);
			newData.put(KEY_SPLIT, 1);
			try {
				int i = mDb.update(TASKS_DATABASE_TABLE, newData, KEY_ROWID
						+ "=?", new String[] { String.valueOf(rowID) });
				Log.d(TAG, "Converting task " + rowID
						+ " to child task returned: " + i);
			} catch (RuntimeException e) {
				Log.e(TAG, e.getLocalizedMessage());
			}
		}

		// Delete the record in tasksplit if the new split state is 0
		if (currentSplit == 1 && split == 0) {
			Log.d(TAG, "alterSplitTask: Tearing down child");
			try {
				// update(String table, ContentValues values, String
				// whereClause,
				// String[] whereArgs)
				int i = mDb.delete(TASKSPLIT_DATABASE_TABLE, KEY_TASK + "=?",
						new String[] { String.valueOf(rowID).toString() });
				Log.d(TAG, "Setting child task " + rowID
						+ " details returned: " + i);
			} catch (RuntimeException e) {
				Log.e(TAG, e.getLocalizedMessage());
			}

			ContentValues newData = new ContentValues(1);
			newData.put(KEY_SPLIT, 0);
			try {
				int i = mDb.update(TASKS_DATABASE_TABLE, newData, KEY_ROWID
						+ "=?", new String[] { String.valueOf(rowID) });
				Log.d(TAG, "Converting child task " + rowID
						+ " to standard task returned: " + i);
			} catch (RuntimeException e) {
				Log.e(TAG, e.getLocalizedMessage());
			}
		}
	}

	/**
	 * Retrieve the last entry in the table. Hopefully this will be deprecated
	 * in favor of something a little more robust in the future.
	 * 
	 * @return rowId or -1 if failed
	 */
	public void deactivateTask(String taskName) {
		Log.d(TAG, "deactivateTask: Issuing DB query.");
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
		Log.d(TAG, "deactivateTask: Issuing DB query.");
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
		Log.d(TAG, "activateTask: Issuing DB query.");
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
		Log.d(TAG, "activateTask: Issuing DB query.");
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
		Log.d(TAG, "incrementTaskUsage: Issuing DB query.");
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
		Log.d(TAG, "fetchVersion: Issuing DB query.");

		Cursor mCursor = mDb.query(true, DATABASE_METADATA,
				new String[] { MAX_COUNT }, null, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		int response = mCursor.getInt(0);
		mCursor.close();
		return response;
	}

	/**
	 * Generic SQL exec wrapper, for use with statements which do not return
	 * values.
	 * 
	 * @param sqlTorun
	 */
	public void runSQL(String sqlTorun) {
		mDb.execSQL(sqlTorun);
	}
}

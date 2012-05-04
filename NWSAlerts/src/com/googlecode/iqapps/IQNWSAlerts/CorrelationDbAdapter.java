/*
 * Copyright 2010 NWSAlerts authors.
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
package com.googlecode.iqapps.IQNWSAlerts;

import java.io.File;
import java.util.HashSet;
import java.util.Vector;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.os.Environment;

import com.googlecode.iqapps.BoundingBox;
import com.googlecode.iqapps.Logger;
import com.googlecode.iqapps.Point2D;

/**
 * Simple correlation database helper class. Defines the basic CRUD operations
 * for the NWSAlerts application, and gives the ability to list all entries as
 * well as retrieve or modify a specific entry.
 * 
 * Graciously stolen from the Android Notepad example.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class CorrelationDbAdapter {
	private final static Logger logger = Logger
			.getLogger("CorrelationDbAdapter");
	public static final String externalSubdirectory = "NWSAlerts";
	public static final String KEY_VERSION = "version";
	public static final String KEY_ROWID = "_id";
	public static final String DB_FALSE = "0";
	public static final String DB_TRUE = "1";
	public static final String MAX_ROW = "max(" + KEY_ROWID + ")";
	public static final String MAX_COUNT = "max(" + KEY_VERSION + ")";

	private ExternalDBHelper mDbHelper;
	private SQLiteDatabase mDb;
	private CursorFactory mCursorFactory;

	/**
	 * Database SQL constants.
	 */
	protected static final String DATABASE_NAME = "county_corr.db";
	private static final String DATABASE_METADATA = "NWSAlertMeta";

	public static final String COR_TABLE = "correlate";
	public static final String KEY_STATE = "state";
	public static final String KEY_COUNTY = "county";
	public static final String KEY_LAT = "latitude";
	public static final String KEY_LON = "longitude";
	public static final String KEY_FIPS = "fips";

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 */
	public CorrelationDbAdapter() {
		logger.debug("in CorrelationDbAdapter");
	}

	/**
	 * Open the correlation database. If it cannot be opened, try to cope as
	 * best as we can. If it cannot be opened or downloaded, throw an exception
	 * to signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public CorrelationDbAdapter open() throws SQLException {
		logger.debug("In open.");

		// final String dataPath = new String("/sdcard/" + externalSubdirectory
		// + "/");
		final String dataPath = new String(Environment
				.getExternalStorageDirectory().getPath()
				+ externalSubdirectory
				+ "/");
		File file = new File(dataPath + DATABASE_NAME);

		if (!file.exists() || RetrieveCorrelationDB.NewerAvailable()) {
			logger.debug("Retrieving Correlation database.");
			RetrieveCorrelationDB.GetDBFileFromWeb();
		}

		if (!file.exists()) {
			logger.error("open: Database file still doesn't exist, returning null.");
			return null;
		}

		mDbHelper = new ExternalDBHelper(DATABASE_NAME, mCursorFactory);
		try {
			mDb = mDbHelper.getReadableDatabase();
		} catch (NullPointerException e) {
			mDb = mDbHelper.getReadableDatabase();
		} catch (SQLiteException e) {
			logger.error("open: " + e.toString());
			return null;
		}
		return this;
	}

	/**
	 * Close the database.
	 */
	public void close() {
		try {
			mDbHelper.close();
		} catch (NullPointerException e) {
			logger.warn("close: " + e.toString());
		}
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

	/**
	 * Method to search the database to see if a county can be found for the
	 * bounding box.
	 * 
	 * @param location
	 *            Location representing the center of the box to be searched.
	 * @param radius
	 *            Radius around the location to search.
	 * @return Boolean whether the bounding box can be resolved to a county.
	 */
	public boolean inCounty(Point2D.Double location, Double radius) {
		return inCounty(new BoundingBox(location, radius));
	}

	/**
	 * Method to search the database to see if a county can be found for the
	 * bounding box.
	 * 
	 * @param bbox
	 *            BoundingBox representing the box to be searched.
	 * @return Boolean whether the bounding box can be resolved to a county.
	 */
	public boolean inCounty(BoundingBox bbox) {
		logger.trace("In inCounty()");
		try {
			Cursor mCursor = mDb.query(false, COR_TABLE,
					new String[] { KEY_COUNTY }, KEY_LAT + " > " + bbox.x1()
							+ " AND " + KEY_LAT + " < " + bbox.x2() + " AND "
							+ KEY_LON + " > " + bbox.y1() + " AND " + KEY_LON
							+ " < " + bbox.y2(), null, null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			} else
				return false;
			int responses = mCursor.getCount();
			mCursor.close();
			if (responses < 1)
				return false;
		} catch (SQLException e) {
			logger.warn(e.toString());
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param location
	 * @param radius
	 * @return
	 */
	public Vector<String> getFIPSCodes(Point2D.Double location, double radius) {
		return getFIPSCodes(new BoundingBox(location, radius));
	}

	/**
	 * 
	 * @param bbox
	 * @return
	 */
	public Vector<String> getFIPSCodes(BoundingBox bbox) {
		logger.trace("In getFIPSCodes()");
		Cursor mCursor = null;
		try {
			mCursor = mDb.query(false, COR_TABLE, new String[] { KEY_FIPS },
					KEY_LAT + " > " + bbox.x1() + " AND " + KEY_LAT + " < "
							+ bbox.x2() + " AND " + KEY_LON + " > " + bbox.y1()
							+ " AND " + KEY_LON + " < " + bbox.y2(), null,
					null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			} else
				return null;
		} catch (NullPointerException e) {
			logger.warn(e.toString());
			return null;
		} catch (SQLException e) {
			logger.warn(e.toString());
			return null;
		}

		HashSet<String> fipsSet = new HashSet<String>();
		// Iterate over res
		try {
			// Can't do this because it apparently "goes backwards," even though
			// it's at the first record.
			// res.first();
			while (!mCursor.isAfterLast()) {
				fipsSet.add(mCursor.getString(0));
				mCursor.moveToNext();
			}
			mCursor.close();
		} catch (SQLException e) {
			logger.warn(e.toString());
			return null;
		}

		Vector<String> fips = new Vector<String>();
		for (String string : fipsSet) {
			fips.add(string);
		}
		return fips;
	}

	/**
	 * 
	 * @param bbox
	 * @return
	 */
	public String getStateFromFIPS(String fipsLoc) {
		logger.trace("In getStateFromFIPS(" + fipsLoc + ")");
		Cursor mCursor = null;
		try {
			mCursor = mDb.query(false, COR_TABLE, new String[] { KEY_STATE },
					KEY_FIPS + " = '" + fipsLoc + "'", null, null, null, null,
					null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			} else {
				logger.debug("Cursor is null, returning.");
				return null;
			}
		} catch (SQLException e) {
			logger.warn(e.toString());
			return null;
		}

		String state = null;
		try {
			state = new String(mCursor.getString(0));
			mCursor.close();
		} catch (SQLException e) {
			logger.warn(e.toString());
			return null;
		}

		return state;
	}

	/**
	 * Method to insert the provided string.
	 * 
	 * @param hashString
	 *            String representing the hash to be retrieved.
	 * @param csvLine
	 * @return Boolean whether the hash was found or not.
	 */
	public void insert(String hashString, String csvLine) {
		logger.trace("In insert(" + hashString + ")");

		try {
			String sql = "INSERT INTO " + COR_TABLE + " (" + KEY_STATE + ","
					+ KEY_LAT + "," + KEY_LON + ") VALUES ('" + hashString
					+ "',1,'" + csvLine + "')";
			mDb.execSQL(sql);
		} catch (SQLException e) {
			logger.warn("execute insert: " + e.toString());
			return;
		}
		return;
	}

	/**
	 * Method to search the database for the supplied string.
	 * 
	 * @param hashString
	 *            String representing the hash to be retrieved.
	 * @param csvLine
	 * @return Boolean whether the hash was found or not.
	 */
	public void delete(String hashString) {
		logger.trace("In delete(" + hashString + ")");

		try {
			mDb.execSQL("DELETE FROM " + COR_TABLE + " WHERE " + KEY_STATE
					+ " = '" + hashString + "';");
		} catch (SQLException e) {
			logger.warn("execute delete: " + e.toString());
			return;
		}
		return;
	}
}

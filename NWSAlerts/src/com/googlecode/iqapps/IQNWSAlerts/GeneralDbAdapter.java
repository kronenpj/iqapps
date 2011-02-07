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

import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.googlecode.iqapps.Logger;

/**
 * Simple database helper class. Defines the basic CRUD operations for the
 * application, and gives the ability to list all entries as well as retrieve or
 * modify a specific entry. G Graciously stolen from the Android Notepad
 * example.
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 */
public class GeneralDbAdapter {
	private static final Logger logger = Logger.getLogger("GeneralDbAdapter");
	public static final String KEY_VERSION = "version";
	public static final String KEY_ROWID = "_id";
	public static final String DB_FALSE = "0";
	public static final String DB_TRUE = "1";
	public static final String MAX_ROW = "max(" + KEY_ROWID + ")";
	public static final String MAX_COUNT = "max(" + KEY_VERSION + ")";

	private static final String TAG = "GeneralDbAdapter";
	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private CursorFactory mCursorFactory;

	protected static final String DATABASE_NAME = "capserial.db";
	private static final int DATABASE_VERSION = 1;

	/**
	 * Database creation SQL statements
	 */
	private static final String METADATA_CREATE = "create table "
			+ "NWSAlertMeta(version integer primary key);";
	private static final String DATABASE_METADATA = "NWSAlertMeta";

	public static final String CAP_TABLE = "capdata";
	public static final String KEY_ID = "_id";
	public static final String KEY_SERIALIZED = "serialized";
	public static final String KEY_NWSID = "nwsid";

	/**
	 * Database creation SQL statement
	 */
	private static final String COR_TABLE_CREATE = "CREATE TABLE " + CAP_TABLE
			+ " (" + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_SERIALIZED
			+ " BLOB NOT NULL, " + KEY_NWSID + " TEXT NOT NULL);";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(METADATA_CREATE);
			db.execSQL(COR_TABLE_CREATE);

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
			switch (newVersion) {
			case 2:
				Log.e(TAG, "Version 2 database code but no alterations.");
			}
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 */
	public GeneralDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Opens the general database.
	 * 
	 * @return An instance of this class.
	 * @throws SQLException
	 */
	public GeneralDbAdapter open() throws SQLException {
		logger.debug("In open.");
		mDbHelper = new DatabaseHelper(mCtx);
		try {
			mDb = mDbHelper.getReadableDatabase();
		} catch (NullPointerException e) {
			mDb = mCtx.openOrCreateDatabase(DATABASE_NAME, 0, mCursorFactory);
		}
		return this;
	}

	/**
	 * Close this database.
	 */
	public void close() {
		logger.trace("In close()");
		mDbHelper.close();
	}

	/**
	 * Retrieve the version number of the database.
	 * 
	 * @return The version of the database.
	 * @throws SQLException
	 */
	public int fetchVersion() throws SQLException {
		logger.trace("In fetchVersion()");
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
	 * Retrieve the referenced serialized CAP object.
	 * 
	 * @param index
	 *            The index of the CAP object to retrieve.
	 * @return Byte array of the CAP object to retrieve.
	 */
	public byte[] getCAPSerialized(int index) {
		logger.trace("In getCAPSerialized()");
		byte[] output;
		try {
			Cursor mCursor = mDb.query(false, CAP_TABLE,
					new String[] { KEY_SERIALIZED }, KEY_ID + " = " + index,
					null, null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			} else
				return null;
			int responses = mCursor.getCount();
			mCursor.close();
			if (responses < 1)
				return null;
			output = mCursor.getBlob(0);
		} catch (SQLException e) {
			logger.warn(e.toString());
			return null;
		}

		return output;
	}

	/**
	 * Retrieve the referenced serialized CAP object.
	 * 
	 * @param nwsid
	 *            NWS ID of the serialized CAP object to retrieve.
	 * @return Byte array with the serialized CAP object referenced NWS ID.
	 */
	public byte[] getCAPSerialized(String nwsid) {
		logger.trace("In getCAPSerialized()");
		byte[] output;
		try {
			Cursor mCursor = mDb.query(false, CAP_TABLE,
					new String[] { KEY_SERIALIZED }, KEY_NWSID + " = " + nwsid,
					null, null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			} else
				return null;
			int responses = mCursor.getCount();
			mCursor.close();
			if (responses < 1)
				return null;
			output = mCursor.getBlob(0);
		} catch (SQLException e) {
			logger.warn(e.toString());
			return null;
		}

		return output;
	}

	/**
	 * Retrieve the index of the CAP entry for the supplied NWS ID.
	 * 
	 * @param nwsid
	 * @return
	 */
	public int getCAPIndexForID(String nwsid) {
		logger.trace("In getCAPIndexForID()");
		int output;
		try {
			Cursor mCursor = mDb.query(false, CAP_TABLE,
					new String[] { KEY_ID }, KEY_SERIALIZED + " =  ?",
					new String[] { nwsid }, null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			} else
				return 0;
			int responses = mCursor.getCount();
			mCursor.close();
			if (responses < 1)
				return 0;
			output = mCursor.getInt(0);
		} catch (SQLException e) {
			logger.warn(e.toString());
			return 0;
		}

		return output;
	}

	/**
	 * Retrieve all NWS IDs from the database.
	 * 
	 * @return String array of NWS IDs from the database.
	 */
	public String[] getNWSIDs() {
		logger.trace("In getNWSIDs()");
		Vector<String> temp = new Vector<String>();
		try {
			Cursor mCursor = mDb.query(false, CAP_TABLE,
					new String[] { KEY_NWSID }, null, null, null, null, null,
					null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			} else
				return null;
			int responses = mCursor.getCount();
			if (responses < 1)
				return null;
			do {
				temp.add(mCursor.getString(0));
			} while (mCursor.moveToNext());
			mCursor.close();
		} catch (SQLException e) {
			logger.warn(e.toString());
			return null;
		} catch (NullPointerException e) {
			// Expected if no alerts are in the database.
			logger.debug("getNWSIDs: " + e.toString());
			return null;
		}

		String[] retString = new String[temp.size()];
		int i = 0;
		for (String string : temp) {
			retString[i] = new String(string);
			i++;
		}
		return retString;
	}

	/**
	 * Put a serialized CAP entry into the database.
	 * 
	 * @param serial
	 *            The serialized CAP entry.
	 * @param nwsid
	 *            The NWS ID of the serialized CAP entry.
	 */
	public void putCAPSerialized(byte[] serial, String nwsid) {
		logger.trace("In putCAPSerialized()");

		try {
			mDb.execSQL("INSERT INTO " + CAP_TABLE + " (" + KEY_SERIALIZED
					+ ", " + KEY_NWSID + ") VALUES ( ?, ? )", new Object[] {
					serial, nwsid });
		} catch (SQLException e) {
			logger.warn("execute insert: " + e.toString());
		}
		return;
	}

	/**
	 * Delete a CAP entry from the database based on the NWS ID.
	 * 
	 * @param nwsid
	 *            The NWS ID of the serialized CAP entry to delete.
	 */
	public void deleteCAP(String nwsid) {
		logger.trace("In deleteCAP(" + nwsid + ")");

		try {
			mDb.execSQL("DELETE FROM " + CAP_TABLE + " WHERE " + KEY_NWSID
					+ " = '" + nwsid + "';");
		} catch (SQLException e) {
			logger.warn("execute delete: " + e.toString());
		}
		return;
	}

	/**
	 * Delete a CAP entry from the database based on an index number.
	 * 
	 * @param index
	 *            The index of the serialized CAP entry to be deleted.
	 */
	public void deleteCAP(int index) {
		logger.trace("In deleteCAP(" + index + ")");

		try {
			mDb.execSQL("DELETE FROM " + CAP_TABLE + " WHERE " + KEY_ID
					+ " = '" + index + "';");
		} catch (SQLException e) {
			logger.warn("execute delete: " + e.toString());
		}
		return;
	}

	public void vacuum() {
		logger.trace("In vacuum");

		try {
			mDb.execSQL("vacuum;");
		} catch (SQLException e) {
			logger.warn("execute vacuum: " + e.toString());
		}
		return;
	}
}

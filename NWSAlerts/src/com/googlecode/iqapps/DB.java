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
package com.googlecode.iqapps;

import java.io.File;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

// From http://pa.rezendi.com/2010/04/sqlite-on-android.html
public class DB {
	private static DB instance;
	SQLiteDatabase mDB;
	DatabaseHelper mDbHelper;
	final Context mCtx;

	public static DB GetFor(Context context) {
		if (instance == null)
			instance = new DB(context);
		if (!instance.isOpen())
			instance.open();
		return instance;
	}

	public static void Close() {
		if (instance != null && instance.isOpen())
			instance.close();
		instance = null;
	}

	/**
	 * Database creation sql statement
	 */
	static final String MESSAGE_CREATE = "create table Message (_id integer primary key autoincrement, "
			+ "content text not null, dateCreated datetime not null, dateViewed datetime, "
			+ "messageTitle text, messageType integer, sender text, recipient text); ";

	// [...other table definitions go here...]

	static final String DATABASE_NAME = "iTravel";
	static final int DATABASE_VERSION = 1;

	static class DatabaseHelper {
		private SQLiteDatabase db;
		private Context mCtx;

		private DatabaseHelper(Context context) {
			mCtx = context;
		}

		public void open() {
			File dbDir = null, dbFile = null;
			// if (Settings.DoSDDB()
			// && Environment.getExternalStorageState().equals(
			// Environment.MEDIA_MOUNTED)) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				dbDir = Environment.getExternalStorageDirectory();
				dbFile = new File(dbDir, "iTravel.sqlite");
			} else
				dbFile = mCtx.getDatabasePath("iTravel");

			if (dbFile.exists()) {
				Log.i("SQLiteHelper", "Opening database at " + dbFile);
				db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
				if (DATABASE_VERSION > db.getVersion())
					upgrade();
			} else {
				Log.i("SQLiteHelper", "Creating database at " + dbFile);
				db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
				create();
			}
		}

		public void close() {
			db.close();
		}

		public void create() {
			Log.w("" + this, "Creating Database " + db.getPath());
			db.execSQL(MESSAGE_CREATE);
			// ...other tables go here
			db.setVersion(DATABASE_VERSION);
		}

		public void upgrade() {
			Log.w("" + this, "Upgrading database " + db.getPath()
					+ " from version " + db.getVersion() + " to "
					+ DATABASE_VERSION + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS Message");
			// ...other tables go here
			create();
		}

		public SQLiteDatabase getWritableDatabase() {
			if (db == null)
				open();
			return db;
		}
	}

	public DB(Context ctx) {
		this.mCtx = ctx;
	}

	public DB open() throws SQLException {
		if (mDbHelper == null)
			mDbHelper = new DatabaseHelper(mCtx);
		if (mDB == null || !mDB.isOpen())
			mDB = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public boolean isOpen() {
		return mDB != null && mDB.isOpen();
	}

	public boolean deleteAll() {
		mDB.delete("Message", null, null);
		// ...other tables go here
		return true;
	}
}

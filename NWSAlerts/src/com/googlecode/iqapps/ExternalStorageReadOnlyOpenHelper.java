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
// From Alexander Yap's blog at:
// http://penguinman-techtalk.blogspot.com/2010/08/loading-large-reference-database-in.html
// No license was evident at the time of retrieval: 28-Aug-2010.
package com.googlecode.iqapps;

import java.io.File;

import com.googlecode.iqapps.IQNWSAlerts.CorrelationDbAdapter;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.AndroidRuntimeException;

public abstract class ExternalStorageReadOnlyOpenHelper {
	private static Logger logger = Logger
			.getLogger("ExternalStorageReadOnlyOpenHelper");
	private SQLiteDatabase database;
	private File dbFile;
	private SQLiteDatabase.CursorFactory factory;

	public ExternalStorageReadOnlyOpenHelper(String dbFileName,
			SQLiteDatabase.CursorFactory factory) {
		this.factory = factory;

		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			throw new AndroidRuntimeException(
					"External storage (SD-Card) not mounted");
		}
		File appDbDir = new File(Environment.getExternalStorageDirectory(),
				CorrelationDbAdapter.externalSubdirectory);
		if (!appDbDir.exists()) {
			logger.trace("Creating dir: " + appDbDir.getPath());
			appDbDir.mkdirs();
		}
		this.dbFile = new File(appDbDir, dbFileName);
		logger.trace("File " + this.dbFile.getPath() + " "
				+ (this.dbFile.exists() ? "exists" : "does not exist"));
	}

	public boolean databaseFileExists() {
		return dbFile.exists();
	}

	private void open() {
		logger.trace("In open");
		if (databaseFileExists()) {
			logger.debug("open: Database file exists.");
			database = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(),
					factory, SQLiteDatabase.OPEN_READONLY);

			if (database == null) {
				logger.error("open: database is null following");
				logger.error("SQLiteDatabase.openDatabase("
						+ dbFile.getAbsolutePath()
						+ ",	factory, SQLiteDatabase.OPEN_READONLY");
			}
		} else {
			logger.error("open: Database file doesn't exist.");
		}
	}

	public synchronized void close() {
		if (database != null) {
			database.close();
			database = null;
		}
	}

	public synchronized SQLiteDatabase getReadableDatabase() {
		return getDatabase();
	}

	private SQLiteDatabase getDatabase() {
		logger.trace("In getDatabase");
		if (database == null) {
			open();
		}
		return database;
	}
}

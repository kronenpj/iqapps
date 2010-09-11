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

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.AndroidRuntimeException;

public abstract class ExternalStorageReadOnlyOpenHelper {
	private SQLiteDatabase database;
	private File dbFile;
	private SQLiteDatabase.CursorFactory factory;
	protected String externalSubdirectory="NWSAlerts";

	public ExternalStorageReadOnlyOpenHelper(String dbFileName,
			SQLiteDatabase.CursorFactory factory) {
		this.factory = factory;

		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			throw new AndroidRuntimeException(
					"External storage (SD-Card) not mounted");
		}
		File appDbDir = new File(Environment.getExternalStorageDirectory(),
				externalSubdirectory);
		if (!appDbDir.exists()) {
			appDbDir.mkdirs();
		}
		this.dbFile = new File(appDbDir, dbFileName);
	}

	public boolean databaseFileExists() {
		return dbFile.exists();
	}

	private void open() {
		if (dbFile.exists()) {
			database = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(),
					factory, SQLiteDatabase.OPEN_READONLY);
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
		if (database == null) {
			open();
		}
		return database;
	}
}

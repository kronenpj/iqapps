package com.googlecode.iqapps.IQNWSAlerts;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.googlecode.iqapps.ExternalStorageReadOnlyOpenHelper;
import com.googlecode.iqapps.Logger;

public class ExternalDBHelper extends ExternalStorageReadOnlyOpenHelper {
	private static Logger logger = Logger.getLogger("ExternalDBHelper");

	public ExternalDBHelper(String dbFileName, CursorFactory factory) {
		super(dbFileName, factory);
		logger.debug("Constructor: " + dbFileName + ", " + factory);
	}

	/* (non-Javadoc)
	 * @see com.googlecode.iqapps.ExternalStorageReadOnlyOpenHelper#getReadableDatabase()
	 */
	@Override
	public synchronized SQLiteDatabase getReadableDatabase() {
		logger.debug("getReadableDatabase");
		return super.getReadableDatabase();
	}
}

package com.googlecode.iqapps.IQNWSAlerts;

import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.googlecode.iqapps.ExternalStorageReadOnlyOpenHelper;

public class ExternalDBHelper extends ExternalStorageReadOnlyOpenHelper {
	final protected String externalSubdirectory = "NWSAlert";

	public ExternalDBHelper(String dbFileName, CursorFactory factory) {
		super(dbFileName, factory);
	}
}

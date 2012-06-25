package com.googlecode.iqapps;

import static com.googlecode.iqapps.IOUtils.close;
import static com.googlecode.iqapps.IOUtils.cp;
import static com.googlecode.iqapps.IOUtils.mv;
import static com.googlecode.iqapps.ReflectionUtils.rRawName;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import android.app.Instrumentation;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class Databases {
	private static final String TAG = Positron.TAG;

	private final Instrumentation instrumentation;

	public Databases(Instrumentation instrumentation) {
		this.instrumentation = instrumentation;
	}

	/** Backup all databases in the target context. */
	public void backup() {
		for (String database : instrumentation.getTargetContext()
				.databaseList()) {
			if (isBackup(database))
				continue;
			backup(database);
		}
	}

	/** Restore all databases that have backups in the target context. */
	public void restore() {
		for (String database : instrumentation.getTargetContext()
				.databaseList()) {
			if (!isBackup(database))
				continue;
			restore(originalOf(database));
		}
	}

	/**
	 * Back up the given database by copying its defining file out of the way.
	 * 
	 * This blows away any previous backup.
	 * 
	 * @param database
	 *            The database to back up.
	 */
	public void backup(String database) {
		Log.i(TAG, "Backing up " + database);
		cp(locationOf(database), locationOf(backupOf(database)));
	}

	/**
	 * Restore the given database by copying the backup file back in place.
	 * 
	 * @param database
	 *            The database to restore.
	 */
	public void restore(String database) {
		Log.i(TAG, "Restoring " + database);
		mv(locationOf(backupOf(database)), locationOf(database));
	}

	private String locationOf(String database) {
		return instrumentation.getTargetContext().getDatabasePath(database)
				.getAbsolutePath();
	}

	private String backupOf(String database) {
		return database.replaceAll("\\.db$", "_backup.db");
	}

	private String originalOf(String database) {
		if (!isBackup(database))
			throw new IllegalArgumentException();
		return database.replaceFirst("_backup\\.db$", ".db");
	}

	private boolean isBackup(String database) {
		return database.matches(".*_backup\\.db$");
	}

	/**
	 * Execute Sql scripts loaded from a raw resources.
	 * 
	 * @param database
	 *            The database to execute the sql.
	 * @param scriptsAsRawResources
	 *            A varargs of R.raw.XXX resource ids of scripts to execute.
	 */
	public void sql(String database, int... scriptsAsRawResources) {
		for (int id : scriptsAsRawResources) {
			InputStream raw = openRawResource(id);
			try {
				sql(database, raw, getRawResourceName(id));
			} finally {
				close(raw);
			}
		}
	}

	/**
	 * Execute Sql from a string.
	 * 
	 * @param database
	 *            The database to execute the sql.
	 * @param script
	 *            Literal sql to run.
	 */
	public void sql(String database, String script) {
		sql(database, new StringReader(script), "(string literal)");
	}

	private void sql(String database, InputStream in, String scriptName) {
		sql(database, new InputStreamReader(in), scriptName);
	}

	private void sql(String database, Reader in, String scriptName) {
		SQLiteDatabase db = null;
		SqlScanner statements = null;
		try {
			db = openOrCreateDatabase(database);
			statements = new SqlScanner(in);
			for (String statement : statements)
				db.execSQL(statement);
		} catch (SQLiteException e) {
			int line = statements.getLineNumber();
			throw new RuntimeException("SQL error in " + scriptName + ", line "
					+ line + ": " + e.getMessage(), e);
		} finally {
			if (statements != null)
				statements.close();
			if (db != null)
				db.close();
		}
	}

	private SQLiteDatabase openOrCreateDatabase(String name) {
		return instrumentation.getTargetContext().openOrCreateDatabase(name, 0,
				null);
	}

	private InputStream openRawResource(int id) {
		return instrumentation.getTargetContext().getResources()
				.openRawResource(id);
	}

	private String getRawResourceName(int id) {
		String targetPackage = instrumentation.getTargetContext()
				.getPackageName();
		try {
			return rRawName(targetPackage, id);
		} catch (RuntimeException e) {
			if (e.getCause() instanceof ClassNotFoundException) {
				Log.e(TAG,
						"Can't find "
								+ targetPackage
								+ ".R.raw.  Are your sql fixture scripts declared as raw resources?");
			}
			throw e;
		}
	}
}

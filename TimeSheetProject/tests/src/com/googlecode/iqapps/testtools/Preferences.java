package com.googlecode.iqapps.testtools;

import static com.googlecode.iqapps.testtools.IOUtils.cp;
import static com.googlecode.iqapps.testtools.IOUtils.mv;

import java.io.File;

import android.app.Instrumentation;
import android.util.Log;

public class Preferences {
	private static final String TAG = Positron.TAG;

	private final Instrumentation instrumentation;

	public Preferences(Instrumentation instrumentation) {
		this.instrumentation = instrumentation;
	}

	/** Backup all preferences files in the target context. */
	public void backup() {
		String pathToPrefs = instrumentation.getTargetContext().getFilesDir()
				.getAbsolutePath().replace("/files", "")
				+ "/shared_prefs";
		Log.i(TAG, "Backing up files in " + pathToPrefs);
		File prefsDir = new File(pathToPrefs);
		for (String preferences : prefsDir.list()) {
			if (isBackup(preferences))
				continue;
			backup(preferences);
		}
	}

	/** Restore all preferences files that have backups in the target context. */
	public void restore() {
		String pathToPrefs = instrumentation.getTargetContext().getFilesDir()
				.getAbsolutePath().replace("/files", "")
				+ "/shared_prefs";
		File prefsDir = new File(pathToPrefs);
		for (String preferences : prefsDir.list()) {
			if (!isBackup(preferences))
				continue;
			restore(originalOf(preferences));
		}
	}

	/**
	 * Back up the given preferences file by copying its defining file out of
	 * the way.
	 * 
	 * This blows away any previous backup.
	 * 
	 * @param preferences
	 *            The preferences file to back up.
	 */
	public void backup(String preferences) {
		Log.i(TAG, "Backing up " + preferences);
		cp(locationOf(preferences), locationOf(backupOf(preferences)));
	}

	/**
	 * Restore the given preferences file by copying the backup file back in
	 * place.
	 * 
	 * @param preferences
	 *            The preferences file to restore.
	 */
	public void restore(String preferences) {
		Log.i(TAG, "Restoring " + preferences);
		mv(locationOf(backupOf(preferences)), locationOf(preferences));
	}

	private String locationOf(String preferences) {
		return instrumentation.getTargetContext().getFilesDir()
				.getAbsolutePath().replace("/files", "")
				+ "/shared_prefs/" + preferences;
	}

	private String backupOf(String preferences) {
		return preferences.replaceAll("\\.xml$", "_backup.xml");
	}

	private String originalOf(String preferences) {
		if (!isBackup(preferences))
			throw new IllegalArgumentException();
		return preferences.replaceFirst("_backup\\.xml$", ".xml");
	}

	private boolean isBackup(String preferences) {
		return preferences.matches(".*_backup\\.xml$");
	}
}

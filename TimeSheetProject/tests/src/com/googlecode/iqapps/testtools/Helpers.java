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

/**
 * @author      (classes and interfaces only, required)
 * @version     (classes and interfaces only, required. See footnote 1)
 * @param       (methods and constructors only)
 * @return      (methods only)
 * @exception   (@throws is a synonym added in Javadoc 1.2)
 * @see         
 */
package com.googlecode.iqapps.testtools;

import java.io.File;

import junit.framework.Assert;
import android.app.Instrumentation;
import android.view.KeyEvent;
import android.widget.Toast;

import com.googlecode.iqapps.IQTimeSheet.MenuItems;
import com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity;
import com.jayway.android.robotium.solo.Solo;

/**
 * @author kronenpj
 * 
 */
public class Helpers {
	// private static Log log = LogFactory.getLog(Helpers.class);
	// private static final String TAG = "Helpers";
	private static final int SLEEPTIME = 50;
	public static final int alignments = 12;
	public static final String DATABASE_NAME = "TimeSheetDB.db";
	public static final String text1 = "Task 1";
	public static final String text2 = "Task 2";
	public static final String text3 = "Task 3";
	public static final String text4 = "Task 4";
	public static final String text5 = "Renamed task";
	public static final String RENAMETASK = "Rename Task";
	public static final String RETIRETASK = "Retire Task";
	public static final String EDITTASKNAME = "Edit Task";

	// Keep the test runner happy.
	public void empty() {
	}

	public static void sleep() {
		sleep(5000);
	}

	public static void sleep(long time) {
		// if (time > 1000 && log.isInfoEnabled())
		// log.info(TAG + ": sleeping for " + time + " ms.");
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

	public static void backup(Solo solo, Instrumentation mInstr,
			TimeSheetActivity mActivity) {
		Assert.assertNotNull(solo);
		Assert.assertNotNull(mInstr);
		Assert.assertNotNull(mActivity);

		while (!solo.getCurrentActivity().isTaskRoot()) {
			solo.goBack();
		}
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		mActivity = (TimeSheetActivity) solo.getCurrentActivity();
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.BACKUP.ordinal()).getItemId();
		Assert.assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		solo.sleep(SLEEPTIME);
	}

	public static void restore(Solo solo, Instrumentation mInstr,
			TimeSheetActivity mActivity) {
		Assert.assertNotNull(solo);
		Assert.assertNotNull(mInstr);
		Assert.assertNotNull(mActivity);

		while (!solo.getCurrentActivity().isTaskRoot()) {
			solo.goBack();
		}
		try {
			mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
			int menuItemID = mActivity.getOptionsMenu()
					.getItem(MenuItems.RESTORE.ordinal()).getItemId();
			Assert.assertTrue(mInstr.invokeMenuActionSync(mActivity,
					menuItemID, 0));
			solo.sleep(SLEEPTIME);

			solo.sendKey(KeyEvent.KEYCODE_DPAD_LEFT);
			solo.sleep(SLEEPTIME);
			solo.sendKey(KeyEvent.KEYCODE_ENTER);
			solo.sleep(SLEEPTIME);
		} catch (IndexOutOfBoundsException e) {
			Toast.makeText(mActivity, "Restoration of database failed.",
					Toast.LENGTH_LONG).show();
		}
	}

	/** Backup all preferences files in the target context. */
	public static void prefBackup(Instrumentation mInstr) {
		String pathToPrefs = mInstr.getTargetContext().getFilesDir()
				.getAbsolutePath().replace("/files", "")
				+ "/shared_prefs";
		File prefsDir = new File(pathToPrefs);
		for (String preferences : prefsDir.list()) {
			if (isBackup(preferences))
				continue;
			prefBackup(preferences, mInstr);
		}
	}

	/** Restore all preferences files that have backups in the target context. */
	public static void prefRestore(Instrumentation mInstr) {
		String pathToPrefs = mInstr.getTargetContext().getFilesDir()
				.getAbsolutePath().replace("/files", "")
				+ "/shared_prefs";
		File prefsDir = new File(pathToPrefs);
		for (String preferences : prefsDir.list()) {
			if (!isBackup(preferences))
				continue;
			prefRestore(originalOf(preferences), mInstr);
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
	public static void prefBackup(String preferences, Instrumentation mInstr) {
		IOUtils.cp(locationOf(preferences, mInstr),
				locationOf(backupOf(preferences), mInstr));
	}

	/**
	 * Restore the given preferences file by copying the backup file back in
	 * place.
	 * 
	 * @param preferences
	 *            The preferences file to restore.
	 */
	public static void prefRestore(String preferences, Instrumentation mInstr) {
		IOUtils.mv(locationOf(backupOf(preferences), mInstr),
				locationOf(preferences, mInstr));
	}

	private static String locationOf(String preferences, Instrumentation mInstr) {
		return mInstr.getTargetContext().getFilesDir().getAbsolutePath()
				.replace("/files", "")
				+ "/shared_prefs/" + preferences;
	}

	private static String backupOf(String preferences) {
		return preferences.replaceAll("\\.xml$", "_backup.xml");
	}

	private static String originalOf(String preferences) {
		if (!isBackup(preferences))
			throw new IllegalArgumentException();
		return preferences.replaceFirst("_backup\\.xml$", ".xml");
	}

	private static boolean isBackup(String preferences) {
		return preferences.matches(".*_backup\\.xml$");
	}
}

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

import junit.framework.Assert;
import android.app.Instrumentation;
import android.view.KeyEvent;

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
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.RESTORE.ordinal()).getItemId();
		Assert.assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		solo.sleep(SLEEPTIME);

		solo.sendKey(KeyEvent.KEYCODE_DPAD_LEFT);
		solo.sleep(SLEEPTIME);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);
	}
}

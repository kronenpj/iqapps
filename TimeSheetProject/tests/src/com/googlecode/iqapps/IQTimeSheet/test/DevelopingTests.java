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
package com.googlecode.iqapps.IQTimeSheet.test;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

import com.googlecode.iqapps.Positron;
import com.googlecode.iqapps.IQTimeSheet.MenuItems;
import com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity;
import com.jayway.android.robotium.solo.Solo;

public class DevelopingTests extends
		ActivityInstrumentationTestCase2<TimeSheetActivity> {
	// private Log log = LogFactory.getLog(DevelopingTests.class);
	// private static final String TAG = "TimeSheetActivityTest";
	private static final int SLEEPTIME = 50;

	private TimeSheetActivity mActivity;
	private Solo solo;
	private Instrumentation mInstr;
	private Positron mPositron;

	public DevelopingTests(Class<TimeSheetActivity> activityClass) {
		super(activityClass);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mInstr = getInstrumentation();
		mPositron = new Positron(mInstr);
		mPositron.prefBackup();
		// startActivity("com.googlecode.iqapps.IQTimeSheet",
		// "com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity");
		mInstr = getInstrumentation();
		solo = new Solo(mInstr, getActivity());
	}

	public void tearDown() {
		solo.finishOpenedActivities();
		mPositron.prefRestore();
	}

	public void empty() {
		setAlignTimePreferenceViaMenu(1); // 2-minute
	}

	private void setAlignTimePreferenceViaMenu(int downCount) {
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.SETTINGS.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("MyPreferenceActivity", 500));
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);

		while (solo.scrollUpList(0))
			;
		solo.clickOnText(mActivity.getResources().getStringArray(
				com.googlecode.iqapps.IQTimeSheet.R.array.alignminutetext)[downCount]);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);
		solo.clickOnText(mActivity
				.getString(com.googlecode.iqapps.IQTimeSheet.R.string.accept));
		solo.goBack();
	}
}
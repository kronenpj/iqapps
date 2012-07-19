package com.googlecode.iqapps.IQTimeSheet.test;

import android.app.Instrumentation;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Suppress;
import android.view.KeyEvent;
import android.widget.ListView;

import com.googlecode.iqapps.TimeHelpers;
import com.googlecode.iqapps.IQTimeSheet.MenuItems;
import com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity;
import com.googlecode.iqapps.testtools.Helpers;
import com.jayway.android.robotium.solo.Solo;

@Suppress // #$##
public class TimeSheetActivityBasic extends
		ActivityInstrumentationTestCase2<TimeSheetActivity> {
	private static final int SLEEPTIME = 50;

	final String renamedTaskText = "Renamed Task";

	private Solo solo;
	private TimeSheetActivity mActivity;
	private ListView mView;
	private Context mCtx;
	private Instrumentation mInstr;

	public TimeSheetActivityBasic() {
		// super("com.googlecode.iqapps.IQTimeSheet", TimeSheetActivity.class);
		super(TimeSheetActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mInstr = getInstrumentation();
		mCtx = mInstr.getTargetContext();
		solo = new Solo(mInstr, getActivity());
	}

	/**
	 * Make sure the application is ready for us to test it.
	 */
	public void test00Preconditions() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		mView = (ListView) mActivity.findViewById(android.R.id.list);
		assertNotNull(mView);
	}

	/**
	 * Make sure the application is ready for us to test it.
	 */
	public void test01BackupDB() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		Helpers.backup(solo, mInstr, mActivity);
	}

	/**
	 * Make sure the application is ready for us to test it.
	 */
	public void test20EraseDB() {
		// TODO: This should be more kind to existing data. Perhaps backing it
		// up and replacing it once we're done testing...

		// Delete the databases associated with the project.
		String[] databases = mCtx.databaseList();
		for (int db = 0; db < databases.length; db++) {
			// assertTrue("dbList: " + databases[db], false);
			mCtx.deleteDatabase(databases[db]);
		}
	}

	public void test21ForEmptyDatabase() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		assertTrue(solo.searchText("Example task entry"));
	}

	public void test22RenameTask() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		// ListView mList = (ListView) solo.getView(android.R.id.list);
		solo.clickLongInList(0, 0);
		solo.sendKey(KeyEvent.KEYCODE_DPAD_DOWN);
		solo.sleep(SLEEPTIME);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);

		// Enter renamedTaskText in first editfield
		// solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, renamedTaskText);
		solo.sleep(SLEEPTIME);

		// Click on Accept button
		final String acceptButton = mActivity
				.getString(com.googlecode.iqapps.IQTimeSheet.R.string.accept);
		solo.clickOnButton(acceptButton);

		// Verify that renamedTaskText is correctly displayed
		assertTrue(solo.searchText(renamedTaskText));
	}

	public void test23StartStopTask() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		// Press the task to start recording time.
		solo.sendKey(KeyEvent.KEYCODE_DPAD_DOWN);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);

		// Press the task to stop recording time.
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
	}

	public void test24EditTask() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		int whatHour = TimeHelpers.millisToHour(TimeHelpers.millisNow());

		// Bring up the edit day activity.
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		solo.sleep(SLEEPTIME);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.EDITDAY_ENTRIES.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		solo.sleep(SLEEPTIME);

		// Select the first item in the list, which was just created.
		solo.sendKey(KeyEvent.KEYCODE_DPAD_DOWN);
		solo.sleep(SLEEPTIME);
		solo.sendKey(KeyEvent.KEYCODE_DPAD_UP);
		solo.sleep(SLEEPTIME);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);

		// Find the start time button and select it.
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		solo.sleep(SLEEPTIME);
		// If it's not midnight or the following hour, change start time
		if (whatHour != 0) {
			solo.sendKey(KeyEvent.KEYCODE_DPAD_LEFT);
			solo.sleep(SLEEPTIME);
		}
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);

		// Change the time to one hour prior.
		solo.sendKey(KeyEvent.KEYCODE_DPAD_DOWN);
		solo.sleep(SLEEPTIME);
		solo.sendKey(KeyEvent.KEYCODE_DPAD_UP);
		solo.sleep(SLEEPTIME);
		// If it's midnight or the following hour, increase instead
		if (whatHour == 0) {
			solo.sendKey(KeyEvent.KEYCODE_DPAD_UP);
			solo.sleep(SLEEPTIME);
			solo.sendKey(KeyEvent.KEYCODE_DPAD_UP);
			solo.sleep(SLEEPTIME);
		}
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);

		// Accept this change.
		solo.clickOnButton(mActivity
				.getString(com.googlecode.iqapps.IQTimeSheet.R.string.accept));
		solo.sleep(SLEEPTIME);

		// Accept the edit
		solo.clickOnButton(mActivity
				.getString(com.googlecode.iqapps.IQTimeSheet.R.string.accept));
		solo.sleep(SLEEPTIME);
	}

	public void test25Report() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		// Bring up the edit day activity.
		solo.sendKey(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.DAY_REPORT.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));

		// Select the footer
		assertTrue(solo.searchText("1.00"));
		solo.sleep(SLEEPTIME);
	}

	/**
	 * Restore the database after we're done messing with it.
	 */
	public void testzzzRestoreDB() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		Helpers.restore(solo, mInstr, mActivity);
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
}

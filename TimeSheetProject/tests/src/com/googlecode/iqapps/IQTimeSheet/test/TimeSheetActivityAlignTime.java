package com.googlecode.iqapps.IQTimeSheet.test;

import junit.framework.AssertionFailedError;
import android.app.Instrumentation;
import android.content.Context;
import android.database.SQLException;
import android.test.ActivityInstrumentationTestCase2;
import android.test.FlakyTest;
import android.test.suitebuilder.annotation.Suppress;
import android.view.KeyEvent;
import android.widget.ListView;

import com.googlecode.iqapps.TimeHelpers;
import com.googlecode.iqapps.IQTimeSheet.MenuItems;
import com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity;
import com.googlecode.iqapps.IQTimeSheet.TimeSheetDbAdapter;
import com.googlecode.iqapps.testtools.Helpers;
import com.jayway.android.robotium.solo.Solo;

@Suppress
public class TimeSheetActivityAlignTime extends
		ActivityInstrumentationTestCase2<TimeSheetActivity> {
	private static final int SLEEPTIME = 50;

	final String renamedTaskText = "Renamed Task";

	private Solo solo;
	private TimeSheetActivity mActivity;
	private ListView mView;
	private Context mCtx;
	private Instrumentation mInstr;

	public TimeSheetActivityAlignTime() {
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
	 * Take a backup of the database so that we can restore the current state
	 * later..
	 */
	public void test01BackupDB() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		Helpers.backup(solo, mInstr, mActivity);
	}

	/**
	 * Make sure the application is ready for us to test it.
	 */
	public void test02EraseDB() {
		eraseDatabase();
	}

	@FlakyTest
	public void test21Set1MinAlign() {
		executeTest("1 minute", "0.02");
	}

	@FlakyTest
	public void test22Set2MinAlign() {
		executeTest("2 minutes", "0.03");
	}

	@FlakyTest
	public void test23Set3MinAlign() {
		executeTest("3 minutes", "0.05");
	}

	@FlakyTest
	public void test24Set4MinAlign() {
		executeTest("4 minutes", "0.07");
	}

	@FlakyTest
	public void test25Set5MinAlign() {
		executeTest("5 minutes", "0.08");
	}

	@FlakyTest
	public void test26Set6MinAlign() {
		executeTest("6 minutes", "0.10");
	}

	@FlakyTest
	public void test27Set10MinAlign() {
		executeTest("10 minutes", "0.17");
	}

	@FlakyTest
	public void test28Set12MinAlign() {
		executeTest("12 minutes", "0.20");
	}

	@FlakyTest
	public void test29Set15MinAlign() {
		executeTest("15 minutes", "0.25");
	}

	@FlakyTest
	public void test30Set20MinAlign() {
		executeTest("20 minutes", "0.33");
	}

	@FlakyTest
	public void test31Set30MinAlign() {
		executeTest("30 minutes", "0.50");
	}

	@FlakyTest
	public void test32Set60MinAlign() {
		executeTest("60 minutes", "1.00");
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

	/**
	 * 
	 */
	private void executeTest(String prefText, String reportText) {
		mActivity = getActivity();
		assertNotNull(mActivity);

		// Setup the test
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.SETTINGS.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		solo.sleep(SLEEPTIME);

		solo.clickInList(0, 0);

		solo.sleep(SLEEPTIME);
		// Need to scroll to the top of the list the first time.
		if (prefText.equalsIgnoreCase("1 minute")) {
			while (solo.scrollUpList(0))
				;
		}
		solo.clickOnText(prefText);
		solo.sleep(SLEEPTIME);
		solo.goBack();
		solo.goBack();

		// Execute the test
		startStopTask();
		solo.sleep(SLEEPTIME);
		changeTaskDuration(prefText.equalsIgnoreCase("60 minutes"));
		solo.sleep(SLEEPTIME);
		checkReport(reportText);

		// Reset the database
		TimeSheetDbAdapter db = new TimeSheetDbAdapter(mActivity);
		try {
			db.open();
		} catch (SQLException e) {
			assertFalse(e.toString(), true);
		}
		db.deleteEntry(db.lastClockEntry());
		eraseDatabase();
	}

	private void startStopTask() {
		// Press the task to start recording time.
		solo.sendKey(KeyEvent.KEYCODE_DPAD_DOWN);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);

		// Press the task to stop recording time.
		solo.sendKey(KeyEvent.KEYCODE_ENTER);

		TimeSheetDbAdapter db = new TimeSheetDbAdapter(mActivity);
		try {
			db.open();
		} catch (SQLException e) {
			assertFalse(e.toString(), true);
		}

		// Forcibly set the start and end times in the database to be on an hour
		// boundary so the various calculations and round-off errors are
		// consistent.
		long startTime = TimeHelpers.millisToAlignMinutes(
				TimeHelpers.millisNow(), 60);
		// If the aligned hour is midnight, but the current hour isn't, drop
		// the task's time back an hour.
		if (TimeHelpers.millisToHour(startTime) == 0
				&& TimeHelpers.millisToHour(TimeHelpers.millisNow()) != 0) {
			startTime -= 3600000;
		}
		db.updateEntry(-1, -1, null, startTime, startTime);
	}

	private void changeTaskDuration(boolean isHour) {
		mActivity = getActivity();
		assertNotNull(mActivity);

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

		// Find the end time button and select it.
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		solo.sleep(SLEEPTIME);
		// mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_LEFT);
		// solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);

		// Change the time to X minutes later.
		solo.sendKey(KeyEvent.KEYCODE_DPAD_DOWN);
		// solo.sleep(SLEEPTIME);
		solo.sendKey(KeyEvent.KEYCODE_DPAD_UP);
		// solo.sleep(SLEEPTIME);
		solo.sendKey(KeyEvent.KEYCODE_DPAD_UP);
		// solo.sleep(SLEEPTIME);
		solo.sendKey(KeyEvent.KEYCODE_DPAD_UP);
		// solo.sleep(SLEEPTIME);
		if (!isHour) {
			solo.sendKey(KeyEvent.KEYCODE_DPAD_RIGHT);
		}
		// solo.sleep(SLEEPTIME);
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

	private void checkReport(String findMe) {
		mActivity = getActivity();
		assertNotNull(mActivity);

		// Bring up the edit day activity.
		solo.sendKey(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.DAY_REPORT.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));

		// Select the footer
		try {
			assertTrue(solo.searchText(findMe));
			solo.sleep(SLEEPTIME);
		} catch (AssertionFailedError e) {
			eraseDatabase();
			throw (e);
		}
	}

	/**
	 * Physically remove the database file
	 */
	private void eraseDatabase() {
		// Delete the databases associated with the project.
		String[] databases = mCtx.databaseList();
		for (int db = 0; db < databases.length; db++) {
			// assertTrue("dbList: " + databases[db], false);
			mCtx.deleteDatabase(databases[db]);
		}
	}
}
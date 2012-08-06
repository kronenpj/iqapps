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
import com.googlecode.iqapps.testtools.Positron;
import com.jayway.android.robotium.solo.Solo;

// @Suppress //#$##
public class EditTaskHandlerTest extends
		ActivityInstrumentationTestCase2<TimeSheetActivity> {
	private static final String EXAMPLE_TASK_ENTRY = "Example task entry";
	private static final String CHILD_TASK_1_65 = "Child Task 1 - 65%";
	private static final String CHILD_TASK_2_35 = "Child Task 2 - 35%";

	private static final int SLEEPTIME = 50;

	final String renamedTaskText = "Renamed Task";

	private Solo solo;
	private TimeSheetActivity mActivity;
	private ListView mView;
	private Context mCtx;
	private Instrumentation mInstr;

	// private Positron mPositron;

	public EditTaskHandlerTest() {
		super(TimeSheetActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mInstr = getInstrumentation();
		mCtx = mInstr.getTargetContext();
		solo = new Solo(mInstr, getActivity());
		// mPositron = new Positron(mInstr);
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
		// mPositron.backup();
	}

	/**
	 * Make sure the application is ready for us to test it.
	 */
	public void test02EraseDB() {
		// TODO: This should be more kind to existing data. Perhaps backing it
		// up and replacing it once we're done testing...

		// Delete the databases associated with the project.
		String[] databases = mCtx.databaseList();
		for (int db = 0; db < databases.length; db++) {
			// assertTrue("dbList: " + databases[db], false);
			mCtx.deleteDatabase(databases[db]);
		}
	}

	public void test03ForEmptyDatabase() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		assertTrue(solo.searchText(EXAMPLE_TASK_ENTRY));
	}

	public void test10CreateSplitTasks() {
		createSplitTask(CHILD_TASK_1_65, EXAMPLE_TASK_ENTRY, 65);
		createSplitTask(CHILD_TASK_2_35, EXAMPLE_TASK_ENTRY, 35);
	}

	public void test20StartStopTask() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		// Press the task to start recording time.
		solo.searchText(EXAMPLE_TASK_ENTRY);
		solo.sendKey(KeyEvent.KEYCODE_DPAD_CENTER);
		solo.sleep(SLEEPTIME);

		// Press the task to stop recording time.
		solo.sendKey(KeyEvent.KEYCODE_DPAD_CENTER);
	}

	public void test30EditTask() {
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

//		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		solo.sleep(SLEEPTIME*5);

		// Find the start time button and select it.
		// The method of using the arrow keys is used because the buttons have
		// dynamic labels.
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		solo.sleep(SLEEPTIME);
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
		solo.sleep(SLEEPTIME);
		solo.sendKey(KeyEvent.KEYCODE_DPAD_LEFT);
		solo.sleep(SLEEPTIME);
		// If it's not midnight or the following hour, change start time
		if (whatHour != 0) {
			solo.sendKey(KeyEvent.KEYCODE_DPAD_LEFT);
			solo.sleep(SLEEPTIME);
		}
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);

		// Change the time to one hour prior.
		// The method of using the arrow keys is used because the button labels
		// aren't unique (+ / -).
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

	public void test40EditTaskCancel() {
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

//		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		solo.sleep(SLEEPTIME*5);

		// Select the cancel button.
		solo.clickOnButton(mActivity
				.getString(com.googlecode.iqapps.IQTimeSheet.R.string.cancel));
		solo.sleep(SLEEPTIME);
	}

	public void test50Report() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		// Bring up the report activity.
		solo.sendKey(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.DAY_REPORT.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));

		// Locate the larger percentage task
		assertTrue(solo.searchText("0.65"));
		solo.sleep(SLEEPTIME);

		// Locate the smaller percentage task
		assertTrue(solo.searchText("0.35"));
		solo.sleep(SLEEPTIME);

		// Locate the footer
		assertTrue(solo.searchText("1.00"));
		solo.sleep(SLEEPTIME);
	}

	public void test60EditTaskDelete() {
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

//		assertTrue(solo.waitForActivity("EditDayEntriesHandler", 500));
		solo.sleep(SLEEPTIME*5);

		// Select the delete button.
		solo.clickOnButton(mActivity
				.getString(com.googlecode.iqapps.IQTimeSheet.R.string.delete));
		solo.sleep(SLEEPTIME);

		// Answer the dialog.
		solo.sendKey(KeyEvent.KEYCODE_DPAD_LEFT);
		solo.sleep(SLEEPTIME);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		solo.sleep(SLEEPTIME);
	}

	/**
	 * Restore the database after we're done messing with it.
	 */
	public void testzzzRestoreDB() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		Helpers.restore(solo, mInstr, mActivity);
		// mPositron.restore();
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	private void createSplitTask(String name, String parent, int percentage) {
		mActivity = getActivity();
		assertNotNull(mActivity);

		// Bring up the new task activity.
		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		solo.sleep(SLEEPTIME);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.NEW_TASK.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		solo.sleep(SLEEPTIME);

		// Enter the name of the new task
		solo.clearEditText(0);
		solo.enterText(0, name);
		solo.sleep(SLEEPTIME);

		// Check the split task check box.
		solo.clickOnCheckBox(0);
		solo.sleep(SLEEPTIME);

		// Choose the first child task from the list.
		solo.pressSpinnerItem(0, 0);
		solo.sleep(SLEEPTIME);

		// Set the percentage desired.
		solo.setProgressBar(0, percentage);
		solo.sleep(SLEEPTIME);

		// Verify the percentage is reflected in the text field.
		assertTrue(solo.searchEditText(String.valueOf(percentage)));

		// Click on Accept button
		final String acceptButton = mActivity
				.getString(com.googlecode.iqapps.IQTimeSheet.R.string.accept);
		solo.clickOnButton(acceptButton);
		solo.sleep(SLEEPTIME);
	}
}

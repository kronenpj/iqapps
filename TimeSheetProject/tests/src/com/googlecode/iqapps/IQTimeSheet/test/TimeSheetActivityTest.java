package com.googlecode.iqapps.IQTimeSheet.test;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ListView;

import com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity;

public class TimeSheetActivityTest extends
		ActivityInstrumentationTestCase2<TimeSheetActivity> {
	// public class TimeSheetActivityTest extends
	// ActivityUnitTestCase<TimeSheetActivity> {

	private TimeSheetActivity mActivity;
	private ListView mView;
	private EditText mEView;
	private Object resourceString;
	private Intent intent;
	private Context ctx;
	private IBinder token;

	public TimeSheetActivityTest() {
		// super("com.googlecode.iqapps.IQTimeSheet", TimeSheetActivity.class);
		super(TimeSheetActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ctx = getInstrumentation().getTargetContext();

		setActivityInitialTouchMode(false);
	}

	/**
	 * Make sure the application is ready for us to test it.
	 */
	public void test0Preconditions() {
		// TODO: This should be more kind to existing data. Perhaps backing it
		// up and replacing it once we're done testing...

		// Delete the databases associated with the project.
		String[] databases = ctx.databaseList();
		for (int db = 0; db < databases.length; db++) {
			// assertTrue("dbList: " + databases[db], false);
			ctx.deleteDatabase(databases[db]);
		}

		mActivity = getActivity();
		assertNotNull(mActivity);

		mView = (ListView) mActivity.findViewById(android.R.id.list);
		assertNotNull(mView);
	}

	/**
	 * Make sure the application is ready for us to test it.
	 */
	public void test1BackupDB() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		sendKeys("DPAD_MENU");
		// FIXME: This is specific to the layout of the menu.
		sendRepeatedKeys(6, KeyEvent.KEYCODE_DPAD_RIGHT);
		sendKeys("ENTER");
		sendKeys("DPAD_DOWN");
		sendKeys("ENTER");
	}

	/**
	 * Make sure the application is ready for us to test it.
	 */
	public void test1EraseDB() {
		// TODO: This should be more kind to existing data. Perhaps backing it
		// up and replacing it once we're done testing...

		// Delete the databases associated with the project.
		String[] databases = ctx.databaseList();
		for (int db = 0; db < databases.length; db++) {
			// assertTrue("dbList: " + databases[db], false);
			ctx.deleteDatabase(databases[db]);
		}
	}

	// @UiThreadTest
	public void testForEmptyDatabase() {
		// Instrumentation mInstr = this.getInstrumentation();

		mActivity = getActivity();
		assertNotNull(mActivity);

		mView = (ListView) mActivity.findViewById(android.R.id.list);
		assertTrue(mView.getCount() > 0);

		int num = mView.getFirstVisiblePosition();
		assertEquals("Example task entry",
				(String) mView.getItemAtPosition(num));
	}

	// @UiThreadTest
	public void testRenameTask() {
		final String text = "Renamed Task";
		Instrumentation mInstr = this.getInstrumentation();

		mActivity = getActivity();
		assertNotNull(mActivity);

		mView = (ListView) mActivity.findViewById(android.R.id.list);
		assertTrue(mView.getCount() > 0);

		// Select the first item and rename it.
		sendKeys("DPAD_DOWN");
		TouchUtils.longClickView(this, mView.getSelectedView());

		sendKeys("DPAD_DOWN ENTER");
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}

		sendRepeatedKeys(30, KeyEvent.KEYCODE_DEL);

		mInstr.sendStringSync(text);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		sendKeys("DPAD_DOWN DPAD_DOWN ENTER");

		int num = mView.getSelectedItemPosition();
		assertEquals(text, (String) mView.getItemAtPosition(num));
	}

	// @UiThreadTest
	public void testStartStopTask() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		mView = (ListView) mActivity.findViewById(android.R.id.list);
		assertTrue(mView.getCount() > 0);

		// Select the first item and rename it.
		// int num = mView.getFirstVisiblePosition();
		// mView.setSelection(num);
		sendKeys("DPAD_DOWN ENTER");
		// TouchUtils.clickView(this, mView);
		// assertTrue(mView.performClick());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		sendKeys("ENTER");
		// assertTrue(mView.performClick());

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Make sure the application is ready for us to test it.
	 */
	public void testzzzRestoreDB() {
		mActivity = getActivity();
		assertNotNull(mActivity);

		sendKeys("DPAD_MENU");
		// FIXME: This is specific to the layout of the menu.
		sendRepeatedKeys(6, KeyEvent.KEYCODE_DPAD_RIGHT);
		sendKeys("ENTER");
		sendKeys("DPAD_DOWN");
		sendKeys("DPAD_DOWN");
		sendKeys("ENTER");
		sendKeys("DPAD_RIGHT");
		sendKeys("ENTER");
	}
}

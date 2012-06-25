package com.googlecode.iqapps.IQTimeSheet.test;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

import com.googlecode.iqapps.IQTimeSheet.AboutDialog;
import com.googlecode.iqapps.IQTimeSheet.MenuItems;
import com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity;
import com.jayway.android.robotium.solo.Solo;

public class AboutDialogTest extends
		ActivityInstrumentationTestCase2<TimeSheetActivity> {
	// private static final String TAG = "AboutDialogTest";
	private Instrumentation mInstr;
	private TimeSheetActivity mActivity;
	private Solo solo;

	public AboutDialogTest() {
		super(TimeSheetActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();

		mActivity = getActivity();
		mInstr = getInstrumentation();
		solo = new Solo(mInstr, mActivity);
	}

	public void testdismissTest() {
		mActivity = getActivity();

		mInstr = getInstrumentation();

		mInstr.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
		int menuItemID = mActivity.getOptionsMenu()
				.getItem(MenuItems.ABOUT.ordinal()).getItemId();
		assertTrue(mInstr.invokeMenuActionSync(mActivity, menuItemID, 0));
		assertTrue(solo.waitForActivity("AboutDialog", 500));
		while (!solo.getCurrentActivity().isTaskRoot()) {
			solo.goBack();
		}
		solo.goBack();
	}
}

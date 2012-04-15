package com.googlecode.iqapps.IQTimeSheet;

import android.test.ActivityInstrumentationTestCase2;

public class AboutDialogTest extends
		ActivityInstrumentationTestCase2<AboutDialog> {
	private static final String TAG = "AboutDialogTest";

	public AboutDialogTest(String pkg, Class<AboutDialog> activityClass) {
		super(pkg, activityClass);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void dismissTest() {
		sendKeys("ENTER");
	}
}

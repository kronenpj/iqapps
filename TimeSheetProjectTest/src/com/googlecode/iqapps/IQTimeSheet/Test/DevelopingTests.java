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
package com.googlecode.iqapps.IQTimeSheet.Test;

import static com.googlecode.autoandroid.positron.PositronAPI.Key.DOWN;
import static com.googlecode.autoandroid.positron.PositronAPI.Key.UP;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.autoandroid.positron.PositronAPI;
import com.googlecode.autoandroid.positron.junit4.TestCase;

public class DevelopingTests extends TestCase {
	private Log log = LogFactory.getLog(DevelopingTests.class);
	private static final String TAG = "TimeSheetActivityTest";

	@Before
	public void setUp() {
		prefBackup();
		startActivity("com.googlecode.iqapps.IQTimeSheet",
				"com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity");
	}

	@After
	public void tearDown() {
		finishAll();
		prefRestore();
	}

	@Test
	public void empty() {
		setAlignTimePreferenceViaMenu(1); // 2-minute
	}
	
	private void setAlignTimePreferenceViaMenu(int downCount) {
		press(PositronAPI.Key.MENU);
		long waitTime = 75;
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.RIGHT, PositronAPI.Key.RIGHT);
		Helpers.sleep(waitTime);
		press(PositronAPI.Key.ENTER);
		assertTrue(waitFor("class.simpleName", "MyPreferenceActivity", 500));
		press(PositronAPI.Key.ENTER);
		Helpers.sleep(waitTime);
		for (int i = 0; i < Helpers.alignments; i++)
			press(UP); // Up from any pre-set value.
		for (int i = 0; i < downCount; i++)
			press(DOWN);
		press(PositronAPI.Key.ENTER);
		Helpers.sleep(waitTime);
		for (int i = downCount; i < Helpers.alignments; i++)
			press(DOWN);
		press(DOWN, PositronAPI.Key.ENTER, PositronAPI.Key.BACK);
	}

}
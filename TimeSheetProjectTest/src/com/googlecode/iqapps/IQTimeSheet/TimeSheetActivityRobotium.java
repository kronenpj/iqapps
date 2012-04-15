///*
// * Copyright 2010 TimeSheet authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.googlecode.iqapps.IQTimeSheet;
//
//import android.test.ActivityInstrumentationTestCase2;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity;
//import com.jayway.android.robotium.solo.Solo;
//
//public class TimeSheetActivityRobotium extends
//		ActivityInstrumentationTestCase2<TimeSheetActivity> {
//  	private Log log = LogFactory.getLog(TimeSheetActivityRobotium.class);
//		private static final String TAG = "TimeSheetActivityRobotium";
//
//	private Solo solo;
//
//	public TimeSheetActivityTest() {
//		super("com.googlecode.iqapps.IQTimeSheet", TimeSheetActivity.class);
//	}
//
//	public void setUp() throws Exception {
//		solo = new Solo(getInstrumentation(), getActivity());
//	}
//
//	public void testTextIsSaved() throws Exception {
//		
//		solo.clickOnText("Other");
//		solo.clickOnButton("Edit");
//		assertTrue(solo.searchText("Edit Window"));
//		solo.enterText(1, "Some text for testing purposes");
//		solo.clickOnButton("Save");
//		assertTrue(solo.searchText("Changes have been made successfully"));
//		solo.clickOnButton("Ok");
//		assertTrue(solo.searchText("Some text for testing purposes"));
//	}
//
//	@Override
//	public void tearDown() throws Exception {
//
//		try {
//			solo.finalize();
//		} catch (Throwable e) {
//
//			e.printStackTrace();
//		}
//		getActivity().finish();
//		super.tearDown();
//
//	}
//}
///*
// * public class TimeSheetActivityTest extends TestCase {
// * 
// * @Before public void setUp() { backup(); sql("TimeSheetDB.db",
// * "DELETE FROM tasks; " + "INSERT INTO tasks (task, active, usage) " +
// * "VALUES ('Get some milk', 1, 50); " +
// * "INSERT INTO tasks (task, active, usage) " +
// * "VALUES ('Nap in the hammock', 1, 40, 0); " +
// * "INSERT INTO tasks (task, active, usage) " +
// * "VALUES ('Walk the allegator', 0, 30, 0);" +
// * "INSERT INTO tasks (task, active, usage) " +
// * "VALUES ('Feed the cat', 1, 20, 0);"); sql("TimeSheetDB.db",
// * "DELETE FROM timesheet; ");
// * startActivity("com.googlecode.iqapps.IQTimeSheet",
// * "com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity"); pause(); }
// * 
// * @After public void tearDown() { restore(); }
// * 
// * // http://code.google.com/p/autoandroid/issues/detail?id=7
// * 
// * @Ignore // Only so the test can be committed while working on something else
// * 
// * @Test public void shouldSelectFirstNoteWithArrowKeysEvenAfterFinishAll() {
// * press(DOWN); finishAll(); startActivity("com.googlecode.iqapps.IQTimeSheet",
// * "com.googlecode.iqapps.IQTimeSheet.TimeSheetActivity"); pause(); press(DOWN);
// * assertEquals(0, intAt("selectedItemPosition")); }
// * 
// * @Test public void verifyOrderOfEntries() { assertEquals("Get some milk",
// * stringAt("listView.0.text")); assertEquals("Nap in the hammock",
// * stringAt("listView.1.text")); assertEquals("Feed the cat",
// * stringAt("listView.2.text")); }
// * 
// * @Test public void shouldChangeSelectionWithTheArrowKeys() { press(DOWN, DOWN,
// * DOWN, UP); int selectedItemPosition = intAt("selectedItemPosition");
// * assertEquals("Nap in the hammock", stringAt(format("listView.%d.text",
// * selectedItemPosition))); }
// * 
// * // @Test // public void shouldSelectByTouchingAnEntry() { //
// * touch("listView.1"); // assertTrue(waitFor("class.simpleName", "NoteEditor",
// * 500)); // assertEquals("Nap in the hammock", stringAt("#note.text")); // }
// * 
// * }
// */
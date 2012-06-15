package com.googlecode.iqapps.IQTimeSheet.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.test.RenamingDelegatingContext;
import android.util.Log;

/**
 * Mock context that allows for a different set of data to be used during
 * testing.
 */
public class TimeSheetMockContext extends RenamingDelegatingContext {
	private static final String TAG = "TimeSheetMockContext";
	private static final String MOCK_FILE_PREFIX = "test.";

	/**
	 * @param context
	 * @param filePrefix
	 */
	public TimeSheetMockContext(Context context) {
		super(context, MOCK_FILE_PREFIX);
		makeExistingFilesAndDbsAccessible();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.test.RenamingDelegatingContext#openFileInput(java.lang.String)
	 */
	@Override
	public FileInputStream openFileInput(String name)
			throws FileNotFoundException {
		Log.d(TAG, "actual location of " + name + " is "
				+ getFileStreamPath(name));
		return super.openFileInput(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.test.RenamingDelegatingContext#openFileOutput(java.lang.String,
	 * int)
	 */
	@Override
	public FileOutputStream openFileOutput(String name, int mode)
			throws FileNotFoundException {
		Log.d(TAG, "actual location of " + name + " is "
				+ getFileStreamPath(name));
		return super.openFileOutput(name, mode);
	}
}

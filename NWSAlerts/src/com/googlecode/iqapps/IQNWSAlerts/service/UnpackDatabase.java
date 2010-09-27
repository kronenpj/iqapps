package com.googlecode.iqapps.IQNWSAlerts.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Environment;

public class UnpackDatabase {
	final static String DB_NAME = "county_corr.db";

	/**
	 * Extracts a file from the assets directory of the APK file.
	 * 
	 * @param mCtx
	 */
	public static void doUnpack(Context mCtx) {
		File dataDir = Environment.getDataDirectory();
		final String dbDestination = dataDir.getAbsolutePath() + "/databases/"
				+ DB_NAME;

		// Check if the database exists before copying
		boolean initialiseDatabase = !(new File(dbDestination)).exists();
		if (initialiseDatabase == false)
			return;

		// Open the .db file in your assets directory
		InputStream is;
		OutputStream os;
		try {
			is = mCtx.getAssets().open(DB_NAME);

			// Copy the database into the destination
			os = new FileOutputStream(dbDestination);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			os.flush();

			os.close();
			is.close();
		} catch (IOException e) {
		}
	}
}

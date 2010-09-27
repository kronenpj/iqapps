/*
 * Copyright 2010 NWSAlert authors.
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
package com.googlecode.iqapps.IQNWSAlerts;

/**
 * 
 * @author Paul Kronenwetter <kronenpj@gmail.com>
 * @author Neil http://stackoverflow.com/users/319625/user319625
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

// Class to export the current database to the SDcard.
public class SDUtils {
	private static final String TAG = "SDUtils";

	public static boolean writeToSD(String outputFileName, String packageName,
			String data) {
		return writeToSD(outputFileName, packageName, data.getBytes());
	}

	public static boolean writeToSD(String outputFileName, String packageName,
			byte[] data) {
		// FIXME: Make this a separate thread.
		// FIXME: Get the downloaded file to have the correct content... :(
		try {
			File sd = Environment.getExternalStorageDirectory();
			Log.d(TAG, "writeToSD: databaseName: " + outputFileName);

			if (sd.canWrite()) {
				String filePath = new String(packageName.substring(packageName
						.lastIndexOf('.') + 1));
				File extDBDir = new File(sd, filePath);
				File extDBFile = new File(sd, filePath + "/" + outputFileName);

				Log.d(TAG, "writeToSD: filePath: " + filePath);
				Log.d(TAG, "writeToSD: extDBFile: " + extDBFile);

				if (!extDBDir.exists()) {
					Log.d(TAG, "Creating directory: "
							+ extDBDir.getAbsolutePath());
					if (!extDBDir.mkdir()) {
						Log.e(TAG, "Directory creation failed.");
					}
				} else {
					Log.d(TAG, "writeToSD: Directory creation skipped.");
				}

				try {
					Log.d(TAG, "writeToSD: Creating new file: "
							+ extDBFile.getAbsolutePath());
					// extDBFile.delete();
					extDBFile.createNewFile();
				} catch (IOException e) {
					Log.d(TAG, "writeToSD: " + e.toString());
				} catch (SecurityException e) {
					Log.d(TAG, "writeToSD: " + e.toString());
				}

				// ByteBuffer src = ByteBuffer.wrap(data);
				// byte[] bSrc = src.array();
				// Log.d(TAG, "bSrc is " + bSrc.length + " bytes in size.");
				FileOutputStream fileOutStream = new FileOutputStream(extDBFile);
				// BufferedOutputStream bos = new BufferedOutputStream(
				// fileOutStream);

				// FileChannel dst = new
				// FileOutputStream(backupFile).getChannel();
				Log.d(TAG, "About to write to file.");
				fileOutStream.write(data);
				Log.d(TAG, "Wrote to file.");
				fileOutStream.flush();
				fileOutStream.close();
				Log.d(TAG, "Closed file.");

				extDBFile = new File(sd, filePath + "/" + outputFileName);
				if (!extDBFile.exists()) {
					Log.e(TAG, "Downloaded file doesn't exist.");
				}
				if (extDBFile.length() != data.length) {
					Log.e(TAG, "Downloaded file doesn't match size.");
				} else {
					Log
							.v(TAG, "File on SD card is size: "
									+ extDBFile.length());
				}

				return true;
			}
		} catch (Exception e) {
			Log.e(TAG, "WriteToSD threw exception: " + e.toString());
		}
		return false;
	}
}
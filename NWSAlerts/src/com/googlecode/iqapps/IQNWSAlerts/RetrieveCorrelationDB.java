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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;

import com.googlecode.iqapps.GetRequest;

public class RetrieveCorrelationDB {
	private static final String dbURL = "http://code.google.com/p/iqapps/";
	private static final String dbMeta = "corrDB.txt";
	private static final String dbFile = "county_corr.db";
	private static String lastFetch = "";

	static public boolean NewerAvailable() {
		String response = GetRequest.getRequest(dbURL + dbMeta);
		if (response == null) {
			// Assume no network is available and claim there's no update.
			return false;
		}

		File lastFile = new File(Environment.getExternalStorageDirectory(),
				"NWSAlerts/" + dbMeta);

		// See if the file exists, read it if it does.
		if (lastFile.exists()) {
			byte[] buffer = new byte[(int) lastFile.length()];
			try {
				BufferedInputStream f = new BufferedInputStream(
						new FileInputStream(lastFile));
				f.read(buffer);
			} catch (FileNotFoundException e) {
				buffer = new String("NoFile").getBytes();
			} catch (IOException e) {
				buffer = new String("NoFile").getBytes();
			} finally {
				lastFetch = new String(buffer);
			}
		} else {
			lastFetch = new String("NoFile");
		}

		/*
		 * Compare what's in the file to what was retrieved. If they're
		 * different, update the local file and return that there is an update
		 * available.
		 */
		if (response.equalsIgnoreCase(lastFetch)) {
			return false;
		} else {
			lastFile.delete();
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(lastFile);
				out.write(response.getBytes());
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			} finally {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
			return true;
		}
		// return true;
	}

	/**
	 * Retrieves the pre-defined dbFile from dbURL.
	 */
	static public void GetDBFileFromWeb() {
		byte[] response;
		response = GetRequest.getRequestBytes(dbURL + dbFile);
		if (response != null && response.length > 10) {
			SDUtils.writeToSD(dbFile, "NWSAlerts", response);
		}
	}
}
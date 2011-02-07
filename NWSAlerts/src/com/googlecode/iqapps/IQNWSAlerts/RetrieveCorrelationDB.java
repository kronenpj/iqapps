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
import com.googlecode.iqapps.Logger;

public class RetrieveCorrelationDB {
	private static final Logger logger = Logger
			.getLogger("RetrieveCorrelationDB");
	private static final String dbURL = "http://iqapps.googlecode.com/svn/wiki/";
	private static final String dbMeta = "corrDB.txt";
	private static final String dbFile = "county_corr.db";
	private static String lastFetch = "";

	static public boolean NewerAvailable() {
		logger.trace("In NewerAvailable.");
		String response = GetRequest.getRequest(dbURL + dbMeta);
		if (response == null) {
			// Assume no network is available and claim there's no update.
			return false;
		}

		File lastFile = new File(Environment.getExternalStorageDirectory(),
				CorrelationDbAdapter.externalSubdirectory + "/" + dbMeta);

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
				logger.debug(e.toString());
			} catch (IOException e) {
				logger.debug(e.toString());
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					logger.debug(e.toString());
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
		logger.trace("In GetDBFileFromWeb.");
		byte[] response = null;
		logger.debug("Trying to retrieve " + dbURL + dbFile);
		response = GetRequest.getRequestBytes(dbURL + dbFile);
		if (response != null && response.length > 10) {
			logger.debug("Retrieved " + response.length + " bytes.");
			logger.debug("Writing database to " + dbFile);
			SDUtils.writeToSD(dbFile,
					CorrelationDbAdapter.externalSubdirectory, response);
			// Maybe this'll make it easier to keep it around...
			// SDUtils.makeReadOnly(dbFile);
		} else {
			logger.debug("Invalid response from request.");
			if (response == null)
				logger.debug("Response is null.");
			else if (response.length < 11) {
				logger.debug("Response is 10 bytes or smaller ("
						+ response.length + " byte(s)).");
			}
		}
	}
}
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
package com.googlecode.iqapps.IQNWSAlerts.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.http.Header;

import android.os.Environment;
import android.util.Log;

import com.googlecode.iqapps.GetRequest;
import com.googlecode.iqapps.HeadRequest;

public class RetrieveAlerts {
	private static final String alertIndexURL = "http://www.weather.gov/alerts-beta/us.php?x=0";
	private static final String alertIndexBase = "http://www.weather.gov/alerts-beta";
	private static final String alertIndexSuffix = ".php?x=0";
	private static final String alertFile = "us_atom.xml";
	private static final String detailFile = "alertdetail.xml";
	private static String lastFetch = "foo";

	// private static final String alertIndexURL =
	// "http://www.weather.gov/alerts-beta/fl.php?x=0";

	static public boolean NewerAvailable() {
		Log.d("RetrieveAlerts", "NewerAvailable");
		// HttpClient client = new HttpClient();
		// HttpConnectionManager conManager = client.getHttpConnectionManager();
		// TODO: This should be a preference.
		// client.getHostConfiguration().setProxy("localhost", 3128);

		// HeadMethod method = new HeadMethod(alertIndexURL);
		//		  
		// Header[] response = null; String temp = null; try {
		// client.executeMethod(method); response = method.getResponseHeaders();
		//		  
		// if (method.getStatusCode() == HttpStatus.SC_OK) { if
		// (method.getResponseHeader("Expires") != null) temp =
		// method.getResponseHeader("Expires").getValue(); }
		//		  
		// } catch (IOException e) { e.printStackTrace(); } finally {
		// method.releaseConnection(); }
		//		  
		// boolean status = false; if (lastFetch != null) if
		// (lastFetch.equalsIgnoreCase(temp)) {
		// System.out.println("No Newer Alerts Available."); status = false; }
		// else { System.out.println("Newer Alerts Available."); status = true;
		// } lastFetch = new String(temp);
		//		  
		// return status;

		Header[] response = HeadRequest.headRequest(alertIndexURL);
		String temp = null;
		for (int i = 0; i < response.length; i++) {
			if (response[i].getName().equalsIgnoreCase("Expires")) {
				temp = response[i].getValue();
				if (lastFetch.equalsIgnoreCase(temp)) {
					return false;
				} else {
					lastFetch = new String(temp);
					return true;
				}
			}
		}
		return true;
	}

	static public String GetAlertIndex() {
		Log.d("RetrieveAlerts", "GetAlertIndex");
		// HttpClient client = new HttpClient();
		// HttpConnectionManager conManager = client.getHttpConnectionManager();
		// TODO: This should be a preference.
		// client.getHostConfiguration().setProxy("localhost", 3128);
		String response = null;

		// HttpMethod method = new GetMethod(alertIndexURL);
		//
		// try {
		// client.executeMethod(method);
		//
		// if (method.getStatusCode() == HttpStatus.SC_OK) {
		// response = method.getResponseBodyAsString();
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// } finally {
		// method.releaseConnection();
		// }

		response = GetRequest.getRequest(alertIndexURL);
		// SDBackup.doSDBackup("us_atom.xml", "NWSAlerts", response);
		return response;
	}

	static public String GetAlertIndex(String state) {
		Log.d("RetrieveAlerts", "GetAlertIndex state: " + state);
		String response = null;
		String URL = new String(alertIndexBase + "/" + state.toLowerCase()
				+ alertIndexSuffix);
		Log.d("RetrieveAlerts", "GetAlertIndex: URL: " + URL);
		if (NWSAlert.properties.getReadFromFile()) {
			File sd = Environment.getExternalStorageDirectory();
			String backupDBPath = new String("NWSAlerts");
			File alertFile = new File(sd, backupDBPath + "/"
					+ RetrieveAlerts.alertFile);
			try {
				byte[] buffer = new byte[(int) alertFile.length()];
				BufferedInputStream f = new BufferedInputStream(
						new FileInputStream(alertFile));
				f.read(buffer);
				response = new String(buffer);
			} catch (IOException e) {
				response = null;
			}
		} else
			response = GetRequest.getRequest(URL);
		// SDBackup.doSDBackup(state.toLowerCase() + "_atom.xml", "NWSAlerts",
		// response);
		return response;
	}

	static public String GetAlert(String URL) {
		Log.d("RetrieveAlerts", "GetAlert: URL: " + URL);
		String response = null;

		if (NWSAlert.properties.getReadFromFile()) {
			File sd = Environment.getExternalStorageDirectory();
			String backupDBPath = new String("NWSAlerts");
			File alertFile = new File(sd, backupDBPath + "/"
					+ RetrieveAlerts.detailFile);
			try {
				byte[] buffer = new byte[(int) alertFile.length()];
				BufferedInputStream f = new BufferedInputStream(
						new FileInputStream(alertFile));
				f.read(buffer);
				response = new String(buffer);
			} catch (IOException e) {
				response = null;
			}
		} else
			response = GetRequest.getRequest(URL);
		return response;
	}
}

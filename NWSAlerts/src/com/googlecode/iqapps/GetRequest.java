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
package com.googlecode.iqapps;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class GetRequest {
	private static final Logger logger = Logger.getLogger("GetRequest");

	static public String getRequest(String url) {
		return HttpHelper.request(getResponse(url));
	}

	static public byte[] getRequestBytes(String url) {
		return HttpHelper.requestBytes(getResponse(url));
	}

	static private HttpResponse getResponse(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (Exception ex) {
			logger.error("getResponse: " + ex.toString());
			return null;
		}
		StatusLine statusLine = response.getStatusLine();
		logger.debug("getResponse:  status: " + statusLine.getStatusCode());
		logger.debug("getResponse:  reason: " + statusLine.getReasonPhrase());
		return response;
	}
}

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

import java.util.Vector;

import android.content.Context;

import com.googlecode.iqapps.BoundingBox;
import com.googlecode.iqapps.Logger;

public class CtyCorrelationDB {
	private static Logger logger = Logger.getLogger("CtyCorrelationDB");
	private static CorrelationDbAdapter dbHandle;

	public CtyCorrelationDB(Context mCtx) {
		logger.trace("CtyCorrelationDb constructor.");
		// Often when invoking the service with a context, weird things occur.
		// dbHandle = new CorrelationDbAdapter(mCtx);
		dbHandle = new CorrelationDbAdapter();
		dbHandle.open();
	}

	public Vector<String> getFIPS(BoundingBox bbox) {
		Vector<String> fips = dbHandle.getFIPSCodes(bbox);

		return fips;
	}

	public String getState(String fipsLoc) {
		return dbHandle.getStateFromFIPS(fipsLoc);
	}
}

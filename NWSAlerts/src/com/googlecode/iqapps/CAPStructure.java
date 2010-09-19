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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class CAPStructure {
	private static Logger logger = Logger.getLogger("CAPStructure");
	private String URL, NWSID;
	private Vector<String> fips, polygon;
	private Calendar published, updated, effective, expires;
	private String category, status, event, urgency, severity, certainty,
			msgType;
	private String title, summary;

	/**
	 * Void constructor.
	 */
	public CAPStructure() {
		URL = null;
		NWSID = null;
		fips = new Vector<String>();
		polygon = new Vector<String>();
		published = null;
		updated = null;
		effective = null;
		expires = null;
		category = null;
		status = null;
		event = null;
		urgency = null;
		severity = null;
		certainty = null;
		title = null;
		summary = null;
		msgType = null;
	}

	/**
	 * Full constructor. Not used in this application, but too much work to
	 * waste.
	 * 
	 * @param uRL
	 * @param nWSID
	 * @param fips
	 * @param published
	 * @param updated
	 * @param effective
	 * @param expires
	 * @param category
	 * @param status
	 * @param event
	 * @param urgency
	 * @param severity
	 * @param certainty
	 * @param msgType
	 * @param title
	 * @param summary
	 */
	public CAPStructure(String uRL, String nWSID, Vector<String> fips,
			Vector<String> polygon, Calendar published, Calendar updated,
			Calendar effective, Calendar expires, String category,
			String status, String event, String urgency, String severity,
			String certainty, String msgType, String title, String summary) {
		URL = uRL;
		NWSID = nWSID;
		this.fips = fips;
		this.polygon = polygon;
		this.published = published;
		this.updated = updated;
		this.effective = effective;
		this.expires = expires;
		this.category = category;
		this.status = status;
		this.event = event;
		this.urgency = urgency;
		this.severity = severity;
		this.certainty = certainty;
		this.msgType = msgType;
		this.title = title;
		this.summary = summary;
	}

	/**
	 * @return the uRL
	 */
	public String getURL() {
		return URL;
	}

	/**
	 * @param uRL
	 *            the uRL to set
	 */
	public void setURL(String uRL) {
		URL = uRL;
	}

	/**
	 * @return the nWSID
	 */
	public String getNWSID() {
		return NWSID;
	}

	/**
	 * @param nWSID
	 *            the nWSID to set
	 */
	public void setNWSID(String nWSID) {
		NWSID = nWSID;
	}

	/**
	 * @return a copy of the fips vector
	 */
	public Vector<String> getFips() {
		return new Vector<String>(fips);
	}

	/**
	 * @param fips
	 *            the fips to copy
	 */
	public void setFips(Vector<String> fips) {
		this.fips = new Vector<String>(fips);
	}

	/**
	 * @param fips
	 *            the fips to parse and add
	 */
	public void addFips(String inFips) {
		// logger.debug("In addFips: " + inFips);
		// logger.debug("Before: " + fips);

		String[] values = inFips.split(" ");
		for (int l = 0; l < values.length; l++) {
			fips.add(values[l]);
		}
		// logger.debug("After : " + fips);
	}

	/**
	 * @return a copy of the polygon vector
	 */
	public Vector<String> getPolygon() {
		return new Vector<String>(polygon);
	}

	/**
	 * @param polygon
	 *            the polygon vector to copy
	 */
	public void setPolygon(Vector<String> polygon) {
		this.polygon = new Vector<String>(polygon);
	}

	/**
	 * @param polygon
	 *            the polygon to add
	 */
	public void addPolygon(String polygon) {
		this.polygon.add(polygon);
	}

	/**
	 * @return the published
	 */
	public Calendar getPublished() {
		return published;
	}

	/**
	 * @param published
	 *            the published to set
	 */
	public void setPublished(Calendar published) {
		this.published = published;
	}

	/**
	 * @return the updated
	 */
	public Calendar getUpdated() {
		return updated;
	}

	/**
	 * @param updated
	 *            the updated to set
	 */
	public void setUpdated(Calendar updated) {
		this.updated = updated;
	}

	/**
	 * @return the effective
	 */
	public Calendar getEffective() {
		return effective;
	}

	/**
	 * @param effective
	 *            the effective to set
	 */
	public void setEffective(Calendar effective) {
		this.effective = effective;
	}

	/**
	 * @return the expires
	 */
	public Calendar getExpires() {
		return expires;
	}

	/**
	 * @param expires
	 *            the expires to set
	 */
	public void setExpires(Calendar expires) {
		this.expires = expires;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @param event
	 *            the event to set
	 */
	public void setEvent(String event) {
		this.event = event;
	}

	/**
	 * @return the urgency
	 */
	public String getUrgency() {
		return urgency;
	}

	/**
	 * @param urgency
	 *            the urgency to set
	 */
	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	/**
	 * @return the severity
	 */
	public String getSeverity() {
		return severity;
	}

	/**
	 * @param severity
	 *            the severity to set
	 */
	public void setSeverity(String severity) {
		this.severity = severity;
	}

	/**
	 * @return the certainty
	 */
	public String getCertainty() {
		return certainty;
	}

	/**
	 * @param certainty
	 *            the certainty to set
	 */
	public void setCertainty(String certainty) {
		this.certainty = certainty;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary
	 *            the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @return the msgType
	 */
	public String getMsgType() {
		return msgType;
	}

	/**
	 * @param msgType
	 *            the msgType to set
	 */
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	/**
	 * Reconstruct a CAPStructure object from a serialized byte array.
	 * 
	 * @param bSer
	 * @return
	 */
	static public CAPStructure deserializeCAP(byte[] bSer) {
		ByteArrayInputStream bIn = new ByteArrayInputStream(bSer);
		ObjectInputStream oIn;
		CAPStructure temp = null;
		try {
			oIn = new ObjectInputStream(bIn);
			temp = (CAPStructure) oIn.readObject();
		} catch (StreamCorruptedException e) {
			logger.debug(e.toString());
		} catch (IOException e) {
			logger.debug(e.toString());
		} catch (ClassNotFoundException e) {
			logger.debug(e.toString());
		}
		return temp;
	}

	/**
	 * Deconstruct a CAPStructure object into a serialized byte array.
	 * 
	 * @param cap
	 * @return
	 */
	static public byte[] serializeCAP(CAPStructure cap) {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ObjectOutputStream oOut;
		try {
			oOut = new ObjectOutputStream(bOut);
		} catch (IOException e) {
			logger.error("Creating OutputStream: " + e.toString());
			return null;
		}
		try {
			oOut.writeObject(cap);
		} catch (IOException e) {
			logger.error("Writing ObjectOutputStream: " + e.toString());
			return null;
		}

		return bOut.toByteArray();
	}

	/**
	 * Override the default Object toString method to do something useful.
	 */
	public String toString() {
		Date pub, upd, eff, exp;
		pub = new Date(published.getTimeInMillis());
		upd = new Date(updated.getTimeInMillis());
		eff = new Date(effective.getTimeInMillis());
		exp = new Date(expires.getTimeInMillis());

		String out;
		out = new String("\n" + "URI   : " + URL + "\n" + "NWS ID : " + NWSID
				+ "\n" + "FIPS # : " + fips + "\n" + "Polygon: " + polygon
				+ "\n" + "Publs'd: " + pub + "\n" + "Upd.   : " + upd + "\n"
				+ "Eff.   : " + eff + "\n" + "Expires: " + exp + "\n"
				+ "Categ. : " + category + "\n" + "Status : " + status + "\n"
				+ "Event  : " + event + "\n" + "Urgency: " + urgency + "\n"
				+ "Severit: " + severity + "\n" + "Certain: " + certainty
				+ "\n" + "Title  : " + title + "\n" + "Summary: " + summary
				+ "\n" + "MesgTyp: " + msgType);
		// logger.trace(out);
		// out = new String("NWS ID : " + NWSID + "<br/>" + "FIPS # : " + fips
		// + "<br/>" + "Polygon: " + polygon + "<br/>" + "Publs'd: " + pub
		// + "<br/>" + "Upd.   : " + upd + "<br/>" + "Eff.   : " + eff
		// + "<br/>" + "Expires: " + exp + "<br/>" + "Categ. : "
		// + category + "<br/>" + "Status : " + status + "<br/>"
		// + "Event  : " + event + "<br/>" + "Urgency: " + urgency
		// + "<br/>" + "Severit: " + severity + "<br/>" + "Certain: "
		// + certainty + "<br/>" + "Title  : " + title + "<br/>"
		// + "Summary: " + summary + "<br/>" + "MesgTyp: " + msgType);
		return out;
	}
}

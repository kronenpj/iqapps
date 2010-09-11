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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.googlecode.iqapps.CAPStructure;
import com.googlecode.iqapps.Logger;

public class ParseAlertIndex {
	private static Logger logger = Logger.getLogger("ParseAlertIndex");
	final String[] nodeTypes = { "id", "cap:effective", "cap:expires",
			"cap:status", "cap:msgType", "cap:category", "cap:urgency",
			"cap:severity", "cap:certainty", "published", "updated", "value",
			"valueName", "title", "summary" };

	/**
	 * Helper routine to parse the dates used in the NWS alert XMl files into a
	 * Calendar object. Eg: 2010-08-22T03:46:00-05:00
	 */
	static Calendar parseNWSDate(String dateIn) {
		// logger.trace("In parseNWSDate.");
		Calendar out = GregorianCalendar.getInstance();

		try {
			int dateIdx = dateIn.indexOf('T');
			int timeIdx;
			timeIdx = dateIn.lastIndexOf('-');
			if (timeIdx == 0) {
				timeIdx = dateIn.lastIndexOf('+');
			}
			if (dateIdx < 1 || timeIdx < 1) {
				logger
						.error("Date/time not parsed correctly. (" + dateIn
								+ ")");
				return null;
			}
			String date = dateIn.substring(0, dateIdx);
			String time = dateIn.substring(dateIdx + 1, timeIdx);
			String timezone = dateIn.substring(timeIdx);

			int yearHyphen = date.indexOf('-');
			if (yearHyphen < 1) {
				logger.error("yearHyphen is negative. (" + date + ")");
				return null;
			}
			int year = Integer.parseInt(date.substring(0, yearHyphen));
			int month = Integer.parseInt(date.substring(yearHyphen + 1, date
					.lastIndexOf('-')));
			int day = Integer.parseInt(date
					.substring(date.lastIndexOf('-') + 1));

			int hourColon = time.indexOf(':');
			if (hourColon < 1) {
				logger.error("hourColon is negative. (" + time + ")");
				return null;
			}
			int hour = Integer.parseInt(time.substring(0, hourColon));
			int minute = Integer.parseInt(time.substring(hourColon + 1, time
					.lastIndexOf(':')));
			int second = Integer.parseInt(time
					.substring(time.lastIndexOf(':') + 1));

			TimeZone tz = TimeZone.getTimeZone("GMT" + timezone);
			out.clear();
			out.setTimeZone(tz);
			out.set(year, month - 1, day, hour, minute, second);
		} catch (StringIndexOutOfBoundsException e) {
			// Ignore
		}

		return out;
	}

	/**
	 * Processes the alert index from
	 * http://www.weather.gov/alerts-beta/us.php?x=1, passed in as a String into
	 * a CAPStructure to be analyzed.
	 * 
	 * @param document
	 *            Alert index in XML format from NWS.
	 * @return Vector of CAPStructure with parsed contents.
	 */
	public Vector<CAPStructure> processIndex(String document) {
		logger.trace("In processIndex.");
		AlertParser myAlertHandler = null;

		if (document == null) {
			logger.debug("Alert Index was not retrieved.");
			return null;
		}
		// logger.debug("Alert Index: \n" + document);
		ByteArrayInputStream docStream = new ByteArrayInputStream(document
				.getBytes());

		try {
			/* Get a SAXParser from the SAXPArserFactory. */
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();

			/* Get the XMLReader of the SAXParser we created. */
			XMLReader xmlReader = saxParser.getXMLReader();
			myAlertHandler = new AlertParser();
			xmlReader.setContentHandler(myAlertHandler);

			/* Parse the document. */
			xmlReader.parse(new InputSource(docStream));
		} catch (ParserConfigurationException e) {
			logger.debug("processIndex: " + e.toString());
			return null;
		} catch (SAXException e) {
			logger.debug("processIndex: " + e.toString());
			return null;
		} catch (IOException e) {
			logger.debug("processIndex: " + e.toString());
			return null;
		}

		// Vector<CAPStructure> retVector = myAlertHandler.getParsedData();
		// logger.debug("Returning Vector of size: " + retVector.size());
		return myAlertHandler.getParsedData();
	}

	/**
	 * Processes the alert index from
	 * http://www.weather.gov/alerts-beta/us.php?x=1, passed in as a String into
	 * a CAPStructure to be analyzed.
	 * 
	 * @param document
	 *            Alert index in XML format from NWS.
	 * @return Vector of CAPStructure with parsed contents.
	 */
	public Vector<CAPStructure> processIndex2(String document) {
		logger.trace("In processIndex2.");
		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;
		Document doc = null;

		if (document == null) {
			logger.debug("AlertIndex was not retrieved.");
			return null;
		}
		// logger.debug("AlertIndex: \n" + document);
		ByteArrayInputStream docStream = new ByteArrayInputStream(document
				.getBytes());
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(docStream);
		} catch (ParserConfigurationException e) {
			logger.error(e.toString());
			return null;
		} catch (SAXException e) {
			logger.info(e.toString());
			logger.debug(document);
			return null;
		} catch (IOException e) {
			logger.error(e.toString());
			return null;
		}

		doc.getDocumentElement().normalize();
		if (document == null) {
			logger.debug("AlertIndex was parsed correctly.");
			return null;
		}

		Vector<CAPStructure> caps = new Vector<CAPStructure>();

		HashMap<String, NodeList> docNodes = new HashMap<String, NodeList>();
		for (int i = 0; i < nodeTypes.length; i++) {
			NodeList nodeList = doc.getElementsByTagName(nodeTypes[i]);
			logger.debug(nodeTypes[i] + ": " + nodeList.getLength());
			docNodes.put(nodeTypes[i], nodeList);
		}

		NodeList nodeEffective = docNodes.get("cap:effective");
		for (int i = 0; i < nodeEffective.getLength(); i++) {
			CAPStructure capsTmp = new CAPStructure();

			NodeList nodeID = docNodes.get("id");
			// There's an extra link node in the file.
			// Kinda fragile, but it's what I have to work with...
			Node nLink = nodeID.item(i + 1);

			try {
				if (nLink != null) {
					String link = nLink.getNodeValue();
					logger.debug("link: " + link);
					capsTmp.setURL(link);
					logger.debug("link in capsTmp: " + capsTmp.getURL());
					String nwsID = new String(link
							.substring(link.indexOf('=') + 1));
					capsTmp.setNWSID(nwsID);
				}

				// This implementation sucks. There should be a better way of
				// doing
				// this, but between the setter/getter methodology and the
				// differing
				// quantities of nodes, it's a little complex for me right now.
				capsTmp.setCategory(docNodes.get("cap:category").item(i)
						.getNodeValue());
				capsTmp.setCertainty(docNodes.get("cap:certainty").item(i)
						.getNodeValue());
				capsTmp.setStatus(docNodes.get("cap:status").item(i)
						.getNodeValue());
				capsTmp.setMsgType(docNodes.get("cap:msgType").item(i)
						.getNodeValue());
				capsTmp.setUrgency(docNodes.get("cap:urgency").item(i)
						.getNodeValue());
				capsTmp.setSeverity(docNodes.get("cap:severity").item(i)
						.getNodeValue());
				capsTmp.setTitle(docNodes.get("title").item(i + 1)
						.getNodeValue());
				capsTmp.setSummary(docNodes.get("summary").item(i)
						.getNodeValue());

				capsTmp.setEffective(parseNWSDate(docNodes.get("cap:effective")
						.item(i).getNodeValue()));
				capsTmp.setExpires(parseNWSDate(docNodes.get("cap:expires")
						.item(i).getNodeValue()));
				capsTmp.setPublished(parseNWSDate(docNodes.get("published")
						.item(i).getNodeValue()));
				// There's an extra 'updated' node in the file.
				// Kinda fragile, but it's what I have to work with...
				capsTmp.setUpdated(parseNWSDate(docNodes.get("updated").item(
						i + 1).getNodeValue()));

				// Special handling for the misguided way that value is used in
				// the feed.
				for (int j = 0; j < 2; j++) {
					Node node = docNodes.get("valueName").item((2 * i) + j);
					try {
						String name = node.getNodeValue();
						if (name != null && name.equalsIgnoreCase("FIPS6")) {
							Node vNode = docNodes.get("value")
									.item((2 * i) + j);
							String value = vNode.getNodeValue();
							String[] values = value.split(" ");
							Vector<String> fipsVec = new Vector<String>();
							for (int l = 0; l < values.length; l++) {
								fipsVec.add(values[l]);
							}
							capsTmp.setFips(fipsVec);
						}
					} catch (NullPointerException e) {
					}
				}
				logger.trace("Added: " + capsTmp.getNWSID());
				caps.add(capsTmp);
			} catch (NullPointerException e) {
				logger.info("caps processing failed: " + e.toString());
				logger.info("caps processing failed: "
						+ e.getLocalizedMessage());
			}
		}

		return caps;
	}

	/**
	 * Completes the cap data from the specific alert. Specifically the Event
	 * and Polygons.
	 * 
	 * @param cap
	 *            Partially-complete CAP data.
	 * @return Completed CAP data.
	 */
	public CAPStructure fillCapDetail(CAPStructure cap) {
		logger.trace("In fillCapDetail.");
		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;
		Document doc = null;
		String URL = cap.getURL();
		String document = RetrieveAlerts.GetAlert(URL);

		ByteArrayInputStream docStream = new ByteArrayInputStream(document
				.getBytes());
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(docStream);
		} catch (ParserConfigurationException e) {
			System.err.println(e.toString());
			return null;
		} catch (SAXException e) {
			System.err.println(e.toString());
			return null;
		} catch (IOException e) {
			System.err.println(e.toString());
			return null;
		}

		doc.getDocumentElement().normalize();

		NodeList nodeList = null;
		Node node = null;
		String temp = null;

		nodeList = doc.getElementsByTagName("event");
		node = nodeList.item(0);
		temp = node.getNodeValue();
		cap.setEvent(temp);

		nodeList = doc.getElementsByTagName("polygon");
		for (int i = 0; i < nodeList.getLength(); i++) {
			node = nodeList.item(i);
			temp = node.getNodeValue();
			cap.addPolygon(temp);
		}

		return cap;
	}
}

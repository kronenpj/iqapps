package com.googlecode.iqapps.IQNWSAlerts.service;

import java.util.GregorianCalendar;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.googlecode.iqapps.CAPStructure;
import com.googlecode.iqapps.IQNWSAlerts.service.ParseAlertIndex;

public class AlertParser extends DefaultHandler {
	private static final String TAG = "AlertParser";
	private CAPStructure myCAPStructure = new CAPStructure();
	private Vector<CAPStructure> mCAPvector = new Vector<CAPStructure>();

	private boolean inEntry = false;
	private boolean inId = false;
	private boolean inGeocode = false;
	private boolean inValue = false;
	private boolean inCategory = false;
	private boolean inStatus = false;
	private boolean inExpires = false;
	private boolean inEffective = false;
	private boolean inUrgency = false;
	private boolean inCertainty = false;
	private boolean inSeverity = false;
	private boolean inPublished = false;
	private boolean inTitle = false;
	private boolean inSummary = false;
	private boolean inUpdated = false;

	/**
	 * Return a copy of the CAPStructure Vector
	 * 
	 * @return A copy of the CAPStructure Vector
	 */
	public Vector<CAPStructure> getParsedData() {
		return new Vector<CAPStructure>(this.mCAPvector);
	}

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void endDocument() throws SAXException {
	}

	/**
	 * Called each time an element is opened in the XMl document.
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		if (localName.equals("entry")) {
			this.inEntry = true;
			this.myCAPStructure = new CAPStructure();
		} else if (localName.equals("id")) {
			this.inId = true;
		} else if (localName.equals("geocode")) {
			this.inGeocode = true;
		} else if (localName.equals("value")) {
			this.inValue = true;
		} else if (localName.equals("cap:category")
				|| localName.equals("category")) {
			this.inCategory = true;
		} else if (localName.equals("cap:status") || localName.equals("status")) {
			this.inStatus = true;
		} else if (localName.equals("cap:expires")
				|| localName.equals("expires")) {
			this.inExpires = true;
		} else if (localName.equals("cap:effective")
				|| localName.equals("effective")) {
			this.inEffective = true;
		} else if (localName.equals("cap:urgency")
				|| localName.equals("urgency")) {
			this.inUrgency = true;
		} else if (localName.equals("cap:severity")
				|| localName.equals("severity")) {
			this.inSeverity = true;
		} else if (localName.equals("cap:certainty")
				|| localName.equals("certainty")) {
			this.inCertainty = true;
		} else if (localName.equals("published")) {
			this.inPublished = true;
		} else if (localName.equals("title")) {
			this.inTitle = true;
		} else if (localName.equals("summary")) {
			this.inSummary = true;
		} else if (localName.equals("updated")) {
			this.inUpdated = true;
		} else {
			// Log.d(TAG, "Falling through: " + localName);
			return;
		}
	}

	/**
	 * Called each time an element is closed in the XMl document.
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("entry")) {
			this.inEntry = false;
			mCAPvector.add(myCAPStructure);
			this.myCAPStructure = new CAPStructure();
		} else if (localName.equals("id")) {
			this.inId = false;
		} else if (localName.equals("geocode")) {
			this.inGeocode = false;
		} else if (localName.equals("value")) {
			this.inValue = false;
		} else if (localName.equals("cap:category")
				|| localName.equals("category")) {
			this.inCategory = false;
		} else if (localName.equals("cap:status") || localName.equals("status")) {
			this.inStatus = false;
		} else if (localName.equals("cap:expires")
				|| localName.equals("expires")) {
			this.inExpires = false;
		} else if (localName.equals("cap:effective")
				|| localName.equals("effective")) {
			this.inEffective = false;
		} else if (localName.equals("cap:urgency")
				|| localName.equals("urgency")) {
			this.inUrgency = false;
		} else if (localName.equals("cap:severity")
				|| localName.equals("severity")) {
			this.inSeverity = false;
		} else if (localName.equals("cap:certainty")
				|| localName.equals("certainty")) {
			this.inCertainty = false;
		} else if (localName.equals("published")) {
			this.inPublished = false;
		} else if (localName.equals("title")) {
			this.inTitle = false;
		} else if (localName.equals("summary")) {
			this.inSummary = false;
		} else if (localName.equals("updated")) {
			this.inUpdated = false;
		}
	}

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		if (this.inEntry) {
			if (this.inId) {
				String temp = new String(ch, start, length);
				int equalSign = 0;
				myCAPStructure.setURL(temp);
				try {
					equalSign = temp.indexOf('=');
					String ID = temp.substring(equalSign + 1);
					myCAPStructure.setNWSID(ID);
					// Log.d(TAG, "ID: " + ID);
				} catch (StringIndexOutOfBoundsException e) {
					// Ignore
					Log.d(TAG, "URL: " + temp);
					Log.d(TAG, "= index: " + equalSign);
					Log.d(TAG, "Invalid URL: Can't parse ID");
				}
			}
			if (this.inUpdated) {
				// Log.d(TAG, "In cap:updated.");
				GregorianCalendar date = (GregorianCalendar) ParseAlertIndex
						.parseNWSDate(new String(ch, start, length));
				myCAPStructure.setUpdated(date);
			}
			if (this.inPublished) {
				// Log.d(TAG, "In cap:published.");
				GregorianCalendar date = (GregorianCalendar) ParseAlertIndex
						.parseNWSDate(new String(ch, start, length));
				myCAPStructure.setPublished(date);
			}
			if (this.inTitle) {
				myCAPStructure.setTitle(new String(ch, start, length));
			}
			if (this.inSummary) {
				myCAPStructure.setSummary(new String(ch, start, length));
			}
			if (this.inEffective) {
				// Log.d(TAG, "In cap:effective.");
				GregorianCalendar date = (GregorianCalendar) ParseAlertIndex
						.parseNWSDate(new String(ch, start, length));
				myCAPStructure.setEffective(date);
			}
			if (this.inExpires) {
				// Log.d(TAG, "In cap:expires.");
				GregorianCalendar date = (GregorianCalendar) ParseAlertIndex
						.parseNWSDate(new String(ch, start, length));
				myCAPStructure.setExpires(date);
			}
			if (this.inStatus) {
				myCAPStructure.setStatus(new String(ch, start, length));
			}
			if (this.inCategory) {
				myCAPStructure.setCategory(new String(ch, start, length));
			}
			if (this.inUrgency) {
				myCAPStructure.setUrgency(new String(ch, start, length));
			}
			if (this.inSeverity) {
				myCAPStructure.setSeverity(new String(ch, start, length));
			}
			if (this.inCertainty) {
				myCAPStructure.setCertainty(new String(ch, start, length));
			}
			if (this.inValue && this.inGeocode) {
				String temp = new String(ch, start, length);
				// Log.d(TAG, "FIPS: " + temp);
				myCAPStructure.addFips(temp);
			}
		}
	}
}

/*
 * Example Chunk: <entry>
 * <id>http://www.weather.gov/alerts-beta/wwacapget.php?x=
 * AZ20100827141800PSRSpecia lWeatherStatementPSR20100827150000AZ</id>
 * <updated>2010-08-27T08:18:00-06:00</updated>
 * <published>2010-08-27T08:18:00-06:00</published> <author>
 * <name>w-nws.webmaster@noaa.gov</name> </author> <title>Special Weather
 * Statement issued August 27 at 8:18AM MDT expiring August 27 at 9:00AM MDT by
 * NWS Phoenix http://www.wrh.noaa.gov/Phoenix/</title> <link
 * href='http://www.weather.gov/alerts-beta/wwacapget.php?x=AZ20100827141800P
 * SRSpecialWeatherStatementPSR20100827150000AZ'/> <summary>THE NATIONAL WEATHER
 * SERVICE IN PHOENIX HAS ISSUED A SIGNIFICANT WEATHE R ADVISORY FOR... WEST
 * CENTRAL PINAL COUNTY IN SOUTH CENTRAL ARIZONA SOUTH CENTR AL MARICOPA COUNTY
 * IN SOUTH CENTRAL ARIZONA UNTIL 800 AM MST AT 717 AM MST...NAT IONAL WEATHER
 * SERVICE METEOROLOGISTS DETECTED A</summary>
 * <cap:effective>2010-08-27T08:18:00-06:00</cap:effective>
 * <cap:expires>2010-08-27T09:00:00-06:00</cap:expires>
 * <cap:status>Actual</cap:status> <cap:msgType>Alert</cap:msgType>
 * <cap:category>Met</cap:category> <cap:urgency>Expected</cap:urgency>
 * <cap:severity>Minor</cap:severity> <cap:certainty>Observed</cap:certainty>
 * <cap:areaDesc>Maricopa; Northwest and North Central Pinal County; Pinal;
 * Southwe st Maricopa County</cap:areaDesc> <cap:geocode>
 * <valueName>FIPS6</valueName> <value>004013 004021</value> </cap:geocode>
 * <cap:parameter> <valueName>VTEC</valueName> <value></value> </cap:parameter>
 * </entry>
 */
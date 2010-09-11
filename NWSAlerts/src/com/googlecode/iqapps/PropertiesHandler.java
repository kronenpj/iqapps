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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author kronenpj
 * 
 */
public class PropertiesHandler {
	protected final static Logger logger = Logger
			.getLogger(PropertiesHandler.class.getSimpleName());
	private Properties properties;
	private final String propertiesDefaultFile = "default.properties";
	private String propertiesFile;

	public PropertiesHandler() {
		logger.trace("In PropertiesHandler(void).");
		propertiesFile = new String(propertiesDefaultFile);
		loadProperties();
	}

	public PropertiesHandler(String file) {
		logger.trace("In PropertiesHandler(String).");
		propertiesFile = new String(file);
		loadProperties();
	}

	public String getProperty(String key) {
		logger.trace("In getProperty.");
		String temp = null;
		try {
			temp = new String(properties.getProperty(key));
		} catch (NullPointerException e) {
			// System.err.println(e.toString());
			// System.err.println("Key: " + key);
		}
		return temp;
	}

	public void setProperty(String key, String value) {
		logger.trace("In setProperty.");
		properties.setProperty(key, value);
	}

	public Properties getProperties() {
		logger.trace("In getProperties.");
		return new Properties(properties);
	}

	public String getPropertiesFile() {
		logger.trace("In getPropertiesFile.");
		return new String(propertiesFile);
	}

	public void savePropertiesFile() {
		logger.trace("In savePropertiesFile.");
		saveProperties();
	}

	public void setPropertiesFile(String propertiesFile) {
		logger.trace("In setPropertiesFile.");
		this.propertiesFile = new String(propertiesFile);
	}

	private void loadProperties() {
		logger.trace("In loadProperties.");
		FileInputStream fis;

		properties = new Properties();

		// If I can't get the properties from the listed file, create defaults.
		try {
			fis = new FileInputStream(propertiesFile);
			properties.loadFromXML(fis);
		} catch (Exception e) {
			setDefaultProperties();
			saveProperties();
		}
	}

	private void setDefaultProperties() {
		logger.trace("In setDefaultProperties.");
		properties.setProperty("jdbc.url", "county_corr.db");
		properties.setProperty("county.file", "bp01jn10.dbx");

		properties.setProperty("log4j.rootLogger", "ERROR, A1");
		properties.setProperty("log4j.appender.A1",
				"org.apache.log4j.ConsoleAppender");
		properties.setProperty("log4j.appender.A1.layout",
				"org.apache.log4j.PatternLayout");
		properties.setProperty("log4j.appender.A1.layout.ConversionPattern",
				"%-4r [%t] %c{1} - %m%n");
		properties.setProperty("log4j.logger.httpclient.wire", "ERROR");
		properties.setProperty("log4j.logger.org.apache", "ERROR");
		properties.setProperty("log4j.logger.org.xml", "ERROR");
		properties.setProperty("log4j.logger.org.w3c", "ERROR");
		properties.setProperty("log4j.logger.javax.xml", "ERROR");

	}

	private void saveProperties() {
		logger.trace("In saveProperties.");
		FileOutputStream out;
		// This is probably bad form.
		// TODO: Be more friendly to the person using this and don't stomp on
		// the file.
		try {
			out = new FileOutputStream(propertiesFile);
			properties.storeToXML(out, null);
		} catch (FileNotFoundException e) {
			System.err.println(e.toString());
		} catch (IOException e) {
			System.err.println(e.toString());
		}
	}
}

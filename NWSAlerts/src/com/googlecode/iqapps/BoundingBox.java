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

public class BoundingBox {
	public final static double earthRadius = 6371.0; // km
	// public final static double earthRadiusMiles = 3959.0; // mi
	public final static double earthRadiusMiles = earthRadius / 1.609344; // mi
	public static boolean metric = true;

	private double x1, y1;
	private double x2, y2;

	/**
	 * Constructor from another BoundingBox.
	 * 
	 * @param bbox
	 */
	public BoundingBox() {
		x1 = 0;
		y1 = 0;
		x2 = 0;
		y2 = 0;
	}

	/**
	 * Constructor from another BoundingBox.
	 * 
	 * @param bbox
	 */
	public BoundingBox(BoundingBox bbox) {
		x1 = bbox.x1();
		y1 = bbox.y1();
		x2 = bbox.x2();
		y2 = bbox.y2();
	}

	/**
	 * Constructor using two points.
	 * 
	 * @param loc1
	 * @param loc2
	 */
	public BoundingBox(Point2D.Double loc1, Point2D.Double loc2) {
		x1 = loc1.x;
		y1 = loc1.y;
		x2 = loc2.x;
		y2 = loc2.y;
	}

	/**
	 * Constructor using discrete coordinates.
	 * 
	 * @param paramX
	 * @param paramY
	 * @param paramX1
	 * @param paramY1
	 */
	public BoundingBox(double paramX, double paramY, double paramX1,
			double paramY1) {
		x1 = paramX;
		y1 = paramY;
		x2 = paramX1;
		y2 = paramY1;
	}

	/**
	 * Constructor created from discrete coordinates and a radius. Assumes a
	 * simple model of the Earth.
	 * 
	 * @param paramX
	 * @param paramY
	 * @param radius1
	 */
	public BoundingBox(double paramX, double paramY, double radius1) {
		BoundingBox bbox = boundingBox(paramX, paramY, radius1);

		x1 = bbox.x1();
		y1 = bbox.y1();
		x2 = bbox.x2();
		y2 = bbox.y2();
	}

	/**
	 * Constructor created from a point and a radius. Assumes a simple model of
	 * the Earth.
	 * 
	 * @param location
	 * @param radius1
	 */
	public BoundingBox(Point2D.Double location, double radius1) {
		BoundingBox bbox = boundingBox(location, radius1);

		x1 = bbox.x1();
		y1 = bbox.y1();
		x2 = bbox.x2();
		y2 = bbox.y2();
	}

	/**
	 * Getter function for the first pair of coordinates of the box.
	 * 
	 * @return The 2D point of the first coordinate of the box.
	 */
	public Point2D.Double getFirst() {
		return new Point2D.Double(x1, y1);
	}

	/**
	 * Getter function for the second pair of coordinates of the box.
	 * 
	 * @return The 2D point of the second coordinate of the box.
	 */
	public Point2D.Double getSecond() {
		return new Point2D.Double(x2, y2);
	}

	/**
	 * Getter function for the x coordinate of the first point on the box.
	 * 
	 * @return The x coordinate of the first point of the box.
	 */
	public double x1() {
		return x1;
	}

	/**
	 * Getter function for the y coordinate of the first point on the box.
	 * 
	 * @return The y coordinate of the first point of the box.
	 */
	public double y1() {
		return y1;
	}

	/**
	 * Getter function for the x coordinate of the second point on the box.
	 * 
	 * @return The x coordinate of the second point of the box.
	 */
	public double x2() {
		return x2;
	}

	/**
	 * Getter function for the y coordinate of the second point on the box.
	 * 
	 * @return The y coordinate of the second point of the box.
	 */
	public double y2() {
		return y2;
	}

	/**
	 * Setter method using another BoundingBox.
	 * 
	 * @param loc1
	 * @param loc2
	 */
	public void setBox(BoundingBox location) {
		x1 = location.x1();
		y1 = location.y1();
		x2 = location.x2();
		y2 = location.y2();
	}

	/**
	 * Setter method using two points.
	 * 
	 * @param loc1
	 * @param loc2
	 */
	public void setBox(Point2D.Double loc1, Point2D.Double loc2) {
		x1 = loc1.x;
		y1 = loc1.y;
		x2 = loc2.x;
		y2 = loc2.y;
	}

	/**
	 * Setter method using a point and a radius. Assumes a simple model of the
	 * Earth.
	 * 
	 * @param location
	 * @param radius
	 */
	public void setBox(Point2D.Double location, double radius) {
		BoundingBox bbox = boundingBox(location, radius);
		x1 = bbox.x1();
		y1 = bbox.y1();
		x2 = bbox.x2();
		y2 = bbox.y2();
	}

	/**
	 * Setter method using discrete coordinates for the points on the box.
	 * 
	 * @param pX1
	 * @param pY1
	 * @param pX2
	 * @param pY2
	 */
	public void setBox(double pX1, double pY1, double pX2, double pY2) {
		x1 = pX1;
		y1 = pY1;
		x2 = pX2;
		y2 = pY2;
	}

	/**
	 * Switches output to Metric units.
	 */
	public static void metric() {
		metric = true;
	}

	/**
	 * Switches output to English Imperial units.
	 */
	public static void imperial() {
		metric = false;
	}

	/**
	 * If the bounding box is using Metric units.
	 */
	public static boolean isMetric() {
		return metric;
	}

	/**
	 * Calculate the distance between the two points of the bounding box using
	 * the haversine formula.
	 * 
	 * @return Distance between two points along the Earth.
	 */
	public double distanceH() {
		return distanceH(x1, y1, x2, y2);
	}

	/**
	 * Calculate the distance between two points using the haversine formula.
	 * 
	 * @param boundingBox
	 * @return Distance between two points along the Earth.
	 */
	public static double distanceH(BoundingBox x) {
		return distanceH(x.x1(), x.y1(), x.x2(), x.y2());
	}

	/**
	 * Calculate the distance between two points using the haversine formula.
	 * 
	 * @param location1
	 * @param location2
	 * @return Distance between two points along the Earth.
	 */
	public static double distanceH(Point2D.Double location1,
			Point2D.Double location2) {
		return distanceH(location1.x, location1.y, location2.x, location2.y);
	}

	/**
	 * Calculate the distance between two points using the haversine formula.
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return Distance between two points along the Earth.
	 */
	public static double distanceH(double lat1, double lon1, double lat2,
			double lon2) {
		double eRadius = (metric ? earthRadius : earthRadiusMiles);
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = eRadius * c;
		return d;
	}

	/**
	 * Calculate the distance between two points of the bounding box using the
	 * spherical law of cosines formula.
	 * 
	 * @return Distance between two points along the Earth.
	 */
	public double distanceC() {
		return distanceC(x1, y1, x2, y2);
	}

	/**
	 * Calculate the distance between two points using the spherical law of
	 * cosines formula.
	 * 
	 * @param location1
	 * @param location2
	 * @return Distance between two points along the Earth.
	 */
	public static double distanceC(Point2D.Double location1,
			Point2D.Double location2) {
		return distanceC(location1.x, location1.y, location2.x, location2.y);
	}

	/**
	 * Calculate the distance between two points using the spherical law of
	 * cosines formula.
	 * 
	 * @param lat1 Latitude of point one
	 * @param lon1 Longitude of point one
	 * @param lat2 Latitude of point two
	 * @param lon2 Longitude of point two
	 * @return Distance between two points along the Earth.
	 */
	public static double distanceC(double lat1, double lon1, double lat2,
			double lon2) {
		double eRadius = (metric ? earthRadius : earthRadiusMiles);
		double d = Math.acos(Math.sin(Math.toRadians(lat1))
				* Math.sin(Math.toRadians(lat2))
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2))
				* Math.cos(Math.toRadians(lon2 - lon1)))
				* eRadius;
		return d;
	}

	/**
	 * Calculates coordinates (in degrees) of South-West & North-East boundary
	 * of a bounding box with a nominal radius of the provided value.
	 * 
	 * @param location
	 *            - Latitude, longitude in degrees of the center of the box.
	 * @param radius
	 *            - Nominal radius of the box in km.
	 * @return - Array of SW Lat, SW Lon, NE Lat, NE Lon coordinates of bounding
	 *         box
	 */
	public static BoundingBox boundingBox(Point2D.Double location, double radius) {
		return boundingBox(location.x, location.y, radius);
	}

	/**
	 * Calculates coordinates (in degrees) of South-West & North-East boundary
	 * of a bounding box with a nominal radius of the provided value.
	 * 
	 * @param lat
	 *            - Latitude in degrees of the center of the box.
	 * @param lon
	 *            - Longitude in degrees of the center of the box.
	 * @param radius
	 *            - Nominal radius of the box in km.
	 * @return - Array of SW Lat, SW Lon, NE Lat, NE Lon coordinates of bounding
	 *         box
	 */
	public static BoundingBox boundingBox(double lat, double lon, double radius) {
		double eRadius = (metric ? earthRadius : earthRadiusMiles);
		// Calculate the latitude of the box coordinates.
		double maxLat = lat + Math.toDegrees(radius / eRadius);
		double minLat = lat - Math.toDegrees(radius / eRadius);
		// Calculate the longitude of the box coordinates.
		// Compensate for degrees longitude getting smaller with increasing
		// latitude
		double maxLon = lon
				+ Math.toDegrees(radius / eRadius
						/ Math.cos(Math.toRadians(lat)));
		double minLon = lon
				- Math.toDegrees(radius / eRadius
						/ Math.cos(Math.toRadians(lat)));

		BoundingBox retBox = new BoundingBox(minLat, minLon, maxLat, maxLon);
		return retBox;
	}
}

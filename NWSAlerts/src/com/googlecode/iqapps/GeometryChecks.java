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

import java.util.Vector;

public class GeometryChecks {
	private static Logger logger = Logger.getLogger(GeometryChecks.class
			.getSimpleName());

	/**
	 * Tests whether the test point is within the ordered set of vertices
	 * specified.
	 * 
	 * @param vert
	 *            Array of vertices in order supplied by NWS.
	 * @param test
	 *            Point to be tested.
	 * @return True if the point is within the polygon supplied.
	 */
	public static boolean containsPointInPolygon(Vector<Point2D.Double> vert,
			Point2D.Double test) {
		int i, j;
		boolean c = false;
		for (i = 0, j = vert.size() - 1; i < vert.size(); j = i++) {
			if (((vert.elementAt(i).y > test.y) != (vert.elementAt(j).y > test.y))
					&& (test.x < (vert.elementAt(j).x - vert.elementAt(i).x)
							* (test.y - vert.elementAt(i).y)
							/ (vert.elementAt(j).y - vert.elementAt(i).y)
							+ vert.elementAt(i).x))
				c = !c;
		}
		return c;
	}

	/**
	 * Tests to see whether the test point is within the polygons supplied. The
	 * polygon format is "x1,y1 x2,y2 x3,y3 ...".
	 * 
	 * This method uses a simple even-odd ray-casting algorithm, which works
	 * well for NWS polygons since they are well-behaved.
	 * 
	 * See: http://en.wikipedia.org/wiki/Point_in_polygon
	 * 
	 * Code based on:
	 * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/
	 * pnpoly.html#The C Code
	 * 
	 * License statement: Copyright 1994-2006, W Randolph Franklin (WRF) You
	 * may use my material for non-profit research and education, provided that
	 * you credit me, and link back to my home page.
	 * 
	 * @param test
	 *            The point to be checked.
	 * @param polygons
	 *            A Vector of Strings containing x,y pairs forming a polygon.
	 * @return True if the test point is determined to be inside one of the
	 *         polygons.
	 */
	public static boolean checkPolygon(Point2D.Double test,
			Vector<String> polygons) {
		for (String poly : polygons) {
			if (poly == null || poly.indexOf(' ') > 1) {
				try {
					String[] polyA = poly.split(" ", 0);

					// If there was no polygon supplied, then just default to
					// yes.
					if (polyA.length <= 1) {
						logger.info("No polygon supplied, returning true.");
						return true;
					}

					Vector<Point2D.Double> polyV = new Vector<Point2D.Double>();
					// Loop through each point supplied and create an array,
					// parsing the strings into Doubles as we go.
					for (int i = 0; i < polyA.length; i++) {
						logger.debug("Polygon point: [" + i + "]" + polyA[i]);
						Point2D.Double point = new Point2D.Double();
						point.setLocation(Double.parseDouble(polyA[i]
								.substring(0, polyA[i].indexOf(','))), Double
								.parseDouble(polyA[i].substring(polyA[i]
										.indexOf(',') + 1)));
						polyV.add(point);
					}
					if (containsPointInPolygon(polyV, test)) {
						logger.info("Point is inside polygon.");
						return true;
					}
				} catch (NullPointerException e) {
					logger.debug(e.toString());
					logger.info("No polygon supplied, returning true.");
					return true;
				}
			} else {
				logger.info("No polygon supplied, returning true.");
				return true;
			}
		}
		return false;
	}
}

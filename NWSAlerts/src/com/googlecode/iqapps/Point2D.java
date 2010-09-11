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

public abstract class Point2D extends Object implements Cloneable {
	public static class Double extends Point2D {
		public double x, y;

		public Double() {
			x = 0.0;
			y = 0.0;
		}

		public Double(double x1, double y1) {
			x = x1;
			y = y1;
		}

		public void setLocation(java.lang.Double x1, java.lang.Double y1) {
			x = x1;
			y = y1;
		}
	}

}

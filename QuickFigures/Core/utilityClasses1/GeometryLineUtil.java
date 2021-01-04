/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Jan 4, 2021
 * Version: 2021.1
 */
package utilityClasses1;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

/**Performs simple geometry formulas*/
public class GeometryLineUtil {
	
	/**the slope of a line*/
	static double getSlope(Line2D l) {
		double rise=l.getY2()-l.getY1();
		double run=l.getX2()-l.getX1();
		return rise/run;
	}
	/**A midpoint*/
	static Point2D getMidpint(Line2D l) {
		double rise=l.getY2()+l.getY1();
		double run=l.getX2()+l.getX1();
		return new Point2D.Double(run/2, rise/2);
	}
	/**The length of a line*/
	static double length(Line2D l) {
		double rise=l.getY2()-l.getY1();
		double run=l.getX2()-l.getX1();
		return Math.sqrt(rise*rise+run*run);
	}
	
	/**creates a line that is a perpendicular bisector of the given line*/
	public static Line2D perpendicularBisector(Line2D l) {
		double m = getSlope(l);
		Point2D p = getMidpint(l);
		double len = length(l)/2;
		double theta = Math.atan(-1/m);
		Double p1 = new Point2D.Double(p.getX()+Math.cos(theta)*len,p.getY()+ Math.sin(theta)*len);
		Double p2 = new Point2D.Double(p.getX()-Math.cos(theta)*len, p.getY()-Math.sin(theta)*len);
		return new Line2D.Double(p1, p2);
	}
	
	
}

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
 * Date Modified: Nov 3, 2022
 * Version: 2023.1
 */
package graphicalObjects;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**Depending on the zoom level of used, the coordinate space
 * of the worksheet will differ from that of the Graphics2D object on which it is 
 * drawn. in order for the package to function correctly, 
 * this interface transforms numbers and shapes to account for the difference
 * between the two */
public interface CordinateConverter {
	
	/**converts from the internal coordinate system to zoomed version that is displayed*/
	double transformX(double ox);
	 double transformY(double oy);
	 Point2D transformP(Point2D op);
	 
	 /**converts from the zoomed on screen coordinates to the internal coordinates*/
		double unTransformX(double ox);
		double unTransformY(double oy);
		Point2D unTransformP(Point2D op);
	 
		/**returns the zoom level/magnification used*/
	double getMagnification();
	
	public AffineTransform getAffineTransform();
	
	/**scales the font from the internally stored size to the sizes needed to draw the item*/
	Font getScaledFont(Font font) ;
	/**scales the  stroke from the internally stored one to the size needed to draw the item*/
	BasicStroke getScaledStroke(BasicStroke bs) ;
	
	/**returns a copy with the x,y positions shifted*/
	CordinateConverter getCopyTranslated(int dx, int dy);
	
	/**returns a copy with the magnification multiplied by the given factor*/
	CordinateConverter getCopyScaled(double ds);
}
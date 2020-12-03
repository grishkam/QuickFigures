/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package graphicalObjects;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public interface CordinateConverter<ImageType> {
	
	/**converts from an absolute cordinate system to a display system*/
	double transformX(double ox);
	 double transformY(double oy);
	 Point2D transformP(Point2D op);
	double getMagnification();
	public AffineTransform getAffineTransform();
	Font getScaledFont(Font font) ;
	BasicStroke getScaledStroke(BasicStroke bs) ;
	
	double unTransformX(double ox);
	double unTransformY(double oy);
	Point2D unTransformP(Point2D op);
	
	CordinateConverter<?> getCopyTranslated(int dx, int dy);
}
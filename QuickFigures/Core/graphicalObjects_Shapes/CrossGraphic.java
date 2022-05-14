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
 * Date Modified: Jan 5, 2021
 * Version: 2022.1
 */
package graphicalObjects_Shapes;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import locatedObject.RectangleEdges;

/**Draws an X shape. work in progress*/
public class CrossGraphic extends SimpleLineGraphic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CrossGraphic(Rectangle rectangle) {
		super(rectangle);
	}
	
	public CrossGraphic(RectangularGraphic crossGraphic) {
		super(crossGraphic);
	}

	public CrossGraphic copy() {
		return new CrossGraphic(this);
	}
	
	/**implements a formula to produce a cross*/
	@Override
	public Shape getShape() {
		Path2D.Double path=new Path2D.Double();
		
	
		ArrayList<Point2D> loc = getEdgePoints();
		path.moveTo(loc.get(0).getX(),loc.get(0).getY());
		path.lineTo(loc.get(1).getX(),loc.get(1).getY());
		path.moveTo(loc.get(2).getX(),loc.get(2).getY());
		path.lineTo(loc.get(3).getX(),loc.get(3).getY());
		return new BasicStroke(1).createStrokedShape(path);
		
	}
	
	ArrayList<Point2D> getEdgePoints() {
		ArrayList<Point2D> output=new ArrayList<Point2D>();
		output.add(RectangleEdges.getLocation(RectangleEdges.LEFT, getRectangle()));
		output.add(RectangleEdges.getLocation(RectangleEdges.RIGHT, getRectangle()));
		output.add(RectangleEdges.getLocation(RectangleEdges.TOP, getRectangle()));
		output.add(RectangleEdges.getLocation(RectangleEdges.BOTTOM, getRectangle()));
		
		
		return output;
		
	}
	

}

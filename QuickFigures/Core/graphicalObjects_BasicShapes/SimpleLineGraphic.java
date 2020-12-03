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
package graphicalObjects_BasicShapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;

/**A diagonal line*/
public class SimpleLineGraphic extends RightTriangleGraphic {
	{name="Simple Line";}
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	
	public static SimpleLineGraphic blankShape(Rectangle r, Color c) {
		SimpleLineGraphic r1 = new SimpleLineGraphic(r);
		
		r1.setDashes(NEARLY_DASHLESS);
		r1.setStrokeWidth(THICK_STROKE_4);
		r1.setStrokeColor(c);
		return r1;
	}
	
	

	public SimpleLineGraphic copy() {
		return new SimpleLineGraphic(this);
	}
	
	public SimpleLineGraphic(Rectangle rectangle) {
		super(rectangle);
	}
	
	public SimpleLineGraphic(RectangularGraphic r) {
		super(r);
	}

	/**implements a formula to produce a line*/
	@Override
	public Shape getShape() {
		Path2D.Double path=new Path2D.Double();
		ArrayList<Point2D> loc = getTrianglePoints();
		path.moveTo(loc.get(0).getX(),loc.get(0).getY());
		path.lineTo(loc.get(1).getX(),loc.get(1).getY());
		return path;
		
	}
	

	RectangularGraphic rectForIcon() {
		SimpleLineGraphic ss = blankShape(new Rectangle(0,0,12,10), Color.BLACK);
		ss.setType(getType());
		return ss;
	}

	
	public void createShapeOnPathItem(ArtLayerRef aref, PathItemRef pi) {
		basicCreateShapeOnPathItem(	aref,pi);
	}


	/**The outline needed to determine if the user has clicked inside the shape or not
	  intricate series of stroked shapes for line outline needed*/
	@Override
	public Shape getOutline() {
		Shape shape = new BasicStroke(this.getStrokeWidth()).createStrokedShape(getShape());
		Area a=new Area(shape);
		a.add(new Area(new BasicStroke(12).createStrokedShape(a)));
		return a;
	}


	

}

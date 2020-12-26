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
package plotTools;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import graphicalObjects.CordinateConverter;
import graphicalObjects_BasicShapes.BasicShapeGraphic;
import utilityClassesForObjects.BasicStrokedItem;

public class ConnectorGraphic extends BasicShapeGraphic {

	Point2D[] anchors=new Point2D[] {new Point(), new Point(), new Point()};
	
	public ConnectorGraphic(Point2D... a) {
		super(new Rectangle());
		anchors=a;
		// TODO Auto-generated constructor stub
	}
	
	public Shape getShape() {
		Path2D path1 = new Path2D.Double();
		path1.moveTo(anchors[0].getX(), anchors[0].getY());
		path1.lineTo(anchors[0].getX(), anchors[1].getY());
		path1.lineTo(anchors[2].getX(), anchors[1].getY());
		path1.lineTo(anchors[2].getX(), anchors[2].getY());
		return path1;
	}
	
	public void drawHandesSelection(Graphics2D g2d, CordinateConverter<?> cords) {
		if (selected) {

			ArrayList<Point2D> anchors2=new ArrayList<Point2D>();
			for(Point2D a: anchors) anchors2.add(a);
			   getGrahpicUtil().drawHandlesAtPoints(g2d, cords,  anchors2);
			   handleBoxes=getGrahpicUtil().lastHandles;
		}
		
	}
	
	
	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		if (handlenum<anchors.length) anchors[handlenum].setLocation(p2);
		
		
			double nx=anchors[0].getX()+anchors[2].getX();
			nx/=2;
			anchors[1].setLocation(nx, anchors[1].getY());
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void moveLocation(double dx, double dy) {
		for(Point2D p: anchors) {
			p.setLocation(p.getX()+dx, p.getY()+dy);
		}
	}
	
	@Override
	public void scaleAbout(Point2D p, double mag) {

		for(Point2D anchor: anchors) {
			Point2D a = scaleAbout(anchor, p,mag,mag);
			anchor.setLocation(a);
		}
		
		BasicStrokedItem.scaleStrokeProps(this, mag);
	}
	
	/**The outline needed to determine if the user has clicked on this line or not
	  */
	@Override
	public Shape getOutline() {
		return  new BasicStroke(this.getStrokeWidth()+1).createStrokedShape(getShape());
	}

}

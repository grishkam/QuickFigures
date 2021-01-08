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
 * Date Modified: Jan 7, 2021
 * Version: 2021.1
 */
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
import graphicalObjects_Shapes.BasicShapeGraphic;
import handles.SmartHandleList;
import locatedObject.BasicStrokedItem;

/**A path consisting of three strait vertical or horizontal lines */
public class ConnectorGraphic extends BasicShapeGraphic {

	Point2D[] anchors=new Point2D[] {new Point(), new Point(), new Point()};
	private transient ConnectorHandleList smartHandles;
	
	public ConnectorGraphic(Point2D... a) {
		super(new Rectangle());
		anchors=a;
	}
	
	/**creates the shape*/
	public Shape getShape() {
		Path2D path1 = new Path2D.Double();
		path1.moveTo(getAnchors()[0].getX(), getAnchors()[0].getY());
		path1.lineTo(getAnchors()[0].getX(), getAnchors()[1].getY());
		path1.lineTo(getAnchors()[2].getX(), getAnchors()[1].getY());
		path1.lineTo(getAnchors()[2].getX(), getAnchors()[2].getY());
		return path1;
	}
	
	/**draws the handles and selection*/
	@Override
	public void drawHandesSelection(Graphics2D g2d, CordinateConverter cords) {
		if (selected) {

			ArrayList<Point2D> anchors2=new ArrayList<Point2D>();
			for(Point2D a: getAnchors()) anchors2.add(a);
			   getGrahpicUtil().drawHandlesAtPoints(g2d, cords,  anchors2);
			   handleBoxes=getGrahpicUtil().lastHandles;
		}
		
	}
	
	
	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		if (handlenum<getAnchors().length) getAnchors()[handlenum].setLocation(p2);
		
		
			double nx=getAnchors()[0].getX()+getAnchors()[2].getX();
			nx/=2;
			getAnchors()[1].setLocation(nx, getAnchors()[1].getY());
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void moveLocation(double dx, double dy) {
		for(Point2D p: getAnchors()) {
			p.setLocation(p.getX()+dx, p.getY()+dy);
		}
	}
	
	@Override
	public void scaleAbout(Point2D p, double mag) {

		for(Point2D anchor: getAnchors()) {
			Point2D a = scalePointAbout(anchor, p,mag,mag);
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
	
	/**returns the cull handle list*/
	@Override
	public SmartHandleList getSmartHandleList() {
		if (smartHandles==null)
			smartHandles=new ConnectorHandleList(this);
		return SmartHandleList.combindLists(smartHandles,super.getButtonList());
	}

	/**returns the anchor locations for the connector*/
	public Point2D[] getAnchors() {
		return anchors;
	}
	
	/**creates a copy*/
	public ConnectorGraphic copy() {
		
		ConnectorGraphic out = new ConnectorGraphic(new Point2D[anchors.length]);
		for(int i=0; i<anchors.length; i++) 
			{	out.anchors[i]=new Point2D.Double();
				out.anchors[i].setLocation(anchors[i]);
			}
		out.copyAttributesFrom(this);
		out.copyColorsFrom(this);
		return out;
	}

}

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
 * Date Modified: Nov 28, 2021
 * Version: 2021.2
 */
package plotParts.stats;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import javax.swing.Icon;

import graphicalObjects.CordinateConverter;
import graphicalObjects_Shapes.ShapeGraphic;
import handles.HasSmartHandles;
import handles.SmartHandleList;
import iconGraphicalObjects.IconTraits;
import locatedObject.BasicStrokedItem;
import locatedObject.RectangleEdges;
import locatedObject.Scales;
import plotTools.ConnectorHandleList;
import standardDialog.graphics.GraphicDisplayComponent;

/**A path consisting of strait vertical or horizontal lines with no curves */
public class ConnectorGraphic extends ShapeGraphic implements Scales, HasSmartHandles{

	Point2D[] anchors=new Point2D[] {new Point(), new Point(), new Point()};
	private transient ConnectorHandleList smartHandles;
	
	boolean horizontal=false;
	
	public ConnectorGraphic(boolean horizontal, Point2D... a) {
		this.setName("Line link");
		anchors=a;
		this.horizontal=horizontal;
	}
	
	/**creates the shape*/
	public Shape getShape() {
		
		if(getAnchors().length==3)
			return buildFrom3Anchors();
		else if(getAnchors().length==2)
			return buildFrom2Anchors();
		return null;
	}

	/**
	 * @return
	 */
	public Shape buildFrom3Anchors() {
		if(isHorizontal()) {
			Path2D path1 = new Path2D.Double();
			path1.moveTo(getAnchors()[0].getX(), getAnchors()[0].getY());
			path1.lineTo(getAnchors()[1].getX(), getAnchors()[0].getY());
			path1.lineTo(getAnchors()[1].getX(), getAnchors()[2].getY());
			path1.lineTo(getAnchors()[2].getX(), getAnchors()[2].getY());
			return path1;
		}
		else {
			Path2D path1 = new Path2D.Double();
			path1.moveTo(getAnchors()[0].getX(), getAnchors()[0].getY());
			path1.lineTo(getAnchors()[0].getX(), getAnchors()[1].getY());
			path1.lineTo(getAnchors()[2].getX(), getAnchors()[1].getY());
			path1.lineTo(getAnchors()[2].getX(), getAnchors()[2].getY());
			return path1;
		}
	}
	
	
	/**returns a strait line connector built from 1 or two 
	 * @return
	 */
	public Shape buildFrom2Anchors() {
		if(isHorizontal()) {
			Path2D path1 = new Path2D.Double();
			path1.moveTo(getAnchors()[0].getX(), getAnchors()[0].getY());
			path1.lineTo(getAnchors()[1].getX(), getAnchors()[0].getY());
			
			return path1;
		}
		else {
			Path2D path1 = new Path2D.Double();
			path1.moveTo(getAnchors()[0].getX(), getAnchors()[0].getY());
			path1.lineTo(getAnchors()[0].getX(), getAnchors()[1].getY());
			return path1;
		}
	}
	
	
	
	
	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		
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
		return  new BasicStroke(this.getStrokeWidth()+2).createStrokedShape(getShape());
	}
	
	
	/**returns the full handle list*/
	@Override
	public SmartHandleList getSmartHandleList() {
		if (smartHandles==null)
			smartHandles=new ConnectorHandleList(this);
		return smartHandles;
	}

	/**returns the anchor locations for the connector*/
	public Point2D[] getAnchors() {
		return anchors;
	}
	
	/**creates a copy*/
	public ConnectorGraphic copy() {
		
		ConnectorGraphic out = new ConnectorGraphic(this.isHorizontal(),new Point2D[anchors.length]);
		for(int i=0; i<anchors.length; i++) 
			{	out.anchors[i]=new Point2D.Double();
				out.anchors[i].setLocation(anchors[i]);
			}
		out.copyAttributesFrom(this);
		out.copyColorsFrom(this);
		return out;
	}

	public boolean isHorizontal() {
		return horizontal;
	}
	
	@Override
	public Rectangle getBounds() {
		return getShape().getBounds();
	}

	@Override
	public String getShapeName() {
		return "Line link";
	}
	
	@Override
	public int handleNumber(double x, double y) {
		return getSmartHandleList().handleNumberForClickPoint(x, y);
	}
	
	
	@Override
	public Icon getTreeIcon() {
		return new GraphicDisplayComponent(createIconArrow() );
		
	}
	
	/**creates a small arrow that is used as an icon for the arrow*/
	ConnectorGraphic createIconArrow() {
		ConnectorGraphic out = new ConnectorGraphic(true, new Point(0,IconTraits.TREE_ICON_HEIGHT/2-1), new Point(IconTraits.TREE_ICON_WIDTH-1,IconTraits.TREE_ICON_HEIGHT/2-1));
		out.copyColorsFrom(this);
		out.copyAttributesFrom(this);
		
		
		return out;
	}
	
	/**If the object is selected, draws the handles that the user may drag. 
	 * this is overwritten so that extra handles from the superclass dont appear*/
	public void drawHandesSelection(Graphics2D g2d, CordinateConverter cords) {
		if (selected &&!handlesHidden) {

			
		}
		
	}

}

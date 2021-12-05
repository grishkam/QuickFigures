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
 * Date Modified: Dec 4, 2021
 * Version: 2021.2
 */
package graphicalObjects_Shapes;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects.CordinateConverter;
import handles.ConnectorHandleList;
import handles.HasSmartHandles;
import handles.SmartHandleList;
import iconGraphicalObjects.IconTraits;
import illustratorScripts.ArtLayerRef;
import illustratorScripts.PathItemRef;
import locatedObject.BasicStrokedItem;
import locatedObject.PathPoint;
import locatedObject.PathPointList;
import locatedObject.Scales;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import popupMenusForComplexObjects.ConnectorMenu;
import popupMenusForComplexObjects.PathGraphicMenu;
import standardDialog.graphics.GraphicDisplayComponent;

/**A path consisting of strait vertical or horizontal lines with no curves.
 * Made to illustrate link between two datasets on a plot.
 * Works similarly to connectors in powerpoint */
public class ConnectorGraphic extends PathGraphic implements Scales, HasSmartHandles{

	private Point2D[] anchors=new Point2D[] {new Point(), new Point(), new Point()};
	private transient ConnectorHandleList smartHandles;
	
	private boolean horizontal=false;
	
	public ConnectorGraphic(boolean horizontal, Point2D... a) {
		this.setName("Line link");
		setAnchors(a);
		this.setHorizontal(horizontal);
		this.setFilled(false);
	}
	
	/**creates the shape*/
	public Shape getShape() {
		try {
			return updateShapeFromAnchors();
		} catch (Exception e) {
			IssueLog.log("problem drawing shape"+this.toString());
			IssueLog.logT(e);
			
		}
		return null;
	}

	/**
	 * @return
	 */
	public Shape updateShapeFromAnchors() {
		ArrayList<Point2D> updatedShape = buildFromAnchors();
		
		PathPointList points = this.getPoints();
		if(getPoints()!=null&&points.size()==updatedShape.size()) 
			{
			for(int i=0; i<updatedShape.size(); i++) {
				Point2D newP = updatedShape.get(i);
				PathPoint pathP = points.get(i);
				pathP.setAnchorPoint(newP);
				pathP.setCurveControl1(newP);
				pathP.setCurveControl2(newP);
				this.updatePathFromPoints();
			}
			} else {
		
			PathPointList newList = new PathPointList();
			for(Point2D p: updatedShape) {
				newList.addPoint(p);
			}
			this.setPoints(newList);
		}
		
		return getPath();
	}

	/**returns a list of points on the connector
	 * @return
	 */
	public ArrayList<Point2D> buildFromAnchors() {
		ArrayList<Point2D> updatedShape = null;
		if (getAnchors().length == 3)
			updatedShape = buildFrom3Anchors();
		else if (getAnchors().length == 2)
			updatedShape = buildFrom2Anchors();
		return updatedShape;
	}
	
	public String summaryString() {
		String output="Connection: ";
		for(Point2D p: anchors) {output+=p;}
		return output;
	}

	/**returns the Shape of the line based on the current anchors
	 * @return
	 */
	public ArrayList<Point2D> buildFrom3Anchors() {
		double x0 = getAnchors()[0].getX();
		double y0 = getAnchors()[0].getY();
		double x1 = getAnchors()[1].getX();
		double y1 = getAnchors()[1].getY();
		double y2 = getAnchors()[2].getY();
		double x2 = getAnchors()[2].getX();
		ArrayList<Point2D> thePath=new ArrayList<Point2D> ();
		if(isHorizontal()) {
			
			thePath.add(new Point2D.Double(x0, y0));
			thePath.add(new Point2D.Double(x1, y0));
			thePath.add(new Point2D.Double(x1, y2));
			thePath.add(new Point2D.Double(x2, y2));
			
		}
		else {
			
			thePath.add(new Point2D.Double(x0, y0));
		
			thePath.add(new Point2D.Double(x0, y1));
			thePath.add(new Point2D.Double(x2, y1));
			thePath.add(new Point2D.Double(x2, y2));
			
		}
		
		return thePath;
	}
	
	
	

	/**returns a strait line connector built from 1 or two 
	 * @return
	 */
	public ArrayList<Point2D> buildFrom2Anchors() {
		ArrayList<Point2D> thePath=new ArrayList<Point2D> ();
		if(isHorizontal()) {
			
			thePath.add(new Point2D.Double(getAnchors()[0].getX(), getAnchors()[0].getY()));
			thePath.add(new Point2D.Double(getAnchors()[1].getX(), getAnchors()[0].getY()));
			thePath.add(new Point2D.Double(getAnchors()[1].getX(), getAnchors()[1].getY()));
			
		}
		else {
			
			thePath.add(new Point2D.Double(getAnchors()[0].getX(), getAnchors()[0].getY()));
			thePath.add(new Point2D.Double(getAnchors()[0].getX(), getAnchors()[1].getY()));
			thePath.add(new Point2D.Double(getAnchors()[1].getX(), getAnchors()[1].getY()));
			
		}
		
		return thePath;
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
		return  new BasicStroke(this.getStrokeWidth()+2).createStrokedShape(super.getRotationTransformShape());
	}
	
	
	/**returns the full handle list*/
	@Override
	public SmartHandleList getSmartHandleList() {
		if (smartHandles==null)
			smartHandles=new ConnectorHandleList(this);
		return SmartHandleList.combindLists(smartHandles, getButtonList());
	}

	/**returns the anchor locations for the connector*/
	public Point2D[] getAnchors() {
		return anchors;
	}
	
	/**creates a copy*/
	public ConnectorGraphic copy() {
		
		ConnectorGraphic out = new ConnectorGraphic(this.isHorizontal(),this.copyAnchors());
		out.setName(getName());
		out.copyAttributesFrom(this);
		out.copyColorsFrom(this);
		
		if (this.arrowHead1!=null) out.arrowHead1=this.getArrowHead1().copy();
		if (this.arrowHead2!=null) out.arrowHead2=this.getArrowHead2().copy();
		
		
		return out;
	}

	public boolean isHorizontal() {
		return horizontal;
	}
	
	@Override
	public Rectangle getBounds() {
		Rectangle bounds = getShape().getBounds();
		if(bounds.width==0)
			bounds.width=1;//align options and possibly other features wont work if area is zero
		if (bounds.height==0)
			bounds.height=1;//align options and possibly other features wont work if area is zero
		return bounds;
	}

	@Override
	public String getShapeName() {
		if(this.getAnchors().length>2)
			return "Bracket Conector";
		return "Elbow Connector";
	}
	
	@Override
	public int handleNumber(double x, double y) {
		return getSmartHandleList().handleNumberForClickPoint(x, y);
	}
	
	
	@Override
	public Icon getTreeIcon() {
		return new GraphicDisplayComponent(createIconLine() );
		
	}
	
	/**creates a small arrow that is used as an icon for the arrow*/
	ConnectorGraphic createIconLine() {
		int hLevel = IconTraits.TREE_ICON_HEIGHT/3-1;
		int wLevel = IconTraits.TREE_ICON_WIDTH-1;
		ConnectorGraphic out = new ConnectorGraphic(true, new Point(0,hLevel-5), new Point(wLevel,hLevel+8));
		if(!isHorizontal())
			out = new ConnectorGraphic(false, new Point(wLevel/2-8,0), new Point(wLevel/2+8,hLevel+10));
		
		if(getAnchors().length>2) {
			out = new ConnectorGraphic(false, new Point(0,             hLevel+5), 
											 new Point(wLevel/2,      hLevel-4), 
											 new Point(wLevel,        hLevel+10));
			
		}
		
		if(getAnchors().length>2&&this.isHorizontal()) {
			out = new ConnectorGraphic(true, 
					new Point(6,0), 
					new Point(wLevel,hLevel*2),
					new Point(0,hLevel+10)
					);
			
		}
		
		
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

	public void setHorizontal(boolean horizontal) {
		this.horizontal = horizontal;
	}

	public void setAnchors(Point2D[] anchors) {
		this.anchors = anchors;
	}
	
	/**since this shape is niether filled nor rotatable*/
	@Override
	public boolean isFillable() {return false;}
	public boolean doesSetAngle() {return false;}
	public double getAngle() {return 0;}
	
	@Override
	public Point2D getLocationUpperLeft() {
		return this.getBounds().getLocation();
	}
	
	@Override
	public void setLocationUpperLeft(double x, double y) {
		Point2D px = getLocationUpperLeft();
		this.moveLocation(x-px.getX(), y-px.getY());
	}
	
	/**sets the location. does not trigger the listeners*/
	public void setLocation(double x,double y) {
		this.setLocationUpperLeft(x, y);
	}
	
	/**sets the location. does not trigger the listeners
	 * @return */
	public Point2D getLocation() {
		return getLocationUpperLeft();
	}
	
	
	/**returns the path point list*/
	public PathPointList getPoints() {
		if(super.getPoints()==null) {
			IssueLog.log("null path point list detected");
			this.updateShapeFromAnchors();
		}
		return super.getPoints();
	}
	
	
	/**override for the superclass*/
	@Override
	public Point2D getTransformPointsForPathGraphic(Point2D p) {
		return p;
	}
	
	
	/**overrides the suberclass*/
	@Override
	public AffineTransform getTransformForPathGraphic() {
		return  AffineTransform.getTranslateInstance(0,0);
	}

	/**returns the number of anchor points for this connector
	 * @return
	 */
	public int nAnchors() {
		return this.getAnchors().length;
	}
	
	/**determines what popup menu matches this shape*/
	public PopupMenuSupplier getMenuSupplier() {
		return new ConnectorMenu(this);
	}
	

	
	public boolean isDrawClosePoint() {
		return false;
	}
	
	
	/**since this subclass is already a path, its path copy is just a normal copy*/
	public PathGraphic createPathCopy() {
		
		PathGraphic createIdenticalPath = createIdenticalPath();
		createIdenticalPath.setLocation(0, 0);
		return createIdenticalPath;
	}
	
	

	/**returns a path that looks the same as this one
	 * @return
	 */
	public PathGraphic createIdenticalPath() {
		PathGraphic output = super.createHeadlessCopy();
		if (this.arrowHead1!=null) output.arrowHead1=this.getArrowHead1().copy();
		if (this.arrowHead2!=null) output.arrowHead2=this.getArrowHead2().copy();
		
		return output;
	}

	
	/**creates the object within an illustrator art layer*/
	public Object toIllustrator(ArtLayerRef aref) {
		return this.createPathCopy().toIllustrator(aref);
	}
	
	/**returns the location of the stroke handle*/
	public Point2D[] getStrokeHandlePoints() {
		/**this math places the handle at the edge of the stroke near the middle of the line*/
		
		Point2D location1 = this.buildFromAnchors().get(0);
		Point2D location2 = this.buildFromAnchors().get(1);
		return calculatePointsOnStrokeBetween(location1, location2);
	}
	
	
	/**returns a copy that lacks arrow heads
	 * @return
	 */
	public PathGraphic createHeadlessCopy() {
		 ConnectorGraphic output = new ConnectorGraphic(horizontal, copyAnchors());
		copyColorAttributeTo(output);
		
		return output;
	}

	/**returns a copy of the anchors
	 * @return
	 */
	Point2D[] copyAnchors() {
		Point2D[] output = new Point2D[anchors.length];
		for(int i=0; i<output.length; i++) output[i]=new Point2D.Double(anchors[i].getX(), anchors[i].getY());
		return output;
	}
	
}

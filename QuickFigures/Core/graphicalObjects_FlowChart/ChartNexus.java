/*******************************************************************************
 * Copyright (c) 2023 Gregory Mazo
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
 * Date Created: May 27, 2023
 * Date Modified: May 27, 2023
 * Version: 2023.2
 */
package graphicalObjects_FlowChart;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.undo.UndoableEdit;

import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicHolder;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import handles.HasHandles;
import handles.HasSmartHandles;
import handles.SmartHandle;
import handles.SmartHandleList;
import locatedObject.PathPoint;
import locatedObject.PathPointList;
import logging.IssueLog;
import undo.Edit;

/**
 
 * 
 */
public class ChartNexus extends BasicGraphicalObject implements GraphicHolder, HasSmartHandles, HasHandles{

	/**
	 * 
	 */
	
	public ArrayList<AnchorAttachment> attachments=new ArrayList<AnchorAttachment>();
	
	
	private static final long serialVersionUID = 1L;

	Point2D location=new Point2D.Double(1, 1);

	private ShapeGraphic shape;
	ShapeLabelTextGraphic label=new ShapeLabelTextGraphic("Item");

	private transient SmartHandleList smartHandleList;
	
	
	public ChartNexus(ShapeGraphic shape) {
		this.shape=shape;this.setName("Nexus");
		if (shape instanceof RectangularGraphic) {
			RectangularGraphic rect=(RectangularGraphic) shape;
			rect.customHandles=this;
		}
	}
	
	
	@Override
	public Point2D getLocationUpperLeft() {
		if(shape!=null)
			return shape.getLocationUpperLeft();
		return location;
	}

	@Override
	public void setLocationUpperLeft(double x, double y) {
		location=new Point2D.Double(x, y);

	}

	@Override
	public Rectangle getExtendedBounds() {
		int e=8;
		int d=e;
		
		Rectangle r = getDefaultShape().getBounds();
		if(shape!=null)
			r = shape.getBounds();
		Rectangle nr = new Rectangle(r);
		nr.x-=e;
		nr.y-=d;
		nr.width+=e*2;
		nr.height+=d*2;
		return nr;
		
	}
	
	

	@Override
	public Shape getOutline() {
		if(shape!=null)
			return getExtendedBounds();
		return getDefaultShape();
	}


	/**
	 * @return
	 */
	public Rectangle2D getDefaultShape() {
		return new Rectangle2D.Double(location.getX()-1, location.getY()-1, 2, 2);
	}

	@Override
	public Rectangle getBounds() {
		if(shape!=null)
			return shape.getBounds();
		return getDefaultShape().getBounds();
	}

	@Override
	public void draw(Graphics2D graphics, CordinateConverter cords) {
			ShapeLabelTextGraphic.updateLocation(shape, label);
	
			for(AnchorAttachment a: attachments)
				a.updateLocation();
			this.updatePathsFromPoints();
			
		
		for(ZoomableGraphic s: this.getAllHeldGraphics()) {s.draw(graphics, cords);}

	}

	/**
	 * updates the paths to reflect the changed in points
	 */
	private void updatePathsFromPoints() {
		for(AnchorAttachment a: attachments) {
			a.getPath().updatePathFromPoints();
		}
		
	}


	@Override
	public void showOptionsDialog() {
		// TODO Auto-generated method stub

	}

	@Override
	public void scaleAbout(Point2D p, double mag) {
		// TODO Auto-generated method stub

	}

	@Override
	public BasicGraphicalObject copy() {
		// TODO Auto-generated method stub
		return null;
	}

	
	

	@Override
	public SmartHandleList getSmartHandleList() {
		
		if(smartHandleList==null)
			smartHandleList = new SmartHandleList();
		boolean startsEmpty=smartHandleList.size()==0;
		ArrayList<Point2D> points = getAttachmentPoints(0.2);
		int j=0;
		for(Point2D p: points) {
			ChartNexusSmartHandle sm ;
			if(startsEmpty||j>=smartHandleList.size()) {
				sm = new ChartNexusSmartHandle(this);
				smartHandleList.add(sm);
			} else {
				sm = (ChartNexusSmartHandle) smartHandleList.get(j);
			}
			sm.setCordinateLocation(p);
			
			
			sm.setHandleNumber(1000+j);
			j++;
		}
		return smartHandleList;
	}


	@Override
	public ArrayList<ZoomableGraphic> getAllHeldGraphics() {
		ArrayList<ZoomableGraphic> output = new  ArrayList<ZoomableGraphic>();
		output.add(shape);
		output.add(label);
		return output;
	}
	
	/**gets the attachment points for the nexus*/
	public ArrayList<Point2D> getAttachmentPoints(double shift) {
		ArrayList<Point2D> output = new ArrayList<Point2D> ();
		if(shape==null)
			{
			output.add(location);
			return output;
			}
		Point center = shape.getCenter();
		PathPointList listedP = shape.createPathCopy().getPoints();
		
		for(int i=0; i<listedP.size(); i++) {
			PathPoint currentP = listedP.get(i);
			int next=i+1; if(next==listedP.size()) next=0;
			PathPoint nextP = listedP.get(next);
			output.add(currentP.getAnchor());
			output.add(PathGraphic.midPoint(currentP.getAnchor(), nextP.getAnchor()));
			
		}
		
		for(Point2D p: output) {
			Point2D bp = PathGraphic.betweenPoint(p, center, 1+shift);
			p.setLocation(bp);
		}
		
		return output;
		
	}

	@Override
	public int handleNumber(double x, double y) {
	
		int handleNumberForClickPoint = getSmartHandleList().handleNumberForClickPoint(x, y);
		
		return handleNumberForClickPoint;
	}
	
	
	/**
	 * @param aa
	 */
	public void addAttachment(AnchorAttachment aa) {
		attachments.add(aa);
		
	}
	
	/**returns the attachment object with the same index as this path point
	 * @param pathPoint
	 * @return 
	 */
	public AnchorAttachment getAttachmentforPoint(PathPoint pathPoint) {
		for(AnchorAttachment a: attachments) {
		
			boolean match = a.pathPoint==a.getPath().getPoints().indexOf(pathPoint);
			if(match)
				return a;
		}
		
		return null;
		
	}


	@Override
	public UndoableEdit requestDeleteOfHeldItem(Object z) {
		if(z==shape) {
			return Edit.removeItem(this);
		}
		return null;
	}
	
	/**returns the index of the nearest attachment point*/
	public int getNearestAttachmentPointIndex(Point2D p) {
		ArrayList<Point2D> points = getAttachmentPoints(0);
		
		Point2D nearestPoint = getNearest(points, p);
		int near_index = points.indexOf(nearestPoint);
		return near_index;
	}
	

	/** Returns the point from the list that is the nearest
 * @param points
 * @param coordinatePoint
 * @return
 */
private Point2D getNearest(ArrayList<Point2D> points, Point2D coordinatePoint) {
	/**returns the nearest point to the locaiton*/
	
		double distance=Double.MAX_VALUE;
		Point2D nearest=null;
		for(Point2D pp:points) {
			if (pp==null) continue;
			double d = pp.distance(coordinatePoint);
			if (d<distance) {
				distance=d;
				nearest=pp;
			}
		}
		
		return nearest;
		
	
}




}

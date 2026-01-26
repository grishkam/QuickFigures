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
 * Date Modified: July 7, 2023
 * Version: 2023.2
 */
package graphicalObjects_FlowChart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.undo.UndoableEdit;

import fLexibleUIKit.MenuItemExecuter;
import fLexibleUIKit.MenuItemMethod;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicHolder;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.RoundedRectangleGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import handles.HasHandles;
import handles.HasSmartHandles;
import handles.SmartHandle;
import handles.SmartHandleList;
import layersGUI.HasTreeLeafIcon;
import locatedObject.LocatedObject2D;
import locatedObject.LocationChangeListener;
import locatedObject.PathPoint;
import locatedObject.PathPointList;
import logging.IssueLog;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import menuUtil.SmartJMenu;
import popupMenusForComplexObjects.DonatesMenu;
import standardDialog.StandardDialog;
import standardDialog.graphics.GraphicDisplayComponent;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.Edit;

/**
 
 * 
 */
public class ChartNexus extends BasicGraphicalObject implements GraphicHolder, HasSmartHandles, HasHandles, HasUniquePopupMenu, DonatesMenu, LocationChangeListener, HasTreeLeafIcon{

	/**
	 * 
	 */
	
	//
	
	
	private static final long serialVersionUID = 1L;

	Point2D location=new Point2D.Double(1, 1);

	private ShapeGraphic shape;
	ShapeLabelTextGraphic label=new ShapeLabelTextGraphic("Item");

	private transient SmartHandleList smartHandleList;
	
	
	public ChartNexus(ShapeGraphic shape, String name) {
		this.setName(name);
		this.setShape(shape);
		
		label.getParagraph().get(0).get(0).setText(name);
		
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
	
			
			
		
		for(ZoomableGraphic s: this.getAllHeldGraphics()) {s.draw(graphics, cords);}

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

	public void resetHandleList() {
		smartHandleList=null;
	}
	

	@Override
	public SmartHandleList getSmartHandleList() {
		
		if(smartHandleList==null)
			smartHandleList = new SmartHandleList();
		boolean startsEmpty=smartHandleList.size()==0;
		ArrayList<Point2D> points = getAttachmentPoints(0.35);
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
		
		boolean includeMidPoint=true;
		if(shape instanceof RoundedRectangleGraphic) {
			includeMidPoint=false;
		}
		
		for(int i=0; i<listedP.size(); i++) {
			PathPoint currentP = listedP.get(i);
			int next=i+1; if(next==listedP.size()) next=0;
			PathPoint nextP = listedP.get(next);
			output.add(currentP.getAnchor());
			if(includeMidPoint)
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
		this.getFlowChart().addAttachment(aa);
		
	}
	
	/**
	 * @return
	 */
	FlowChart getFlowChart() {
		GraphicLayer parentLayer = this.getParentLayer();
		if(parentLayer==null)
			IssueLog.log("Chart nexus is not part of a flow chart layer");
		if(parentLayer instanceof FlowChart)
			return (FlowChart) getParentLayer();
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


	/**
	 * @param rectangleNew
	 * @return
	 */
	public RectangularGraphic createPartner(Rectangle rectangleNew) {
		ShapeGraphic copy=shape.copy();
		if(copy instanceof RectangularGraphic) {
			RectangularGraphic r=(RectangularGraphic) copy;
			r.setRectangle(rectangleNew);
			return r;
		}
		return new RectangularGraphic(rectangleNew);
	}



	public PopupMenuSupplier getMenuSupplier() {
		return new NexusMenu(this);
	}

	public RectangularGraphic getShapeAsRectangle() {
		if(shape instanceof RectangularGraphic) {
			return (RectangularGraphic) shape;
		}
		
		return null;
	}


	public ShapeGraphic getShape() {
		return shape;
	}


	public void setShape(ShapeGraphic shape) {
		if(this.shape!=null)
			this.shape.removeLocationChangeListener(this);
		this.shape=shape;
		shape.getTagHashMap().put(FlowChart.FLOW_CHART_PART, true);
		shape.getTagHashMap().put(FlowChart.FLOW_CHART_NEXUS, this);
		if (shape instanceof RectangularGraphic) {
			RectangularGraphic rect=(RectangularGraphic) shape;
			rect.customHandles=this;
			shape.addLocationChangeListener(this);
			resetHandleList();
			
		}
		
	}


	@Override
	public JMenu getDonatedMenuFor(Object requestor) {
		
		if(requestor==shape) {
			return getMenuForShape();
		}
		return null;
	}


	/**
	 * @return
	 */
	private SmartJMenu getMenuForShape() {
		SmartJMenu sm = new SmartJMenu("Chart");
		ShapeSwitchMenu shapeSwitchMenu = new ShapeSwitchMenu(getFlowChart(), this);
		sm.add(shapeSwitchMenu);
		new MenuItemExecuter(this).addToJMenu(sm);
		return sm;
	}


	@Override
	public void objectMoved(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void objectSizeChanged(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void objectEliminated(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void userMoved(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void userSizeChanged(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}


	/**returns the icon*/
	@Override
	public Icon getTreeIcon() {
		return new GraphicDisplayComponent(createIcon() );
	}
	
	/**creates a simple path that is used for the icon*/
	public static GraphicGroup createIcon() {
		RectangularGraphic output1 = new RectangularGraphic(new Rectangle2D.Double(0,1, 7,7));
		RectangularGraphic output2 = new RectangularGraphic(new Rectangle2D.Double(9,1, 7,7));
		ArrowGraphic a= new ArrowGraphic(new Point(2, 5), new Point(14, 5));
		a.setStrokeColor(Color.black);
		a.getHead(ArrowGraphic.FIRST_HEAD).setArrowHeadSize(3);
		a.setStrokeWidth(1);
		output1.setStrokeColor(Color.black);
		output2.setStrokeColor(Color.black);
		return new GraphicGroup(false, output1, output2, a);
	}
	
	
	/**A method to create a new nexus with a menu option*/
	@MenuItemMethod(menuText = "Add new nexus")
	public AbstractUndoableEdit2 createNewNexus() {
		return new ChartNexusSmartHandle(this).createNewNexus(StandardDialog.getNumberFromUser("Add how many new nodes to chart?", 1).intValue());
	}
	
	public String toString() {
		return label.toString();
	}

	
}

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
 * Date Created: May 25, 2023
 * Date Modified: July 7, 2023
 * Version: 2023.2
 */

/**
 
 * 
 */
package graphicalObjects_FlowChart;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.ToolbarTester;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import handles.SmartHandleForPathGraphic;
import handles.SmartHandleList;
import imageDisplayApp.ImageWindowAndDisplaySet;
import layout.BasicObjectListHandler;
import locatedObject.LocatedObject2D;
import locatedObject.PathPoint;
import locatedObject.PathPointList;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import undo.AbstractUndoableEdit2;
import undo.Edit;

/**
 A handle that can be used to move a line from one attachment site to another
 */
public class AnchoredHandle extends SmartHandleForPathGraphic {

	private AnchorObjectGraphic anchorPath;


	/**
	 * @param path
	 * @param point
	 */
	public AnchoredHandle(AnchorObjectGraphic path, PathPoint point) {
		super(path, point);
		anchorPath=path;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public static final int ATTACHMENT_CONTROL=33; 
	
	
	/**creates a list of points for the given path graphic*/
	public static SmartHandleList getPathSmartHandles(PathGraphic p) {
		SmartHandleList output = new SmartHandleList();
		PathPointList list = p.getPoints();
		for(int i=0; i<list.size(); i++) {
			locatedObject.PathPoint point = list.get(i);
			
			
			addHandlesFor(p, output, i, point);
			
		}
		return output;
	}
	
	
	/**Adds the smart handles for a specific point
	 * @param p
	 * @param output
	 * @param i
	 * @param point
	 */
	public static void addHandlesFor(PathGraphic p, SmartHandleList output, int i, locatedObject.PathPoint point) {
		AnchoredHandle handle;
		handle = new AnchoredHandle((AnchorObjectGraphic) p, point);
		handle.setUphandleType( CURVE_CONTROL_POINT1, i);
		output.add(handle);
		
		handle = new AnchoredHandle((AnchorObjectGraphic) p, point);
		handle.setUphandleType(CURVE_CONTROL_POINT2, i);
		output.add(handle);
		
		 handle = new AnchoredHandle((AnchorObjectGraphic) p, point);
		 if(i==0 ||i==p.getPoints().size()-1) {
			 handle.setUphandleType(ATTACHMENT_CONTROL, i);
		
			 }
		 else
			 handle.setUphandleType(ANCHOR_POINT, i);
		 
			output.add(handle);
	}
	
	/**returns the handle color*/
	public Color getHandleColor() {
		if(isAttachmentcontrol())
			return Color.lightGray;
		return super.getHandleColor();
	}
	
	/**What to do when a handle is moved from point p1 to p2.
	  */
	public void handleDrag(CanvasMouseEvent e ) {
		
		BasicObjectListHandler oh=new BasicObjectListHandler();
		Point coordinatePoint = e.getCoordinatePoint();
		ArrayList<LocatedObject2D> therois = oh.getAllClickedRoi(e.getAsDisplay().getImageAsWorksheet(),coordinatePoint, Object.class);
		if(isAttachmentcontrol())
		for(LocatedObject2D o: therois) {
			if(o instanceof ChartNexus) {
				ChartNexus r=(ChartNexus) o;
				
				onHandleDragToNexus(coordinatePoint, r);
				
				//IssueLog.log("Dragging over "+r);
				//IssueLog.log("attachment is "+p);
				
				
			}
		}
		
		super.handleDrag(e);
	}


	/**Called when the handle is near a specific nexus, anchors the point at a location on the 
	 * @param coordinatePoint
	 * @param r
	 */
	public void onHandleDragToNexus(Point coordinatePoint, ChartNexus r) {
		AnchorObjectGraphic theAnchorPath = anchorPath;
		PathPoint thePathPoint = pathPoint;
		
		AnchorAttachment.changeAttachmentLocation(coordinatePoint, r, theAnchorPath, thePathPoint);
	}




	/**
	 * @return
	 */
	public boolean isAttachmentcontrol() {
		return this.getPathHandleType()==ATTACHMENT_CONTROL;
	}
	
	
	



		public static void main(String[] args) {
			
			//	System.setProperty("apple.laf.useScreenMenuBar", "true");
		    //  System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Name");
			IssueLog.sytemprint=true;
			IssueLog.windowPrint=false;
			IssueLog.sytemprint=true;
			ToolbarTester.startToolbars(true);
			 ImageWindowAndDisplaySet ii = ImageWindowAndDisplaySet.createAndShowNew("Figure", 400,300);
			 
			 FlowChart fc = new FlowChart("flow chart");
			ii.getImageAsWorksheet().getTopLevelLayer().add(fc);
			
			RectangularGraphic r1 = new RectangularGraphic(new Rectangle(20,20, 50, 50));
			r1.setStrokeColor(Color.PINK);
			RectangularGraphic r2 = new RectangularGraphic(new Rectangle(120,60, 80, 50));
			r2.setStrokeColor(Color.green);
			
			
			ChartNexus cn = new ChartNexus(r2);
			ChartNexus cn2 = new ChartNexus(r1);
			fc.addItemToLayer(cn);
			fc.addItemToLayer(cn2);
		
			
			AnchorObjectGraphic line = new AnchorObjectGraphic(cn, cn2, null);
			
			Edit.addItem(cn.getParentLayer(),(ZoomableGraphic) line);
			
		}
		
		/**obsolete
		 * @return
		 */
		//@MenuItemMethod(menuActionCommand = "label", menuText = "Attach Label", subMenuName="more", orderRank=2)
		public AbstractUndoableEdit2 performLabel(RectangularGraphic targetShape) {
			TextGraphic textCopy = new ShapeLabelTextGraphic( targetShape.getName());//targetShape.createPathCopy();
			textCopy.setFontSize(targetShape.getBounds().height/4);
			textCopy.setLocationType(RectangleEdges.CENTER);
			textCopy.setLocation(targetShape.getCenter());
			return Edit.addItem(targetShape.getParentLayer(),(ZoomableGraphic) textCopy);
		}
		
		/**returns the location of this point*/
		public Point2D getCordinateLocation() {
			
			Point2D cordinateLocation = super.getCordinateLocation();
			if(this.isAttachmentcontrol())
				return new Point2D.Double(cordinateLocation.getX(), cordinateLocation.getY()-10);
			return cordinateLocation;
		}
		
		/**creates a single duplicate
		 * @return
		
		@MenuItemMethod(menuActionCommand = "label line", menuText = "Attach line", subMenuName="more", orderRank=3)
		public AbstractUndoableEdit2 performLine(RectangularGraphic targetShape) {
			AnchorObjectGraphic line = new AnchorObjectGraphic(targetShape, null);
			
			return Edit.addItem(targetShape.getParentLayer(),(ZoomableGraphic) line);
		} */
		
}

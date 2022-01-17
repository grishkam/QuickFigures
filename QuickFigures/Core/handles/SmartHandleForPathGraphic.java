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
 * Version: 2022.0
 */
package handles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import applicationAdapters.CanvasMouseEvent;
import graphicalObjects.CordinateConverter;
import graphicalObjects_Shapes.PathGraphic;
import locatedObject.PathPoint;
import locatedObject.PathPointList;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import pathGraphicToolFamily.AddRemoveAnchorPointTool;
import undo.AbstractUndoableEdit2;

/**This class of handles is for moving points in a path*/
public class SmartHandleForPathGraphic extends  SmartHandle {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**constant values for the type of point*/
	public static final int ANCHOR_POINT=0, CURVE_CONTROL_POINT1=1, CURVE_CONTROL_POINT2=2; 
	public static final int FACTOR_CODE=1000;//the maximum number of handles 
	private PathPoint pathPoint;
	private PathGraphic pathGraphic;
	
	int pointNumber=-1;//starts without a valid point number
	
	int type=ANCHOR_POINT;
	

	/**creates a list of points for the given path graphic*/
	public static SmartHandleList getPathSmartHandles(PathGraphic p) {
		SmartHandleList output = new SmartHandleList();
		PathPointList list = p.getPoints();
		for(int i=0; i<list.size(); i++) {
			locatedObject.PathPoint point = list.get(i);
			SmartHandleForPathGraphic handle;// = new SmartHandleForPathGraphic(p, point);
		
			
			handle = new SmartHandleForPathGraphic(p, point);
			handle.setUphandleType( CURVE_CONTROL_POINT1, i);
			output.add(handle);
			
			handle = new SmartHandleForPathGraphic(p, point);
			handle.setUphandleType(CURVE_CONTROL_POINT2, i);
			output.add(handle);
			
			 handle = new SmartHandleForPathGraphic(p, point);
				handle.setUphandleType(ANCHOR_POINT, i);
				output.add(handle);
			
		}
		return output;
	}
	
	
	
	/**constructs a handle for the given point
	 * @param path the path
	 * @param point the path point
	 * */
	public SmartHandleForPathGraphic(PathGraphic path, PathPoint point) {
		
		this.pathPoint=point;
		this.pathGraphic=path;
	}
	
	/**creates a handle of type type for the given path point
	 * @param number the index of the path point
	 * @param type the type of handle, anchor or curve control
	 * @param path the path
	 * @param point the path point*/
	public SmartHandleForPathGraphic(PathGraphic path, PathPoint point, int type, int number) {
		
		this.pathPoint=point;
		this.pathGraphic=path;
		
		setUphandleType(type, number);
	}

	/**creates a copy of the point*/
	static Point2D.Double copyPoint(Point2D p) {
		return new Point2D.Double(p.getX(), p.getY());
	}
	
	/**returns the location of this point*/
	public Point2D getCordinateLocation() {
		
		return getPathGraphicCordinateLocation(type);
	}
	
	/**gets the coordinate location of this point on the worksheet's coordinates
	 * this method takes into account that a Path object has a displacement*/
	private Point2D getPathGraphicCordinateLocation(int type) {
		Point2D p = copyPoint(getPathCordinateLocation(type));
		pathGraphic.getTransformForPathGraphic().transform(p, p);
		return p;
	}

	/**returns the coordinates of the path point as given in the path point list*/
	private Point2D getPathCordinateLocation(int type) {
		if (type==CURVE_CONTROL_POINT1) {
			return pathPoint.getCurveControl1();
		}
		if (type==CURVE_CONTROL_POINT2) {
			return pathPoint.getCurveControl2();
		}
		return pathPoint.getAnchor();
	}
	
	/**returns the handle color*/
	public Color getHandleColor() {
		if (type==CURVE_CONTROL_POINT1) {
			return Color.green;
		}
		if (type==CURVE_CONTROL_POINT2) {
			return Color.red;
		}
		if (pathPoint.isPrimarySelected()) {
			return Color.yellow;
			
		}
		if (pathPoint.isSelected()) {
			return Color.yellow;
		}
		return Color.gray;
	}
	
	/**returns true if the handle should be drawn as an ellipse*/
	public boolean isEllipseShape() {return pathPoint.isPrimarySelected()&&type==ANCHOR_POINT;}
	
	/**Draws a points and lines connecting them*/
	@Override
	public void draw(Graphics2D graphics, CordinateConverter cords) {
		super.draw(graphics, cords);		
		drawLinesConnectingPoints(graphics, cords);
		
		  drawMarkForClosePoint(graphics);
	}


	/**
	 * Draws lines that connect the curve control to the anchor points
	 * @param graphics
	 * @param cords
	 */
	private void drawLinesConnectingPoints(Graphics2D graphics, CordinateConverter cords) {
		Point2D thep  = cords.transformP(getPathGraphicCordinateLocation(0));
		Point2D thep1 = cords.transformP(getPathGraphicCordinateLocation(1));
		Point2D thep2 = cords.transformP(getPathGraphicCordinateLocation(2));  
	
		  graphics.setColor(Color.green.darker());
		if (this.type==CURVE_CONTROL_POINT1) drawLineOnGraphics(graphics, thep, thep1);
		  graphics.setColor(Color.red.darker());
		  if (this.type==CURVE_CONTROL_POINT2)  drawLineOnGraphics(graphics, thep, thep2);
	}

	/**draws a line from point 1 to point 2*/
	private void drawLineOnGraphics(Graphics2D graphics, Point2D p1, Point2D p2) {
		  graphics.setStroke(new BasicStroke(1));
		graphics.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
		
	}


	/**
	 * Draws a mark to indicate that the given point is a path close point
	 * @param graphics
	 */
	private void  drawMarkForClosePoint(Graphics2D graphics) {
		if (isAnchorPointHandle()&&(pathPoint.isClosePoint()||pathGraphic.getPoints().indexOf(pathPoint)==0)) {
			  graphics.setColor(Color.black);
			  graphics.setStroke(this.getHandleStroke());
			  Rectangle b = this.lastDrawShape.getBounds();
			  int y=(int)b.getCenterY();
			  graphics.drawLine(b.x,y, (int)b.getMaxX(),y);
			  
		  }
	}


	/**returns true if the this is an anchor point*/
	public boolean isAnchorPointHandle() {
		return type==ANCHOR_POINT;
	}

	/**initializes the handle id number for point number i*/
	public void setUphandleType(int type, int i) {
		this.type=type;
		if(type!=ANCHOR_POINT) handlesize+=1;
		this.setHandleNumber(i+type*FACTOR_CODE);
	}
	
	@Override
	public boolean isSelected() {
		return pathPoint.isSelected();
	}
	
	
	public boolean isPrimarySelected() {
		return pathPoint.isPrimarySelected()&&pathPoint.isSelected();
	}
	
	@Override
	public void select() {
		 pathPoint.select();
	}

	@Override
	public void deselect() {
		 pathPoint.deselect();
		 pathPoint.setPrimarySelected(false);
	}
	
	/**Sets a specific point as the primary selected point*/
	public void setPrimarySelected(PathPoint pSel) {
		for(PathPoint pEach: this.pathGraphic.getPoints()) {
			if(pEach==pSel) pSel.setPrimarySelected(true);
			else  pEach.setPrimarySelected(false);
		}
	}
	
	/**deselects all points except for the one given*/
	public void deslectAllExcept(PathPoint pSel) {
		for(PathPoint pEach: this.pathGraphic.getPoints()) {
			if(pEach==pSel) {}
			else  pEach.deselect();
		}
	}
	
	@Override
	public void handlePress(CanvasMouseEvent e){
		
		if(e.clickCount()==2) {pathPoint.deselect();clearReshapeHandles(); return;}
		else if (!isSelected()) {pathPoint.deselect();}
		else if (e.shiftDown()) {pathPoint.deselect();clearReshapeHandles();return;}//clicking on a selected point with shift down will deselect it
		
		if(!pathPoint.isSelected())
			{
				pathPoint.select();
				clearReshapeHandles();
			}
		
		if(pathPoint.isSelected())  setPrimarySelected(pathPoint);
		
		if(!e.shiftDown()) {
			deslectAllExcept(pathPoint);//unless ths user is holding shift to select multiple points, others should not be selected
		} 
		pathGraphic.selectedsegmentindex=pathGraphic.getPoints().indexOf(pathPoint);
		
	}



	/**
	called after there is a change to which points are selected. In this case the 
	handle list used to move the selected points is no longer valid and can be eliminated
	 */
	private void clearReshapeHandles() {
		pathGraphic.reshapeListForSelectedPoints=null;
	}
	
	
	
	/**What to do when a handle is moved from point p1 to p2.
	  */
	public void handleDrag(CanvasMouseEvent e ) {
		Point2D p2=e.getCoordinatePoint();
		try{
		
		java.awt.geom.Point2D.Double p =pathGraphic.convertPointToInternalCrdinates(p2);
		boolean b = e.altKeyDown()&&e.isControlDown();
		if (isAnchorPointHandle()||e.shiftDown()) {
			
			
			java.awt.geom.Point2D.Double pAnchor = pathPoint.getAnchor();
			
			double dx=p.getX()-pAnchor.getX();
			double dy=p.getY()-pAnchor.getY();
			if (pathGraphic.getHandleMode()==PathGraphic.MOVE_ALL_SELECTED_HANDLES||e.shiftDown()) {
				PathPointList pts = pathGraphic.getPoints().getSelectedPointsOnly();
				for(locatedObject.PathPoint point: pts) {
					point.move(dx, dy);
				}
				if (!pathPoint.isSelected())pathPoint.move(dx, dy);
			}
			else
				pathPoint.move(dx, dy);
		} else {
			boolean linkedHandles = this.getHandleMode()==PathGraphic.CURVE_CONTROL_HANDLES_LINKED;
			
			boolean symetricHandles = this.getHandleMode()==PathGraphic.CURVE_CONTROL_SYMETRIC_MODE;
			if(!linkedHandles)symetricHandles=false;
			
			
			if (type==CURVE_CONTROL_POINT1) {
			
				pathPoint.setCurveControl(p);
				if (linkedHandles||e.altKeyDown()) pathPoint.makePointAlongLine(true);
				
				if (symetricHandles||b) { pathPoint.makePointAlongLine(true);pathPoint.makePointEquidistantFromAnchor(true);}
			}
			else
			if (type==CURVE_CONTROL_POINT2) {
				
				pathPoint.setCurveControl2(p);
				if (linkedHandles||e.altKeyDown()) pathPoint.makePointAlongLine(false);
				if (symetricHandles||b) { pathPoint.makePointAlongLine(false);pathPoint.makePointEquidistantFromAnchor(false);}
				
			}
		}
		
		
		pathGraphic.updatePathFromPoints();
		
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}



	private int getHandleMode() {
		// TODO Auto-generated method stub
		return pathGraphic.getHandleMode();
	}
	
	
	@Override
	public boolean isHidden() {
		if (isAnchorPointHandle()) 
			return false;
		if (pathGraphic.getHandleMode()==PathGraphic.CURVE_CONTROL_HANDLES_LINKED&&pathGraphic.selectedsegmentindex== getPointNumber()) return false;
		if (pathGraphic.getHandleMode()==PathGraphic.CURVE_CONTROL_HANDLES_LINKED) return false;
		if (isACurveControl() && !pathGraphic.isCurvemode()) return true;
		if (type==CURVE_CONTROL_POINT2 && !pathGraphic.isSuperCurveControlMode()) return true;
		if (type==CURVE_CONTROL_POINT1&&!isPrimarySelected()) {
			return true;
		}
		if (type==CURVE_CONTROL_POINT2 &&!isPrimarySelected()) {
			return true;
		}
		
		return false;
	}
	
	/**returns the point number of the handle*/
	private int getPointNumber() {
		int num=this.getHandleNumber()-type*FACTOR_CODE;
	
		return num;
	}


	/**returns true if this handle is for a curve control point*/
	boolean isACurveControl() {
		if (isAnchorPointHandle()) return false;
		return true;
	}
	
	
	public JPopupMenu getJPopup() {
		return new PathHandlePopup();
	}

	/**A simple popup menu for the handle
	 * TODO: test the undo*/
	private class PathHandlePopup extends SmartPopupJMenu implements ActionListener {

		/**
		 * 
		 */
		
		String closePointCom="closedPoint", selPointCom= "selectP", allUnSel="Unselect All";
		String uncurveCom="Remove Curvature";
		String removePoint="Remove Point", addPoint="Add Point";
		
		
		private static final long serialVersionUID = 1L;
		public PathHandlePopup() {
			
			String closePoint= pathPoint.isClosePoint()? "Unclose Path":"Close Path";
			addMenuItem(closePoint, closePointCom);
			
			String select= pathPoint.isSelected()? "Unselect Point":"Select Point";
			
			addMenuItem(select, selPointCom);
			
		
			addMenuItem( allUnSel,  allUnSel);
			addMenuItem( uncurveCom,  uncurveCom);
			addMenuItem(removePoint,  removePoint);
			addMenuItem(addPoint,  addPoint);
		}
		
		public void addMenuItem(String item, String itemComm) {
			JMenuItem output = new JMenuItem();
			this.add(output)	;
			output.addActionListener(this);
			output.setText(item);
			output.setActionCommand(itemComm);
		}
		
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			AbstractUndoableEdit2 undo = pathGraphic.provideDragEdit();
			String com=arg0.getActionCommand();
			if (com.equals(closePointCom)) {
				pathPoint.setClosePoint(!pathPoint.isClosePoint());
				pathGraphic.updatePathFromPoints();
			}
			if (com.equals(selPointCom)) {
				if (pathPoint.isSelected()) pathPoint.deselect();
				else pathPoint.select();
				pathGraphic.updatePathFromPoints();
			}
			if (com.equals( allUnSel)) {
				PathPointList pp = pathGraphic.getPoints();
				pp.deselectAll();
			}
			if (com.equals(uncurveCom)) {
				pathPoint.setCurveControl1(pathPoint.getAnchor());
				pathPoint.setCurveControl2(pathPoint.getAnchor());
				pathGraphic.updatePathFromPoints();
			}
			
			if (com.equals(removePoint)) {
				pathGraphic.getPoints().remove(pathPoint);
				pathGraphic.updatePathFromPoints();
			}
			
			if (com.equals(addPoint)) {
				new AddRemoveAnchorPointTool(false).addOrRemovePointAtLocation(pathGraphic, false, super.getMemoryOfMouseEvent().getCoordinatePoint());
			}
			super.getUndoManager().addEdit(undo);
			
		}
	
}
	


	
}

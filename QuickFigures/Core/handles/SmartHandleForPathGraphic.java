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
import menuUtil.SmartPopupJMenu;
import pathGraphicToolFamily.AddRemoveAnchorPointTool;
import undo.AbstractUndoableEdit2;

/**This class of handles is for moving points in a path*/
public class SmartHandleForPathGraphic extends  SmartHandle {
	
	
	
	public static final int ANCHOR_POINT=0, CURVE_CONTROL_POINT1=1, CURVE_CONTROL_POINT2=2,  FACTOR_CODE=1000;
	private PathPoint pathPoint;
	private PathGraphic pathGraphic;
	
	int pointNumber=-1;
	
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
	
	
	
	/**constructs a handle for the given point*/
	public SmartHandleForPathGraphic(PathGraphic p, PathPoint point) {
		
		this.pathPoint=point;
		this.pathGraphic=p;
	}
	
	public SmartHandleForPathGraphic(PathGraphic p, PathPoint point, int type, int number) {
		
		this.pathPoint=point;
		this.pathGraphic=p;
		
		setUphandleType(type, number);
	}

	
	static Point2D copyPoint(Point2D p) {
		return new Point2D.Double(p.getX(), p.getY());
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Point2D getCordinateLocation() {
		
		return getPathGraphicCordinateLocation(type);
	}
	
	public Point2D getPathGraphicCordinateLocation(int type) {
		Point2D p = copyPoint(getPathCordinateLocation(type));
		pathGraphic.getTransformForPathGraphic().transform(p, p);
		return p;
	}

	
	private Point2D getPathCordinateLocation(int type) {
		if (type==CURVE_CONTROL_POINT1) {
			return pathPoint.getCurveControl1();
		}
		if (type==CURVE_CONTROL_POINT2) {
			return pathPoint.getCurveControl2();
		}
		return pathPoint.getAnchor();
	}
	

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
	
	public void setPrimarySelected(PathPoint pSel) {
		for(PathPoint pEach: this.pathGraphic.getPoints()) {
			if(pEach==pSel) pSel.setPrimarySelected(true);
			else  pEach.setPrimarySelected(false);
		}
	}
	
	/**deselects all point except for the selected one*/
	public void deslectAllExcept(PathPoint pSel) {
		for(PathPoint pEach: this.pathGraphic.getPoints()) {
			if(pEach==pSel) {}
			else  pEach.deselect();
		}
	}
	
	@Override
	public void handlePress(CanvasMouseEvent e){
		
		if(e.clickCount()==2) {pathPoint.deselect(); return;}
		else if (!isSelected()) {pathPoint.deselect();}
		else if (e.shfitDown()) {pathPoint.deselect();return;}
		
		if(!pathPoint.isSelected())
			{pathPoint.select();
			pathGraphic.reshapeList2=null;
			}
		
		if(pathPoint.isSelected())  setPrimarySelected(pathPoint);
		
		if(!e.shfitDown()) {
			deslectAllExcept(pathPoint);
		} 
		pathGraphic.selectedsegmentindex=pathGraphic.getPoints().indexOf(pathPoint);
		
	}
	
	
	
	/**What to do when a handle is moved from point p1 to p2.
	  */
	public void handleDrag(CanvasMouseEvent e ) {
		Point2D p2=e.getCoordinatePoint();
		try{
		
		java.awt.geom.Point2D.Double p =pathGraphic.convertPointToInternalCrdinates(p2);
		boolean b = e.altKeyDown()&&e.isControlDown();
		if (isAnchorPointHandle()||e.shfitDown()) {
			
			
			java.awt.geom.Point2D.Double pAnchor = pathPoint.getAnchor();
			
			double dx=p.getX()-pAnchor.getX();
			double dy=p.getY()-pAnchor.getY();
			if (pathGraphic.getHandleMode()==PathGraphic.MOVE_ALL_SELECTED_HANDLES||e.shfitDown()) {
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
			t.printStackTrace();
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

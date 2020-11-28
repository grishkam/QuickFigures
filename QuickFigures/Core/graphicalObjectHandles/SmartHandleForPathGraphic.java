package graphicalObjectHandles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import applicationAdapters.CanvasMouseEventWrapper;
import graphicalObjects.CordinateConverter;
import graphicalObjects_BasicShapes.PathGraphic;
import menuUtil.SmartPopupJMenu;
import pathGraphicToolFamily.AddRemoveAnchorPointTool;
import utilityClassesForObjects.PathPoint;
import utilityClassesForObjects.PathPointList;

public class SmartHandleForPathGraphic extends  SmartHandle {
	
	
	static int factorCode=1000;
	
	public static int anchorP=0, curveControl1=1, curveControl2=2;
	private PathPoint pathPoint;
	private PathGraphic pathGraphic;
	
	int pointNumber=-1;
	
	int type=anchorP;
	
	
	//public SmartHandleForPathGraphic getCurveControlForAnchor(){}
	
	/**creates a list of points for the given path graphic*/
	public static SmartHandleList getPathSmartHandles(PathGraphic p) {
		SmartHandleList output = new SmartHandleList();
		PathPointList list = p.getPoints();
		for(int i=0; i<list.size(); i++) {
			utilityClassesForObjects.PathPoint point = list.get(i);
			SmartHandleForPathGraphic handle;// = new SmartHandleForPathGraphic(p, point);
		
			
			handle = new SmartHandleForPathGraphic(p, point);
			handle.setUphandleType(1, i);
			output.add(handle);
			
			handle = new SmartHandleForPathGraphic(p, point);
			handle.setUphandleType(2, i);
			output.add(handle);
			
			 handle = new SmartHandleForPathGraphic(p, point);
				handle.setUphandleType(0, i);
				output.add(handle);
			
		}
		return output;
	}
	
	
	

	public SmartHandleForPathGraphic(PathGraphic p, PathPoint point) {
		super(0, 0);
		this.pathPoint=point;
		this.pathGraphic=p;
		// TODO Auto-generated constructor stub
	}
	
	public SmartHandleForPathGraphic(PathGraphic p, PathPoint point, int type, int number) {
		super(0, 0);
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
		if (type==curveControl1) {
			return pathPoint.getCurveControl1();
		}
		if (type==curveControl2) {
			return pathPoint.getCurveControl2();
		}
		return pathPoint.getAnchor();
	}
	

	public Color getHandleColor() {
		if (type==curveControl1) {
			return Color.green;
		}
		if (type==curveControl2) {
			return Color.red;
		}
		if (pathPoint.isPrimarySelected()) {
			 
			return Color.yellow;
			
		} else setEllipseShape(false);
		if (pathPoint.isSelected()) {
			return Color.yellow;
		}
		return Color.gray;
	}
	
	public boolean isEllipseShape() {return pathPoint.isPrimarySelected()&&type==anchorP;}
	
	@Override
	public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
		super.draw(graphics, cords);
		
		 
	
		  graphics.setColor(Color.green.darker());
		Point2D thep  = cords.transformP(getPathGraphicCordinateLocation(0));
		Point2D thep1 = cords.transformP(getPathGraphicCordinateLocation(1));
		Point2D thep2 = cords.transformP(getPathGraphicCordinateLocation(2));  
	
		if (this.type==curveControl1) drawLineOnGraphics(graphics, thep, thep1);
		  graphics.setColor(Color.red.darker());
		  if (this.type==curveControl2)  drawLineOnGraphics(graphics, thep, thep2);
		
		  if (isAnchorPointHandle()&&(pathPoint.isClosePoint()||pathGraphic.getPoints().indexOf(pathPoint)==0)) {
			  graphics.setColor(Color.black);
			  graphics.setStroke(this.getHandleStroke());
			  Rectangle b = this.lastDrawShape.getBounds();
			  int y=(int)b.getCenterY();
			  graphics.drawLine(b.x,y, (int)b.getMaxX(),y);
			  
		  }
		  
		if (this.isSelected()) {
			
		}
	}



	public boolean isAnchorPointHandle() {
		return type==anchorP;
	}

	
	
	
	public void drawLineOnGraphics(Graphics2D graphics, Point2D p1, Point2D p2) {
		  graphics.setStroke(new BasicStroke(1));
		graphics.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
		
	}
	
	
	public void setUphandleType(int type, int i) {
		this.type=type;
		if(type!=anchorP) handlesize+=1;
		this.setHandleNumber(i+type*factorCode);
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
	
	public void deslectAllExcept(PathPoint pSel) {
		for(PathPoint pEach: this.pathGraphic.getPoints()) {
			if(pEach==pSel) {}
			else  pEach.deselect();
		}
	}
	
	@Override
	public void handlePress(CanvasMouseEventWrapper e){
		
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
	public void handleDrag(CanvasMouseEventWrapper e ) {
		Point2D p2=e.getCoordinatePoint();
		try{
		
		//java.awt.geom.Point2D.Double p = new Point2D.Double();
		//pathGraphic.getTransformForPathGraphic().inverseTransform(p2, p);
		java.awt.geom.Point2D.Double p =pathGraphic.convertPointToInternalCrdinates(p2);
		boolean b = e.altKeyDown()&&e.isControlDown();
		if (isAnchorPointHandle()||e.shfitDown()) {
			
			
			java.awt.geom.Point2D.Double pAnchor = pathPoint.getAnchor();
			
			double dx=p.getX()-pAnchor.getX();
			double dy=p.getY()-pAnchor.getY();
			if (pathGraphic.getHandleMode()==PathGraphic.MOVE_ALL_SELECTED_HANDLES||e.shfitDown()) {
				PathPointList pts = pathGraphic.getPoints().getSelectedPointsOnly();
				for(utilityClassesForObjects.PathPoint point: pts) {
					point.move(dx, dy);
				}
			}
			else
			pathPoint.move(dx, dy);
		} else {
			boolean linkedHandles = this.getHandleMode()==PathGraphic.CURVE_CONTROL_HANDLES_LINKED;
			
			boolean symetricHandles = this.getHandleMode()==PathGraphic.CURVE_CONTROL_SYMETRIC_MODE;
			if(!linkedHandles)symetricHandles=false;
			
			
			if (type==curveControl1) {
			
				pathPoint.setCurveControl(p);
				if (linkedHandles||e.altKeyDown()) pathPoint.makePointAlongLine(true);
				
				if (symetricHandles||b) { pathPoint.makePointAlongLine(true);pathPoint.makePointEquidistantFromAnchor(true);}
			}
			else
			if (type==curveControl2) {
				
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
		// TODO Auto-generated method stub
		if (isAnchorPointHandle()) return false;
		if (pathGraphic.getHandleMode()==PathGraphic.CURVE_CONTROL_HANDLES_LINKED&&pathGraphic.selectedsegmentindex== getPointNumber()) return false;
		if (pathGraphic.getHandleMode()==PathGraphic.CURVE_CONTROL_HANDLES_LINKED) return false;
		if (isACurveControl() && !pathGraphic.isCurvemode()) return true;
		if (type==curveControl2 && !pathGraphic.isSuperCurveControlMode()) return true;
		if (type==curveControl1&&!isPrimarySelected()) {
			return true;
		}
		if (type==curveControl2 &&!isPrimarySelected()) {
			return true;
		}
		
		return false;
	}
	
	private int getPointNumber() {
		// TODO Auto-generated method stub
		int num=this.getHandleNumber()-type*factorCode;
	
		return num;
	}



	boolean isACurveControl() {
		if (isAnchorPointHandle()) return false;
		return true;
	}
	
	
	public JPopupMenu getJPopup() {
		return new pathHandlePopup();
	}

	private class pathHandlePopup extends SmartPopupJMenu implements ActionListener {

		/**
		 * 
		 */
		
		String closePointCom="closedPoint", selPointCom= "selectP", allUnSel="Unselect All";
		String uncurveCom="Remove Curvature";
		String removePoint="Remove Point", addPoint="Add Point";
		
		
		private static final long serialVersionUID = 1L;
		public pathHandlePopup() {
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
			
			
		}
	
}
	


	
}

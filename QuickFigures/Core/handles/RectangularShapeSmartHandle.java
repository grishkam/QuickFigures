/**
 * Author: Greg Mazo
 * Date Modified: Dec 26, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package handles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import actionToolbarItems.EditManyShapes;
import applicationAdapters.CanvasMouseEvent;
import graphicalObjects.CordinateConverter;
import graphicalObjects_Shapes.RectangularGraphic;
import objectDialogs.WidthAndHeightDialog;
import standardDialog.StandardDialog;
import undo.UndoStrokeEdit;
import utilityClassesForObjects.RectangleEdges;

/**A handle for editing shapes whose sizes are defined by a rectangle
 * @see  RectangularGraphic
 * */
public class RectangularShapeSmartHandle extends SmartHandle {
	
	public static final int STROKE_HANDLE_TYPE = 11, ROTATION_HANDLE = 10;

	
	private RectangularGraphic rect;
	private UndoStrokeEdit strokeUndo;

	public RectangularShapeSmartHandle(int type, RectangularGraphic r) {
		
		this.setHandleNumber(type);
		this.rect=r;
		
		
	}
	public void draw(Graphics2D graphics, CordinateConverter cords) {
		this.updateLocation(getHandleNumber());
		if (this.getHandleNumber()==rect.getLocationType()) {
			this.setHandleColor(Color.red);
			
		} else this.setHandleColor(Color.white);
		
		if (isRotationHandle()) {
			this.setHandleColor(Color.orange);
			super.drawLineBetweenPoints(graphics, cords, rect.getCenterOfRotation(), this.getCordinateLocation());
		}
		
		if (getHandleNumber()==STROKE_HANDLE_TYPE) {
			this.setHandleColor(Color.magenta);
		}
		
		if (this.isRotationHandle()&&specialShape==null) {
			int x2 = (int) (-handlesize*1.5);
			int w = (int) (handlesize*3);
			this.specialShape=new Ellipse2D.Double(x2, x2, w, w);
		}
		
		super.draw(graphics, cords);
	}

	public boolean isRotationHandle() {
		return this.getHandleNumber()==ROTATION_HANDLE;
	}

	
	/**Sets the locations of the handles based on the rectangles, size, location and rotation and
	 */
	public void updateLocation(int type) {
		if (type!=ROTATION_HANDLE) {
			Point2D p = RectangleEdges.getLocation(type,rect.getBounds());
			rect.undoRotationCorrection(p);
			setCordinateLocation(p);
			
			}  else {
			setCordinateLocation( rect.getRotationHandleLocation());
			}
		
		if (type==STROKE_HANDLE_TYPE) {
			Point2D p = rect.getStrokeHandlePoints()[0];
			//rect.undoRotationCorrection(p);
			setCordinateLocation(p);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void handleDrag(CanvasMouseEvent w) {
		if (isStrokeHandle()) {
			
			Point2D p = rect.getStrokeHandlePoints()[1];
			Point c = w.getCoordinatePoint();
			double d = 2*p.distance(c);
			if( rect.getStrokeHandlePoints()[0].distance(c)>12) {d=0.5;}
			rect.setStrokeWidth((float)d);
			if(strokeUndo!=null)	
				{
				strokeUndo.establishFinalState();
				addStrokeUndo(w);
			}
		} 
	}
	
	
	public void showJPopup(CanvasMouseEvent w) {
		EditManyShapes multi = new EditManyShapes(true,  rect.getStrokeWidth());
		multi.setSelector(w.getSelectionSystem());
		multi.getPopup().showForMouseEvent(w);;
	}
	
	private void addStrokeUndo(CanvasMouseEvent w) {
		if (!w.getAsDisplay().getUndoManager().hasUndo(strokeUndo))
			w.getAsDisplay().getUndoManager().addEdit(strokeUndo);
	}
	
	/**when the user double click a handle with the mouse, this will show a dialog*/
	@Override
	public void handlePress(CanvasMouseEvent w) {
		if (this.isStrokeHandle()) strokeUndo= new UndoStrokeEdit(rect);
		if(w.isPopupTrigger()) {
			showJPopup(w);;
			return;
		}
		if (isStrokeHandle()&&w.clickCount()==2) {
			double nSW = StandardDialog.getNumberFromUser("Input Stroke Width", rect.getStrokeWidth());
			if (nSW<200) {
				rect.setStrokeWidth((float) nSW);
				addStrokeUndo(w);
			}
			
		} 
		else 
		if (this.isRotationHandle()&&w.clickCount()==2) {
			double nSW = StandardDialog.getNumberFromUser("Input angle", rect.getAngle(), true);
			rect.setAngle(nSW);
		} else if (w.clickCount()==2) {
			new WidthAndHeightDialog(rect).showDialog();
		}
		
		
	}

	public boolean isStrokeHandle() {
		return this.getHandleNumber()==STROKE_HANDLE_TYPE;
	}
	
	/**What to do when a handle is moved from point p1 to p2*/
	public void handleMove(Point2D p1, Point2D p2) {
		rect.handleSmartMove(getHandleNumber(), (Point) p2) ;
		rect.afterHandleMove(getHandleNumber(), (Point) p1,  (Point) p2);

	}
	
}
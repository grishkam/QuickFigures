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
 * Version: 2021.1
 */
package handles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import actionToolbarItems.EditManyObjects;
import applicationAdapters.CanvasMouseEvent;
import graphicalObjects.CordinateConverter;
import graphicalObjects_Shapes.RectangularGraphic;
import locatedObject.RectangleEdges;
import objectDialogs.WidthAndHeightDialog;
import standardDialog.StandardDialog;
import undo.UndoStrokeEdit;

/**A handle for editing shapes whose sizes are defined by a bounding rectangle
 * @see  RectangularGraphic the superclass for all these shapes
 * */
public class RectangularShapeSmartHandle extends SmartHandle {
	
	/**These are the codes for specific handle types. for other handle codes @see RectangleEdgePositions */
	public static final int STROKE_HANDLE_TYPE = 11, ROTATION_HANDLE = 10;

	
	private RectangularGraphic targetShape;
	private UndoStrokeEdit strokeUndo;

	public RectangularShapeSmartHandle(int type, RectangularGraphic r) {
		
		this.setHandleNumber(type);
		this.targetShape=r;
		if(this.isRotationHandle()) {
			
			//	this.message=""+'\u21ba';//TODO: determine a pleasing way to draw a rotation symbol with this icon
		}
		
		
	}
	
	/**returns the font of the handle message*/
	protected Font getMessageFont() {
		return new Font("Monospaced", Font.BOLD, 24);
	}
	
	/**Draws the handle*/
	public void draw(Graphics2D graphics, CordinateConverter cords) {
		this.updateLocation(getHandleNumber());
		if (this.getHandleNumber()==targetShape.getLocationType()) {
			this.setHandleColor(Color.red);
			//marks the location type with a red handle
		} else this.setHandleColor(Color.white);
		
		if (isRotationHandle()) {
			this.setHandleColor(Color.orange);//rotation handles are orange
			super.drawLineBetweenPoints(graphics, cords, targetShape.getCenterOfRotation(), this.getCordinateLocation());
			
			if (specialShape==null) {//rotation handles are drawn with a special ellipse
				int x2 = (int) (-handlesize*1.5);
				int w = (int) (handlesize*3);
				this.specialShape=new Ellipse2D.Double(x2, x2, w, w);
			}
		}
		
		if (getHandleNumber()==STROKE_HANDLE_TYPE) {
			this.setHandleColor(Color.magenta);//stroke handles are magenta
		}
		
		
		
		super.draw(graphics, cords);
	}

	/**returns true if this is a rotation handle*/
	public boolean isRotationHandle() {
		return this.getHandleNumber()==ROTATION_HANDLE;
	}

	
	/**Sets the locations of the handles based on the rectangular shape's size, location and rotation and
	 */
	public void updateLocation(int type) {
		if (type!=ROTATION_HANDLE) {
			Point2D p = RectangleEdges.getLocation(type,targetShape.getBounds());
			targetShape.undoRotationCorrection(p);
			setCordinateLocation(p);
			
			}  else {
			setCordinateLocation( targetShape.getRotationHandleLocation());
			}
		
		if (type==STROKE_HANDLE_TYPE) {
			Point2D p = targetShape.getStrokeHandlePoints()[0];
			
			setCordinateLocation(p);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**Handle drag method implements the stroke handles movement
	 * The handle move method takes care of the other types*/
	@Override
	public void handleDrag(CanvasMouseEvent w) {
		if (isStrokeHandle()) {
			
			Point2D p = targetShape.getStrokeHandlePoints()[1];
			Point c = w.getCoordinatePoint();
			double d = 2*p.distance(c);
			if( targetShape.getStrokeHandlePoints()[0].distance(c)>12) {d=0.5;}
			targetShape.setStrokeWidth((float)d);
			if(strokeUndo!=null)	
				{
				strokeUndo.establishFinalState();
				addStrokeUndo(w);
			}
		} 
	}
	
	
	public void showJPopup(CanvasMouseEvent w) {
		EditManyObjects multi = new EditManyObjects(true,  targetShape.getStrokeWidth());
		multi.setSelector(w.getSelectionSystem());
		multi.getPopup().showForMouseEvent(w);;
	}
	
	/**adds the stroke undo to the undo manager*/
	private void addStrokeUndo(CanvasMouseEvent w) {
		if (!w.getAsDisplay().getUndoManager().hasUndo(strokeUndo))
			w.getAsDisplay().getUndoManager().addEdit(strokeUndo);
	}
	
	/**when the user double click a handle with the mouse, this will show a dialog*/
	@Override
	public void handlePress(CanvasMouseEvent w) {
		if (this.isStrokeHandle()) strokeUndo= new UndoStrokeEdit(targetShape);
		if(w.isPopupTrigger()) {
			showJPopup(w);;
			return;
		}
		if (isStrokeHandle()&&w.clickCount()==2) {
			double nSW = StandardDialog.getNumberFromUser("Input Stroke Width", targetShape.getStrokeWidth());
			if (nSW<200) {
				targetShape.setStrokeWidth((float) nSW);
				addStrokeUndo(w);
			}
			
		} 
		else 
		if (this.isRotationHandle()&&w.clickCount()==2) {
			double nSW = StandardDialog.getNumberFromUser("Input angle", targetShape.getAngle(), true);
			targetShape.setAngle(nSW);
		} else if (w.clickCount()==2) {
			new WidthAndHeightDialog(targetShape).showDialog();
		}
		
		
	}

	/**returns true if this is a stroke handle*/
	public boolean isStrokeHandle() {
		return this.getHandleNumber()==STROKE_HANDLE_TYPE;
	}
	
	/**What to do when a handle is moved from point p1 to p2*/
	public void handleMove(Point2D p1, Point2D p2) {
		targetShape.handleSmartMove(getHandleNumber(),  p2) ;
		targetShape.afterHandleMove(getHandleNumber(),  p1,   p2);

	}
	
}
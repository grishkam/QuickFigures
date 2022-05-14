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
 * Version: 2022.1
 */
package handles;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;

import applicationAdapters.CanvasMouseEvent;
import graphicalObjects_Shapes.AngleParameter;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import standardDialog.StandardDialog;
import undo.SimpleItemUndo;

/**A handle that allows the user to alter an angle parameter. 
  angle parameters determine the angle and radius that are used 
  to determine the appearance of certain shapes */
public class AngleHandle extends SmartHandle {

	
	private RectangularGraphic theShape;//the target shape is always a subclass of rectangular graphic
	private AngleParameter theAngle;//the parameter being altered
	private double handleDrawAngle;//the angle at which to draw the handle
	
	public static final int ANGLE_TYPE=0, ANGLE_AND_RADIUS_TYPE=2, RADIUS_TYPE=1, ANGLE_RATIO_TYPE=3, ANGLE_RATIO_AND_RAD_TYPE=4;
	private int type=ANGLE_TYPE;
	public double maxRatio=1;//the maximum radius ratio that this handle will allow the user to set
	private SimpleItemUndo<AngleParameter> undo;
	private boolean undoadded;
	

	
	public AngleHandle(RectangularGraphic r, AngleParameter angle, Color c, double startAngle, int handleNumber) {
		
		this.theShape=r;
		this.theAngle=angle;
		this.setHandleColor(c);
		this.setHandleDrawAngle(startAngle);
		this.setHandleNumber(handleNumber);
		this.type=angle.getType();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**returns the point at which the handle is drawn*/
	public Point2D getCordinateLocation() {
		double currentAngle=getHandleDrawAngle()+theAngle.getAngle();
		if (this.doesAngleRatio()) currentAngle=getHandleDrawAngle()+this.getStandardAngle()*theAngle.getRatioToStandardAngle();
		return  theShape.getPointInside(theAngle.getRatioToMaxRadius(), currentAngle);
	}
	
	/**returns the location corresponding to the maximum radius for the angle parameter*/
	public Point2D getMaxRadiusLocation() {
		double currentAngle=getHandleDrawAngle()+theAngle.getAngle();
		return  theShape.getPointInside(1, currentAngle);
	}
	
	/**returns the location of the handle for an angle of zero*/
	public Point2D getZeroLocation() {
		double currentAngle=getHandleDrawAngle();
		return theShape.getPointInside(theAngle.getRatioToMaxRadius(), currentAngle);
	}
	
	public void setType(int t) {
		type=t;
	}
	
	
	
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		
		
		Point p2 = lastDragOrRelMouseEvent.getCoordinatePoint();
		;
			
			
		if (setsAngle()) {
			setAngleToHandleDragLocation(p2);
			}
		
		if(setsRadius()) {
			setRadiusToHandleDragLocation(p2);
		}
			
			if (undoadded==false) {
				undoadded=true;
				addUndoToManager(lastDragOrRelMouseEvent);
			} else undo.establishFinalState();
	}

	/**
	 called when the user drags the handle to point p2. changes the value of the radius based on the drag location
	 */
	protected void setRadiusToHandleDragLocation(Point p2) {
		double original=theAngle.getRatioToMaxRadius();
		
		Point2D p0 = this.getCenterOfRotation();
		double d1 = p0.distance(p2);
		double d2= p0.distance(getMaxRadiusLocation());
		double ratio = d1/d2;
		if(ratio>maxRatio) ratio=maxRatio;
		theAngle.setRatioToMaxRadius(ratio);
		
		
		if(theAngle.attached!=null)for(AngleParameter a: theAngle.attached) {
			if(a==null) continue;
			a.increaseRadiusRatio(theAngle.getRatioToMaxRadius()-original);
		}
	}

	/**
	 called when the user drags the handle to point p2. changes the value of the angle based on the drag location
	 */
	protected void setAngleToHandleDragLocation(Point p2) {
		double original=theAngle.getAngle();
		double orginalRatio=theAngle.getRatioToStandardAngle();
		
		double inner = ShapeGraphic.getAngleBetweenPoints(getCenterOfRotation(), getZeroLocation() );
		double inner2 = ShapeGraphic.getAngleBetweenPoints(getCenterOfRotation(), p2 );

		double diff=(inner2-inner);
		
		if(diff<0) diff+=2*Math.PI;
			theAngle.setAngle(diff);
		
		double angleRatio=diff/this.getStandardAngle();
		if (inner2-inner<0)
			{
			
			diff=(inner2-inner);
			angleRatio=diff/this.getStandardAngle();
			}
		theAngle.setRatioToStandardAngle(angleRatio);
		
		if(theAngle.attached!=null)for(AngleParameter a: theAngle.attached) {
			if(a==null) continue;
			a.increaseAngleRatio(theAngle.getRatioToStandardAngle()-orginalRatio);
			a.increaseAngle(theAngle.getAngle()-original);
		}
	}
	
	

	public double getStandardAngle() {
		return Math.PI*2;
	}

	public boolean setsRadius() {
		if (type==ANGLE_RATIO_AND_RAD_TYPE) return true; 
		if (type==ANGLE_AND_RADIUS_TYPE) return true;
		return type==RADIUS_TYPE;
	}

	public boolean setsAngle() {
		if (doesAngleRatio()) return true;
		if (type==ANGLE_RATIO_AND_RAD_TYPE) return true; 
		if (type==ANGLE_AND_RADIUS_TYPE) return true;
		return type==ANGLE_TYPE;
	}
	


	private Point2D getCenterOfRotation() {
		return theShape.getCenterOfRotation();
	}
	
	@Override
	public void handleRelease(CanvasMouseEvent w) {
		
	}
	
	/***/
	@Override
	public void handlePress(CanvasMouseEvent w) {
		undo = new SimpleItemUndo<AngleParameter> (theAngle);
		undoadded = false;
		
		if (w.clickCount()==2&& setsAngle()) {
			showUserDialogForAngle(w);
		} 
		
		if (w.clickCount()==2&& setsRadius()) {
			showUserDialogForRadius(w);
		} 
	}

	/**
		displays a dialog for altering the angle parameter's radius
	 */
	protected void showUserDialogForRadius(CanvasMouseEvent w) {
		double nSW = StandardDialog.getNumberFromUser("Input ratio", theAngle.getRatioToMaxRadius(), false);
		if(nSW>maxRatio) nSW=maxRatio;
		theAngle.setRatioToMaxRadius(nSW);
		addUndoToManager(w);
	}

	/**
		displays a dialog for altering the angle parameter's angle
	 */
	protected void showUserDialogForAngle(CanvasMouseEvent w) {
		if (doesAngleRatio()) {
			double nSW = StandardDialog.getNumberFromUser("Input angle ratio", theAngle.getRatioToStandardAngle(), false);
			theAngle.setRatioToStandardAngle(nSW);
		} else {
		double nSW = StandardDialog.getNumberFromUser("Input angle", theAngle.getAngle(), true);
		theAngle.setAngle(nSW);}
		addUndoToManager(w);
	}

	public boolean doesAngleRatio() {
		if (type==ANGLE_RATIO_AND_RAD_TYPE) return true;
		return type==ANGLE_RATIO_TYPE;
	}

	/**Adds the undo*/
	public void addUndoToManager(CanvasMouseEvent w) {
		undo.establishFinalState();
		w.addUndo(undo);
	}

	/**The handle may be drawn at an angle that is shifted from the angle of the parameter.
	   returns the draw angle*/
	public double getHandleDrawAngle() {
		return handleDrawAngle;
	}

	/**The handle may be drawn at an angle that is shifted from the angle of the parameter.
	  sets the draw angle*/
	public void setHandleDrawAngle(double handleDrawAngle) {
		this.handleDrawAngle = handleDrawAngle;
	}

	/**this handle is responsible for adding its own undo the the undo mnager*/
	@Override
	public boolean handlesOwnUndo() {
		return true;
	}
	
	
	
}

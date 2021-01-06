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
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import applicationAdapters.CanvasMouseEvent;
import graphicalObjects_Shapes.RectangleEdgeParameter;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import locatedObject.RectangleEdges;
import standardDialog.StandardDialog;
import undo.SimpleItemUndo;
import utilityClasses1.NumberUse;

/**A handle that slides along the edge of a rectangular graphic.
these allow the user to alter  a rectangle edge parameter*/
public class RectangleEdgeHandle extends SmartHandle {

	/**constants of the two varieties of edge handle*/
	public static final int LENGTH_TYPE=0, RATIO_TYPE=1;
	
	private RectangularGraphic theShape;
	private RectangleEdgeParameter theParameter;
	
	
	int type= LENGTH_TYPE;
	private SimpleItemUndo<RectangleEdgeParameter> undo;
	private boolean undoaddedAlready;

	/**some handles are drawn at a location that is shifted over by a certain amount. this variable controls that amounts*/
	private int displaceAmount = 150;
	/**some handles are drawn at a location that is shifted over by a certain amount. this variable controls that amounts*/
	double displaceFactor=0;

	
	public RectangleEdgeHandle(RectangularGraphic r,  RectangleEdgeParameter a, Color c, int handleNumber, int type, double displace) {
		this.theShape=r;
		this.theParameter=a;
		this.setHandleColor(c);
		this.type=type;
		this.displaceFactor=displace;
		this.setHandleNumber(handleNumber);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**moves shifts a point in a direcion */
	private void displacePoint(Point2D p) {
		Point2D pZero = RectangleEdges.getLocation(theParameter.zeroLocation,theShape.getRectangle());
		Point2D pMax = RectangleEdges.getLocation(theParameter.maxLengthLocation,theShape.getRectangle());
		double angle = ShapeGraphic.getAngleBetweenPoints(pZero, pMax)+Math.PI/2;
		
		
		double d=displaceFactor*displaceAmount;
		p.setLocation(p.getX()+d*Math.cos(angle), p.getY()+d*Math.sin(angle));
	} 
	
	/**returns the locaiton of this handle*/
	public Point2D getCordinateLocation() {
		Point2D pZero = RectangleEdges.getLocation(theParameter.zeroLocation,theShape.getRectangle());
		Point2D pMax = RectangleEdges.getLocation(theParameter.maxLengthLocation,theShape.getRectangle());
		double angle = ShapeGraphic.getAngleBetweenPoints(pZero, pMax);
		java.awt.geom.Point2D.Double p = new Point2D.Double(pZero.getX()+theParameter.getLength()*Math.cos(angle), pZero.getY()+theParameter.getLength()*Math.sin(angle));
		if (type==RATIO_TYPE) {
			double r = theParameter.getRatioToMaxLength();
			double x3 = pZero.getX()*(1-r)+pMax.getX()*r;
			double y3 = pZero.getY()*(1-r)+pMax.getY()*r;
			p=new Point2D.Double(x3, y3);
		}
		displacePoint(p);
		theShape.undoRotationCorrection(p);
		return p;
	}
	
	/**returns the location that corresponds to a parameter value of 0*/
	public Point2D getZeroLocation() {
		Point2D p = RectangleEdges.getLocation(theParameter.zeroLocation,theShape.getRectangle());
		theShape.undoRotationCorrection(p);
		return p;
		}
	
	/**returns the location that corresponds to the maximun parameter value (or a ratio of 1)*/
	public Point2D getMaxLocation() {
		Point2D p = RectangleEdges.getLocation(theParameter.maxLengthLocation,theShape.getRectangle());
		theShape.undoRotationCorrection(p);
		return p;
		}
	
	/**Called when a handle is dragged*/
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		
		Point2D pz =  getZeroLocation();
		Point2D maxLocation = getMaxLocation();
		Point p2 = lastDragOrRelMouseEvent.getCoordinatePoint();
		
		double angle = -NumberUse.getAngleBetweenPoints(pz, maxLocation);
		double maxDistance=distanceOnAxis(pz, maxLocation, angle);
		double distance=distanceOnAxis(pz, p2, angle);
		
		/**if the mouse has been dragged to an invalid location (farther than the zero point)*/
		if (maxLocation.distance(p2)>maxLocation.distance(pz)) distance=0;
		if(distance<0) distance=0;
		
		/**if the mouse drag beyond the point of max distance*/
		if(distance>maxDistance) distance=maxDistance;
		
		/**Sets the parameter value*/
		theParameter.setLength(distance);
		theParameter.setRatioToMaxLength(distance/maxDistance);
		addUndo(lastDragOrRelMouseEvent);
	}


	
	/**Returns the distance between points along an axis of the given angle.
	  does not work for every possible combination of points and angles*/
	private double distanceOnAxis(Point2D pz, Point2D maxLocation, double angle) {
		AffineTransform a = AffineTransform.getRotateInstance(angle, 0, 0);
		
		Point2D p3=new Point2D.Double();
		Point2D p4=new Point2D.Double();
		a.transform(pz, p3);
		a.transform(maxLocation, p4);
		return Math.abs(p4.getX()-p3.getX());
	}

	@Override
	public void handlePress(CanvasMouseEvent w) {
		undo = new SimpleItemUndo<RectangleEdgeParameter>(theParameter);
		undoaddedAlready=false;
		
		if (w.clickCount()==2 && type!=RATIO_TYPE) {
			double nSW = StandardDialog.getNumberFromUser("Input ", theParameter.getLength());
			theParameter.setLength(nSW);
			addUndo(w);
		} 
		if (w.clickCount()==2 && type==RATIO_TYPE) {
			double nSW = StandardDialog.getNumberFromUser("Input ", theParameter.getRatioToMaxLength());
			if(nSW>1) nSW=1;
			theParameter.setRatioToMaxLength(nSW);
			addUndo(w);

		} 
	}

	public void addUndo(CanvasMouseEvent w) {
		undo.establishFinalState();
		if(undoaddedAlready) {return;}
		w.addUndo(undo);
		undoaddedAlready=true;
	}
	
	public boolean handlesOwnUndo() {
		return true;
	}
}

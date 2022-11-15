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
 * Date Created: Nov 4, 2022
 * Date Modified: Nov 14, 2022
 * Copyright (C) 2022 Gregory Mazo
 * Version: 2022.2
 */
/**
 
 * 
 */
package graphicalObjects_SpecialObjects;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

import channelMerging.PreProcessInformation;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_Shapes.BasicShapeGraphic;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import locatedObject.PathPointList;
import locatedObject.RotatesFully;
import locatedObject.Scales;
import logging.IssueLog;
import undo.CombinedEdit;
import undo.ProvidesDialogUndoableEdit;

/**
 A class for storing a list of objects that are drawn in front of an image panel
 */
public class OverlayObjectList extends GraphicLayerPane implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	boolean manualEditsMade=false;

	private PreProcessInformation lastProcess=null;
	
	/**
	 * @param name
	 */
	public OverlayObjectList(String name) {
		super(name);
	}


	/**
	 * 
	 */
	public OverlayObjectList() {
		this("Overlay");
	}

	/**returns the object list*/
	public ArrayList<?> getOverlayObjects() {
		return super.getAllGraphics();
	}


	/**Creates a copy*/
	public  OverlayObjectList copy() {
		OverlayObjectList output = new  OverlayObjectList(this.getName());
		for(Object o:this.getOverlayObjects()) {
			if(o instanceof BasicGraphicalObject)
			 output.addItemToArray(((BasicGraphicalObject) o).copy());
		}
		output.setLastProcess(getLastProcess());
		return output;
	}
	
	/**Called when the user tries to move objects between layers*/
	public boolean canAccept(ZoomableGraphic z) {
		if(z instanceof GraphicLayer)
			return false;
		if(z instanceof PanelLayoutGraphic)
			return false;
		if(z instanceof BarGraphic)
			return false;
		if(z instanceof ImagePanelGraphic)
			return false;
	
		return super.canAccept(z);
	}
	
	/**returns an undo*/
	public CombinedEdit getUndoForEditWindow() {
		CombinedEdit output = new CombinedEdit();
		for(Object o:this.getOverlayObjects()) {
			if(o instanceof BasicGraphicalObject) {
			 output.addEditToList(((BasicGraphicalObject) o).provideDragEdit());
			//need to add a dialog edit
			}
			if(o instanceof ProvidesDialogUndoableEdit) {
				 output.addEditToList(((ProvidesDialogUndoableEdit) o).provideUndoForDialog());
				//need to add a dialog edit
				}
		}
		return output;
	}


	/**Records that the user has made edits to the overlay
	 * @param b
	 */
	public void setEdited(boolean b) {
		manualEditsMade=b;
	}
	public boolean manualEditsMade() {return manualEditsMade;}

	/**This updates the crop area for the overlay*/
	public OverlayObjectList changeCropArea( PreProcessInformation p ) {
		OverlayObjectList inverted = cropOverlayAtAngle(this, this.getLastProcess(), true);
		return cropOverlayAtAngle(inverted, p, false);
	}
	
	
	public static OverlayObjectList cropOverlayAtAngle(OverlayObjectList overlayObjects, PreProcessInformation p, boolean reverse) {
		 if(p==null) {
			 if(reverse) {
				 IssueLog.log("Cannot complete reversal ");
			 }
			 return cropOverlayAtAngle(overlayObjects,null,  0, 1, reverse);
		 }
		return cropOverlayAtAngle(overlayObjects, p.getRectangle(),  p.getAngle(), p.getScale(), reverse);
	}
	

	
	/** Will create a crop and scale copy of the overlay
	 * TODO: make it work with ovals, arrows and rectangles
	 * @param overlay
	 * @param r
	 * @param angle
	 * @param scale
	 * @return
	 */
	public static OverlayObjectList cropOverlayAtAngle(OverlayObjectList overlayObjects, Rectangle r, double angle, double scale, boolean reverse) {

		
		OverlayObjectList output = new OverlayObjectList("");
		output.setLastProcess(new PreProcessInformation(r, angle, scale));
		
		if(r==null) {
			return overlayObjects.copy() ;
			}
		
		AffineTransform rotTransform = AffineTransform.getRotateInstance(angle, r.getCenterX(), r.getCenterY());
		AffineTransform translate = AffineTransform.getTranslateInstance(-r.getMinX(), -r.getMinY());
		AffineTransform scaleTransform= AffineTransform.getScaleInstance(scale, scale);
		
		if(reverse) {
			rotTransform = AffineTransform.getRotateInstance(-angle, r.getCenterX(), r.getCenterY());
			translate = AffineTransform.getTranslateInstance(r.getMinX(), r.getMinY());
			scaleTransform= AffineTransform.getScaleInstance(1/scale, 1/scale);
		}
		
		for(Object o: overlayObjects.getOverlayObjects()) try {
			
			
			if(o instanceof BasicShapeGraphic) {
				/**
				BasicShapeGraphic b = ((BasicShapeGraphic) o).copy();
				Shape shape1=b.getShape();
				shape1=rotTransform.createTransformedShape(shape1);
				
				shape1=translate.createTransformedShape(shape1);
				if(scale!=1) {
					
					shape1=scaleTransform.createTransformedShape(shape1);
					}
				b.setShape(shape1);
				b.setName("crop of "+b.getName());
				
				output.add(b.createPathCopy());*/
				}
			
			
				
				/**if(o instanceof RectangularGraphic) {
					RectangularGraphic o2 = (RectangularGraphic) o;
					ShapeGraphic b2;
					if(angle!=0)
						b2 = createCroppedPathVersion(scale, rotTransform, translate, scaleTransform, o2);
					else 
						{
						b2=o2.copy();
						b2.moveLocation(-r.getMinX(), -r.getMinY());
						b2.scaleAbout(new Point.Double(), scale);
						}
					output.add(b2);
				}
				else*/ 
				
				if(o instanceof PathGraphic) {
					PathGraphic o2 = (PathGraphic) o;
					ShapeGraphic b2;
					if(!reverse)
						b2 = createCroppedPathVersion(scale, rotTransform, translate, scaleTransform, o2);
					else 
						b2 = createCroppedPathVersion(1/scale,scaleTransform, translate, rotTransform,  o2);
					
					output.add(b2);
				} else if ((o instanceof BasicGraphicalObject)&(o instanceof Scales) &(o instanceof  RotatesFully)) {
					//this part works for text, rectangles, arrows  
					BasicGraphicalObject o2 = (BasicGraphicalObject) o;
					BasicGraphicalObject b2=o2.copy();
					if(!reverse) {
						((RotatesFully)b2).rotateAbout(new Point2D.Double(r.getCenterX(), r.getCenterY()), angle );
						b2.moveLocation(-r.getMinX(), -r.getMinY());
						b2.scaleAbout(new Point.Double(), scale);
					}
					if(reverse) {
						b2.scaleAbout(new Point.Double(), 1/scale);
						b2.moveLocation(r.getMinX(), r.getMinY());
						((RotatesFully)b2).rotateAbout(new Point2D.Double(r.getCenterX(), r.getCenterY()), -angle );	
					}
					
					output.add(b2);
				}				
				
			
		}catch (Throwable t){
			  IssueLog.log(t);
		  }
		return output;
	}



	/**returns a cropped and scaled path
	 * @param scale
	 * @param rotTransform
	 * @param translate
	 * @param scaleTransform
	 * @param o2
	 * @return
	 */
	public static PathGraphic createCroppedPathVersion(double scale, AffineTransform rotTransform, AffineTransform translate,
			AffineTransform scaleTransform, ShapeGraphic o2) {
		PathGraphic b2 = o2.createPathCopy();
		PathPointList points = b2.getPoints();
		
		points.applyAffine(rotTransform);
		points.applyAffine(translate);
		if (scale!=1)points.applyAffine(scaleTransform);
		
		
		b2.setPoints(points);
		b2.updatePathFromPoints();
		b2.setName(b2.getName());
		return b2;
	}


	public PreProcessInformation getLastProcess() {
		return lastProcess;
	}


	public void setLastProcess(PreProcessInformation lastProcess) {
		this.lastProcess = lastProcess;
	}
}

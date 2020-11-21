package undo;

import java.awt.Color;

import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.ShapeGraphic;
import graphicalObjects_BasicShapes.TextGraphic;

/**an undoable edit for changes to the color of objects*/
public class ColorEditUndo extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TextGraphic text=null;
	ShapeGraphic shape=null;
	ImagePanelGraphic image=null;
	
	Color oldTextColor=null;
	Color newTextColor=null;
	
	
	Color oldfillColor=null;
	Color newfillColor=null;
	private Color oldstrokeColor;
	private Color newstrokeColor;
	
	private Color oldFrameColor;
	private Color newFrameColor;
	
	
	public ColorEditUndo(Object o) {
		if (o instanceof TextGraphic) {
			text=(TextGraphic) o;
			 oldTextColor=text.getTextColor();
		}
		
		if (o instanceof ShapeGraphic) {
			shape=(ShapeGraphic) o;
			oldfillColor=shape.getFillColor();
			oldstrokeColor=shape.getStrokeColor();
		}
		
		if (o instanceof ImagePanelGraphic) {
			image=(ImagePanelGraphic) o;
			 oldFrameColor=image.getFrameColor();
		}
	}
	
	public void establishFinalColors() {
		if (text!=null) newTextColor=text.getTextColor();
		if (shape!=null) { 
			newfillColor=shape.getFillColor();
			newstrokeColor=shape.getStrokeColor();
			}
		if(image!=null) {
			newFrameColor=image.getFrameColor();
		}
		
	}
	
	
	public void undo() {
		
		if (text!=null) text.setTextColor(oldTextColor);
		if (shape!=null) {
			shape.setFillColor(oldfillColor);
			shape.setStrokeColor(oldstrokeColor);
		}
		if(image!=null) {
			image.setFrameColor(oldFrameColor);
		}
		
	}
	public void redo() {
		if (text!=null) text.setTextColor(newTextColor);
		if (shape!=null) {
			shape.setFillColor(newfillColor);
			shape.setStrokeColor(newstrokeColor);
		}
		if(image!=null) {
			image.setFrameColor(newFrameColor);
		}
		
	}

}

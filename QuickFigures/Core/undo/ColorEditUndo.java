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
package undo;

import java.awt.Color;

import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;

/**an undoable edit for changes to the color of objects
 * works for image panels, shapes or text*/
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

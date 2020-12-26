/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import applicationAdapters.CanvasMouseEvent;
import graphicalObjects_SpecialObjects.TextGraphic;

/**A smart handle for editing text items. can be used for changing location of font size*/
public class SmartHandleForText extends SmartHandle {

	public static final int ROTATION_HANDLE = 0, TEXT_FONT_SIZE_HANDLE = 1;
	
	private static final long serialVersionUID = 1L;

	private TextGraphic textItem;

	private Point2D baseLineStart;

	public SmartHandleForText(TextGraphic textGraphic, int handleForm) {
		this.textItem=textGraphic;
		this.setHandleNumber(handleForm);
	}
	
	public Point2D getCordinateLocation() {
		if (this.getHandleNumber()==TEXT_FONT_SIZE_HANDLE) {
				 return textItem.getUpperLeftCornerOfBounds();
			
		}
		
		if (this.getHandleNumber()==ROTATION_HANDLE) {
			 	 return textItem.getBaseLineEnd();
			
		}
		
		return super.getCordinateLocation();
	}
	
	public Color getHandleColor() {
		if (textItem.isEditMode()) return Color.red;
		if(getHandleNumber()==ROTATION_HANDLE) return Color.orange;
		return Color.white;
	}
	
	public int handleSize() {
		if (textItem.isEditMode()) return 1;
		return handlesize;
	}
	
	public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
		baseLineStart = textItem.getBaseLineStart();//stores the baseline position
	}
	
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		if (this.getHandleNumber()==ROTATION_HANDLE) {
			double angle=TextGraphic.distanceFromCenterOfRotationtoAngle(textItem.getCenterOfRotation(), lastDragOrRelMouseEvent.getCoordinatePoint());

				textItem.setAngle(angle);
				
		}
		Point p2 = lastDragOrRelMouseEvent.getCoordinatePoint();
		
		if (getHandleNumber()==TEXT_FONT_SIZE_HANDLE ) {
			
			
			Point2D handleLoc=p2;
			
			java.awt.geom.Point2D.Double rotStart = new Point2D.Double();
			java.awt.geom.Point2D.Double rotLeft = new Point2D.Double();
			try {
				/**Sets the font size to match the distance between the drag point and the baseline*/
				textItem.getRotationTransform().createInverse().transform(baseLineStart, rotStart);
				textItem.getRotationTransform().createInverse().transform(handleLoc, rotLeft);
				double newsize =textItem.getFont().getSize();
				double d = Math.abs(rotLeft.y-rotStart.y);
				if(d>3) newsize=d;
				userSetNewSize(newsize);
				return;
			
			} catch (NoninvertibleTransformException e) {
				/**if an exception occurs, sets the font size the old way. not as natural for rotated text*/
				
				double distY=-p2.getY()+baseLineStart.getY();
				double cos = Math.cos(textItem.getAngle());
				double newsize =textItem.getFont().getSize();
				if (cos!=0)
					newsize=distY/cos;
				if (newsize>50)newsize =textItem.getFont().getSize();
				
				userSetNewSize(newsize);
			}
			
		}
		
	}
	public void userSetNewSize(double newsize) {
		if ((int)newsize!=textItem.getFont().getSize()
				&& newsize>1 
				&& newsize<300) {
			Font font = textItem.getFont().deriveFont((float)(newsize));
			textItem.setFont(font);
				}
	}
}

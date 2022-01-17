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
 * Version: 2022.0
 */
package genericTools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JColorChooser;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.SimpleRing;
import icons.GraphicToolIcon;
import standardDialog.graphics.GraphicDisplayComponent;
import undo.ColorEditUndo;

/**A tool that changes the color of an object at the press location*/
public class ObjectColorFillTool  extends Object_Mover {
	
	private Color foregroundColor=Color.red;
	
	
	{
		realtimeshow=true;
		 
	 iconSet.setIcon(0, new BrushIcon(0));
	 iconSet.setIcon(1, new BrushIcon(1));
	 iconSet.setIcon(2, new BrushIcon(2));
	}

	@Override
	public void mousePressed() {
		
		if (getPrimarySelectedObject()==null) return;
		ColorEditUndo ceu = new ColorEditUndo(getPrimarySelectedObject());
		this.getImageDisplayWrapperClick().getUndoManager().addEdit(ceu);
		
		getPrimarySelectedObject().dropObject(getTheForegroundColor(), getClickedCordinateX(), getClickedCordinateY());
		
		ceu.establishFinalColors();
		
		this.getImageClicked().updateDisplay();

	}
	@Override
	public void mouseDragged() {
		mousePressed();

	}
	public Color getTheForegroundColor() {
		return foregroundColor;
	}
	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}
	
	@Override
	public void showOptionsDialog() {
		this.foregroundColor = JColorChooser.showDialog(null, "Color", getTheForegroundColor());
	}
	
public String getToolTip() {
		
		return "Set Object Colors";
	}
	
	/**an icon for the brush tool*/
	public class BrushIcon extends GraphicToolIcon {

	
		public BrushIcon(int type) {
			super(type);
		}
		
		protected void paintObjectOntoIcon(Component arg0, Graphics g, int arg2,
				int arg3) {
			getItemIcon(true).paintIcon(arg0, g, arg2, arg3);
		}

		@Override
		public GraphicToolIcon copy(int type) {
			return new BrushIcon(type);
		}
		
	

	
		
	}
	
	@Override
	public String getToolName() {
			return "Color Brush Tool";
		}
	
	public GraphicDisplayComponent getItemIcon(boolean selected) {
		GraphicGroup gg=new GraphicGroup();
		RectangularGraphic blankRect = RectangularGraphic.blankRect(new Rectangle(6,6,8,10), Color.black);
		CircularGraphic oval = new CircularGraphic(blankRect, 0);
		oval.setRectangle(new Rectangle(8, 1, 6, 9));
		blankRect.setFillColor(getTheForegroundColor());
		blankRect.setStrokeWidth(1);
		oval.setStrokeWidth(1);
		blankRect.setAngle(-Math.PI/4);
		
		SimpleRing ring = new SimpleRing(blankRect.getRectangle().getBounds(), 1);
		ring.moveLocation(-4, -4);
		ring.setWidth(10);
		ring.setHeight(10);
		ring.setRingRatio(0.65);
		ring.setStrokeColor(Color.black);
		SimpleRing pouringPaint=ring.copy();
		pouringPaint.setRingRatio(0.25);;
		pouringPaint.moveLocation(7, 4);
		pouringPaint.setAngle(-Math.PI/6);
		pouringPaint.setStrokeColor(null);
		pouringPaint.setWidth(12);
		pouringPaint.setHeight(12);
		pouringPaint.setFillColor(this.getTheForegroundColor());
		ring.setHeight(6); ring.setAngle(-Math.PI/8); ring.moveLocation(2, 0);
		ring.setWidth(5);ring.moveLocation(2, 2);
		
		
		
		;
	
		gg.getTheInternalLayer().add(oval);
		gg.getTheInternalLayer().add(pouringPaint);
		gg.getTheInternalLayer().add(blankRect);
		
		 GraphicDisplayComponent output = new GraphicDisplayComponent(gg);;
		 output.setRelocatedForIcon(false);
		
		 return output;
	}
	
	
	/**changes the mouse cursor depending on there the mouse is*/
	public void updateCursorIfOverhandle() {
		
	}

}

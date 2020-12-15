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
package graphicTools;


import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

import applicationAdapters.ImageWrapper;
import externalToolBar.TreeIconWrappingToolIcon;
import genericMontageKit.OverlayObjectManager;
import graphicalObjects_BasicShapes.RectangularGraphic;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.RectangleEdgePosisions;

/**Tool used to draw a rectangular object on the figure*/
public class RectGraphicTool extends GraphicTool implements ShapeAddingTool{
	
	boolean started=false;
	
	/**the model shape stores the default settings for the tool
	  every shape created matches model with respect to color, line width, and other traits*/
	RectangularGraphic model= new RectangularGraphic(0,0,0,0);
	RectangularGraphic lastSetObject=null;
	{
		getModel().setStrokeColor(Color.black);
	{
		super.set=TreeIconWrappingToolIcon.createIconSet(getModel());//sets up the tool icons

		super.temporaryTool=true;//set to true if the toolbar will switch back to the object mover tool when done
	}}
	
	/**what occurs when the mouse is pressed. Called from a method in the superclass*/
	public void onPress(ImageWrapper gmp, LocatedObject2D roi2) {
		handleRectangularObject(gmp, roi2);
	}
	
	
	/**When given a bounding box, creates a shape to fit it*/
	public RectangularGraphic createShape(Rectangle r) {
		return new RectangularGraphic(r);
	}
	
	public void handleRectangularObject(ImageWrapper gmp, Object roi2) {
				
		RectangularGraphic currentRect;
		establishMovedIntoOrClickedHandle(true);//if the user clicked a handle on an existing rectangle
		
		/**if a Rectangles handle was clicked on sets that as the current rectangle */
		if (roi2 instanceof RectangularGraphic&&getSelectedHandleNumber()>-1) {
			currentRect=(RectangularGraphic) roi2;
			
		} else
			{
			/**starts the edit. creates a rectangle changes its setting to match the model*/
			started=true;
			currentRect= createShape(new Rectangle(getClickedCordinateX(), getClickedCordinateY(), 15,15));
			currentRect.copyColorsFrom(getModel());
			currentRect.copyAttributesFrom(getModel());
			/**adds the item*/
			gmp.getGraphicLayerSet().getSelectedContainer().add(currentRect);
			
			/**Adds an undo*/
			addUndoerForAddItem(gmp, gmp.getGraphicLayerSet().getSelectedContainer(), currentRect);
			
			/**sets the selected handle to the one in the lower right hand corners. Mouse drag will work on this handle */
			setSelectedHandleNumber( RectangleEdgePosisions.LOWER_RIGHT);
			
		}
		
		/**Ensures the selected object is the current rectangle*/
		setPrimarySelectedObject(currentRect);
		
		
	}
	
	public void mouseReleased() {
		started=false;
		super.mouseReleased();
	}
	

	public void mouseDragged() {
		if (started) {
			Rectangle2D r = OverlayObjectManager.createRectangleFrom2Points(this.clickedCord(), this.draggedCord());
			if (this.getPrimarySelectedObject() instanceof RectangularGraphic) {
				RectangularGraphic rr=(RectangularGraphic) getPrimarySelectedObject();
				rr.setRectangle(r);
				lastSetObject=rr;
			}
		}
		else super.mouseDragged();
		
	}
	
	/**Shows the model shape's options dialog. The options in that dialog fulfill the role of a tool dialog*/
	@Override
	public void showOptionsDialog() {
		getModel().showOptionsDialog();
	}
	
	
	@Override
	public String getToolTip() {
			return "Draw a "+getShapeName();
		}
	@Override
	public String getToolName() {
		return "Draw "+getShapeName();
	}

	/**returns the name of the model shape (default is rectangle)*/
	public String getShapeName() {
		return getModel().getShapeName();
	}


	public Icon getIcon() {
		return getModel().getTreeIcon();
	}


	public RectangularGraphic getModel() {
		return model;
	}
	
	
}

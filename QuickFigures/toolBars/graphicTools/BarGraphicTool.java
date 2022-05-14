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
package graphicTools;

import java.awt.Color;
import java.awt.Point;

import applicationAdapters.ImageWorkSheet;
import figureFormat.ScaleBarExamplePicker;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import icons.TreeIconWrappingToolIcon;
import locatedObject.AttachmentPosition;
import locatedObject.LocatedObject2D;
import undo.CombinedEdit;
import undo.UndoAddItem;
import undo.UndoAddOrRemoveAttachedItem;

/**A tool to add scale bars to panels. no longer included in the tool bar but its methods are called by popup menus
   Excluded from current version due to more convenient ways to add a scale bar*/
public class BarGraphicTool extends GraphicTool {
	
	private static BarGraphicTool currentBar;
	
	public BarGraphicTool() {
		BarGraphicTool.currentBar = this;
	}
	
	BarGraphic model = new BarGraphic(); {
		
		model.setFillColor(Color.white);
		model.setAttachmentPosition(AttachmentPosition.defaultScaleBar());
		model.setProjectionType(2);
	}
	
	BarGraphic modelIcon() {
		BarGraphic output = model.copy();
		output.setFillColor(Color.black);
		output.getBarText().setTextColor(Color.black);
		return output;
	}
	
	{super.iconSet=TreeIconWrappingToolIcon.createIconSet(modelIcon());}
	
	/**creates a scale bar for image panel roi2.
	 * @return */
	public BarGraphic getOrcreateBar(ImageWorkSheet gmp, LocatedObject2D roi2) {
		if (roi2 instanceof 	BarGraphic) {
			return (BarGraphic) roi2;
		}
		GraphicLayer selectedContainer = gmp.getTopLevelLayer().getSelectedContainer();
			CombinedEdit undo=new CombinedEdit();
			int x = getClickedCordinateX();
			int y = getClickedCordinateY();
			
		BarGraphic bg = addBarGraphic( roi2, selectedContainer, undo, x, y);
		
		gmp.updateDisplay();
		this.getImageDisplayWrapperClick().getUndoManager().addEdit(undo);
		
		bg.showOptionsDialog();
		
		return bg;
	}


	/**adds a scale bar to the image*/
	public BarGraphic addBarGraphic(LocatedObject2D roi2, GraphicLayer selectedContainer,
			CombinedEdit undo, int x, int y) {
		BarGraphic bg = new BarGraphic();
	
		if(bg!=null) {
			bg.copyAttributesFrom(model);
			bg.copyColorsFrom(model);
			bg.setLocation(x, y);
	}
	
		
		if (roi2 instanceof ImagePanelGraphic ) {
			ImagePanelGraphic b=(ImagePanelGraphic) roi2;
			b.addLockedItem(bg);
			undo.addEditToList(new UndoAddOrRemoveAttachedItem(b, bg, false));
			
			/**	double[] dims = b.getScaleInfo().convertPixelsToUnits(new Dimension(b.getUnderlyingImageWidth(), b.getUnderlyingImageHeight()));
			double num = NumberUse.findNearest(dims[0]/3, BarGraphic.reccomendedBarLengths);
			bg.setLengthInUnits(num);*/
			
			BarGraphic.optimizeBar(bg, b);
			
			 
			ScaleBarExamplePicker.setBarDefaultsBasedOnHeight(b.getBounds().height, bg, model.getFillColor());
		
			bg.getAttachmentPosition().setToNearestInternalSnap(bg.getBounds(), b.getBounds(), new Point(x, y));
			
		
						} 
		
		selectedContainer.add(bg);
		
		UndoAddItem undo2 = new UndoAddItem(selectedContainer, bg);
		undo.addEditToList(undo2);
		
		
		
		
		return bg;
	}
	
	
	public void onPress(ImageWorkSheet gmp, LocatedObject2D roi2) {
		getOrcreateBar(gmp, roi2);
		
	}
	
	@Override
	public void showOptionsDialog() {
		model.showOptionsDialog();
		{super.iconSet=TreeIconWrappingToolIcon.createIconSet(modelIcon());}
	}
	
	
	@Override
	public String getToolTip() {
			return "Add a scale bar";
		}
	
	@Override
	public String getToolName() {
			return "Add Scale Bar";
		}


	/**returns a scale bar tool object*/
	public static BarGraphicTool getCurrentBarTool() {
		if(currentBar==null) currentBar=new BarGraphicTool();
		return currentBar;
	}




}

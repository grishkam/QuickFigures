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
 * Date Modified: Jan 4, 2021
 * Version: 2022.1
 */
package imageMenu;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import imageDisplayApp.StandardWorksheet;
import imageDisplayApp.ImageWindowAndDisplaySet;
import layout.BasicObjectListHandler;
import locatedObject.LocatedObject2D;
import messages.ShowMessage;
import sUnsortedDialogs.ObjectListChoice;

/**Combines multiple worksheets into one*/
public class CombineImages extends BasicMenuItemForObj {

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		ArrayList<DisplayedImage> choices = getChoices();
		if(choices.size()==0) {
			
			ShowMessage.showOptionalMessage("no worksheet", true, "no worksheet selected");
			return;
		}
		
		ImageWindowAndDisplaySet figure =  ImageWindowAndDisplaySet.createAndShowNew("new set", 0,0);;
		
		
			for(DisplayedImage figure2: choices) {
				CombineImages.combineInto(figure, figure2, false);
			BasicObjectListHandler boh = new BasicObjectListHandler();
		
		 boh.resizeCanvasToFitAllObjects(figure.getImageAsWorksheet());
		 figure.updateDisplay();
		}
	figure.autoZoom();
	}
	

public static ArrayList<DisplayedImage> getChoices() {
	 
		 ArrayList<DisplayedImage> alldisp = new CurrentFigureSet().getVisibleDisplays();
		ArrayList<DisplayedImage> foruse = new ObjectListChoice<DisplayedImage>("").selectMany("Choose Which to combine", alldisp, 4);
		 
		 return foruse;
	 
	 
}

public static DisplayedImage getChoice(String prompt) {
	 
	 ArrayList<DisplayedImage> allDisp = new CurrentFigureSet().getVisibleDisplays();
	DisplayedImage foruse = new ObjectListChoice<DisplayedImage>("").select(prompt, allDisp);
	 
	 return foruse;


}

	@Override
	public String getCommand() {
		return "Combine two or more";
	}

	@Override
	public String getNameText() {
		return "Combine Open Worksheets";
	}

	@Override
	public String getMenuPath() {
		return "Edit";
	}

	
	
	

	/**Combines two displays by adding one of them to the other*/
	public static void combineInto(ImageWindowAndDisplaySet recipient, DisplayedImage figure2, boolean horizontal) {
		if (recipient==null) return;
		if (figure2==null) return;
		int h = recipient.getTheSet().getHeight();
		int w = recipient.getTheSet().getWidth();
			Dimension dims = getCombinedSize(recipient, figure2, horizontal);
	if (horizontal)
			combineInto(recipient, figure2, new Point(w,0)); 
	else
			combineInto(recipient, figure2, new Point(0,h));
	
	
	recipient.getTheSet().setHeight(dims.height);
	recipient.getTheSet().setWidth(dims.width);
	}
	
	static Dimension getCombinedSize(ImageWindowAndDisplaySet recipient, DisplayedImage figure2, boolean horizontal) {
		
		int w=0;
		int h=0;
		if (horizontal)
			w=recipient.getTheSet().getWidth()+ figure2.getImageAsWorksheet().width();
		else
			w=Math.max(recipient.getTheSet().getWidth(), figure2.getImageAsWorksheet().width());
		
		if (!horizontal)
			h=recipient.getTheSet().getHeight()+ figure2.getImageAsWorksheet().height();
		else
			h=Math.max(recipient.getTheSet().height(), figure2.getImageAsWorksheet().height());
		
		return new Dimension(w,h);
	}
	
	
	
	
	/**Combines two displays by adding one of them to the other*/
	public static void combineInto(ImageWindowAndDisplaySet recipient, DisplayedImage addition, Point XYDisplace) {
		GraphicLayer layer = addition.getImageAsWorksheet().getTopLevelLayer();
		for(ZoomableGraphic ob1: layer.getAllGraphics()) {
			if (ob1 instanceof LocatedObject2D) {
				LocatedObject2D ob2=(LocatedObject2D) ob1;
				ob2.moveLocation(XYDisplace.getX(), XYDisplace.getY());
			}
		}
		recipient.updateDisplay();
		addition.updateDisplay();
		StandardWorksheet set = recipient.getTheSet();
		
		set.getTopLevelLayer().add(layer);
		
		
		
		set.setWidth((int) (set.getWidth()+XYDisplace.getX()));
		set.setHeight((int) (set.getHeight()+XYDisplace.getY()));
		
		addition.closeWindowButKeepObjects();//.getTheWindow().closeGroupWithoutObjectDeath();
		recipient.updateDisplay();
	}


	

}

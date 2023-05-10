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
 * Date Modified: May 10, 2023
 * Version: 2023.2
 */
package addObjectMenus;

import java.awt.Color;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import locatedObject.ScaleInfo;
import standardDialog.graphics.GraphicDisplayComponent;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.Edit;
import undo.UndoAddItem;
import utilityClasses1.ArraySorter;

/**A menu option that adds a scale bar to the selected image panel
 * this assumes that an image panel is selected*/
public class BarGraphicAdder extends BasicGraphicAdder {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BarGraphic modelbar = new BarGraphic(); {modelbar.moveLocation(-4, 0);}
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		
		
		
		
		return null;
	}
	
	
	/**performs the action. Subclasses need only implement the add method*/
	public void run() {
		GraphicLayer l = null;
		if(selector!=null &&selector.getSelectedLayer()!=null)l=selector.getSelectedLayer();
	
		if(l==null)
			l=this.selector.getWorksheet().getTopLevelLayer();
		CombinedEdit c=new CombinedEdit();
		
		
		
		ArrayList<ZoomableGraphic> items = selector.getSelecteditems();
		ArraySorter.removeThoseNotOfClass(items, ImagePanelGraphic.class);
		for(ZoomableGraphic item: items) {
				BarGraphic ag = getModelBar().copy();
				ImagePanelGraphic it = (ImagePanelGraphic) item;
				it.addLockedItem(ag);
				
				AbstractUndoableEdit2 undo = Edit.addItem(it.getParentLayer(), ag);
				c.addEditToList(undo);
			}
		
	
		
		
		
		
		if(c.empty()) {} else {
			this.getUndoManager().addEdit(c);
		}
	}


	@Override
	public String getCommand() {
	
		return "add scale bar";
	}

	@Override
	public String getMenuCommand() {
	
		return "Scale Bar";
	}
	
	public BarGraphic getModelBar() {
		return modelbar;
	}
	
	public BarGraphic getModelForIcon() {
		BarGraphic out = getModelBar().copy();
		out.setStrokeColor(Color.black);
		out.setFillColor(Color.black);
		out.getBarText().setTextColor(Color.black);
		out.setScaleInfo(new ScaleInfo("units",.10,.10));
		return out;
	}
	public Icon getIcon() {
		 BarGraphic m = getModelForIcon();
		if (m==null)return null;
		GraphicDisplayComponent out = new GraphicDisplayComponent(m, .5);
		m.setLocation(0,16);
		out.setCurrentItemInsets(new Insets(5,5,1,5));
		 return out;
	}
	
	
	@Override
	public String getMenuPath() {
		return "To selected panels";
	}
}
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
 * Version: 2021.1
 */
package figureFormat;

import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import layout.basicFigure.BasicLayout;
import undo.UndoLayoutEdit;

/**A graphical item picker for layouts*/
public class GridLayoutExamplePicker extends GraphicalItemPicker<DefaultLayoutGraphic> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	{super.optionname="Chose Layout";}
	
	public GridLayoutExamplePicker(DefaultLayoutGraphic model) {
		super(model);
	}

	@Override
	boolean isDesirableItem(Object o) {
		if (o instanceof DefaultLayoutGraphic) return true;
		return false;
	}
	
	/**if the object is a layout, changes the borders and spaces to fit the format of the modelLayout
	 * @return */
	@Override
	public AbstractUndoableEdit applyProperties(Object item) {
		if (this.getModelItem()==null) return null;
		if (!(item instanceof DefaultLayoutGraphic)) return null;
		DefaultLayoutGraphic item2=(DefaultLayoutGraphic) item;
		UndoLayoutEdit undo = new UndoLayoutEdit(item2);
		item2.generateCurrentImageWrapper();//required or the items within the layout wont get moved
		
		
		BasicLayout layout = item2.getPanelLayout();
		BasicLayout modelLayout = super.getModelItem().getPanelLayout();
		item2.getEditor().setBordersToModelLayout(layout, modelLayout);
		item2.getEditor().setLabelSpacesToModelLayout(layout, modelLayout);
		
		return undo;
	}
	
	/**returns true if the dialog should show this object itself in a combo box or popup menu.
	  if false, will just show the objects name*/
	boolean displayGraphicChooser() {
		return false;
	}

}

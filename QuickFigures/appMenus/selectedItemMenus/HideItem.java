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
package selectedItemMenus;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import layersGUI.GraphicTreeUI;
import locatedObject.Hideable;
import undo.UndoHideUnhide;

public class HideItem extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		return "Hide";
	}
	
	@Override
	public void setSelection(ArrayList<ZoomableGraphic> a) {
		array=new ArrayList<ZoomableGraphic>();
		for(ZoomableGraphic z: a) {
			if (z instanceof GraphicLayer) {
				GraphicLayer l=(GraphicLayer) z;
				array.addAll(l.getAllGraphics());
			} else array.add(z);
		}
	}

	@Override
	public void run() {
		UndoHideUnhide undo = new UndoHideUnhide(this.array, false);
		for(ZoomableGraphic i: this.array) {
			if (i instanceof Hideable) {
				((Hideable) i).setHidden(true);
			}
		}
		this.getSelector().getGraphicDisplayContainer().getUndoManager().addEdit(undo);
	}
	
	public boolean isValidForLayerSelector(LayerSelector graphicTreeUI) {
		if (graphicTreeUI instanceof GraphicTreeUI)
		return true;
		return false;
		}

}

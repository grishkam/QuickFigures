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
import locatedObject.LocatedObject2D;
import undo.CombinedEdit;
import undo.UndoAddItem;

public class DuplicateItem extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		return "Duplicate";
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
		CombinedEdit undo = new CombinedEdit();
		//new BasicOverlayHandler().copyRois(input)
	
		for(ZoomableGraphic i: this.array) {
			
			if (i==null) continue;
			if (i instanceof LocatedObject2D) {
				LocatedObject2D h=(LocatedObject2D) i;
				GraphicLayer layerforadd = i.getParentLayer();
				
				ZoomableGraphic copy1 = (ZoomableGraphic) h.copy();
				undo.addEditToList(new UndoAddItem(layerforadd, copy1));
				layerforadd.add(copy1);
			}
		}
		
		this.getSelector().getWorksheet().getUndoManager().addEdit(undo);

	}

}

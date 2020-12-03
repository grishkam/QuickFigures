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
package addObjectMenus;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import selectedItemMenus.CopyItem;
import undo.UndoAddManyItem;
import utilityClassesForObjects.LocatedObject2D;

import java.util.ArrayList;

public class PasteItem extends BasicGraphicAdder{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	GraphicLayer layer;
	

	
	
	
	

	@Override
	public ZoomableGraphic add(GraphicLayer layer) {
		if ( CopyItem.thearray==null) return null;
	
		
		ArrayList<ZoomableGraphic> copiedArray = new ArrayList<ZoomableGraphic> ();
		
		for(ZoomableGraphic s: CopyItem.thearray) {
			if (s instanceof LocatedObject2D) {
				LocatedObject2D l=(LocatedObject2D) s;
				l=l.copy();
				copiedArray.add((ZoomableGraphic) l);
				if (layer!=null ) layer.add((ZoomableGraphic) l);
			}
		}
		
		UndoAddManyItem undo = new UndoAddManyItem(layer,copiedArray );
		selector.getGraphicDisplayContainer().getUndoManager().addEdit(undo);
		
		return null;
	}

	






	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "Paste";
	}







	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		return getCommand();
	}

}
	//


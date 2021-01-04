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
import locatedObject.LocatedObject2D;
import locatedObject.Selectable;

public class DeselectItem extends HideItem implements MultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean deselect=true;
	
	public DeselectItem(boolean b) {
		deselect=b;
	}



	@Override
	public String getMenuCommand() {
		if (!deselect) return "Items of Same Type (press a)";
		return "De-Select All";
	}
	


	@Override
	public void run() {
		if (deselect) for(ZoomableGraphic i: this.array) {
			if (i==null) continue;
			if (i instanceof Selectable) {
				Selectable h=(Selectable) i;
				h.deselect();
			}
		}
		
		if (!deselect) for(ZoomableGraphic i: this.array) {
			selectThoseOfSameClass(i);
		}

	}



	private void selectThoseOfSameClass(ZoomableGraphic sel) {
		ArrayList<LocatedObject2D> all = selector.getImageWrapper().getLocatedObjects();
		for(LocatedObject2D item: all) {
			if(sel==null||item==null) continue;
			if(item.getClass()==sel.getClass()) item.select();
		}
	}
	
	public String getMenuPath() {return "Select";}
}

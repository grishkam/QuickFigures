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
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package selectedItemMenus;

import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects.ZoomableGraphic;
import iconGraphicalObjects.IconUtil;
import locatedObject.ArrayObjectContainer;
import locatedObject.LocatedObject2D;
import standardDialog.graphics.GraphicDisplayComponent;

/**selects all objects that are of the same class as a currently selected item*/
public class SelectAllButton extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LocatedObject2D targetType;
	@Override
	public String getMenuCommand() {
		return "Select Same Type (Press 'a')";
	}
	
	public SelectAllButton(LocatedObject2D typeSpeci) {
		this.targetType=typeSpeci;
	}

	@Override
	public void run() {
		
		
		ArrayList<ZoomableGraphic> selItems = this.getSelector().getSelecteditems();
		ArrayList<LocatedObject2D> all = this.getSelector().getImageWrapper().getLocatedObjects();
		if(targetType!=null) {
			ArrayObjectContainer.selectAllOfType(all, targetType);
			return;
		}
		
		for(ZoomableGraphic sel: selItems) {
			if(sel==null) continue;
			if(sel instanceof LocatedObject2D)
				ArrayObjectContainer.selectAllOfType(all, (LocatedObject2D) sel);
		}

	}
	
	public Icon getIcon() {
		return new GraphicDisplayComponent(IconUtil.createAllIcon("all")  );
	}
	


}

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
 * Version: 2023.2
 */
package addObjectMenus;

import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayer;
import iconGraphicalObjects.IconUtil;
import utilityClasses1.ArraySorter;

/**Adds a group to the figure and grouped selected shapes
  into that group*/
public class GroupAdder extends BasicGraphicAdder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		GraphicGroup gg = new GraphicGroup();
		ArrayList<ZoomableGraphic> i = this.selector.getSelecteditems();
		gc.add(gg);
	
		ArraySorter.removeThoseOfClass(i, GraphicLayer.class);
		int index = -1;
		for(ZoomableGraphic item:i) {
			if (item instanceof KnowsParentLayer) {
				KnowsParentLayer it=(KnowsParentLayer) item;
				
				if(it.getParentLayer()!=null)
					{
					index=gc.getItemArray().indexOf(item);
					it.getParentLayer().remove(item);
					
					}
				if(gg.getParentLayer()!=null)
				gg.getTheInternalLayer().add(item);
			}
		
		}
		if (index>-1) gc.moveItemToIndex(gg, index);
		return gg;
	}

	@Override
	public String getCommand() {
		return "add group";
	}

	@Override
	public String getMenuCommand() {
		return "Add Shape Group";
	}
	
	@Override
	public String getMenuPath() {
		return "Shapes";
	}
	
	public Icon getIcon() {
		return IconUtil.createFolderIcon(false, GraphicGroup.defaultFolderColor);
	}

}

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
 * Version: 2021.1
 */
package undo;

import graphicalObjects.KnowsParentLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import layersGUI.GraphicSetDisplayTree;

/**an undoable edit for removing and item from a layer*/
public class UndoAbleEditForRemoveItem extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicLayer layer;
	private ZoomableGraphic added;
	private int index;

	
	public UndoAbleEditForRemoveItem(GraphicLayer layer, ZoomableGraphic added) {
		
		this.added=added;
		if ((layer==null||!layer.getItemArray().contains(added))&&added!=null) {
			KnowsParentLayer kpl=(KnowsParentLayer) added;
			this.layer=kpl.getParentLayer();
		} else
		this.layer=layer;
		this.actedOnObjects.add(added);
		if(this.layer!=null)
		this.index=this.layer.getItemArray().indexOf(added);//finds the stack index
		
	}
	
	
	
	public UndoAbleEditForRemoveItem(GraphicLayer origin, ZoomableGraphic item, GraphicSetDisplayTree tree) {
		this(origin, item);
		this.tree=tree;
	}

	public void redo() {
		if (layer!=null)layer.remove(added);
	}
	
	public void undo() {
		if (layer!=null) 
		{
		
			layer.add(added);
			if (index<0||index>=layer.getItemArray().size()) return;
			/**moves item to its original stack index*/
			ZoomableGraphic atindex = layer.getItemArray().get(index);
			if (added!=null||atindex!=null)
				layer.swapmoveObjectPositionsInArray(added, atindex);
			
			if (tree!=null) {tree.addUserObjectToSelection(added);}
			if (tree!=null) {tree.expandPathForUserObject(layer);}
		}
	}

	


}

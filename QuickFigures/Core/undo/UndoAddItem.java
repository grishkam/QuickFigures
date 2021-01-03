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
package undo;


import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import layersGUI.GraphicSetDisplayTree;

/**undo for the addition of an item to the layer*/
public class UndoAddItem extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicLayer layer;
	private ZoomableGraphic added;
	private GraphicSetDisplayTree tree;
	
	public UndoAddItem(GraphicLayer layer, ZoomableGraphic added) {
		this.layer=layer;
		this.setAddedItem(added);
		this.actedOnObjects.add(added);
	}
	
	public UndoAddItem(GraphicLayer destination, ZoomableGraphic item, GraphicSetDisplayTree tree) {
		this(destination, item);
		this.tree=tree;
	}

	public void undo() {
		if (layer!=null)layer.remove(getAddedItem());
		
		
	}
	public void redo() {
		if (layer!=null)layer.add(getAddedItem());
		if (tree!=null) {tree.addUserObjectToSelection(getAddedItem());}
		if (tree!=null) {tree.expandPathForUserObject(layer);}
	}

	public ZoomableGraphic getAddedItem() {
		return added;
	}

	public void setAddedItem(ZoomableGraphic added) {
		this.added = added;
	}
	
	

}

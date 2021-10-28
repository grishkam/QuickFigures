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
 * Version: 2021.2
 */
package undo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import layersGUI.GraphicSetDisplayTree;

/**Implements undo for an operation that reorders items in a layer
 * also updats the layers window to fit*/
public class UndoReorder extends AbstractUndoableEdit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicSetDisplayTree tree;
	ArrayList<ZoomableGraphic> oldOrder ;
	GraphicLayer layer;
	private ArrayList<ZoomableGraphic> newOrder;
	
	
	public UndoReorder(GraphicLayer gl) {
		layer = gl;
		 oldOrder = new ArrayList<ZoomableGraphic>();
		 oldOrder.addAll(gl.getItemArray());
	}
	
	public void saveNewOrder() {
		 newOrder = new ArrayList<ZoomableGraphic>();
		 newOrder.addAll(layer.getItemArray());
	}
	
	@Override
	public void undo() {
		layer.setOrder(oldOrder);
		if (tree!=null) tree.repaint();
	}
	
	@Override
	public void redo() {
		if (newOrder!=null) {
			layer.setOrder(newOrder);
		}
		if (tree!=null) tree.repaint();
	}

	public void setTree(GraphicSetDisplayTree tree) {
		this.tree=tree;
		
	}
	
	public boolean canRedo() {
		return true;
	}

}

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


import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

/**undo for any changes to the direct contents of a layer*/
public class UndoLayerContentChange extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicLayer layer;
	private ArrayList<ZoomableGraphic> iItems;//the original content
	private ArrayList<ZoomableGraphic> fItems;//the final content
	
	public UndoLayerContentChange(GraphicLayer layer) {
		this.layer=layer;
		iItems=currentItems(layer);
		
	}

	protected ArrayList<ZoomableGraphic> currentItems(GraphicLayer layer) {
		ArrayList<ZoomableGraphic> i = new ArrayList<ZoomableGraphic>(); 
		i.addAll(layer.getItemArray());
		return i;
	}
	
	public void establishFinalState() {
		fItems=currentItems(layer);
	}

	public void undo() {
		for(ZoomableGraphic i:currentItems(layer)) {layer.remove(i);}
		for(ZoomableGraphic i:iItems) {layer.addItemToLayer(i);;}
		
	}
	public void redo() {
		for(ZoomableGraphic i:currentItems(layer)) {layer.remove(i);}
		for(ZoomableGraphic i:fItems) {layer.addItemToLayer(i);;}
	}
	
	

}

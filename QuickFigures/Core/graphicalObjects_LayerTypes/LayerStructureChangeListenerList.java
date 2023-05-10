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
 * Date Modified: Jan 4, 2021
 * Version: 2023.2
 */
package graphicalObjects_LayerTypes;

import java.io.IOException;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import utilityClasses1.ArraySorter;

/**This class maintains a list of layer structure change listeners.
 * listeners are notified when objects are added to, removes from or moved with the layer.
 * methods in this class also handle listener notifications*/

public class LayerStructureChangeListenerList extends ArrayList<LayerStructureChangeListener<ZoomableGraphic, GraphicLayer>> implements LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void itemsSwappedInContainer(GraphicLayer gc, ZoomableGraphic z1,
			ZoomableGraphic z2) {
		for(LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> ar:this) {
		if (ar==null) continue;
			ar.itemsSwappedInContainer(gc, z1, z2);
		}
		
	}

	@Override
	public void itemRemovedFromContainer(GraphicLayer gc, ZoomableGraphic z) {
		for(LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> ar:this) {
			if (ar==null) continue;
			ar.itemRemovedFromContainer(gc, z);
		}
	}

	@Override
	public void itemAddedToContainer(GraphicLayer gc, ZoomableGraphic z) {
		for(LayerStructureChangeListener<ZoomableGraphic, GraphicLayer> ar:this) {
			if (ar==null) continue;
			ar.itemAddedToContainer(gc, z);
		}
		
	}

	@Override
	public GraphicLayer getSelectedLayer() {
		return null;
	}
	
	private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException {
		/**ensures that any non useful items stored in this list will not be serialized
		  */
		ArraySorter.removeDeadItems(this);
		ArraySorter.removeNonSerialiazble(this);
		out.defaultWriteObject();
	}

	
	
	
}

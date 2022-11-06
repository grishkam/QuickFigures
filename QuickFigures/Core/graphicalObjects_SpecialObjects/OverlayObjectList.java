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
 * Date Created: Nov 4, 2022
 * Date Modified: Nov 6, 2022
 * Copyright (C) 2022 Gregory Mazo
 * Version: 2022.1
 */
/**
 
 * 
 */
package graphicalObjects_SpecialObjects;

import java.io.Serializable;
import java.util.ArrayList;

import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import undo.CombinedEdit;

/**
 A class for storing a list of objects that are drawn in front of an image panel
 */
public class OverlayObjectList extends GraphicLayerPane implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @param name
	 */
	public OverlayObjectList(String name) {
		super(name);
	}


	/**
	 * 
	 */
	public OverlayObjectList() {
		this("Overlay");
	}

	/**returns the object list*/
	public ArrayList<?> getOverlayObjects() {
		return super.getAllGraphics();
	}


	/**Creates a copy*/
	public  OverlayObjectList copy() {
		OverlayObjectList output = new  OverlayObjectList(description);
		for(Object o:this.getOverlayObjects()) {
			if(o instanceof BasicGraphicalObject)
			 output.addItemToArray(((BasicGraphicalObject) o).copy());
		}
		return output;
	}
	
	/**Called when the user tries to move objects between layers*/
	public boolean canAccept(ZoomableGraphic z) {
		if(z instanceof GraphicLayer)
			return false;
		return super.canAccept(z);
	}
	
	/**returns an undo*/
	public CombinedEdit getUndoForEditWindow() {
		CombinedEdit output = new CombinedEdit();
		for(Object o:this.getOverlayObjects()) {
			if(o instanceof BasicGraphicalObject)
			 output.addEditToList(((BasicGraphicalObject) o).provideDragEdit());
		}
		return output;
	}
}

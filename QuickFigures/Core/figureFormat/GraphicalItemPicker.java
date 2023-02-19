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
 * Version: 2023.1
 */
package figureFormat;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects_LayerTypes.GraphicLayer;
import locatedObject.ObjectContainer;

/**Class contains methods for 3 purposes.
1) Identify items that belong to a certain category
2) store and example item of the given category with traits exemplary of a desired format
3) Apply the traits that are characteristic of that format to all of the item

This subclass of (@see ItemPicker) slects objects of class BasicGraphicalObject
*/
public abstract class GraphicalItemPicker<ItemType extends BasicGraphicalObject> extends ItemPicker<ItemType> {

	/**
	  
	 */
	private static final long serialVersionUID = 1L;
	String optionname="Item";
	
	public GraphicalItemPicker(ItemType model) {
		super(model);
	}

	/**Determines if the object is 
	 * in the right category for this picker
	 * subclasses overwrite this*/
	@Override
	boolean isDesirableItem(Object o) {
		return false;
	}
	
	/**Searches the array for desirable objects.
	 * returns the list of desired items as an array of basic graphical objects*/
	public ArrayList<BasicGraphicalObject> getDesiredItemsAsGraphicals(ArrayList<?> input) {
		ArrayList<BasicGraphicalObject> output = new ArrayList<BasicGraphicalObject>();
		for(Object ob:input) {
			if (isDesirableItem(ob)) try {
				output.add((BasicGraphicalObject) ob);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return output;
	}
	/**Searches the object container for desirable objects.
	 * returns the list of desired items as an array of basic graphical objects*/
	public ArrayList<BasicGraphicalObject> getDesiredItemsAsGraphicals(ObjectContainer oc ) {
		return getDesiredItemsAsGraphicals(oc.getLocatedObjects());
	}
	/**Searches the layer for desirable objects.
	 * returns the list of desired items as an array of basic graphical objects*/
	public ArrayList<BasicGraphicalObject> getDesiredItemsAsGraphicals(GraphicLayer oc ) {
		return getDesiredItemsAsGraphicals(oc.getAllGraphics());
	}

	@Override
	public String getOptionName() {
		return optionname;
	}

	/**Will Apply the traits of the desired format to the given object
	 * @return */
	@Override
	public AbstractUndoableEdit applyProperties(Object item) {
		return null;
	}
	
	/**returns true if the dialog should show this object itself in a combo box or popup menu.
	  if false, will just show the objects name*/
	boolean displayGraphicChooser() {
		return true;
	}
	
	/**When given a display layer for a multidimensional image, changes the format of this object
	  to better fit the image*/
	public void setTheSizeFor(MultichannelDisplayLayer wrap) {
		
	}

	
	
}

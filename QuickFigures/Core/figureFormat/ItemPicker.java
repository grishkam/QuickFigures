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

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import locatedObject.ObjectContainer;
import logging.IssueLog;
import undo.CombinedEdit;

/**Class contains methods for 3 purposes.
	1) Identify items that belong to a certain category
	2) store and example item of the given category with traits exemplary of a desired format
	3) Apply the traits that are characteristic of that format to all of the item
	
	Each subclass targets a different sort of object
	 */
public abstract class ItemPicker<ItemType extends Serializable> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**the model item that has been selected as an example of the target format*/
	protected ItemType modelItem;
	
	public ItemPicker() {
	}
	
	public ItemPicker(ItemType model) {
		modelItem=model;
	}
	

	/**returns true if the object has the right traits to belong to the category*/
	abstract boolean isDesirableItem(Object object);
	/**returns a String describing what sort of category the picker targets*/
	public abstract String getOptionName() ;
	
	/**Applies the format to the target
	 * @return */
	public abstract AbstractUndoableEdit applyProperties(Object target);
	/**Applies the format to the list
	 * @return */
	public  CombinedEdit applyPropertiesToList(Iterable<?> list) {
		CombinedEdit undo = new CombinedEdit();
	  for(Object o: list) {
		  undo.addEditToList(
				  applyProperties(o)
				  );
	  }
	  undo.establishFinalState();
	  return undo;
  }
	
	/**Returns a new list containing a subset of the input list.
	  The returned subset contains only objects that 
	  fit the criteria for the category*/
	@SuppressWarnings("unchecked")
	ArrayList<ItemType> getDesiredItemsOnly(ArrayList<?> input) {
		ArrayList<ItemType> output = new ArrayList<ItemType>();
		for(Object ob:input) {
			if (isDesirableItem(ob)) try {
				output.add((ItemType) ob);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return output;
	}
	
	/**Returns an array of objects from the container that match the criteria. */
	public ArrayList<ItemType> getDesiredObjects(ObjectContainer theContainer) {
		return getDesiredItemsOnly(theContainer.getLocatedObjects());
	}
	

	/**returns unique a key referring to this object. */
	public String getKeyName() {
		return getOptionName();
	}


/**the getter method for the model item*/
	public ItemType getModelItem() {
		return modelItem;
	}
	/**the and setter method for the model item. allows the item to be set to null
	  but does not allow anything that fails the isDesirableItem method to be set*/
	@SuppressWarnings("unchecked")
	public void setModelItem(Object modelItem) {
		if (modelItem==null) {
			this.modelItem=null;
			return;
		}
		if (!isDesirableItem(modelItem)) return;
		
		
		try{this.modelItem = (ItemType)modelItem;} catch (Exception e) {
			/**The isDesireable item method should have performed the check but just in case someone did not implements that properly*/
			e.printStackTrace();
			IssueLog.log("problem. wrong class");
		}
	}
	
	

}

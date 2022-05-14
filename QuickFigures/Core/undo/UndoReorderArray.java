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
 * Version: 2022.1
 */
package undo;

import java.util.ArrayList;

import javax.swing.undo.AbstractUndoableEdit;

import utilityClasses1.ArraySorter;
import utilityClasses1.ItemSwapper;

/**Implements undo for an operation that reorders items in a layer
 * also updats the layers window to fit*/
public class UndoReorderArray<TargetType> extends AbstractUndoableEdit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<TargetType> oldOrder ;
	
	private ArrayList<TargetType> newOrder;
	private ArrayList<TargetType> list;
	
	
	public UndoReorderArray(ArrayList<TargetType> gl) {
		this.list=gl;
		 oldOrder = new ArrayList<TargetType>();
		 oldOrder.addAll(gl);
	}
	

	
	public void saveNewOrder() {
		 newOrder = new ArrayList<TargetType>();
		 newOrder.addAll(list);
	}
	
	@Override
	public void undo() {
		new ArraySorter<TargetType>().setOrder(oldOrder, list, createItemSwapper());
		
	}

	/**
	 * @return
	 */
	public ItemSwapper<TargetType> createItemSwapper() {
		return new ItemSwapper<TargetType>() {

			@Override
			public void swapItemPositions(TargetType itemType, TargetType itemType2) {
				int i = list.indexOf(itemType);
				int j = list.indexOf(itemType2);
				list.set(i, itemType2);
				list.set(j, itemType);
			}};
	}
	
	@Override
	public void redo() {
		new ArraySorter<TargetType>().setOrder(newOrder, list, createItemSwapper());
		
	}

	
	
	public boolean canRedo() {
		return true;
	}

}

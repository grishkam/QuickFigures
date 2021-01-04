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
package undo;


import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import locatedObject.LocatedObject2D;
import locatedObject.TakesAttachedItems;

public class Edit {
	public static AbstractUndoableEdit2 addItem(GraphicLayer parentLayer, ZoomableGraphic z ) {
		UndoAddItem output = new UndoAddItem(parentLayer, z);
		parentLayer.add(z);
		return output;
	}
	
	public static UndoAbleEditForRemoveItem removeItem(GraphicLayer parentLayer, ZoomableGraphic z ) {
		UndoAbleEditForRemoveItem output = new UndoAbleEditForRemoveItem(parentLayer, z);
		parentLayer.remove(z);
		return output;
	}
	
	public static UndoReorder swapItemOrder(GraphicLayer parentLayer, ZoomableGraphic z, ZoomableGraphic z2) {
		UndoReorder output = new UndoReorder(parentLayer);
		parentLayer.swapItemPositions(z, z2);
		return output;
	}
	
	public static CombinedEdit createGenericEdit(Iterable<?> objects) {
		CombinedEdit output=new CombinedEdit();
		for(Object item: objects) {
			if(item instanceof ProvidesDialogUndoableEdit) {
				output.addEditToList(((ProvidesDialogUndoableEdit) item).provideUndoForDialog());
			}
		}
		
		return output;
	}
	
	public static CombinedEdit createGenericEditForItem(Object item) {
		CombinedEdit output=new CombinedEdit();
	
			if(item instanceof ProvidesDialogUndoableEdit) {
				output.addEditToList(((ProvidesDialogUndoableEdit) item).provideUndoForDialog());
			}
		
		return output;
	}
	
	public static AbstractUndoableEdit2 detachItem(TakesAttachedItems t, LocatedObject2D target) {
		UndoTakeLockedItem undo = new UndoTakeLockedItem(t, target, true);
		t.removeLockedItem(target);
		return undo;
	}
}

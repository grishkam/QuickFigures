package undo;


import javax.swing.undo.UndoableEdit;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.TakesLockedItems;

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
	
	public static CompoundEdit2 createGenericEdit(Iterable<?> objects) {
		CompoundEdit2 output=new CompoundEdit2();
		for(Object item: objects) {
			if(item instanceof ProvidesDialogUndoableEdit) {
				output.addEditToList(((ProvidesDialogUndoableEdit) item).provideUndoForDialog());
			}
		}
		
		return output;
	}
	
	public static CompoundEdit2 createGenericEditForItem(Object item) {
		CompoundEdit2 output=new CompoundEdit2();
	
			if(item instanceof ProvidesDialogUndoableEdit) {
				output.addEditToList(((ProvidesDialogUndoableEdit) item).provideUndoForDialog());
			}
		
		return output;
	}
	
	public static AbstractUndoableEdit2 detachItem(TakesLockedItems t, LocatedObject2D target) {
		UndoTakeLockedItem undo = new UndoTakeLockedItem(t, target, true);
		t.removeLockedItem(target);
		return undo;
	}
}

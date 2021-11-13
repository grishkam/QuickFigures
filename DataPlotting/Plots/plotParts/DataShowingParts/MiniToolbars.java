/**
 * Author: Greg Mazo
 * Date Modified: Nov 13, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package plotParts.DataShowingParts;

import actionToolbarItems.SetNumberX;
import actionToolbarItems.SetNumberX.ValueSetter;
import handles.miniToolbars.ActionButtonHandleList;
import undo.AbstractUndoableEdit2;
import undoForPlots.DataShapeUndo;

/**
 class that contains methods for adding 
 */
public class MiniToolbars {

	/**
	 * @param list
	 */
	public static void addExtraHandles(DataShowingShape dataShape, ActionButtonHandleList list) {
		ValueSetter widthSetter = new SetNumberX.ValueSetter() {
			
			@Override
			public void setValue(Object a, double value2) {
				if(a instanceof DataShowingShape) {
					DataShowingShape d=(DataShowingShape) a;
					d.setBarWidth(value2);
				}
				
			}
			
			@Override
			public double getValue(Object a) {
				if(a instanceof DataShowingShape) {
					DataShowingShape d=(DataShowingShape) a;
					return ((DataShowingShape) a).getBarWidth();
				}
				throw new NullPointerException();
				
			}
			
			@Override
			public AbstractUndoableEdit2 createUndo(Object a) {
				if(a instanceof DataShowingShape) {
					DataShowingShape d=(DataShowingShape) a;
					
					;
					return DataShapeUndo.createCompleteUndoForShape(d);
				}
				return null;
			}
		};
		
		SetNumberX operator = new SetNumberX(dataShape, dataShape.getBarWidth(), widthSetter, "Bar Width", null);
		list.addOperationList(operator, operator.createManyNumberSetters(new int[] {4,12,20,40}));
	}
}

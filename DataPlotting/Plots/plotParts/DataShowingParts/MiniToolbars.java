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

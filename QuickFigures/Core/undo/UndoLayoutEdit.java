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
 * Version: 2021.2
 */
package undo;

import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import layout.basicFigure.BasicLayout;

/**An undo for changes to a layout.
 * also an undo for any chages to the objects within a layout
 * @see UndoMoveItems
 * @see DefaultLayoutGraphic
 * @see BasicLayout
 * */
public class UndoLayoutEdit extends UndoMoveItems {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	BasicLayout layout;
	BasicLayout oldlayout;
	BasicLayout newlayout;
	
	public UndoLayoutEdit(DefaultLayoutGraphic layoutGraphic) {
		super(layoutGraphic.generateCurrentImageWrapper().getLocatedObjects());
		 layout= layoutGraphic.getPanelLayout();
		 oldlayout=layout.duplicate();
	}
	
	public UndoLayoutEdit(BasicLayout panelLayout) {
		super(panelLayout.getVirtualWorksheet().getLocatedObjects());
		 layout= panelLayout;
		 oldlayout=layout.duplicate();
		 
	}
	
	public void establishFinalLocations() {
		super.establishFinalLocations();
		newlayout=layout.duplicate();
		
	}
	
	public void undo() {
		super.undo();
		layout.setToMatch(oldlayout);
	}
	
	public void redo() {
		super.redo();
		layout.setToMatch(newlayout);
	}

}

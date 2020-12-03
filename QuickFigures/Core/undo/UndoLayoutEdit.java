/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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

import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;

public class UndoLayoutEdit extends UndoMoveItems {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	BasicMontageLayout layout;
	BasicMontageLayout oldlayout;
	BasicMontageLayout newlayout;
	
	public UndoLayoutEdit(MontageLayoutGraphic layoutGraphic) {
		super(layoutGraphic.generateCurrentImageWrapper().getLocatedObjects());
		 layout= layoutGraphic.getPanelLayout();
		 oldlayout=layout.duplicate();
	}
	
	public UndoLayoutEdit(BasicMontageLayout panelLayout) {
		super(panelLayout.getWrapper().getLocatedObjects());
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

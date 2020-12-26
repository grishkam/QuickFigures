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

import graphicalObjects_SpecialObjects.BarGraphic;

public class UndoScaleBarEdit extends AbstractUndoableEdit2 {

	private BarGraphic fBar;
	private BarGraphic iBar;
	private BarGraphic theBar;
	private UndoSnappingChange barTextUndo;
	
	public UndoScaleBarEdit(BarGraphic a) {
		theBar=a;
		if(theBar!=null)
			{iBar=theBar.copy();
			barTextUndo=new UndoSnappingChange(theBar.getBarText());
		 }
	}
	

	public void establishFinalState() {
		if(theBar!=null)
			fBar=theBar.copy();
		if( barTextUndo!=null)  barTextUndo.establishFinalState();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void redo() {
		if(theBar!=null)
			theBar.copyAttributesFrom(fBar);
		if( barTextUndo!=null)  barTextUndo.redo();
	}
	
	public void undo() {
		if(theBar!=null)
			theBar.copyAttributesFrom(iBar);
		if( barTextUndo!=null)  barTextUndo.undo();
	}

}

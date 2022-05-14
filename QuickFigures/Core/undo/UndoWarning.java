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

import standardDialog.StandardDialog;
import standardDialog.strings.InfoDisplayPanel;

public class UndoWarning extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String st;
	
	public UndoWarning(String message) {
		this.st=message;
	}
	
	public void redo() {
		//undo();
	}
	
	public void undo() {
		
		StandardDialog undo = new StandardDialog("Undo Warning",false);
		undo.add("PPI", 
				new InfoDisplayPanel("Warning :", "Undo for this item is inperfect. Sorry about that"+'\n'+st));
		undo.setLocation(800, 800);

		undo.showDialog();
	}

}

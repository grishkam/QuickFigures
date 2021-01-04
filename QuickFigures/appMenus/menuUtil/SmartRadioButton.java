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
package menuUtil;

import javax.swing.JRadioButton;

import applicationAdapters.CanvasMouseEvent;
import logging.IssueLog;
import undo.UndoManagerPlus;

/**A special JMenu item that also stores an undo manager*/
public final class SmartRadioButton extends JRadioButton implements  SmartMenuItem{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected CanvasMouseEvent me;
	protected UndoManagerPlus undoManager;

	@Override
	public void setLastMouseEvent(CanvasMouseEvent e) {
		this.me=e;
		
	}

	@Override
	public void setUndoManager(UndoManagerPlus undoManager) {
		IssueLog.log("undo manager being set to "+undoManager);
		this.undoManager=undoManager;
		
	}

	@Override
	public UndoManagerPlus getUndoManager() {
		// TODO Auto-generated method stub
		return undoManager;
	}

}

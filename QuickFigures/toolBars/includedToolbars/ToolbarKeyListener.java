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
package includedToolbars;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import applicationAdapters.DisplayedImage;
import graphicActionToolbar.CurrentFigureSet;
import logging.IssueLog;

public class ToolbarKeyListener implements KeyListener {

	public ToolbarKeyListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
			DisplayedImage figureDisplay = new CurrentFigureSet().getCurrentlyActiveDisplay();
			
			boolean WindowsOrMacMeta=false;
	 		if (IssueLog.isWindows() &&arg0.isControlDown()) {
	 			
	 			WindowsOrMacMeta=true;
	 		}
	 		if (!IssueLog.isWindows() &&arg0.isMetaDown()) WindowsOrMacMeta=true;
	 		
	 		
	 		/**implementation of undo and redo*/
	 		if (arg0.getKeyCode()==KeyEvent.VK_Z&&WindowsOrMacMeta) {
				if (figureDisplay.getUndoManager().canUndo()) {
					figureDisplay.getUndoManager().undo();
								
					};
			}
	 		

			if (arg0.getKeyCode()==KeyEvent.VK_Y&&WindowsOrMacMeta) {
						
						if (figureDisplay.getUndoManager().canRedo())figureDisplay.getUndoManager().redo();
					}
			if(figureDisplay!=null)
			 figureDisplay.updateDisplay();
	
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}

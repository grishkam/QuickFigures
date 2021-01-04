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
package includedToolbars;

import java.awt.dnd.DropTarget;

import javax.swing.AbstractButton;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.MenuBarForApp;
import basicMenusForApp.OpeningFileDropHandler;
import externalToolBar.AbstractExternalToolset;
import genericTools.GeneralTool;
import genericTools.ToolBit;
import logging.IssueLog;

/**The superclass for most of the QuickFigures toolbars*/
public class QuickFiguresToolBar extends AbstractExternalToolset<DisplayedImage>  {


	public QuickFiguresToolBar() {
		this.toolbar.setFloatable(false);
		
	}
	
	/**shows the JFrame*/
	public void showFrame() {
		if (this.usesMenuBar())getframe().setJMenuBar(new MenuBarForApp());
		super.showFrame();
		
	}
	
	public synchronized void addToolKeyListeners() {
		
		for (AbstractButton jb: super.buttons) {
			jb.addKeyListener(new ToolbarKeyListener());
		}
	}
	
	public void addToolBit(ToolBit t) {
		addTool(new  GeneralTool( t));
	}
	public void addDragAndDrop() {
		new DropTarget(getframe(), new OpeningFileDropHandler());
	}

	/**whether to set the menu bar for this class*/
	protected boolean usesMenuBar() {
		if (!IssueLog.isWindows()) return true;
		return false;
	}


}

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
 * Date Modified: Jan 4, 2021
 * Version: 2021.1
 */
package basicMenusForApp;

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import applicationAdapters.DisplayedImage;
import layersGUI.GraphicTreeUI;
import messages.ShowMessage;

/**A menu item that shows the layers GUI*/
public class TreeShower  extends BasicMenuItemForObj {

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if(diw==null) {
			ShowMessage.showMessages("open a worksheet first");
			return;
		}
		GraphicTreeUI tree = new GraphicTreeUI(diw.getImageAsWorksheet());
		tree.showTreeForLayerSet(diw.getImageAsWorksheet()) ;
		TreeWindowCloser closer = new TreeWindowCloser(tree, diw.getWindow());
		
		diw.getWindow().addWindowListener(closer);
	}

	@Override
	public String getCommand() {
		return "ShowMeTheTree";
	}

	@Override
	public String getNameText() {
		return "Show Layers";
	}

	@Override
	public String getMenuPath() {
		return "Edit";
	}
	
	/**A window listener that closes the tree window if the worksheet window is closed*/
	class TreeWindowCloser implements WindowListener {
		GraphicTreeUI currentTree=null;
		public TreeWindowCloser(GraphicTreeUI tree, Window w) {
			
			currentTree=tree;
		}

		@Override
		public void windowActivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			
			currentTree.closeWindow();
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}

}

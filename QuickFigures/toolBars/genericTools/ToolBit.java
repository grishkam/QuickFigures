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
package genericTools;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import externalToolBar.DragAndDropHandler;
import icons.IconSet;
import imageDisplayApp.ImageWindowAndDisplaySet;

/**interface determines the functions of a tool within one of the toolbars
 * that can be installed onto the toolbar
   */
public interface ToolBit {
	
	IconSet getIconSet();

	void mousePressed();

	void mouseDragged();

	void mouseEntered();
	
	void mouseExited();
	
	void mouseReleased();

	void mouseMoved();

	void mouseClicked();


	void setToolCore(ToolCore toolCore);



	void showOptionsDialog();


	ArrayList<JMenuItem> getPopupMenuItems();

	boolean keyPressed(KeyEvent e);

	boolean keyReleased(KeyEvent e);

	boolean keyTyped(KeyEvent e);

	String getToolName();
	
	void handleFileListDrop(ImageWindowAndDisplaySet imageAndDisplaySet, Point location, ArrayList<File> file);
	
	DragAndDropHandler getDragAndDropHandler();
	
	String getToolTip();
	
	boolean isActionTool();
	
	void performLoadAction();
	
	boolean treeSetSelectedItem(Object o);
	
	void onToolChange(boolean b);
	
	String getToolSubMenuName();

	

}

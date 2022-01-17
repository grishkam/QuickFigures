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
 * Version: 2022.0
 */
package genericTools;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import externalToolBar.DragAndDropHandler;
import icons.IconSet;
import imageDisplayApp.ImageWindowAndDisplaySet;

/**An interface that determines the propertie of a general tool.
 * @see GeneralTool
 * @see BasicToolBit
 * @see ToolCore
 * Simpler to code that 
 * interface determines the functions of a tool within one of the toolbars
  that can be installed onto the toolbar
   */
public interface ToolBit {
	
	IconSet getIconSet();

	/**Called upon certain tool actions*/
	void mousePressed();
	void mouseDragged();
	void mouseEntered();
	void mouseExited();
	void mouseReleased();
	void mouseMoved();
	void mouseClicked();
	
	boolean keyPressed(KeyEvent e);
	boolean keyReleased(KeyEvent e);
	boolean keyTyped(KeyEvent e);

	/**Sets the tool core that stores information about */
	void setToolCore(ToolCore toolCore);

	void showOptionsDialog();


	ArrayList<JMenuItem> getPopupMenuItems();



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

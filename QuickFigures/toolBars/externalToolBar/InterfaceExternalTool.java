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
 * Version: 2022.2
 */
package externalToolBar;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;

import applicationAdapters.CanvasMouseEvent;
import imageDisplayApp.ImageWindowAndDisplaySet;

/**Tools that are installed directly on a toolbar implement this interface*/
public interface InterfaceExternalTool<ImageType>  {
	
	/**standard methods for how the tool reacts to mouse actions and key strokes */
	public void mousePressed(ImageType imp, CanvasMouseEvent e) ;
	public void mouseClicked(ImageType imp, CanvasMouseEvent e) ;
	public void mouseDragged(ImageType imp, CanvasMouseEvent e) ;
	public void mouseReleased(ImageType imp, CanvasMouseEvent e) ;
	public void mouseExited(ImageType imp, CanvasMouseEvent e) ;
	public void mouseEntered(ImageType imp, CanvasMouseEvent e) ;
	public void mouseMoved(ImageType imp, CanvasMouseEvent e) ;
	public boolean keyPressed(ImageType imp, KeyEvent e) ;
	public boolean keyReleased(ImageType imp, KeyEvent e) ;
	public boolean keyTyped(ImageType imp, KeyEvent e) ;
	public InterfaceKeyStrokeReader<ImageType> getCurrentKeyStrokeReader();
	
	
	/**The method that shows an options meny for the tool */
	public void showOptionsDialog();
	
	/**A method that shows the menu for menu tools. */
	public void controlClickDialog(Component c);
	/**A method that performs an action for action tools*/
	public void performLoadAction();
	
	/**Retrieves a list of menu items if one wants a simplistic popup menu to appear on 
	  control click*/
	ArrayList<JMenuItem> getPopupMenuItems();
	
	
	
	/**lets the code know that this is a tool that just performs an action is not to be set as the current tool*/
	boolean isActionTool();
	/**true if this is a tool that just shows a menu and nothing more. false otherwise.*/
	boolean isMenuOnlyTool();
	
	/**returns the basic traits*/
	String getToolName() ;
	
	/**Text that helps the user understand what the tool does*/
	public String getToolTip();
	
	/**icons for the tool*/
	Icon getToolNormalIcon() ;

	/**icons for the tool to display when it is selected*/
	Icon getToolPressedImageIcon() ;
	
	/**icons for the tool to display when it is rolled over*/
	Icon getToolRollOverImageIcon() ;

	/**Each tool may function differently for drag and drop operation.
	  the returned object determines how drag and drop is handled.
	  */
	public DragAndDropHandler getDraghandler();
	
	public void handleFileListDrop(ImageWindowAndDisplaySet imageAndDisplaySet, Point location, ArrayList<File> file);
	
	
	/**Called when the toolbar switches to this tool (true) or away from this tool (false)*/
	public void onToolChange(boolean b);
	
	
	/**Changes the selected object*/
	public boolean userSetSelectedItem(Object o);
	
	

	
	/**Called by the toolbar after a Button is created for this tool. 
	  tool will stroke the JButton. 
	  Programmer may want to add additional action listerners to the button but
	  method is not crucial for any current features */
	public void introduceButton(JButton jb);
	
	/**Sets the image clicked and the location clicked. not used by classed in the current draft of QuickFigures*/
	public void setImageAndClickPoint(ImageType imp, int x, int y);
	
	
	
}

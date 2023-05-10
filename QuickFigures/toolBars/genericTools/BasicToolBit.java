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
 * Version: 2023.2
 */
package genericTools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JMenuItem;

import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWorkSheet;
import externalToolBar.DragAndDropHandler;
import icons.IconSet;
import imageDisplayApp.CanvasOptions;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageMenu.CanvasAutoResize;
import includedToolbars.ObjectToolset1;
import layout.BasicObjectListHandler;
import layout.basicFigure.BasicLayout;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import undo.UndoManagerPlus;
import utilityClasses1.ArraySorter;

/**A simple implementation of the ToolBit interface that does not do
  anything in particular but is used as a superclass for other tool bits*/
public class BasicToolBit implements ToolBit {
	public static final String selectorToolName = "Object Selector/Mover";
	private ToolCore toolCore;
	private IconSet iconSet=null;
	private BasicObjectListHandler oh=new BasicObjectListHandler();
	
	

	protected Cursor normalCursor=new Cursor(Cursor.DEFAULT_CURSOR);

	@Override
	public void mousePressed() {

	}

	@Override
	public void mouseDragged() {

	}

	@Override
	public void mouseEntered() {
	}
	
	@Override
	public void mouseExited() {
		
	}

	@Override
	public void setToolCore(ToolCore toolCore) {
		this.toolCore=toolCore;
	}
	
	private ToolCore getToolCore() {
		return toolCore;
		
	}

	/**TODO determine if this method is still important for the function of the layout tools
	  Previously, as the canvas was resized in the middle of layout editing, this method was needed to */
	protected void setStoredMouseDisplacement() {
		this.getToolCore().setMouseDisplacementX(getXDisplaceMent());
		this.getToolCore().setMouseDisplacementY(getYDisplaceMent());
	}

	@Override
	public IconSet getIconSet() {
		return iconSet;
	}
	
	public void setIconSet(IconSet icons) {
		iconSet=icons;
	}
	
	public int getMouseButtonClick() {
		return this.getToolCore().getMouseButtonClick();
	}
	
	
	public boolean shiftDown() {
		return this.getToolCore().shiftDown();
	}
	
	public boolean altKeyDown() {
		return this.getToolCore().altKeyDown();
	}

	/**Certain tools, reset the original location after each mouse drag.
	 * that location is stored as the 'click point' even if it is not literally 
	 * the place the user clicked*/
	public void setClickPointToDragReleasePoint() {
		getToolCore().setClickPointToDragReleasePoint();
		
	}
	
	public String getToolName() {
		return this.getClass().toString().replace('_', ' ');
	}
	
	public void createIconSet(String... sts) {
		IconSet output = new IconSet(sts);
		iconSet=output;
	}

	
	public ImageWorkSheet getImageClicked() {
		if (toolCore==null) {
			return null;
		}
		return toolCore.getImageWrapperClick();
	}
	
	
	public DisplayedImage getImageDisplayWrapperClick() {
		if (toolCore==null) {
			return null;
			//sIssueLog.log("no tool core!!!");
		}
		return toolCore.getClickedImage();//.getImageWrapperClick();
	}
	
	public UndoManagerPlus getUndoManager() {
		return getImageDisplayWrapperClick() .getUndoManager();
	}
	
	public int clickCount() {
		if (toolCore==null) {
			IssueLog.log("no tool core!!!");
		}
		return toolCore.clickCount();
	}
	
	public BasicObjectListHandler getObjecthandler() {
		return oh;
	}
	
	public int getXDisplaceMent() {
		return this.getToolCore().getXDisplaceMent();
	}
	
	
	public int getYDisplaceMent() {
		return this.getToolCore().getYDisplaceMent();
	}
	
	public int getClickedCordinateX() {
		return this.getToolCore().getClickedCordinateX();
	}
	
	public int getClickedCordinateY() {
		return this.getToolCore().getClickedCordinateY();
	}
	
	public int getDragCordinateX() {
		return this.getToolCore().getDragCordinateX();
	}
	public int getDragCordinateY() {
		return this.getToolCore().getDragCordinateY();
	}
	public Point2D getDragPoint() {
		return new Point2D.Double(getDragCordinateX(), getDragCordinateY());
	}
	
	public int getReleaseCordinateY() {
		return this.getToolCore().getReleaseCordinateY();
	}



	public int getReleaseCordinateX() {
		// TODO Auto-generated method stub
		return this.getToolCore().getReleaseCordinateX();
	}
	
	public int getMouseYClick() {
		return this.getToolCore(). getMouseYClick();
	}
	
	public int getMouseXClick() {
		return this.getToolCore(). getMouseXClick();
	}
	
	public int getColIndexClick() {
		return this.getToolCore(). getColIndexClick();
	}
	

	public int getRowIndexClick() {
		return this.getToolCore(). getRowIndexClick();
	}
	
	public int getColIndexDrag() {
		return this.getToolCore(). getColIndexDrag();
	}
	public int getRowIndexDrag() {
		return this.getToolCore(). getRowIndexDrag();
	}
	
	
	@Override
	public void mouseReleased() {

	}
	
	public CanvasMouseEvent getLastMouseEvent() {
		return this.getToolCore(). getLastMouseEvent();
	}
	
	public CanvasMouseEvent getLastDragOrLastReleaseMouseEvent() {
		return this.getToolCore(). getLastDragMouseEvent();
	}
	
	public Color getForeGroundColor() {
		return this.getToolCore().getForeGroundColor();
	}

	public int getMouseDisplacementY() {
		return this.getToolCore().getMouseDisplacementY();
	}
	
	public int getMouseDisplacementX() {
		return this.getToolCore().getMouseDisplacementX();
	}
	
	@Override
	public void mouseMoved() {
	
		
	}

	@Override
	public void mouseClicked() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showOptionsDialog() {
		// TODO Auto-generated method stub
		
	}
	
	public int  getPanelIndexClick() {
		return getToolCore().getPanelIndexClick();
	}
	public int  getPanelIndexDrag() {
		return getToolCore().getPanelIndexDrag();
	}
	
	public int getMouseXdrag() {
		return getToolCore().getMouseXdrag();
	}
	public int getMouseYdrag() {
		return getToolCore().getMouseYdrag();
	}
	
	public ImageWorkSheet currentlyInFocusWindowImage() {
		return this.getToolCore().currentlyInFocusWindowImage();
	}


	@Override
	public ArrayList<JMenuItem> getPopupMenuItems() {
		return null;
	}
	
	public void updateClickedDisplay() {
		if(this.getImageClicked()!=null)
		this.getImageClicked() .updateDisplay();
	}
	
	protected Point clickedCord() {
		return new Point(this.getClickedCordinateX(), this.getClickedCordinateY());
	}
	
	protected Point draggedCord() {
		return new Point(this.getDragCordinateX(), this.getDragCordinateY());
	}

	public void setRowColClickForLayout(BasicLayout lay) {
		this.getToolCore().setRowColClickForLayout(lay);
		
	}

	public void setMouseXClick(int mouseXClick) {
		getToolCore().setMouseXClick(mouseXClick);
		
	}

	public void setMouseYClick(int mouseYclick) {
		getToolCore().setMouseYClick(mouseYclick);
		
	}

	public void setRowColDragForLayout(BasicLayout lay) {
		getToolCore().setRowColDragForLayout(lay);
		
	}

	

	public void setCursor(Cursor currentCursor) {
		//this.getImageDisplayWrapperClick().getWindow().setCursor(currentCursor);
		
	}

	public void resizeCanvas() {
		if (CanvasOptions.current.resizeCanvasAfterEdit)
			resizeCanvas(getImageDisplayWrapperClick());
	}
	
	protected void resizeCanvas(DisplayedImage wrap) {
		
				new CanvasAutoResize(false).performActionDisplayedImageWrapper(wrap);
	}

	@Override
	public boolean keyPressed(KeyEvent e) {
		return false;
	}

	@Override
	public boolean keyReleased(KeyEvent e) {
		return false;
	}

	@Override
	public boolean keyTyped(KeyEvent e) {
		return false;
	}

	@Override
	public void handleFileListDrop(ImageWindowAndDisplaySet imageAndDisplaySet, Point location, ArrayList<File> file) {

		
	}

	@Override
	public DragAndDropHandler getDragAndDropHandler() {
		return null;
		
	}

	@Override
	public String getToolTip() {
		return null;
	}

	@Override
	public boolean isActionTool() {
		
		return false;
	}

	@Override
	public void performLoadAction() {
	
		
	}


	/**Switches the toolbar over to the default tool*/
	protected Object todefaultTool() {
		if(isDefaultTool()) {} else {
			return ObjectToolset1.setCurrentTool(selectorToolName);
		}
		return null;
	}

	/**
	returns true if this tool is the starting tool for the toolbar
	 */
	protected boolean isDefaultTool() {
		return this.getClass().equals(Object_Mover.class);
	}

	@Override
	public boolean treeSetSelectedItem(Object o) {
		
		return false;
	}
	
	
	
	/**returns the object that may be clicked on at a given point*/
	public LocatedObject2D getObjectAt(ImageWorkSheet click, int x, int y) {
		ArrayList<LocatedObject2D> therois = getObjecthandler().getAllClickedRoi(click, x, y, Object.class);
		ArraySorter.removeHiddenItemsFrom(therois);//removes hidden items
		return new ArraySorter<LocatedObject2D>().getFirstNonNull(therois);
		
	}

	/**Called when a tool is about to be switched away from (false) or switched to (true)*/
	@Override
	public void onToolChange(boolean b) {
		
		
	}

	@Override
	public String getToolSubMenuName() {
		return null;
	}

	
	

}

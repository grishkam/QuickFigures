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
 * Date Modified: Nov 6, 2022
 * Version: 2022.1
 */
package imageDisplayApp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import javax.swing.JPanel;

import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.DisplayedImage;
import basicAppAdapters.GenericCanvasMouseAction;
import basicMenusForApp.MenuItemForObj;
import graphicalObjects.BasicCoordinateConverter;
import graphicalObjects.CordinateConverter;
import handles.IconHandle;
import handles.SmartHandle;
import handles.miniToolbars.ActionButtonHandleList;
import handles.miniToolbars.HasMiniToolBarHandles;
import imageMenu.ZoomFit;

/**A components that displays a mini toolbar
 that depends on the item selected and the worksheet*/
public class MiniToolBarPanel extends JPanel implements MouseListener {

	ArrayList<MenuItemForObj> permanentObjects=new ArrayList<MenuItemForObj>();
	private DisplayedImage displaySet;
	private ActionButtonHandleList buttonList;
	private ActionButtonHandleList alternateList;
	private Object lastItem;
	boolean vertical=true;
	
	{this.setForeground(Color.orange);}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	public MiniToolBarPanel( DisplayedImage display, boolean vertical) {
		this.vertical=vertical;
		setDisplay(display);
		
		addAction(new ZoomFit("In"));
		addAction(new ZoomFit("Out"));
		addAction(new ZoomFit(ZoomFit.SCREEN_FIT));
		
		
		buttonList=createActionList();
		
		this.addMouseListener(this);
		
	}



	/**changes the display set for this panels
	 * @param display
	 */
	public void setDisplay( DisplayedImage display) {
		if(display==this.displaySet)
			return;
		this.displaySet=display;
		if (display!=null)
			display.setSidePanel(this);
		getChangingButtonList();
	}
	
	

	public MiniToolBarPanel( DisplayedImage display) {
		this(display, true);
	}

	public Dimension getPreferredSize() {
		if (!vertical)
			return new Dimension(360, 30);
		return new Dimension(30, 325);
	}
	
	public void addAction(MenuItemForObj object) {
		if (object.getIcon()!=null) {
			permanentObjects.add(object);
		}
	}
	
	public void paintComponent(Graphics g) {
		
		if (g instanceof Graphics2D)
		{
			Graphics2D g2 = (Graphics2D) g;
			
		
			Rectangle r = new Rectangle(0,0, this.getWidth(), this.getHeight());
			g.setColor(Color.white);
			g2.fill(r);
			
			g.setColor(Color.black);
			g2.setStroke(new BasicStroke(2));
			g2.draw(r);
			this.getStableButtonList().draw(g2, new BasicCoordinateConverter());
			ActionButtonHandleList bl = this.getChangingButtonList();
			if (bl!=null) {
				bl.draw(g2, new BasicCoordinateConverter());
			}
		}
		
	}

	/**
	returns the currently used action button handle list
	 */
	public ActionButtonHandleList getStableButtonList() {
		
		return buttonList;
	}
	
	/**
	returns the currently used action button handle list
	 */
	public ActionButtonHandleList getChangingButtonList() {
		DisplayedImage displaySet2 = getDisplaySet();
		if(displaySet2==null) return null;
		Object selectedItem = displaySet2.getImageAsWorksheet().getSelectionObject();
		if (selectedItem!=lastItem) {
			updateAlternateList(selectedItem);
			lastItem=selectedItem;
		}
		if(alternateList!=null) return alternateList;
		
		return null;
	}

	/**
	 * @return
	 */
	public DisplayedImage getDisplaySet() {
		return displaySet;
	}

	/**
	 updates the alternate mini toolbar to account for the newly selected item
	 */
	public void updateAlternateList(Object selectedItem) {
		if(selectedItem instanceof  HasMiniToolBarHandles) {
			HasMiniToolBarHandles s = (HasMiniToolBarHandles) selectedItem;
			alternateList = s.createActionHandleList();
			
			if(alternateList!=null)
				{
				alternateList.setVertical(vertical);
				alternateList.setLocation(getToolLocations2());
				
				}
		}
		else alternateList=null;
	}
	
	/**this class provides a means for the menu bar items to appear as components of a miniToolbar*/
	public class MenuBarIconHandle extends IconHandle {

		private MenuItemForObj item;
		public MenuBarIconHandle(MenuItemForObj i, Point2D offSet) {
			super(i.getIcon(), new Point());
			super.xShift=(int) offSet.getX();
			super.yShift=(int) offSet.getY();
			item=i;
		}

		public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
			if( getDisplaySet()==null) return;
			item.performActionDisplayedImageWrapper( getDisplaySet());
			 getDisplaySet().updateDisplay();
		}
		private static final long serialVersionUID = 1L;
		}
	
	/**creates a set of action handles*/
	public ActionButtonHandleList createActionList() {
		ActionButtonHandleList output = new ActionButtonHandleList(vertical);
		for(MenuItemForObj ob:permanentObjects)output.add(new  MenuBarIconHandle(ob, new Point(1,1)));
		output.setLocation(getToolLocations());
		return output;
	}

	/**
	 returns the location of the first toolbar
	 */
	public Double getToolLocations() {
		if (!vertical) 
			return new Point2D.Double(2, 14);
		return new Point2D.Double(15,5);
	}
	
	/**
	 returns the location of the second mini toolbar
	 */
	public Double getToolLocations2() {
		if (!vertical)  
			return new Point2D.Double(90,14);
		return new Point2D.Double(15,85);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	/**generates a mouse press event for the handle*/
	@Override
	public void mousePressed(MouseEvent e) {
		
		GenericCanvasMouseAction cew = new LocalMouseEvent(getDisplaySet(), e);
		mousePressOnHandleList(cew, getStableButtonList());
		this.mousePressOnHandleList(cew, this.getChangingButtonList());
	}

	/**
 	determines if the user mouse press was within the handle list
   and calls the handlePress method for the listwhen a mouse press on the panel occurs
	 */
	public void mousePressOnHandleList(GenericCanvasMouseAction cew, ActionButtonHandleList bList) {
		if(bList==null) return;
		SmartHandle handle = bList.getHandleForClickPoint(cew.getCoordinatePoint());
		if (handle!=null) handle.handlePress(cew);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.repaint();
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public class LocalMouseEvent extends GenericCanvasMouseAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public LocalMouseEvent(DisplayedImage displaySet, MouseEvent e) {
			super(displaySet, e);
		}

		public CordinateConverter getUsedConverter() {
			return new BasicCoordinateConverter();
		}
	}
	
}
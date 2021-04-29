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
 * Version: 2021.1
 */
package handles.miniToolbars;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.DisplayedImage;
import basicAppAdapters.GenericCanvasMouseAction;
import graphicalObjects.CordinateConverter;
import handles.IconHandle;
import handles.SmartHandle;
import handles.SmartHandleList;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import selectedItemMenus.BasicMultiSelectionOperator;
import selectedItemMenus.LayerSelectionSystem;
import selectedItemMenus.MultiSelectionOperator;
import standardDialog.graphics.GraphicComponent;

/**A set of handles functions as a 'mini toolbar' for some objects.
 Each subclass of this list, forms a different grid of handles with icons  */
public class ActionButtonHandleList extends SmartHandleList {

	
	private static final int DEFAULT_ICON_SPACING = 1;
	private static final int DEFAULT_MAX_GRID_ = 8;
	
	
	private static final long serialVersionUID = 1L;
	private Point2D location=new Point2D.Double(0,0);
	double rightMostX=50;
	double lowerMostY=50;
	private double spacing=DEFAULT_ICON_SPACING;//the space between icons on the toolbar
	double between=1;//size of the gap between handles
	int numHandleID=30;//the handle number id that for newly added handles
	protected int maxGrid=DEFAULT_MAX_GRID_;
	
	private boolean vertical=false;
	
	
	public ActionButtonHandleList() {
		
	}
	
	/***/
	public ActionButtonHandleList(boolean vertical) {
		this.vertical=vertical;
	}
	
	/**sets the location of this list*/
	public void setLocation(Point2D p) {
		if(p==null) return;
		this.location=p;
		updateHandleLocations(1);
	}
	
	
	/**updates the location of the handles and draws the handles*/
	public void draw(Graphics2D g, CordinateConverter cords) {
		
		
		this.updateHandleLocations(cords.getMagnification());
		
		super.draw(g, cords);
	}
	
/**Sets the locations of each handle such that they appear the same regardless of magnification
 */
	public void updateHandleLocations(double magnify) {
		spacing = 1/magnify;
		if (vertical)
			updateLocationsForVertical();
		else
			updateLocationsForHorizontal();
	}


/**
 Sets the handle locations each handle to form a horizontal 
 */
public void updateLocationsForHorizontal() {
	double xi= getLocation().getX();
	double y= getLocation().getY();
	int colIndex = 0;
	lowerMostY=0;
	rightMostX=0;
	
	for(SmartHandle handle: this) {
		if(handle.isHidden()) continue;
		handle.setCordinateLocation(new Point2D.Double(xi+handle.getDrawnHandleWidth()*spacing/2, y));
		double handleXSpace = ((double)handle.getDrawnHandleWidth()+between)*spacing;//how much space until the next handle
		xi+=handleXSpace;
		
		colIndex++;
		if(colIndex>=maxGrid) {
			xi=getLocation().getX();
			y+=handleXSpace;//not the y space? might cause issues if the handles are not square
			colIndex=0;
		}
		
		if (y>lowerMostY) {
			lowerMostY=y;
		}
		if (xi>rightMostX) {
			rightMostX=xi;
		}
		
		
	}
}

/**
sets the locations of each handle to form a vertical array
*/
public void updateLocationsForVertical() {
	double xi= getLocation().getX();
	double yi= getLocation().getY();
	int rowIndex = 0;
	lowerMostY=0;
	rightMostX=0;
	
	for(SmartHandle handle: this) {
		if(handle.isHidden()) continue;
		handle.setCordinateLocation(new Point2D.Double(xi, yi+handle.getDrawnHandleHeight()*spacing/2));
		double handleYSpace = ((double)handle.getDrawnHandleHeight()+between)*spacing;//how much space until the next handle
		yi+=handleYSpace;
		
		rowIndex++;
		if(rowIndex>=maxGrid) {
			xi=getLocation().getX();
			yi+=handleYSpace;//not the y space? might cause issues if the handles are not square
			rowIndex=0;
		}
		
		if (yi>lowerMostY) {
			lowerMostY=yi;
		}
		
		if (xi>rightMostX) {
			rightMostX=xi;
		}
		
		
		
	}
}
	

	/**An icon that performs an action 
	 * @see MultiSelectionOperator for the class that defines the action
	 * */
	public class GeneralActionHandle extends IconHandle {
		
		protected MultiSelectionOperator operation;//the action to be performed
		protected IconHandle alternativePopup=null;//if non-null, this will display a popup rather than perform the operation when pressed 
		public boolean useOperatorPopup=false;
		
		public void draw(Graphics2D graphics, CordinateConverter cords) {
			updateHandleLocations(cords.getMagnification());
			updateIcon();
			super.draw(graphics, cords);
		}


		public GeneralActionHandle(MultiSelectionOperator i, int num) {
			super(i.getIcon(), new Point(0,0));
			this.setHandleNumber(num);
			this.operation=i;
			
		}
		
		public void setAlternativePopup(IconHandle i) {
			alternativePopup=i;
		}
		
		public String toString() {
			return operation.getMenuCommand();
		}

		/**updates the icon based on the getIcon method from the operation. This sometimes results in 
		  a change of icon that informs the user*/
		public void updateIcon() {
			super.setIcon(operation.getIcon());
		}
		
		
		
		private static final long serialVersionUID = 1L;
		
		/**when the user presses the handle*/
		public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
			if ( canvasMouseEventWrapper.shiftDown())
				return;//nothing  is
			if (alternativePopup!=null) {
				alternativePopup.showPopupMenu(canvasMouseEventWrapper);
				return;
			}
			
			
			performOperation(canvasMouseEventWrapper);
			
		}




		/**Executes the operation on the target specified by the mouse event*/
		public void performOperation(CanvasMouseEvent canvasMouseEventWrapper) {
			LayerSelectionSystem selector = canvasMouseEventWrapper.getSelectionSystem();
			operation.setSelector(selector);
			operation.setSelection(selector.getSelecteditems());
			operation.run();
			
			canvasMouseEventWrapper.getAsDisplay().updateDisplay();
		}

	

	}
	

	/**A handle that shows a popup menu to the user with options defined by an array of actions
	 * @see MultiSelectionOperator for the actions that can be assigned to this handle*/
	public class GeneralActionListHandle extends GeneralActionHandle {

		public MultiSelectionOperator itemForIcon;
		public MultiSelectionOperator itemForInputPanel;
		SmartPopupJMenu popupMenuForListHandle=new SmartPopupJMenu();
		
		/**A list of handles for each menu item*/
		ActionButtonHandleList sublist=new ActionButtonHandleList();
		
		CanvasMouseEvent lastEvent;
		boolean usePalete=false;
		private Component iPanel;

		/**creates a handle
		 * @param iconItem the item that will determine which icon is the handle icon
		 * @param items the items that will appear in the menu*/
		public GeneralActionListHandle(MultiSelectionOperator iconItem, int num, MultiSelectionOperator[] items) {
			super(iconItem, num);
			
			itemForIcon=iconItem;
			setItemsforMenu(items);
		}
		
		public void setyShift(int yShift) {
			this.yShift = yShift;
			for(SmartHandle i:sublist) {
				if (i instanceof GeneralActionHandle) {
					((GeneralActionHandle) i).setyShift(yShift);
				}
			}
			
		}
		public void setxShift(int xShift) {
			this.xShift = xShift;
			for(SmartHandle i:sublist) {
				if (i instanceof GeneralActionHandle) {
					((GeneralActionHandle) i).setxShift(xShift);
				}
			}
		}
		
		
		/**The appearance of some icons will change to reflect changes in a target object*/
		public void updateIcon() {
			if (itemForIcon!=null) super.setIcon(itemForIcon.getIcon());
			
		}
		
		/**Sets the items that will apear in the popup menu*/
		public void setItemsforMenu(MultiSelectionOperator... items) { 
			setItemsforMenu(null, items);
		}

		/**sets which items will apear in the popup menu (or a submenu)*/
		public JMenuItem setItemsforMenu(String submenu, MultiSelectionOperator... items) {
			JMenuItem mostRecentAddedItem=null;
			
			for(MultiSelectionOperator i: items) {
				JMenuItem j = new JMenuItem(i.getMenuCommand());
				if(i.getMenuItemRenderer()!=null) {
					j=i.getMenuItemRenderer();
					j.setText(i.getMenuCommand());
				}
				j.setIcon(i.getIcon());
				if(i.getMenuItemFont()!=null) {
					j.setFont(i.getMenuItemFont());
				}
				j.addActionListener(new MenuAction(i, this));
				sublist.add(new GeneralActionHandle(i, (int)(Math.random()*100000)));
				
				if (submenu!=null) {popupMenuForListHandle.getSubmenuOfName(submenu).add(j);} else
				popupMenuForListHandle.add(j);
				mostRecentAddedItem = j;
			}
			
			return mostRecentAddedItem;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public Component findInputPanel() {
			if (itemForInputPanel!=null)
				return itemForInputPanel.getInputPanel();
			return this.itemForIcon.getInputPanel();
		}
		
		/***/
		public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
			if(canvasMouseEventWrapper.shiftDown())
				return;//nothing is done if shift is down
			lastEvent=canvasMouseEventWrapper;
		
			updateInputPanel(canvasMouseEventWrapper);
			
			if (alternativePopup!=null) {
				alternativePopup.showPopupMenu(canvasMouseEventWrapper);
				return;
			}
			if (usePalete) {
				 sublist.showInPopupPalete(canvasMouseEventWrapper, null);
			} else {
				int clickedYScreen = canvasMouseEventWrapper.getClickedYScreen();
				if (this.lastDrawShape!=null) clickedYScreen=(int) lastDrawShape.getBounds().getMaxY();
				popupMenuForListHandle.show(canvasMouseEventWrapper.getComponent(),canvasMouseEventWrapper.getClickedXScreen(), clickedYScreen);
			}
			
			
			canvasMouseEventWrapper.getAsDisplay().updateDisplay();
			
		}

		/**
		sets up the input panel, a component that will be placed after the menu items
		 */
		private void updateInputPanel(CanvasMouseEvent canvasMouseEventWrapper) {
			if(findInputPanel()!=null) 
				{
					if(iPanel!=null)
						popupMenuForListHandle.remove(iPanel);
					itemForIcon.setSelector(canvasMouseEventWrapper.getSelectionSystem());
					if (itemForInputPanel!=null )itemForInputPanel.setSelector(canvasMouseEventWrapper.getSelectionSystem());
					iPanel=findInputPanel();
					popupMenuForListHandle.add(iPanel);
					popupMenuForListHandle.pack();
				}
		}
		
		/**An action listener for the menu items*/
		public class MenuAction implements ActionListener {

			private MultiSelectionOperator operate;
			private GeneralActionListHandle hand;

			public MenuAction(MultiSelectionOperator i, GeneralActionListHandle hand) {
				this.operate=i;
				this.hand=hand;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				LayerSelectionSystem selector = lastEvent.getSelectionSystem();
				operate.setSelector(selector);
				operate.run();
				
				updateIcon();
				if (hand!=null)hand.updateIcon();
				lastEvent.getAsDisplay().updateDisplay();
				
			}

		}
		
	}
	
	/**Adds a list of actions handle list*/
	protected GeneralActionListHandle addOperationList(MultiSelectionOperator o, MultiSelectionOperator[] ops) {
		if (o==null) {IssueLog.log("cannot add for null icon");}
		GeneralActionListHandle h = new GeneralActionListHandle(o, numHandleID, ops);
		return addOperationList(o, h);
	}


	/**Adds a handle to this list
	 * @param icon the item that determines the icon
	 * @param addedHandle the list handle*/
	protected GeneralActionListHandle addOperationList(MultiSelectionOperator icon, GeneralActionListHandle addedHandle) {
		addedHandle.itemForIcon=icon;
		add(addedHandle);
		numHandleID++;
		addedHandle.setxShift(5);
		addedHandle.setyShift(5);
		return addedHandle;
	}
	
	/**Adds an action to this handle list*/
	public void createGeneralButton(BasicMultiSelectionOperator i) {
		GeneralActionHandle h = new GeneralActionHandle(i, numHandleID);
		add(h);
		numHandleID++;
	}
	
	/**shows a popup menu for the mouse event*/
	public void  showInPopupPalete(CanvasMouseEvent canvasMouseEventWrapper, String message) {
		JPopupMenu j = getPopup(canvasMouseEventWrapper, message);
		
		j.show(canvasMouseEventWrapper.getAwtEvent().getComponent(), canvasMouseEventWrapper.getAwtEvent().getX(), canvasMouseEventWrapper.getAwtEvent().getY());
	}

	/**creates a popup menu for the mouse event*/
	public JPopupMenu getPopup(CanvasMouseEvent canvasMouseEventWrapper, String message) {
		this.setLocation(new Point2D.Double(35,35));
		JPopupMenu j = new SmartPopupJMenu();
		GraphicComponent panel=new GraphicComponent();
		panel.getGraphicLayers().add(this);
		panel.setPrefferedSize((int)rightMostX, (int)lowerMostY);
		panel.addMouseListener(new HandlePressMouseListener(this, canvasMouseEventWrapper.getAsDisplay()));
		
		if(message!=null) j.add(new JLabel(message));
		j.add(panel);
		return j;
	}
	
	/**creates a popup menu for the mouse event*/
	public JPopupMenu getPopup(CanvasMouseEvent canvasMouseEventWrapper) {
		return getPopup(canvasMouseEventWrapper, null);
	}
	
	/**A mouse listener that is used when handles are placed into a popup menu with an array of 
	 * handles drawn in a GraphicComponent (@see GraphicComponent) rather than a JMenu with a list JMenu items*/
	private class HandlePressMouseListener implements MouseListener {

		private ActionButtonHandleList theList;
		private DisplayedImage imp;

		public HandlePressMouseListener(ActionButtonHandleList a, DisplayedImage imp) {
			theList=a;
			this.imp=imp;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if(theList==null||e==null) return;
						SmartHandle h = theList.getHandleForClickPoint(new Point(e.getX(), e.getY()));
						if (h!=null)h.handlePress(new GenericCanvasMouseAction(imp, e));;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	/**sets the orientation to vertical*/
	public void setVertical(boolean b) {
		this.vertical=b;
		
	}
	
	/**implemented by subclasses*/
	public void updateLocation() {
		
		
	}

	public Point2D getLocation() {
		return location;
	}
}

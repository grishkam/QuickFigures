package graphicalObjectHandles;

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
import basicAppAdapters.GMouseEvent;
import graphicalObjects.CordinateConverter;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import selectedItemMenus.BasicMultiSelectionOperator;
import selectedItemMenus.LayerSelector;
import selectedItemMenus.MultiSelectionOperator;
import standardDialog.GraphicComponent;

/**A set of handles functions as a 'mini-toolbar' for some objects.
 Each subclass of this list, forms a different grid of handles with icons  */
public class ActionButtonHandleList extends SmartHandleList {

	private static final int DEFAULT_ICON_SPACING = 1;
	private static final int DEFAULT_MAX_GRID_ = 8;
	
	
	private static final long serialVersionUID = 1L;
	protected Point2D.Double location=new Point2D.Double(0,0);
	double rightMostX=50;
	double lowerMostY=50;
	private double spacing=DEFAULT_ICON_SPACING;
	int numHandleID=30;
	protected int maxGrid=DEFAULT_MAX_GRID_;
	double between=1;
	private boolean vertical=false;
	
	
	public ActionButtonHandleList() {
		
	}
	public ActionButtonHandleList(boolean vertical) {
		this.vertical=vertical;
	}
	
	public void setLocation(Point2D.Double p) {
		if(p==null) return;
		this.location=p;
		updateHandleLocations(1);
	}
	
	
	/**draws the handles*/
	public void draw(Graphics2D g, CordinateConverter<?> cords) {
		
		
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
 
 */
public void updateLocationsForHorizontal() {
	double xi= location.getX();
	double y= location.getY();
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
			xi=location.getX();
			y+=handleXSpace;//not the y space? might cause issues if the handles are not square
			colIndex=0;
		}
		
		if (y+handle.getHeight()>lowerMostY) {
			lowerMostY=y+handle.getHeight();
		}
		if (xi+handle.getWidth()>rightMostX) {
			rightMostX=xi+handle.getWidth();
		}
		
		
	}
}

/**
sets the locations of each handle to form a vertical array
*/
public void updateLocationsForVertical() {
	double xi= location.getX();
	double yi= location.getY();
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
			xi=location.getX();
			yi+=handleYSpace;//not the y space? might cause issues if the handles are not square
			rowIndex=0;
		}
		
		if (yi+handle.getHeight()>lowerMostY) {
			lowerMostY=yi+handle.getHeight();
		}
		if (xi+handle.getWidth()>rightMostX) {
			rightMostX=xi+handle.getWidth();
		}
		
		
	}
}
	

	/**An icon that performs an action*/
	public class GeneralActionHandle extends IconHandle {
		
		protected MultiSelectionOperator operation;//the action to be performed
		protected IconHandle alternativePopup=null;//if non-null, this will display a popup rather than perform the operation when pressed 
		public boolean useOperatorPopup=false;
		
		public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
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
			
			if (alternativePopup!=null) {
				alternativePopup.showPopupMenu(canvasMouseEventWrapper);
				return;
			}
			
			
			performOperation(canvasMouseEventWrapper);
			
		}




		/**Executes the operation on the target specified by the mouse event*/
		public void performOperation(CanvasMouseEvent canvasMouseEventWrapper) {
			LayerSelector selector = canvasMouseEventWrapper.getSelectionSystem();
			operation.setSelector(selector);
			operation.setSelection(selector.getSelecteditems());
			operation.run();
			
			canvasMouseEventWrapper.getAsDisplay().updateDisplay();
		}

	

	}
	

	/**A handle that shows a popup menu to the user with options defined by an array of actions*/
	public class GeneralActionListHandle extends GeneralActionHandle {

		public MultiSelectionOperator itemForIcon;
		SmartPopupJMenu p=new SmartPopupJMenu();
		ActionButtonHandleList sublist=new ActionButtonHandleList();
		CanvasMouseEvent lastEvent;
		boolean usePalete=false;
		private  MultiSelectionOperator[] allItems;
		private Component iPanel;

		public GeneralActionListHandle(MultiSelectionOperator i, int num, MultiSelectionOperator[] items) {
			super(i, num);
			
			itemForIcon=i;
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
		
		
		public void updateIcon() {
			
			if (itemForIcon!=null) super.setIcon(itemForIcon.getIcon());
			
		}
		
		public void setItemsforMenu(MultiSelectionOperator... items) { 
			setItemsforMenu(null, items);
		}

		public JMenuItem setItemsforMenu(String submenu, MultiSelectionOperator... items) {
			JMenuItem mostRecentAddedItem=null;
			this.allItems=items;
			for(MultiSelectionOperator i: items) {
				JMenuItem j = new JMenuItem(i.getMenuCommand());
				j.setIcon(i.getIcon());
				if(i.getMenuItemFont()!=null) {
					j.setFont(i.getMenuItemFont());
				}
				j.addActionListener(new MenuAction(i, this));
				sublist.add(new GeneralActionHandle(i, (int)(Math.random()*100000)));
				//if (i.getMenuPath()!=null) {p.getSubmenuOfName(i.getMenuPath()).add(j);} else
				//if(j.getText().equals(mostRecentAddedItem.getText()))continue;
				if (submenu!=null) {p.getSubmenuOfName(submenu).add(j);} else
				p.add(j);
				mostRecentAddedItem = j;
			}
			
			return mostRecentAddedItem;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		
		public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
			lastEvent=canvasMouseEventWrapper;
		
			if(this.itemForIcon.getInputPanel()!=null) 
				{
				if(iPanel!=null)
					p.remove(iPanel);
				itemForIcon.setSelector(canvasMouseEventWrapper.getSelectionSystem());
				iPanel=itemForIcon.getInputPanel();
				p.add(iPanel);
				p.pack();
				}
			
			if (alternativePopup!=null) {
				alternativePopup.showPopupMenu(canvasMouseEventWrapper);
				return;
			}
			if (usePalete) {
				 sublist.showInPopupPalete(canvasMouseEventWrapper, null);
			}
			else 
			p.show(canvasMouseEventWrapper.getComponent(),canvasMouseEventWrapper.getClickedXScreen(), canvasMouseEventWrapper.getClickedYScreen());
			
			
			canvasMouseEventWrapper.getAsDisplay().updateDisplay();
			
		}
		
		public class MenuAction implements ActionListener {

			private MultiSelectionOperator operate;
			private GeneralActionListHandle hand;

			public MenuAction(MultiSelectionOperator i, GeneralActionListHandle hand) {
				this.operate=i;
				this.hand=hand;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				LayerSelector selector = lastEvent.getSelectionSystem();
				operate.setSelector(selector);
				operate.run();
				//hand.updateIcon();
				updateIcon();
				lastEvent.getAsDisplay().updateDisplay();
				
			}

		}
		
	}
	
	protected GeneralActionListHandle addOperationList(MultiSelectionOperator o, MultiSelectionOperator[] ops) {
		if (o==null) {IssueLog.log("cannot add for null icon");}
		GeneralActionListHandle h = new GeneralActionListHandle(o, numHandleID, ops);
		return addOperationList(o, h);
	}



	protected GeneralActionListHandle addOperationList(MultiSelectionOperator o, GeneralActionListHandle h) {
		h.itemForIcon=o;
		add(h);
		numHandleID++;
		h.setxShift(5);
		h.setyShift(5);
		return h;
	}
	
	public void createGeneralButton(BasicMultiSelectionOperator i) {
		GeneralActionHandle h = new GeneralActionHandle(i, numHandleID);
		add(h);
		numHandleID++;
	}
	
	public void  showInPopupPalete(CanvasMouseEvent canvasMouseEventWrapper, String message) {
		JPopupMenu j = getPopup(canvasMouseEventWrapper, message);
		
		j.show(canvasMouseEventWrapper.getAwtEvent().getComponent(), canvasMouseEventWrapper.getAwtEvent().getX(), canvasMouseEventWrapper.getAwtEvent().getY());
	}

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
	
	public JPopupMenu getPopup(CanvasMouseEvent canvasMouseEventWrapper) {
		return getPopup(canvasMouseEventWrapper, null);
	}
	
	public class HandlePressMouseListener implements MouseListener {

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
						if (h!=null)h.handlePress(new GMouseEvent(imp, e));;
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
	public void updateLocation() {
		// TODO Auto-generated method stub
		
	}
}

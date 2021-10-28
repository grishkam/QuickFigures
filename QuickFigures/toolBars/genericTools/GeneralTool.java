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
 * Version: 2021.2
 */
package genericTools;
import appContext.CurrentAppContext;
import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWorkSheet;
import externalToolBar.*;
import figureOrganizer.PanelListElement;
import graphicActionToolbar.CurrentFigureSet;
import icons.CompoundIcon;
import icons.IconSet;
import layout.BasicObjectListHandler;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutEditorDialogs;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import menuUtil.SmartJMenu;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JMenuItem;

/** A tool that appears as an icon within the toolbar and may contain
  multiple tool 'bits' that can be used. The specifics of what the tool does
  depends on the bit being used. User can switch bits with a popup menu
 	This class may contain a single tool bit or many
 * An implementation of the ToolCore
   It is used by multiple tools so any edits to it may interfere with the function of many of the tools
   in the toolbar. Although many parts of this could be coded more efficiently and others are likely not needed at
   all. I have not revised the code
   */
public class GeneralTool extends BlankTool<DisplayedImage> implements ActionListener, ToolCore{
	
	public BasicObjectListHandler oh=new BasicObjectListHandler();
	
	static Cursor defaultCursor=new Cursor(Cursor.DEFAULT_CURSOR);
	
	/**The tool bit defines the specific */
	private ToolBit toolbit=null;
	
	private ArrayList<ToolBit> additionalBits;
	
	protected GeneralTool() {}

	/**Creates a tool containing the given tool bit*/
	public GeneralTool(ToolBit bit) {
		setUpToolBit(bit);
		
	}
	
	
	
	public GeneralTool(ArrayList<ToolBit> pathGraphicBits) {
		 additionalBits=pathGraphicBits;
		 this.setUpToolBit(pathGraphicBits.get(0));
	}
	
	public GeneralTool(ToolBit bit, ToolBit... bits2) {
		 additionalBits=new ArrayList<ToolBit>();
		 additionalBits.add(bit);
		 for(ToolBit bit2: bits2) {
			 additionalBits.add(bit2);
		 }
		 this.setUpToolBit(bit);
	}

	void setUpToolBit(ToolBit bit) {
		this.setToolbit(bit);
		bit.setToolCore(this);
	}
	
	private int clickedCordinateX;
	private int clickedCordinateY;
	private int dragCordinateX;
	private int dragCordinateY;

	private int mouseDisplacementX;
	private int mouseDisplacementY;
	
	private Point releasePoint;
	private Point releasePointMouse;
	private int mouseXClick;
	private int mouseYClick;
	private int mouseXdrag;
	private int mouseYdrag;
	private DisplayedImage imageClick;
	private ImageWorkSheet imageWrapperClick;
	
	protected CanvasMouseEvent event ;//the latest mouse event of any kind
	protected CanvasMouseEvent eventClick ;
	protected CanvasMouseEvent eventDrag ;
	protected CanvasMouseEvent eventRelease ;
	boolean markEditedRegionsWithRoi=true;
	
	public boolean isUpdaterImage=false;
	protected PanelListElement panelClick;
	protected PanelListElement panelDrag;
	
	private int panelIndexClick;
	private int panelIndexDrag;
	private int colIndexClick;
	private int colIndexDrag;
	private int rowIndexClick;
	private int rowIndexDrag;
	
	private int clickCount;
	private transient CanvasMouseEvent lastDragMe;
	private Cursor currentCursor;
	

	private int mouseButton;
	private DisplayedImage dispImageClick;
	private CanvasMouseEvent lastDragOrReleaseEvent;
	
	
	
	
	@Override
	public void mousePressed(DisplayedImage imp, CanvasMouseEvent e) {
		 event =e;
		 eventClick=event;
		 
		setClickPoint(imp,event);
		try {
			getToolbit().mousePressed();
			mousePressed();
			getImageWrapperClick().updateDisplay();
		} catch (Throwable e2) {
			IssueLog.logT(e2);
		}
		
		}
	
	public void mousePressed() {
		
	}
	
	public void setCursor(Cursor c, int i) {
		DisplayedImage disp = event.getAsDisplay();
		if (disp!=null)disp.setCursor(c);
	}


	
	@Override
	public void mouseEntered(DisplayedImage imp, CanvasMouseEvent  e) {
		try {
			CanvasMouseEvent event1 = e;
			event=e;
			setCursor(currentCursor, 0);
			setClickPoint(imp,event1);
			getToolbit().mouseEntered();
			mouseEntered();
			getImageWrapperClick().updateDisplay();
		} catch (Throwable e2) {
			IssueLog.logT(e2);
		}
		}
	
	public void mouseEntered() {
		
	}
	
	@Override
	public void mouseMoved(DisplayedImage imp, CanvasMouseEvent  e) {
		
		
		try {
			
			CanvasMouseEvent event1 = e;
			event=event1;
			setClickPoint(imp,event1);
			if (getToolbit()!=null) getToolbit().mouseMoved();
			mouseMoved();
			if (getImageWrapperClick()==null) {
				 IssueLog.log("wrapper not innitialized");
			}
		} catch (Throwable e2) {
			IssueLog.logT(e2);
		}
		}
	
	
	
	public void mouseMoved() {
		
	}

	@Override
	public void mouseExited(DisplayedImage imp, CanvasMouseEvent  e) {
		try {
			setReleaseOrDragPoint(imp, e);
			getToolbit().mouseExited();
			mouseExited();
			this.getImageWrapperClick().updateDisplay();
		} catch (Throwable e2) {
			IssueLog.logT(e2);
		}
		}
	
	public void mouseExited() {
		
	}

	@Override
	public void mouseDragged(DisplayedImage imp, CanvasMouseEvent  e) {
		setReleaseOrDragPoint(imp,e);
	event=e;
			lastDragOrReleaseEvent =  e;
		
		
		 try {
			this.getToolbit().mouseDragged();
			mouseDragged();
			this.getImageWrapperClick().updateDisplay();
		} catch (Throwable e2) {
			IssueLog.logT(e2);
		}
	}
	
	
	public void mouseDragged() {
		
	}

	@Override
	public void mouseReleased(DisplayedImage imp, CanvasMouseEvent  e) {
		setReleasePoint(imp,e);
		event=e;
			lastDragOrReleaseEvent = e;
		
		getToolbit().mouseReleased();
		mouseReleased();
		this.getImageWrapperClick().updateDisplay();
	}
	
	

	public Color getForeGroundColor() {
		return CurrentAppContext.getGeneralContext().getForeGroundColor();
		
	}
	
	public void mouseReleased() {}
	


	/** Returns the difference in cordinates between the last mouse press
	  and the last drag (or release). Note, these are coordinates are
	  the Figure cordinates and NOT the JComponent coordinates of the MouseEvents.
	  
	 */
	@Override
	public int getXDisplaceMent() {
		setOScreen1();
		setOScreen2();
		return getDragCordinateX() -getClickedCordinateX() ;
	}
	@Override
	public int getYDisplaceMent() {
		setOScreen1();
		setOScreen2();	
		return getDragCordinateY()-getClickedCordinateY() ;
	}
	
	
	
	public void setXY1(DisplayedImage imp, CanvasMouseEvent event) {
		
		setMouseXClick(event.getClickedXScreen());
		setMouseYClick(event.getClickedYScreen());
		setClickedCordinateX(event.getCoordinateX());
		setClickedCordinateY(event.getCoordinateY());
	
	
	}
	

	public void setXY_Point2(DisplayedImage imp, CanvasMouseEvent  e) {
		CanvasMouseEvent event=e;
		
		setMouseXdrag(event.getClickedXScreen());
		setMouseYdrag(event.getClickedYScreen());
		setDragCordinateX(event.getCoordinateX());
		setDragCordinateY(event.getCoordinateY());

		
	}
	private void setXY3(DisplayedImage imp, CanvasMouseEvent  e) {
		CanvasMouseEvent event=e;
		this.releasePointMouse=new Point(event.getClickedXScreen(), event.getClickedYScreen());
		this.releasePoint=new Point(event.getCoordinateX(), event.getCoordinateY());
		
	}

	
	
	


	public void setClickPoint(DisplayedImage imp, CanvasMouseEvent event) {
		if (imp==null) return;
	
		eventClick=event;
		
		setImageClick(imp);
		
		mouseButton=event.mouseButton();
		
		setImageWrapperClick(event.getAsDisplay().getImageAsWorksheet());
		setXY1(imp, event);
		
		clickCount=event.clickCount();
		
	}

	

	
	/**This takes converts the stored mouse click/press event location (point1) to a coordinate location of the figure
	  */
	private void setOScreen1() {
		CanvasMouseEvent canvasMouse =event;
		setClickedCordinateX(canvasMouse.convertClickedXImage(getMouseXClick()));
		setClickedCordinateY(canvasMouse.convertClickedYImage(getMouseYClick()));
		
	}
	/**This takes converts the stored mouse drag/release event location to a coordinate location of the figure*/
	private void setOScreen2() {
		CanvasMouseEvent canvasMouse = event;
		setDragCordinateX(canvasMouse.convertClickedXImage(getMouseXdrag()));
		setDragCordinateY(canvasMouse.convertClickedYImage(getMouseYdrag()));
	}
	
	
	/**Stores a release of drag point location*/
	public void setReleaseOrDragPoint(DisplayedImage imp, CanvasMouseEvent  e) {
		this.setImageClick(imp);
		this.lastDragMe=e;
		this.event=e;
		this.eventDrag=event;
		lastDragOrReleaseEvent=e;
		this.setImageWrapperClick(event.getAsDisplay().getImageAsWorksheet());
		setXY_Point2(imp,e);
	}
	
	private void setReleasePoint(DisplayedImage imp, CanvasMouseEvent e) {
		this.eventRelease=e;
		this.setImageClick(imp);
		this.setImageWrapperClick(event.getAsDisplay().getImageAsWorksheet());
		setXY3(imp,e);
		
	}
	
	


	public void setRowColDragForLayout(BasicLayout ml) {
		if (ml==null) {return;}
		setPanelIndexDrag(ml.getPanelIndex(getDragCordinateX() , getDragCordinateY() ));
		int[] gridpos=ml.getGridCordAtIndex(getPanelIndexDrag());
		setColIndexDrag(gridpos[0]);
		setRowIndexDrag(gridpos[1]);
	}
	
	public void setRowColClickForLayout(BasicLayout ml) {
		if (ml==null) {IssueLog.log3("Do not have a layout when asked"); return;}
		setPanelIndexClick(ml.getPanelIndex(getClickedCordinateX() , getClickedCordinateY()));
		int[] gridpos=ml.getGridCordAtIndex(getPanelIndexClick());
		setColIndexClick(gridpos[0]);
		setRowIndexClick(gridpos[1]);
	}
	

	
	public LayoutEditorDialogs getMontageEditorDialogs() {return new LayoutEditorDialogs();}

	

	@Override
	public void setMarkerRoi(int type) {
		//setMarkerRoi(type, getMainLayout());
	}
	
	public void setMarkerRoi(int type, BasicLayout ml) {
		if (ml==null) {IssueLog.log3("must have a layout to set a marker roi");return;}
		setMarkerRoi(ml.getSelectedSpace(1, type));
	}


	@Override
	public void setMarkerRoi(int index, int type) {
		//setMarkerRoi(getMainLayout().getSelectedSpace(index, type));
	}
	
	@Override
	public void setMarkerRoi(Shape s) {
		this.getImageWrapperClick().getOverlaySelectionManagger().select(s, getStrokeWidth(), 0);
		//getObjectAdapter().select(getImageClick(), s, );
	}
	
	public void setMarkerRoi(Shape s, int strokeWidth) {
		getImageWrapperClick().getOverlaySelectionManagger().select( s, strokeWidth, 0);
	}
	
	public void removeSelection() {
		removeMarkerRoi() ;
	}
	
	public void removeMarkerRoi() {
		this.getImageWrapperClick().getOverlaySelectionManagger().removeObjectSelections();
		//getObjectAdapter().deselect(getImageClick());
	//	imp.killRoi(); imp.updateAndDraw();
	}
	
	public int getStrokeWidth() {
		if (getImageClick()==null)return 8;
		
		return getImageWrapperClick().width()/200;
		}
	

	

	
	@Override
	public void actionPerformed(ActionEvent arg0) {}
	

	
	
	@Override
	public void createIconSet(String... sts) {
		IconSet output = new IconSet(sts);
		setIconSet(output);
		
	}
	

	
	/**Returns a menu item with this object as an action listener.*/
	public MenuItem createMenuItem(String st) {
		MenuItem output = new MenuItem(st);
		output.addActionListener(this);
		return output;
	}
	/**Returns a menu item with this object as an action listener.*/
	public JMenuItem createJMenuItem(String st) {
		JMenuItem output = new JMenuItem(st);
		output.addActionListener(this);
		return output;
	}
	
	
	public BasicObjectListHandler getObjecthandler() {
		return oh;
	}
	
	
	
	
	public DisplayedImage getImageClick() {
		return imageClick;
	}


	public void setImageClick(DisplayedImage imageClick) {
		this.imageClick = imageClick;
		if (event!=null)this.dispImageClick=event.getAsDisplay();
	}

	@Override
	public int getClickedCordinateX() {
		return clickedCordinateX;
	}

	
	public void setClickedCordinateX(int clickedCordinateX) {
		this.clickedCordinateX = clickedCordinateX;
	}


	@Override
	public int getClickedCordinateY() {
		return clickedCordinateY;
	}


	@Override
	public void setClickedCordinateY(int clickedCordinateY) {
		this.clickedCordinateY = clickedCordinateY;
	}

	@Override
	public int getDragCordinateX() {
		return dragCordinateX;
	}

	
	@Override
	public void setDragCordinateX(int dragCordinateX) {
		this.dragCordinateX = dragCordinateX;
	}

	
	@Override
	public int getDragCordinateY() {
		return dragCordinateY;
	}

	
	@Override
	public void setDragCordinateY(int dragCordinateY) {
		this.dragCordinateY = dragCordinateY;
	}

	
	@Override
	public int getMouseDisplacementX() {
		
		return mouseDisplacementX;
	}

	
	@Override
	public void setMouseDisplacementX(int mouseDisplacementX) {
		this.mouseDisplacementX = mouseDisplacementX;
	}

	
	@Override
	public int getMouseDisplacementY() {
		return mouseDisplacementY;
	}

	@Override
	public void setMouseDisplacementY(int mouseDisplacementY) {
		this.mouseDisplacementY = mouseDisplacementY;
	}

	
	@Override
	public int getMouseXClick() {
		return mouseXClick;
	}

	
	@Override
	public void setMouseXClick(int mouseXClick) {
		
		this.mouseXClick = mouseXClick;
	}

	
	@Override
	public int getMouseYClick() {
		return mouseYClick;
	}

	
	@Override
	public void setMouseYClick(int mouseYClick) {
		this.mouseYClick = mouseYClick;
	}

	@Override
	public int getMouseXdrag() {
		return mouseXdrag;
	}

	@Override
	public void setMouseXdrag(int mouseXdrag) {
		this.mouseXdrag = mouseXdrag;
	}

	
	@Override
	public int getMouseYdrag() {
		return mouseYdrag;
	}

	
	@Override
	public void setMouseYdrag(int mouseYdrag) {
		this.mouseYdrag = mouseYdrag;
	}

	
	
 


	
	


	@Override
	public int getPanelIndexClick() {
		return panelIndexClick;
	}

	@Override
	public void setPanelIndexClick(int panelIndexClick) {
		this.panelIndexClick = panelIndexClick;
	}

	@Override
	public int getPanelIndexDrag() {
		return panelIndexDrag;
	}


	@Override
	public void setPanelIndexDrag(int panelIndexDrag) {
		this.panelIndexDrag = panelIndexDrag;
	}

	
	@Override
	public int getColIndexClick() {
		return colIndexClick;
	}


	@Override
	public void setColIndexClick(int colIndexClick) {
		this.colIndexClick = colIndexClick;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#getColIndexDrag()
	 */
	@Override
	public int getColIndexDrag() {
		return colIndexDrag;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#setColIndexDrag(int)
	 */
	@Override
	public void setColIndexDrag(int colIndexDrag) {
		this.colIndexDrag = colIndexDrag;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#getRowIndexClick()
	 */
	@Override
	public int getRowIndexClick() {
		return rowIndexClick;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#setRowIndexClick(int)
	 */
	@Override
	public void setRowIndexClick(int rowIndexClick) {
		this.rowIndexClick = rowIndexClick;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#getRowIndexDrag()
	 */
	@Override
	public int getRowIndexDrag() {
		return rowIndexDrag;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#setRowIndexDrag(int)
	 */
	@Override
	public void setRowIndexDrag(int rowIndexDrag) {
		this.rowIndexDrag = rowIndexDrag;
	}

	/* (non-Javadoc)
	 * @see GenericMontageUIKit.ToolCore#getImageWrapperClick()
	 */
	@Override
	public ImageWorkSheet getImageWrapperClick() {
		return imageWrapperClick;
	}

	
	@Override
	public void setImageWrapperClick(ImageWorkSheet imageWrapperClick) {
		this.imageWrapperClick = imageWrapperClick;
	}

	public LocatedObject2D getTemporarySelection() {
		return this.getImageWrapperClick().getOverlaySelectionManagger().getSelection(1);
		
	}

	

	public ToolBit getToolbit() {
		if (toolbit==null) {toolbit=new BasicToolBit();
		toolbit.setToolCore((ToolCore) this) ;
		}
		return toolbit;
	}

	public void setToolbit(ToolBit toolbit) {
		toolbit.setToolCore(this);
		this.toolbit = toolbit;
	}

	public int clickCount() {
		// TODO Auto-generated method stub
		return clickCount;
	}

	public CanvasMouseEvent getLastMouseEvent() {
		// TODO Auto-generated method stub
		return event;
	}
	
	

	public void setClickPointToDragReleasePoint() {
		CanvasMouseEvent event1 = lastDragMe;//this.getAdapter().createCanvasMouseEventWrapper(getImageClick(), lastDragMe);
		//setCursor(currentCursor, 0);
		setClickPoint(getImageClick(),event1);
		
	}
	
	public IconSet getIconSet() {
		if (this.getToolbit().getIconSet()!=null) {
			if (this.additionalBits!=null && this.additionalBits.size()>1) {
				return CompoundSet(true, getToolbit().getIconSet());
			
			}
			
			return getToolbit().getIconSet();
		}
		
		return super.getIconSet();//iconSet;
	}
	
	public static IconSet CompoundSet(boolean b, IconSet i) {
		return new IconSet(
				new CompoundIcon(b, i.getIcon(0)),
				new CompoundIcon(b, i.getIcon(1)),
				new CompoundIcon(b, i.getIcon(2))
				);
				
	}
	
	public boolean shiftDown() {
		return event.shiftDown();
	}
	
	public boolean altKeyDown() {
		return event.altKeyDown();
	}


	
	@Override
	public void mouseClicked(DisplayedImage imp, CanvasMouseEvent  e) {
		event=e;
		setReleaseOrDragPoint(imp,e);
		 try {
			this.getToolbit().mouseClicked();
			mouseClicked();
			this.getImageWrapperClick().updateDisplay();
		} catch (Throwable e2) {
			IssueLog.logT(e2);
		}
	}
	
	public void mouseClicked() {
		// TODO Auto-generated method stub
		
	}

	public void setSelectedObject(LocatedObject2D lastRoi) {
		getImageWrapperClick().getOverlaySelectionManagger().setSelection(lastRoi, 0);
	
	}


	@Override
	public int getReleaseCordinateY() {
		// TODO Auto-generated method stub
		return releasePoint.y;
	}

	@Override
	public int getReleaseCordinateX() {
		// TODO Auto-generated method stub
		return  releasePoint.x;
	}
	@Override
	public void showOptionsDialog() {
		if (this.getToolbit()!=null) getToolbit().showOptionsDialog();
	}

	@Override
	public ImageWorkSheet currentlyInFocusWindowImage() {
		return CurrentFigureSet.getCurrentActiveDisplayGroup().getImageAsWorksheet();
		}

	@Override
	public int getMouseXrelease() {
		if (releasePointMouse==null) return 0;
		return  releasePointMouse.x;
	}

	@Override
	public int getMouseYrelease() {
		if (releasePointMouse==null) return 0;
		return  releasePointMouse.y;
	}
	
	HashMap<String, SmartJMenu> subMenus=new HashMap<String, SmartJMenu> ();
	private SmartJMenu getSubMenu(String st, ArrayList<JMenuItem> pen) {
		SmartJMenu out = subMenus.get(st);
		if(out==null) {
			out=new SmartJMenu(st);
			subMenus.put(st, out);
			pen.add(out);
		}
		
		return out;
	}
	
	/**Get the items for a popup menu*/
	public ArrayList<JMenuItem> getPopupMenuItems() {	
		ArrayList<JMenuItem> pen = this.getToolbit().getPopupMenuItems() ;
		subMenus=new HashMap<String, SmartJMenu> ();
		if (pen==null&&additionalBits!=null&&additionalBits.size()>1) {
			pen=new ArrayList<JMenuItem>();
			for(ToolBit bit1:additionalBits ) {
				if(bit1==null) continue;
				if(bit1.getToolSubMenuName()!=null) {
					 getSubMenu(bit1.getToolSubMenuName(), pen).add(createJButtonForBit(bit1));
					 continue;
				}
				pen.add(createJButtonForBit(bit1));
			}
		}
		
		return pen;
	}
	
	private JMenuItem createJButtonForBit(ToolBit bit) {
		JMenuItem item=new JMenuItem();
		
		item.addActionListener(new ToolBitSwitch(bit));
		bit.getIconSet().setItemIcons(item);
		item.setText(bit.getToolName());
		return item;
	}
	
	public String getToolName()  {
		return this.getToolbit().getToolName();
	}
	
	 class ToolBitSwitch implements ActionListener {

		 ToolBit bit;
		ToolBitSwitch(ToolBit b) {
			bit=b;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (toolbit!=null) toolbit.onToolChange(false);
			bit.onToolChange(true);
			setUpToolBit(bit);

			
			getIconSet().setItemIcons(getToolButton()) ;
			getToolButton().setIcon(getToolPressedImageIcon());
			if(bit.isActionTool())getToolButton().setIcon(getToolNormalIcon());
			getToolButton().setRolloverEnabled(false);
			
			getToolButton().setToolTipText(getToolTip());
			if (bit.isActionTool()) {
				bit.performLoadAction();
			} 
			else {
				
			}
			
		}}
	

	@Override
	public int getMouseButtonClick() {
		return mouseButton;
	}

	@Override
	public DisplayedImage getClickedImage() {
		if (dispImageClick!=null) return dispImageClick;
		if(event==null) return null;
		return event.getAsDisplay();
	}

	@Override
	public void setClickedImage(DisplayedImage d) {
		dispImageClick=d;
		
	}

	@Override
	public CanvasMouseEvent getLastDragMouseEvent() {
		// TODO Auto-generated method stub
		return lastDragOrReleaseEvent;
	}
	
	@Override
	public boolean keyPressed(DisplayedImage imp, KeyEvent e) {
		setImageClick(imp);
		return this.getToolbit().keyPressed(e);
	}

	@Override
	public boolean keyReleased(DisplayedImage imp, KeyEvent e) {
		setImageClick(imp);
		return this.getToolbit().keyReleased(e);
	}

	@Override
	public boolean keyTyped(DisplayedImage imp, KeyEvent e) {
		setImageClick(imp);
		return this.getToolbit().keyTyped(e);
		
	}
	

	
	public DragAndDropHandler getDraghandler() {
		return this.getToolbit().getDragAndDropHandler();
	}
	
	
public void handleFileListDrop(Point location, ArrayList<File> file) {
		
	}

@Override
public String getToolTip() {
	return this.getToolbit().getToolTip();
}

@Override
public boolean isActionTool() {
	return this.getToolbit().isActionTool();
}

public void performLoadAction() {
	this.getToolbit().performLoadAction();
}

@Override
public boolean userSetSelectedItem(Object o) {
	
	return getToolbit().treeSetSelectedItem( o);
}

@Override
public void onToolChange(boolean b) {
	getToolbit().onToolChange(b);
	
}
	
}

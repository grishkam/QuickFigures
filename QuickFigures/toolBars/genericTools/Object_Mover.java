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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPopupMenu;

import addObjectMenus.ClipboardAdder;
import appContext.ImageDPIHandler;
import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWrapper;
import basicMenusForApp.CurrentSetLayerSelector;
import externalToolBar.DragAndDropHandler;
import externalToolBar.GraphicToolIcon;
import externalToolBar.IconSet;
import genericMontageKit.PanelLayout;
import genericMontageKit.OverlayObjectManager;
import graphicalObjectHandles.SmartHandle;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjectHandles.HasSmartHandles;
import graphicalObjectHandles.LockedItemHandle;
import graphicalObjectHandles.ReshapeHandleList;
import graphicalObjectHandles.HasHandles;
import graphicalObjects.CursorFinder;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_BasicShapes.BarGraphic.BarTextGraphic;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import graphicalObjects_BasicShapes.PathGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import imageDisplayApp.CanvasOptions;
import imageMenu.CanvasAutoResize;
import includedToolbars.StatusPanel;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import menuUtil.HasUniquePopupMenu;
import objectDialogs.GraphicItemOptionsDialog;
import standardDialog.StandardDialog;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.UndoMoveItems;
import undo.UndoTextEdit;
import utilityClasses1.ArraySorter;
import utilityClassesForObjects.ArrayObjectContainer;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.LocatedObjectGroup;
import utilityClassesForObjects.LocationChangeListenerList;
import utilityClassesForObjects.RectangleEdges;
import utilityClassesForObjects.Scales;
import utilityClassesForObjects.ScalesFully;
import utilityClassesForObjects.Selectable;
import utilityClassesForObjects.Snap2Rectangle;
import utilityClassesForObjects.TakesLockedItems;
import undo.UndoAbleEditForRemoveItem;
import undo.UndoDragHandle;
import undo.UndoManagerPlus;

/**The most important tool in the toolbar, allows user to select objects, drag handes, bring up popup menus and 
  more....*/
public class Object_Mover extends BasicToolBit implements ToolBit  {
	/**
	 * 
	 */
	private static final int NO_HANDLE = HasHandles.NO_HANDLE_;

	/**
	 * 
	 */
	private static final int NORMAL_OBJECT_MOVER = 0;

	protected IconSet set=GraphicToolIcon.createIconSet(new LocalIcon(0));
	
	protected boolean createSelector=true;	
			
			private Cursor handleCursor=new Cursor(Cursor.HAND_CURSOR);
			
	protected LocatedObject2D selectedItem;
	protected ArrayList<LocatedObject2D> otherSelectedItems;
	public boolean realtimeshow=false;
	protected boolean ignorehidden=true;
	protected boolean bringSelectedToFront=false;
	private boolean resizeAfterMousDrags=false;
	
	  int orix;
	  int oriy;
	
	  	public static final int DO_NOT_SELECT_IN_GROUP=0, SELECT_IN_GROUP=1;
	 private static int selectingroup=DO_NOT_SELECT_IN_GROUP;
	 
	 /**The undoable edit that will be added to the unto manager*/
		 protected UndoMoveItems currentUndo;
		 boolean addedToManager=false;//true if the above edit has been added to the manager
	  
		protected Class<?> excludedClass=null;
		protected Class<?> onlySelectThoseOfClass=Object.class;

	public Object_Mover() {
		
		
	}
	private int handle=-1;
	int mode=NORMAL_OBJECT_MOVER;

	private ArrayList<LocatedObject2D> rois2;

	protected int pressX,  pressY;
	protected Rectangle2D selection;
	
	AbstractUndoableEdit2 smartHandleMoveUndo=null;
	private UndoDragHandle currentundoDragHandle;
	private boolean addedcurrentundoDragHandle;
	private ReshapeHandleList lastGroupHandleList;
	private SmartHandle smartHandle;


	private SmartHandleList canvasHandleList;


	private boolean draggingCanvasHandle;

	/**Sets which object is the currently selected one*/
	public LocatedObject2D setPrimarySelectedObject(LocatedObject2D roi) {
		CanvasMouseEvent me = getLastClickMouseEvent();
		if (me==null) return setPrimarySelectedObject(false, roi);
		
		return setPrimarySelectedObject(me.shfitDown(), roi);
	}
	
	/**Sets the currently selected object, 
	 * if shift is false, deselects all other objects.
	 *
	 * @param shift is the shift key down
	 * */
	public LocatedObject2D setPrimarySelectedObject(boolean shift, LocatedObject2D targetItem) {
		if (shift){
				if (selectedItem!=null && selectedItem!=targetItem) 
					selectedItem.makePrimarySelectedItem(false);//
		} else
			{
			deselect(selectedItem);
			deselectAll() ;
		}
		
		otherSelectedItems=null;
		selectedItem=null;
		
		
		selectedItem=targetItem;
		select(selectedItem);
		if (selectedItem!=null) {
					selectedItem.makePrimarySelectedItem(true); 			
		}
		if (getImageClicked()!=null) 
			getImageClicked().setPrimarySelectionObject(selectedItem);
		
		return selectedItem;
	}
	
	
	protected void deselectAll() {
		if (getImageClicked()!=null)
			deselectAll(this.getImageClicked().getGraphicLayerSet());
	}
	
	 void deselectAll(GraphicLayer gl) {
		if (gl==null) return;
		ArrayList<ZoomableGraphic> ls = gl.getAllGraphics();
		deselectAll(ls);
	}
	
	protected void deselectAll(ArrayList<?> ls) {
		getImageClicked().getOverlaySelectionManagger().removeSelections();
		for(Object l: ls) try  {
				deselect(l);
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}
	
	
	public void deselect(Object roi1) {
		if (roi1!=null && roi1 instanceof Selectable) try {
			((Selectable)roi1).deselect();
			this.getImageClicked().getOverlaySelectionManagger().setSelection(null, 1);
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}
	protected void selectAll(ArrayList<?> ls) {
		for(Object l: ls) {
			
			if (l instanceof Selectable) {
				Selectable s=(Selectable) l;
				s.select();
			}
		}
	}
	
	/**returns all the selected items within the layer*/
	protected static ArrayList<LocatedObject2D> getAllSelected(GraphicLayer layer1) {
		ArrayList<LocatedObject2D> list=new ArrayList<LocatedObject2D>();
		if (layer1==null) return null;
		ArrayList<ZoomableGraphic> ls = layer1.getAllGraphics();
		for(ZoomableGraphic l: ls) {
			
			if (l instanceof Selectable && l instanceof LocatedObject2D) {
				Selectable s=(Selectable) l;
				if (s.isSelected())   list.add((LocatedObject2D) l);
			}
		}
		return list;
	}
	
	public void setSelectedHandleNumber(int i) {
		handle=i;
	}
	
	/**returns the handle id number of the clickpoint. 
	 * Also stores that as the selected handle number*/
	public int establishMovedIntoOrClickedHandle(boolean press) {
		this.setSelectedHandleNumber(NO_HANDLE);//starts off without a handle
		
		for(LocatedObject2D object: this.getAllSelectedItems(false))
			if (object instanceof HasHandles) {
				HasHandles handledObject = (HasHandles)object;
				int handleNumber = handledObject.handleNumber(getMouseXClick(), getMouseYClick());
				setSelectedHandleNumber(handleNumber);
				if (handleNumber>0) {
					if (press)this.setPrimarySelectedObject(object);
					this.setSelectedHandleNumber(handleNumber);
					break;
				}
			
		}
		
		/**if no handle is selected for an individual object at this point,
		 *  checks for handles in an object group handle list*/
		if(getSelectedHandleNumber()==NO_HANDLE && getObjectGroupHandleList()!=null) {
			int num2 = getObjectGroupHandleList().handleNumberForClickPoint(getMouseXClick(), getMouseYClick());
			setSelectedHandleNumber(num2);
		}
		
		/**If no handle is set at this point, check to determine if a canvas handle is at that point*/
		if(getSelectedHandleNumber()==NO_HANDLE) {
			int num2 = getCanvasHandleList().handleNumberForClickPoint(getMouseXClick(), getMouseYClick());
			setSelectedHandleNumber(num2);
			if (num2>0) draggingCanvasHandle=true; else draggingCanvasHandle=false;
		} else  draggingCanvasHandle=false;
	
		
		
		
		return getSelectedHandleNumber();
	}
	

	
	

	
	
	public void mousePressed() {
		
		selection=null;
		CanvasMouseEvent e = getLastClickMouseEvent();
		pressX= getClickedCordinateX();
		pressY= getClickedCordinateY();
		
		
		establishMovedIntoOrClickedHandle(true);
		

		boolean startsSelected=false;
		LocatedObject2D objectAtPressLocation = getObjectAt(getImageClicked(), pressX, pressY);
		if(objectAtPressLocation!=null) 
			startsSelected=objectAtPressLocation.isSelected();
		
		SmartHandle sh= this.findSelectedSmartHandle();
		
		/**what to do in the event of a handle press*/
						if (sh!=null) {
							sh.handlePress(this.getLastClickMouseEvent());
							this.setPressedSmartHandle(sh);
						} else setPressedSmartHandle(null);
				
		if (getSelectedHandleNumber()!=NO_HANDLE && getPrimarySelectedObject() instanceof HasHandles) {
				HasHandles h=(HasHandles) getPrimarySelectedObject();
				h.handlePress(getSelectedHandleNumber(), new Point(getClickedCordinateX(),  getClickedCordinateY()));
				}
					
		
		if (e.isPopupTrigger()) {
			forPopupTrigger(objectAtPressLocation, e,sh);
			return;
		}
		
		/**If an object or a handle was pressed, makes sure the object is selected*/
		if (objectAtPressLocation!=getPrimarySelectedObject()&&getSelectedHandleNumber()==NO_HANDLE) {
			objectAtPressLocation=setPrimarySelectedObject(objectAtPressLocation);
		} else {
			select(getPrimarySelectedObject());
			if (getPrimarySelectedObject()!=null)
				getPrimarySelectedObject().makePrimarySelectedItem(true);
		}
		
		/**if neither a handle nor an object was pressed, then no object is selected*/
		if(objectAtPressLocation==null&&getSelectedHandleNumber()==NO_HANDLE) setPrimarySelectedObject(null);
		
			
		if (getPrimarySelectedObject()==null) return;
			
			if (getPrimarySelectedObject() instanceof HasHandles &&getSelectedHandleNumber()>NO_HANDLE) {
				getSelectionObjectAshashangles().handleMouseEvent(this.getLastClickMouseEvent(), handle, getButton(),0, MouseEvent.MOUSE_PRESSED, null);
				createUndoForDragHandle() ;
			
			}
			boolean shift=e.shfitDown();
			boolean copyObjects = this.getLastClickMouseEvent().altKeyDown();
			
		if (startsSelected && shift && this.handle==NO_HANDLE) {
			/**when holding down the shift key, the user sometimes wants to deselect an item not select it*/
			deselect(objectAtPressLocation);
		}
			 rois2 = getAllSelectedItems(copyObjects);
			 		currentUndo=new UndoMoveItems(rois2);//establishes the undo
			 		addedToManager=false;
			 		
			 		/**remembers the starting location of the selected item*/
			if (getPrimarySelectedObject()!=null) {
				orix=(int)getPrimarySelectedObject().getBounds().getX(); 
				oriy=(int) getPrimarySelectedObject().getBounds().getY();
				}
		this.getObjectGroupHandleList();
		this.getCanvasHandleList();
			if(this.textEditMode() &&this.handle<0 &&!e.isPopupTrigger()) {
				this.mousePressOnTextCursor((TextGraphic) this.getPrimarySelectedObject());
			}
		
		}
	

	protected void setPressedSmartHandle(SmartHandle sh) {
		smartHandle=sh;
	}
	
	protected SmartHandle getPressedSmartHandle() {
		return smartHandle;
	}

	protected void createUndoForDragHandle() {
		if (getSelectionObjectAshashangles()==null) return;
		currentundoDragHandle=new UndoDragHandle(handle, this.getSelectionObjectAshashangles(), new Point(getClickedCordinateX(),  getClickedCordinateY()));
		if (getPrimarySelectedObject() instanceof BasicGraphicalObject) {
			smartHandleMoveUndo=((BasicGraphicalObject)getPrimarySelectedObject()).provideDragEdit();
		}
		addedcurrentundoDragHandle=false;
	}
	
	ArrayList<GraphicLayer> getSelectedLayers(GraphicLayer s) {
		
		ArrayList<GraphicLayer> output = new ArrayList<GraphicLayer> ();
		for(GraphicLayer z:s.getSubLayers()) {
			if(isLayerSelected(z)) output.add(z);
		}
		
		return output;
	}
	
	static boolean isLayerSelected(GraphicLayer l) {
		for(ZoomableGraphic z: l.getItemArray()) {
			if (z==null) continue;
			if(z instanceof GraphicLayer) {
				if (isLayerSelected((GraphicLayer) z)) continue;
			}
			if(!(z instanceof Selectable)) return false;
			Selectable s=(Selectable) z;
			if(!s.isSelected()) return false;
		}
		
		return true;
	}
	
	/**returns all the selected objects*/
	protected ArrayList<LocatedObject2D> getAllSelectedItems(boolean createCopy) {
		ArrayList<LocatedObject2D> out=new ArrayList<LocatedObject2D>();
		 otherSelectedItems= getAllSelected(getImageClicked().getGraphicLayerSet());
		
		if (otherSelectedItems!=null)
			out.addAll(otherSelectedItems);
		if(!out.contains(getPrimarySelectedObject())&&getPrimarySelectedObject()!=null) {
			out.add(this.getPrimarySelectedObject());
			
		}
		removeIgnoredAndHidden(otherSelectedItems);
			
			if (createCopy) {
				ArrayList<LocatedObject2D> out2 = getObjecthandler().copyRois(out);
				for(LocatedObject2D roia: out2) {if (roia instanceof Selectable) ((Selectable) roia).select();}
				for(LocatedObject2D roia: otherSelectedItems) {if (roia instanceof Selectable) ((Selectable) roia).deselect();}
				getObjecthandler().addRoisToImage(out2, getImageClicked());
				otherSelectedItems=out2;
				selectedItem=out2.get(0);
				return out2;
				}
			
		return out;
	}
	
		/**removes any rois that are hidden if ignorehidded boolean is set to true.
		 * Also removes objects of the excluded class*/
	protected void removeIgnoredAndHidden(ArrayList<LocatedObject2D> rois) {
		if (this.ignorehidden) {
			if (this.ignorehidden) ArraySorter.removehideableItems(rois);
				}
		ArraySorter.removeThoseOfClass(rois, getExcludedClass());
	}
		
	

	
	
	/**what to do if a popup menu must be shown*/
	protected void forPopupTrigger(LocatedObject2D roi2, CanvasMouseEvent e, SmartHandle sh ) {
		JPopupMenu menu =null;
		
		
		if (super.shiftDown()) {
			GroupOfobjectPopup menuAll = new GroupOfobjectPopup( new CurrentSetLayerSelector());
			menu=menuAll;
			menuAll.addItemsFromJMenu( obtainUniquePopup(roi2,e), "this item");
			
		}
		if (sh!=null) {
			menu=sh.getJPopup();
		} 
		
		if (menu==null&& roi2 instanceof HasUniquePopupMenu) {
			menu = obtainUniquePopup(roi2, e);
			
			try {
				if ( isAttachedItem(roi2)) {
					LockedItemHandle lockHandle = this.findHandleForLockedItem(roi2);
					if (lockHandle!=null) menu.add(lockHandle.createAdjustPositionMenuItem());
				}
			} catch (Exception e1) {
			
			}
			
			}
		
		if (menu instanceof SmartPopupJMenu) {
			SmartPopupJMenu c = (SmartPopupJMenu) menu;
			c.setUndoManager(this.getImageDisplayWrapperClick().getUndoManager());
			c.setLastMouseEvent(e);
		}
		
		
		 Component c = getLastClickMouseEvent().getComponent();
		 if (menu!=null) menu.show(c, e.getClickedXScreen(), e.getClickedYScreen());
	}

	protected JPopupMenu obtainUniquePopup(LocatedObject2D roi2, CanvasMouseEvent e) {
		JPopupMenu menu=null;
		HasUniquePopupMenu has=(HasUniquePopupMenu) roi2;
		if (has==null) return null;
		if (has.getMenuSupplier()!=null) menu = has.getMenuSupplier().getJPopup();
		return menu;
	}
	
	boolean hasGroups(ArrayList<LocatedObject2D> therois) {
		return ArraySorter.getNOfClass(therois, LocatedObjectGroup.class)>0;
	}
	
	/**returns the object that is at the given click location*/
	public LocatedObject2D getObjectAt(ImageWrapper click, int x, int y) {
		ArrayList<LocatedObject2D> therois = getObjectsAtPressLocationWithoutFiltering(click, x, y);
		
		while (isSelectingroup()==SELECT_IN_GROUP&& hasGroups(therois)) {
			replaceGroupsWithContents(therois,  x,  y);
		}
		if (isSelectingroup()>1) {
			int i=isSelectingroup();
			while(i>1&& hasGroups(therois)) {
				replaceGroupsWithContents(therois,  x,  y);
				i--;
			}
			
		}
		
		return getFirstNonhiddenItem(therois);
		
	}

	public LocatedObject2D getFirstNonhiddenItem(ArrayList<LocatedObject2D> therois) {
		ArraySorter.removeThoseOfClass(therois, getExcludedClass());
		if (this.ignorehidden)ArraySorter.removehideableItems(therois);
		
		return new ArraySorter<LocatedObject2D>().getFirstNonNull(therois);
	}

	protected ArrayList<LocatedObject2D> getObjectsAtPressLocationWithoutFiltering(ImageWrapper click, int x, int y) {
		return getObjecthandler().getAllClickedRoi(click, x, y,this.onlySelectThoseOfClass, true);
	}
	
	/**iterates through the array. whenever it sees a locatedObjectGroup group, it will place the 
	  contents of the group in the array. This is for the select in group feature*/
	public void replaceGroupsWithContents(ArrayList<LocatedObject2D> therois, int x, int y) {
		ArraySorter<LocatedObject2D> as = new ArraySorter<LocatedObject2D>();
			ArrayList<LocatedObject2D> aa = as.getThoseOfClass(therois, LocatedObjectGroup.class);
			for(LocatedObject2D groupd:aa) {
				LocatedObjectGroup l=(LocatedObjectGroup)  groupd;
				ArrayList<LocatedObject2D> newrois = getObjecthandler().getAllClickedRoi(l.getObjectContainer(), x, y,this.onlySelectThoseOfClass);
				as.replace(therois, groupd, newrois);
			}
	}
	
	/**creates an array that goes down 1 level into the groups */
	public ArrayList<LocatedObject2D> replaceGroupsWithContents2(ArrayList<LocatedObject2D> therois, int x, int y) {
	
			ArrayList<LocatedObject2D> output=new ArrayList<LocatedObject2D>();
			for(LocatedObject2D groupd:therois) {
				if (groupd instanceof LocatedObjectGroup){
				LocatedObjectGroup l=(LocatedObjectGroup)  groupd;
				ArrayList<LocatedObject2D> newrois = getObjecthandler().getAllClickedRoi(l.getObjectContainer(), x, y,this.onlySelectThoseOfClass);
				output.addAll(newrois);
				}
				else output.add(groupd);
			
			}
			return output;
	}
	

	public void mouseMoved() {
		
		
		this.establishMovedIntoOrClickedHandle(false);
		
		updateCursorIfOverhandle();
		
		StatusPanel.updateStatus("("+getClickedCordinateX()+", "+getClickedCordinateY()+")");
		
		
		/**for certain subclasses, changes the selected item as the mouse moves*/
		if (!realtimeshow) return;
		
		//establishClickedHandle();
		
		LocatedObject2D roi2 =  getObjectAt(getImageClicked(), getClickedCordinateX(),getClickedCordinateY());
		
		
		
		
		if (roi2!=getPrimarySelectedObject()&&getSelectedHandleNumber()==-1) {
			setPrimarySelectedObject(roi2);
		} else select(getPrimarySelectedObject());
		if (roi2==null) setPrimarySelectedObject(null);
			//roi1=this.getObjecthandler().getClickedRoi(imp, onscreenx, onscreeny);
			
			if (getPrimarySelectedObject()==null) return;
			
			
		
			if (getPrimarySelectedObject()!=null) {
				orix=(int)getPrimarySelectedObject().getBounds().getX(); oriy=(int) getPrimarySelectedObject().getBounds().getY();
				}
			
			
			
			
			getImageClicked().updateDisplay();
		}

	/**changes the mouse cursor depending on there the mouse is*/
	public void updateCursorIfOverhandle() {
		if (this.getSelectedHandleNumber()==-1) {
			super.getImageDisplayWrapperClick().setCursor(getNormalCursor());
		}
		else 	super.getImageDisplayWrapperClick().setCursor(getHandleCursor());
	}
	
	
	


	private HasHandles getSelectionObjectAshashangles() {
		if (getPrimarySelectedObject() instanceof HasHandles )
			return (HasHandles) getPrimarySelectedObject() ;
		return null;
	}
	
	/**returns the selected SmartHandle based on the handle number*/
	private SmartHandle findSelectedSmartHandle() {
		SmartHandle output=null;
		if (getPrimarySelectedObject() instanceof HasSmartHandles){
			HasSmartHandles handetConatiner = (HasSmartHandles) getPrimarySelectedObject();
			output= handetConatiner.getSmartHandleList().getHandleNumber(getSelectedHandleNumber());
		}
		
		/**if no smart handle is already found, checks for a 
		 * locked item handle (special the handle for moving an item that is attached to another)*/
		if(output==null && getSelectedHandleNumber()!=NO_HANDLE ) {
					output = findHandleToUseForLockedItem();
					if (output!=null) {
						setSelectedExtraHandle(output);
					} else setSelectedExtraHandle(null);
			} else 	setSelectedExtraHandle(null);
		
		/**if the selections are scalable, checks for an object group handle list */
		if(output==null && selectionsScale()) {
			ReshapeHandleList objectGroupHandleList = getObjectGroupHandleList();
			if ( objectGroupHandleList!=null)
				output= objectGroupHandleList.getHandleNumber(getSelectedHandleNumber());
		}
		
		/**If no handle is found yet, checks for the handle number in the canvas handle list*/
		if (output==null&&this.canvasHandleList!=null) {
			output=canvasHandleList.getHandleNumber(getSelectedHandleNumber());
			
		}
	
		
		return output;
	}

	protected SmartHandle findHandleToUseForLockedItem() {
		SmartHandle output;
		output=this.findHandleForLockedItem(getPrimarySelectedObject());
		if (output!=null) output=findHandleForLockedItem(getPrimarySelectedObject()).createDemiVersion();
		return output;
	}

	private ReshapeHandleList getObjectGroupHandleList() {
		if(this.getClass()!=Object_Mover.class) return null; //not needed for subclasses
		if(!this.selectionsScale()) return null;
		ReshapeHandleList newHandleList = new ReshapeHandleList(this.getAllSelectedItems(false), 5, 90000000, selectionsScale2Ways(), 0, false);
		
		/**if the new grouped handle list is much like the old one, just used the old one*/
		if(newHandleList.isSimilarList(lastGroupHandleList)) newHandleList=lastGroupHandleList;
			else if (lastGroupHandleList!=null) {
				lastGroupHandleList.finishEdit();
			}
		
		lastGroupHandleList=newHandleList;
		lastGroupHandleList.updateRectangle();
		getImageDisplayWrapperClick().getImageAsWrapper().getOverlaySelectionManagger().setSelectionHandles(lastGroupHandleList);
		return lastGroupHandleList;
	}
	
	private SmartHandleList getCanvasHandleList() {
		
		canvasHandleList = this.getImageDisplayWrapperClick().getCanvasHandles();
		
		getImageDisplayWrapperClick().getImageAsWrapper().getOverlaySelectionManagger().setPermanentHandles(canvasHandleList);
		return canvasHandleList;
	}

	private boolean selectionsScale() {
		ArrayList<LocatedObject2D> all = getAllSelectedItems(false);
		if(all.size()<2) return false;
		for (LocatedObject2D a: all) {
			if (a==null||a instanceof Scales) continue;
			return false;
		}
		return true;
	}
	
	private boolean selectionsScale2Ways() {
		ArrayList<LocatedObject2D> all = getAllSelectedItems(false);
		if(all.size()<2) return false;
		for (LocatedObject2D a: all) {
			if (a==null||a instanceof ScalesFully) continue;
			return false;
		}
		return true;
	}

	public void setSelectedExtraHandle(SmartHandle output) {
		this.getImageDisplayWrapperClick().getImageAsWrapper().getOverlaySelectionManagger().setSelectionHandles(SmartHandleList.createList(output));
	}
	
	public void mouseClicked() {
		
		
		
		
		GraphicItemOptionsDialog.setCurrentImage(getImageClicked());
		if (getPrimarySelectedObject() instanceof HasHandles &&handle>-1) {
			getSelectionObjectAshashangles().handleMouseEvent(getLastClickMouseEvent(), handle, getButton(),clickCount(), MouseEvent.MOUSE_CLICKED, null);
			
		} else
		if (getPrimarySelectedObject() instanceof BasicGraphicalObject) {
			if (getPrimarySelectedObject()==null) return;
			//if (getSelectedObject().dropObject(super.getLastClickMouseEvent(), getClickedCordinateX(), getClickedCordinateY())==null) ;
			
						BasicGraphicalObject b=(BasicGraphicalObject) getPrimarySelectedObject();
						b.handleMouseEvent(this.getLastClickMouseEvent(), handle, getButton(), clickCount(), MouseEvent.MOUSE_CLICKED);
			
			
		}
		getImageClicked().updateDisplay();
		
		
		
	}
	
	/**snaps the roi to fit in its panel. integer closeness determines how nearby panel has to be*/
	public static void snapRoi(LocatedObject2D l, Rectangle2D r3, int closeness, boolean perfect) {
		if (l==null||r3==null) return;
		
		Rectangle r1 = l.getBounds();
		Rectangle r2=r3.getBounds();
		Point p=Snap2Rectangle.snapBoundsSide(r1, r2, closeness);
		if(perfect) {
			if(l.getBounds().getWidth()==r2.getWidth()) r1.x=r2.x;
			if(l.getBounds().getHeight()==r2.getHeight()) r1.y=r2.y;
		}
		
		if (l instanceof PanelLayoutGraphic ) {
			PanelLayoutGraphic s=(PanelLayoutGraphic) l;
			int x = p.x-l.getBounds().x;
			int y=p.y-l.getBounds().y; 
			s.moveLayoutAndContents(x, y);
			} else l.setLocationUpperLeft(p.x, p.y);
		
	}
	
	public Rectangle2D  getSnapRect() {
		return getNearestPanelRect( this.getImageClicked(), this.getDragPoint(), ignorehidden, this.getPrimarySelectedObject());
	}
	
	
	protected static ArrayList<PanelLayout> getPotentialSnapLayouts(ImageWrapper imageWrapperClick, double x, double y, boolean ignoreHidden, LocatedObject2D exempt) {
		  
		
		ArrayList<LocatedObject2D> As = imageWrapperClick.getLocatedObjects();
		ArraySorter.removeThoseNotOfClass(As, PanelLayoutGraphic.class);
		if (ignoreHidden) ArraySorter.removehideableItems(As);
		As.remove(exempt);
		
		ArrayList<PanelLayout> layouts=new ArrayList<PanelLayout>();
		for(LocatedObject2D l:As) {
			if (l==null) continue;
			PanelLayoutGraphic p=(PanelLayoutGraphic) l;
			layouts.add(p.getPanelLayout());
		}
		//layouts.add(this.getImageWrapperClick().createLayout());
		return layouts;
	}
	
	/**returns the nearest layout panel to point xy*/
	public static Rectangle2D getNearestPanelRect(ImageWrapper imageWrapperClick, Point2D p, boolean ignoreHidden, LocatedObject2D exempt) {
		
		ArrayList<PanelLayout> layouts = getPotentialSnapLayouts(imageWrapperClick, p.getX(), p.getY(), ignoreHidden, exempt);
		
		PanelLayout ml2=getNearestPanelLayout(p, layouts);
	
		if (ml2==null) return null;
		Rectangle2D r2=ml2.getNearestPanel(p.getX(), p.getY());
		
		return r2;
	}
	
	public static PanelLayout getNearestPanelLayout(Point2D p, ArrayList<PanelLayout> As) {
		
		HashMap<PanelLayout, Rectangle2D> nearestPanels=new HashMap<PanelLayout, Rectangle2D>();
		
		for(PanelLayout l:As) {
			if (l==null) continue;
			PanelLayout pnal=(PanelLayout) l;
			nearestPanels.put(pnal, pnal.getNearestPanel((int)p.getX(),(int) p.getY()));
		}
		
		
		PanelLayout nearest=null;
		double distance=Double.MAX_VALUE;
		
	
		for(PanelLayout i: nearestPanels.keySet()) {
		Rectangle2D rect = nearestPanels.get(i);
			double d2 = p.distance(rect.getCenterX(), rect.getCenterY());
			if (d2<distance) {
				distance=d2;
				nearest=i;
			}
		}
		
		return nearest;
	}
	
	
	public void mouseReleased() {
		
		SmartHandle sh = getPressedSmartHandle();//.getSelectionSmartHamndles();
		
		/**what to do in the event of a smart handle release*/
						if (sh!=null) {
							sh.handleRelease(this.getLastDragOrLastReleaseMouseEvent());
						}
						
		/**what to do in the case of all types of handle release*/
		if (getSelectedHandleNumber()>-1&&getPrimarySelectedObject() instanceof HasHandles) {

			HasHandles h=(HasHandles) getPrimarySelectedObject();
			h.handleRelease(getSelectedHandleNumber(), new Point(getClickedCordinateX(),  getClickedCordinateY()), new Point(getDragCordinateX(), getDragCordinateY()));
	
		}
		
		
		
		afterRelease();
		
		
	}
	
	/**called after a mouse release*/
	protected void afterRelease() {
		getImageClicked().getOverlaySelectionManagger().select(null, 0); 
		if (getPrimarySelectedObject() instanceof HasHandles &&handle>-1) {
			getSelectionObjectAshashangles().handleMouseEvent(this.getLastDragOrLastReleaseMouseEvent(), handle, getButton(),this.clickCount(), MouseEvent.MOUSE_RELEASED, null);
			if(getPrimarySelectedObject() instanceof BasicGraphicalObject &&isResizeCanvasAfterMouseRelease()) 
				new CanvasAutoResize().performActionDisplayedImageWrapper(getImageDisplayWrapperClick());

			return;
		}
		
	
		
		
		/**If the user drags around a rectangle, selects the rois inside*/
		if ((getPrimarySelectedObject()==null ||this.getPrimarySelectedObject()!=null&&!getPrimarySelectedObject().getOutline().contains(pressX, pressY))&&createSelector) {
			selectRoisInDrawnSelector() ;
			findSelectedSmartHandle();
		}


		if (this.getPrimarySelectedObject() instanceof PathGraphic&&createSelector &&this.shiftDown()) {
			PathGraphic path=(PathGraphic) getPrimarySelectedObject() ;
			path.selectHandlesInside(	selection);//this part does not help
			
		}
		
		/**sets these field to negative so next mouse action cannot affect last undoer*/
		smartHandleMoveUndo=null;
		 currentundoDragHandle=null;
		addedcurrentundoDragHandle=false;
		
		if(this.isResizeCanvasAfterMouseRelease()) new CanvasAutoResize().performActionDisplayedImageWrapper(getImageDisplayWrapperClick());
		this.setPressedSmartHandle(null);
	}
	
	protected void selectRoisInDrawnSelector() {
		Rectangle2D rect = selection;
		//getImageWrapperClick().getSelectionManagger().select(rect, 0);
		if(rect!=null&&rect.getHeight()>2&&rect.getWidth()>2) {
		//	
			ArrayList<LocatedObject2D> items = this.getObjecthandler().getOverlapOverlaypingItems(rect.getBounds(),this.getImageClicked());
			removeIgnoredAndHidden(items);
			if(items.size()==0) return;
			LocatedObject2D lastItem = items.get(items.size()-1);
			boolean selLast=pressX>this.getLastDragOrLastReleaseMouseEvent().getCoordinatePoint().getX();
			if (selLast)this.setPrimarySelectedObject(lastItem);
			else this.setPrimarySelectedObject(items.get(0));
			for(Object i: items) {
				select(i);
				if (i instanceof PathGraphic)
					((PathGraphic) i).selectHandlesInside(rect);
			}
			
		}
		selection=null;
	}

	/**dont remember why i had the button as always 0*/
	private int getButton() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void mouseDragged() {
		
		/**selects text if text edit mode */
		if (this.textEditMode())  {
			mouseDragForTextCursor();
			return;
		}
		 
		
		if (getPrimarySelectedObject()!=null) {			
			mousDragForObjectOrHandle();			
		}  else if (draggingCanvasHandle){
			/**just for the cavas resize handle*/
			SmartHandle sh = this.getPressedSmartHandle();//.getSelectionSmartHamndles();
			if (sh!=null) sh.handleDrag(getLastDragOrLastReleaseMouseEvent());
		}
		
		updateSelectingMask();
		
		getImageClicked().updateDisplay();	
		
	}

	/**
	 * 
	 */
	protected void mousDragForObjectOrHandle() {
		if (getSelectedHandleNumber()>-1) {
			performHandleDrag();
		}
		else
		if (mode==NORMAL_OBJECT_MOVER) {
			
			 performObjectDrag();
		}
	}

	/**
	 * 
	 */
	public void performObjectDrag() {
		moveRois(super.getXDisplaceMent(), super.getYDisplaceMent()) ;
		if (currentUndo!=null) {
				 currentUndo.establishFinalLocations();
				 if (!this.addedToManager)
					 {this.getImageDisplayWrapperClick().getUndoManager().addEdit(currentUndo);
					 this.addedToManager=true;
					 }
		}
		setClickPointToDragReleasePoint();
	}

	/**
	 * 
	 */
	public void performHandleDrag() {
		/**for objects that are of the has handles interface*/
		if (getPrimarySelectedObject() instanceof HasHandles)
			{HasHandles h=getSelectionObjectAshashangles();
			h.handleMove(getSelectedHandleNumber(), new Point(getClickedCordinateX(),  getClickedCordinateY()), new Point(getDragCordinateX(), getDragCordinateY()));
			}
		
		SmartHandle sh = this.getPressedSmartHandle();//.getSelectionSmartHamndles();
		if (sh!=null) sh.handleDrag(getLastDragOrLastReleaseMouseEvent());
		
		
		/**lets the object know its handle is getting a mouse drag*/
		if (getPrimarySelectedObject() instanceof HasHandles)
			getSelectionObjectAshashangles().handleMouseEvent(this.getLastDragOrLastReleaseMouseEvent(), handle, getButton(),0, MouseEvent.MOUSE_DRAGGED, null);
			
			
			if (currentundoDragHandle==null) {
				createUndoForDragHandle() ;
				}
			if(sh.handlesOwnUndo()) { currentundoDragHandle=null; currentundoDragHandle=null;}
			
			if (!addedcurrentundoDragHandle&&currentundoDragHandle!=null) {
				
				if (smartHandleMoveUndo!=null &&smartHandleMoveUndo.isMyObject(getPrimarySelectedObject()))
					this.getImageDisplayWrapperClick().getUndoManager().addEdit(smartHandleMoveUndo);
				else	
					this.getImageDisplayWrapperClick().getUndoManager().addEdit(currentundoDragHandle);
				
				addedcurrentundoDragHandle=true;
			}
		
			/**updates the undoable edit so that it reflects this drag motion and not the previous*/
			if (currentundoDragHandle!=null)currentundoDragHandle.setFinalLocation(new Point(getDragCordinateX(), getDragCordinateY()));
			if (smartHandleMoveUndo!=null &&smartHandleMoveUndo.isMyObject(getPrimarySelectedObject())) smartHandleMoveUndo.establishFinalState();
	}

	/**
	creates a marker for the region that the user is dragging the mouse over
	 */
	public void updateSelectingMask() {
		selection=null;
		if (useSelectorNow() ) {
			Rectangle2D rect = OverlayObjectManager.createRectangleFrom2Points(new Point2D.Double(pressX, pressY), this.draggedCord());
			getImageClicked().getOverlaySelectionManagger().select(rect, 0);
			selection=rect;
			}
	}

	public boolean useSelectorNow() {
		if (this.shiftDown()&&createSelector&&(this.getPrimarySelectedObject()==null|| (getPrimarySelectedObject()!=null&&getPrimarySelectedObject().getOutline().contains(pressX, pressY)))) return true;
		return (getAllSelectedItems(false).size()==0)&&createSelector&&getSelectedHandleNumber()<0;
	}
	
	public void moveRois(int x, int y) {
		
		
		
		boolean performSnap=false;
		performSnap = isMetaOrControlDown();
		
		ArrayList<LocatedObject2D> items = getAllSelectedItems(false);
		
		
		
		for(LocatedObject2D roi: items) {
			if (roi.isUserLocked()<=0)
					moveSingleObject(roi, x, y);
				if (movingAttachedItem() &&items.size()==1) {
					StatusPanel.updateStatus("moving attached item");
					moveAttachedObject(roi, x, y);
				}
					if (performSnap) {
						Rectangle2D r4 = getNearestPanelRect(this.getImageClicked(), RectangleEdges.getLocation(RectangleEdges.CENTER, roi.getBounds()), ignorehidden, this.getPrimarySelectedObject());
						snapRoi(roi, r4, 8, false);
						}
		}
			
			
	}



	

	protected boolean isMetaOrControlDown() {
		boolean performSnap;
		if ( IssueLog.isWindows()){
			performSnap=this.getLastClickMouseEvent().isControlDown();
		} else {performSnap=this.getLastDragOrLastReleaseMouseEvent().isMetaDown();}//altKeyDown();
		return performSnap;
	}
	
	protected void moveSingleObject(LocatedObject2D sel, int x, int y) {
		if (sel==null) return;
		if (sel instanceof PanelLayoutGraphic ) {
			PanelLayoutGraphic s=(PanelLayoutGraphic) sel;
			s.moveLayoutAndContents(x, y);
			} else sel.moveLocation(x,y);
		LocationChangeListenerList listen = sel.getListenerList();
		if (listen!=null) listen.notifyListenersOfUserMove(sel);
	}
	
	
	

	/**Select the primary object*/
	public void select(Object roi1) {
		setSelectedItemForDisplay(null);
		
		
		if (roi1!=null && roi1 instanceof Selectable) {
			((Selectable)roi1).select();
			setSelectedItemForDisplay(roi1);
			if (getImageClicked()==null) return;
			OverlayObjectManager manager = getImageClicked().getOverlaySelectionManagger();
			LockedItemHandle sHandle = this.findHandleForLockedItem(roi1);
			manager.setSelectionGraphic3(SmartHandleList.createList(sHandle));
			
			if (sHandle!=null) {
				LockedItemHandle demiVersion = sHandle.createDemiVersion();
				demiVersion.handlePress(getLastClickMouseEvent());
				manager.setSelectionGraphic3(SmartHandleList.createList( demiVersion));
				if (this.getPressedSmartHandle()==null)this.setPressedSmartHandle(demiVersion);
			}
			
			if (bringSelectedToFront&&roi1 instanceof LocatedObject2D){
				LocatedObject2D locObject = (LocatedObject2D) roi1;
				
				
				LocatedObject2D old = manager.getSelection(1);
				
				
				manager.setSelection(locObject, 1);
				/**keeps both new and old object selected*/
				if (this.shiftDown() &&old instanceof Selectable)  {
					Selectable s=(Selectable) old;
					s.select();
				}
					
			}
		}
	}

	protected void setSelectedItemForDisplay(Object roi1) {
		if(getImageDisplayWrapperClick()==null) return;
		getImageDisplayWrapperClick().setSelectedItem((Selectable) roi1);
	}
	
	

	

	
	@Override
	public void mouseEntered() {
		super.getImageDisplayWrapperClick().setCursor(getNormalCursor());
		
	}
	

	public void mouseExited() {
		
		if (this.getImageClicked()!=null) getImageClicked().updateDisplay();
		
	}
	
	@Override
	public String getToolName() {
		
		return selectorToolName;
	}

	public int getSelectedHandleNumber() {
		return handle;
	}

	public LocatedObject2D getPrimarySelectedObject() {
		return selectedItem;
	}



	public IconSet getIconSet() {
		return set;
	}
	
	
	public void createIconSet(String... args) {
		set=new IconSet(args);
		
	}

	public Class<?> getExcludedClass() {
		return excludedClass;
	}

	public void setExcludedClass(Class<?> excludedClass) {
		this.excludedClass = excludedClass;
	}
	
	static Class<?>[] potentialExcludedClasses=new Class<?>[] {null, PanelLayoutGraphic.class, ImagePanelGraphic.class, TextGraphic.class};
	static String[]  nalesofExcludedClasses=new String[] {"none", "Layouts", "ImagePanels", "Text"};
	
	int getindexofExcluded() {
	Class<?> c = this.getExcludedClass();
	if (c==null) return 0;
	 int i=0;
	 while(i< potentialExcludedClasses.length&&!c.equals(potentialExcludedClasses[i]) ) {
		 i++;
	 }
	 
	 return i;
	}
	
	@Override
	public void showOptionsDialog() {
		
		 try {
			MoverDialog md = new MoverDialog(this);
			md.showDialog();
		} catch (Throwable e) {
			IssueLog.logT(e);
		}
		 
	}
	
	public int isSelectingroup() {
		return selectingroup;
	}

	public void setSelectingroup(int selectingroup) {
		Object_Mover.selectingroup = selectingroup;
	}

	public Cursor getNormalCursor() {
		return normalCursor;
	}

	public void setNormalCursor(Cursor normalCursor) {
		this.normalCursor = normalCursor;
	}

	public Cursor getHandleCursor() {
		return handleCursor;
	}

	public void setHandleCursor(Cursor handleCursor) {
		this.handleCursor = handleCursor;
	}

	public class MoverDialog extends StandardDialog {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object_Mover mover;
		
		public MoverDialog(Object_Mover mover) {
			setModal(true);
			this.mover=mover;
			add("excludedClass", new ChoiceInputPanel("Select which class to ignore", nalesofExcludedClasses, mover.getindexofExcluded()));
			String[] groupops=new String[] {"Don't", "Do", "1 Level Down", "2 Level Down"};
			add("groupsel", new ChoiceInputPanel("Reach into Groups",groupops, mover.isSelectingroup()));
			add("selGroup", new BooleanInputPanel("Select in Group", GraphicGroup.treatGroupsLikeLayers));
		}
		@Override
		public void onOK() {
			mover.setExcludedClass(potentialExcludedClasses[this.getChoiceIndex("excludedClass")]);
			int choiceIngroup = this.getChoiceIndex("groupsel");
			mover.setSelectingroup(choiceIngroup);
			
			GraphicGroup.treatGroupsLikeLayers=this.getBoolean("selGroup");
		}
		
		
	}

	
	public boolean keyPressed(KeyEvent arg0) {
		if(this.textEditMode()) {
			this.keyPressOnSelectedTextItem(arg0);
			return false;
		}

		if (!arg0.isShiftDown()) {
			DisplayedImage imageDisplayWrapperClick = getImageDisplayWrapperClick();
			switch (arg0.getKeyCode()) {
			
			
			case KeyEvent.VK_LEFT: {moveRois(-2,0) ; ;break;}
			case KeyEvent.VK_RIGHT: {moveRois(2,0); ;break;}
			case KeyEvent.VK_UP: {moveRois(0,-2); ;break;}
			case KeyEvent.VK_DOWN: {moveRois(0,2) ;break;}
			case KeyEvent.VK_ESCAPE: {
				
				//in this situation the window should close but it does not ask
				break;
			}
			case KeyEvent.VK_ENTER: {
				todefaultTool();
				break;
			}
			case KeyEvent.VK_SPACE: {
				todefaultTool();
				break;
			}
			case KeyEvent.VK_TAB: {
				todefaultTool();
				break;
			}
			case KeyEvent.VK_DELETE: {
				
				deleteSelectedItemsAndLayers(imageDisplayWrapperClick);
				;break;}
			case KeyEvent.VK_BACK_SPACE: {
				if(arg0.isShiftDown()) {
					deleteSelectedItemsAndLayers(imageDisplayWrapperClick);
					;break;
				}
				this.getImageDisplayWrapperClick().getUndoManager().addEdit(new UndoAbleEditForRemoveItem(null,(ZoomableGraphic)getPrimarySelectedObject() ));
				this.getImageClicked().getGraphicLayerSet().remove((ZoomableGraphic) getPrimarySelectedObject());
				this.selectedItem=null;
				imageDisplayWrapperClick.setSelectedItem(null);
				;break;}
			case KeyEvent.VK_A: {
				ArrayList<LocatedObject2D> all = imageDisplayWrapperClick.getImageAsWrapper().getLocatedObjects();
				if (arg0.isControlDown()||arg0.isMetaDown()) {
					
					for(LocatedObject2D item: all) {
						if (item instanceof Selectable) {
							Selectable s=(Selectable) item;s.select();
						}
					}
				} else {
					LocatedObject2D sel = this.getPrimarySelectedObject();
					ArrayObjectContainer.selectAllOfType(all, sel);
				}
				
				;break;}
			case KeyEvent.VK_V: {
				
				if (arg0.isAltDown()) {
					GraphicLayer cc = getImageClicked().getGraphicLayerSet().getSelectedContainer();
					ImagePanelGraphic image = (ImagePanelGraphic) new ClipboardAdder(false).add(cc);
					image.setRelativeScale(ImageDPIHandler.ratioFor300DPI());
					image.setLocation(this.getClickedCordinateX(), this.getClickedCordinateY());
					getImageClicked().updateDisplay();
				}
				break;
			}
}
		}
	
		return false;
	}

	/**removes the selected items from the image*/
	public void deleteSelectedItemsAndLayers(DisplayedImage imageDisplayWrapperClick) {
		ArrayList<LocatedObject2D> rois3 = getAllSelectedItems(false);
		ArrayList<GraphicLayer> rois4 = this.getSelectedLayers(this.getImageClicked().getGraphicLayerSet());
		CombinedEdit undoableEdit=new CombinedEdit();
		imageDisplayWrapperClick.getUndoManager().addEdit(undoableEdit);
		for (LocatedObject2D roi: rois3){
			undoableEdit.addEditToList(new UndoAbleEditForRemoveItem(null,(ZoomableGraphic)roi ));
			this.getImageClicked().getGraphicLayerSet().remove((ZoomableGraphic) roi);
		}
		for (GraphicLayer roi: rois4){
			undoableEdit.addEditToList(new UndoAbleEditForRemoveItem(null,(ZoomableGraphic)roi ));
			this.getImageClicked().getGraphicLayerSet().remove((ZoomableGraphic) roi);
		}
		this.selectedItem=null;
		imageDisplayWrapperClick.setSelectedItem(null);
	}




	
	
	public DragAndDropHandler getDragAndDropHandler() {
		return new MoverDragHandler(this);
	}
	
	
	@Override
public String getToolTip() {
		
		return "Select, Move and Manipulate Objects";
	}
	
	boolean textEditMode() {
		if (getPrimarySelectedObject() instanceof TextGraphic) {
			if (((TextGraphic)getPrimarySelectedObject()).isEditMode())
				return true;
		}
		
		return false;
	}
	
/**next series of fields and methods are for text edit mode*/
	
	protected TextGraphic lastText=null;
	protected int lastCursor;
	protected UndoTextEdit lastUndo;
	protected static LocatedObject2D lastToolsSelectedItem;
	/**Implemented when a mouse press is done on a test item*/
	public void mousePressOnTextCursor(TextGraphic textob) {
		
		int x = this.getClickedCordinateX();
		int y = this.getClickedCordinateY();
		new CursorFinder().setCursorFor(textob, new Point(x,y));
		textob.setHighlightPositionToCursor();
		lastText=textob;//put here recently because it was in the text tool
		if (lastText!=null)
			lastCursor=lastText.getCursorPosition();
	}
	public void mouseDragForTextCursor() {
		int x = this.getDragCordinateX();
		int y = this.getDragCordinateY();
		new CursorFinder().setCursorFor(lastText, new Point(x,y));
		if(lastText==null) return;
		lastText.setSelectedRange(lastCursor, lastText.getCursorPosition());
	}

	/**What to do if a key is typed into a text item*/
	public void keyPressOnSelectedTextItem(KeyEvent arg0) {
		TextGraphic textob=(TextGraphic) getPrimarySelectedObject();
		
		if (this.lastUndo==null ||lastUndo.getTextItem()!=textob) {
			this.lastUndo=new UndoTextEdit(textob);
		}
		
		textob.handleKeyPressEvent(arg0);
		UndoManagerPlus man = this.getImageDisplayWrapperClick().getUndoManager();
		if (!man.hasUndo(lastUndo)) man.addEdit(lastUndo);
		lastUndo.setUpFinalState();
		this.updateClickedDisplay();
	}

	
	
	

	
	
	public void keyTypedOnTextItem(KeyEvent arg0) {
		if(this.textEditMode()) {
			TextGraphic textob=(TextGraphic) getPrimarySelectedObject();
			textob.handleKeyTypedEvent(arg0);
			return;
		}
		
		
	}
	
	@Override
	public boolean treeSetSelectedItem(Object o) {
		if (o!=getPrimarySelectedObject() && o instanceof LocatedObject2D) {
			setPrimarySelectedObject((LocatedObject2D) o);
			return true;
		}
		return false;
	}
	
	
	/**looks through a list of takeslocked items and returns the one that has a given objet
	 * @return 
	 */
	public static TakesLockedItems getLockContainterForObject(LocatedObject2D object, ArrayList<LocatedObject2D> list) {
		for(LocatedObject2D t: list) try {
			if (t==null||!(t instanceof TakesLockedItems)) continue;
			TakesLockedItems taker=(TakesLockedItems) t;
			if (taker.hasLockedItem(object)) return taker;
		} catch (Throwable t2) {
			IssueLog.logT(t2);
		}
		return null;
	}
	/**gets all the object in a particular image that can take a locked item.*/
	public static ArrayList<LocatedObject2D> getPotentialLockAcceptors(ImageWrapper gmp) {
		ArrayList<LocatedObject2D> aRoi;
		aRoi=gmp.getLocatedObjects();
		ArraySorter<LocatedObject2D> as = new ArraySorter<LocatedObject2D>();
		aRoi=as.getThoseOfClass(aRoi, TakesLockedItems.class);
		
		return aRoi;
	}
	/**finds what object holds the attached item*/
	public TakesLockedItems findLockContainer(LocatedObject2D object2) {
		return getLockContainterForObject(object2, getPotentialLockAcceptors(getImageClicked()));
	}
	/**returns true if the user is trying to move an item that is attached to another object*/
	private boolean movingAttachedItem() {
		return isAttachedItem(this.getPrimarySelectedObject());
	}
	private boolean isAttachedItem(LocatedObject2D object) {
		if(object instanceof BarGraphic.BarTextGraphic) {
			BarGraphic.BarTextGraphic b=(BarTextGraphic) object;
			if (b.locationAutoMatic()) return true;
		}
		return findLockContainer(object)!=null;
	}
	
	/**called if the user is trying to move an item that is attached to another object*/
	private void moveAttachedObject(LocatedObject2D roi, int x, int y) {
		
		if(roi instanceof BarGraphic.BarTextGraphic) {
			BarGraphic.BarTextGraphic b=(BarTextGraphic) roi;
			if (b.locationAutoMatic()) b.getLocationHandleForBarText().handleDrag(getLastDragOrLastReleaseMouseEvent());;
		}
		
		
		LockedItemHandle lockedItemHandle = findHandleForLockedItem(roi);
		if(lockedItemHandle==null) return;
		lockedItemHandle=lockedItemHandle.createDemiVersion();
		lockedItemHandle.handleDrag(getLastDragOrLastReleaseMouseEvent());
		
	
	}

	public LockedItemHandle findHandleForLockedItem(Object r) {
		if (r instanceof LocatedObject2D) {
			LocatedObject2D object=(LocatedObject2D) r;
		TakesLockedItems tk = findLockContainer(object);
		if(tk==null) return null;
		if (tk.getSmartHandleList()==null) return null;
		LockedItemHandle lockedItemHandle = tk.getSmartHandleList().getLockedItemHandle(object);
		return lockedItemHandle;
		}
		return null;
	}
	
	
	/**Called when a tool is about to be switched away from (false) or switched to (true)*/
	@Override
	public void onToolChange(boolean b) {
		if (!b) {
			lastToolsSelectedItem=this.getPrimarySelectedObject();
		} else {
			if (lastToolsSelectedItem!=null) {
				selectedItem= lastToolsSelectedItem;
				lastToolsSelectedItem=null;
			}
		}
		
	}
	
	/**determines whether the canvas is automatically resized*/
	public boolean isResizeCanvasAfterMouseRelease() {
		if (CanvasOptions.current.resizeCanvasAfterEdit)
		return resizeAfterMousDrags;
		return false;
	}

	public void setResizeAfterMousDrags(boolean resizeAfterMousDrags) {
		this.resizeAfterMousDrags = resizeAfterMousDrags;
	}

	class LocalIcon extends GraphicToolIcon {
		
		public LocalIcon(int type) {
			super(type);
		}

		@Override
		protected void paintObjectOntoIcon(Component arg0, Graphics g, int arg2, int arg3) {
			super.paintCursorIcon=true;
			
		}

		@Override
		public GraphicToolIcon copy(int type) {
			return new LocalIcon(type);
		}}

}

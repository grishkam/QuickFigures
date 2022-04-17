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
 * Version: 2022.0
 */
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

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import addObjectMenus.ClipboardAdder;
import appContext.ImageDPIHandler;
import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWorkSheet;
import basicMenusForApp.CurrentWorksheetLayerSelector;
import externalToolBar.DragAndDropHandler;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.CursorFinder;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import graphicalObjects_SpecialObjects.BarGraphic.BarTextGraphic;
import handles.HasHandles;
import handles.HasSmartHandles;
import handles.AttachmentPositionHandle;
import handles.ReshapeHandleList;
import handles.SmartHandle;
import handles.SmartHandleList;
import icons.GraphicToolIcon;
import icons.IconSet;
import imageDisplayApp.KeyDownTracker;
import imageDisplayApp.OverlayObjectManager;
import imageDisplayApp.UserPreferences;
import includedToolbars.StatusPanel;
import layout.PanelLayout;
import locatedObject.ArrayObjectContainer;
import locatedObject.CarriesLockTaker;
import locatedObject.LocatedObject2D;
import locatedObject.LocatedObjectGroup;
import locatedObject.LocationChangeListenerList;
import locatedObject.RectangleEdges;
import locatedObject.Scales;
import locatedObject.ScalesFully;
import locatedObject.Selectable;
import locatedObject.Snap2Rectangle;
import locatedObject.TakesAttachedItems;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import messages.ShowMessage;
import menuUtil.HasUniquePopupMenu;
import menuUtil.SmartJMenu;
import objectDialogs.GraphicItemOptionsDialog;
import standardDialog.StandardDialog;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.UndoMoveItems;
import undo.UndoTextEdit;
import utilityClasses1.ArraySorter;
import undo.UndoAbleEditForRemoveItem;
import undo.UndoDragHandle;
import undo.UndoManagerPlus;

/**The most used tool in the toolbar, allows user to select objects, drag handles, bring up popup menus and 
  more....*/
public class Object_Mover extends BasicToolBit implements ToolBit  {
	/**
	 * 
	 */
	public static final int CODE_FOR_RESHAPE_HANDLE_LIST = 90000000;
	private static final int NO_HANDLE = HasHandles.NO_HANDLE_;

	/**
	 * 
	 */
	private static final int NORMAL_OBJECT_MOVER = 0;

	protected IconSet iconSet=GraphicToolIcon.createIconSet(new LocalIcon(0));
	
	/**true if this tool or a subclass allows the user to select regions of the cancas*/
	protected boolean createSelector=true;	
			
			private Cursor handleCursor=new Cursor(Cursor.HAND_CURSOR);
			
	protected LocatedObject2D primarySelectedItem;
	
	protected ArrayList<LocatedObject2D> otherSelectedItems;
	public boolean realtimeshow=false;
	
	/***/
	protected boolean ignorehidden=true;
	
	protected boolean bringSelectedToFront=false;
	
	 boolean paneMode=false;
	
	
	
	
	 public static final int DO_NOT_SELECT_IN_GROUP=0, SELECT_IN_GROUP=1, SELECT_ONE_LEVEL_DOWN=2, SELECT_2_LEVEL_DOWN=3;
	 /**Determines whether to use a specific method of selecting items within groups*/
	 private static int selectingroup=DO_NOT_SELECT_IN_GROUP;
	 
	 /**The undoable edit that will be added to the undo manager to allow a user to undo mouse drags*/
		 protected UndoMoveItems currentUndo;
		 protected boolean addedToManager=false;//true if the above edit has been added to the manager
	  
		protected Class<?> excludedClass=null;
		private Class<?> onlySelectThoseOfClass=Object.class;

	public Object_Mover() {
		
		
	}
	private int handle=NO_HANDLE;
	int mode=NORMAL_OBJECT_MOVER;

	/**For storing a list of selected items*/
	private ArrayList<LocatedObject2D> rois2;

	/**for storing the most recent mouse press point*/
	protected int pressX,  pressY;
	protected Rectangle2D areaSelection;
	
	AbstractUndoableEdit2 smartHandleMoveUndo=null;
	private UndoDragHandle currentundoDragHandle;
	private boolean addedcurrentundoDragHandle;
	private ReshapeHandleList lastGroupHandleList;
	private SmartHandle smartHandle;

	/**the canvas size handle*/
	private SmartHandleList canvasHandleList;

	/**is set to true after the user drags the canvas size handle (this is a special case)*/
	private boolean draggingCanvasHandle;

	/**is set to true if the point pressed is not within the primary selected object*/
	private boolean notWithinPrimary;
	private boolean popup;//is set to true if a popup is to be shown

	/**The handle that the mouse recently moved over*/
	private static SmartHandle lastMoveOverHandle;

	/**Sets which object is the currently selected one*/
	public LocatedObject2D setPrimarySelectedObject(LocatedObject2D roi) {
		CanvasMouseEvent me = getLastMouseEvent();
		if (me==null) return setPrimarySelectedObject(false, roi);
		
		return setPrimarySelectedObject(me.shiftDown(), roi);
	}
	
	/**Sets the currently selected object, 
	 * if shift is false, deselects all other objects.
	 *
	 * @param shift is the shift key down
	 * */
	public LocatedObject2D setPrimarySelectedObject(boolean shift, LocatedObject2D targetItem) {
		
		if (shift){
				//in the case of a shift down click, the old item will be switch from primary selected to ordinary selected
				if (primarySelectedItem!=null && primarySelectedItem!=targetItem) 
					primarySelectedItem.makePrimarySelectedItem(false);
		} else	{
				//deselects the previous  item if shift is not down
				if (primarySelectedItem!=targetItem)
							{   deselect(primarySelectedItem);
							    deselectAllExcept(targetItem) ;   }
		}
		
		otherSelectedItems=null;
		primarySelectedItem=null;
		primarySelectedItem=targetItem;
		primarySelect(primarySelectedItem);//makes sure the item is set as selected
		
		if (getImageClicked()!=null) //informs the image that it has a primary selected item
			getImageClicked().setPrimarySelectionObject(primarySelectedItem);
		
		return primarySelectedItem;
	}

	/**
	sets the item as a primary selected item
	 */
	public void primarySelect(LocatedObject2D primarySelectedItem) {
		select(primarySelectedItem);
		if (primarySelectedItem!=null) {
					primarySelectedItem.makePrimarySelectedItem(true); 			
		}
	}
	
	/**Deselects all items excluding one
	 * @param exempt the one excluded*/
	protected void deselectAllExcept(Object exempt) {
		if (getImageClicked()!=null)
			deselectAll(this.getImageClicked().getTopLevelLayer(), exempt);
	}
	/**Deselects all items in the layer except one
	 * @param exempt the one excluded*/
	 void deselectAll(GraphicLayer gl, Object exempt) {
		if (gl==null) return;
		ArrayList<ZoomableGraphic> ls = gl.getAllGraphics();
		deselectAll(ls, exempt);
	}
	 /**Deselects all items in the list except one
		 * @param exempt the one excluded*/
	protected void deselectAll(ArrayList<?> ls, Object exempt) {
		getSelectionManager().removeObjectSelections();
		for(Object l: ls) try  {
			if(l==exempt) continue;
				deselect(l);
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}
	
	/**deselects the given item*/
	public void deselect(Object roi1) {
		if (roi1!=null && roi1 instanceof Selectable) try {
			((Selectable)roi1).deselect();
			if(getImageClicked()!=null)
				this.getImageClicked().getOverlaySelectionManagger().setSelection(null, 1);
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}
	
	/**Selects the items in the list*/
	public static void selectAll(ArrayList<?> ls) {
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
	
	
	
	/**returns the handle id number for the handle that is at the last clickpoint
	 * Also stores that as the selected handle number*/
	public int establishMovedIntoOrClickedHandle(boolean press) {
		this.setSelectedHandleNumber(NO_HANDLE);//starts off without a handle ID
		
		//checks each handle of each item that is set as selected
		for(LocatedObject2D object: this.getAllSelectedItems(false)) {
			
			if (object instanceof HasHandles) {
							HasHandles handledObject = (HasHandles)object;
							int handleNumber = handledObject.handleNumber(getMouseXClick(), getMouseYClick());
							
							setSelectedHandleNumber(handleNumber);
							if (handleNumber!=NO_HANDLE) {
									if (press)
										this.setPrimarySelectedObject(object);//in the context of a mouse press, changes the primary selected item
									
									this.setSelectedHandleNumber(handleNumber);
									break;
							}
						
					}
			
		}
		
		/**if no handle is selected for an individual object at this point,
		 * check the special case of handle lists that apply to a group of items
		 *  checks for handles in an object group handle list*/
		if(getSelectedHandleNumber()==NO_HANDLE && getObjectGroupHandleList()!=null) {
			int num2 = getObjectGroupHandleList().handleNumberForClickPoint(getMouseXClick(), getMouseYClick());
			setSelectedHandleNumber(num2);
		}
		
		/**If no handle is set at this point, checks
		 * another special case. There is a handle devoted to changing the canvas size
		 * determines if a canvas handle is at the click point*/
		if(getSelectedHandleNumber()==NO_HANDLE) {
			int num2 = getCanvasHandleList().handleNumberForClickPoint(getMouseXClick(), getMouseYClick());
			setSelectedHandleNumber(num2);
			if (num2>0) draggingCanvasHandle=true; else draggingCanvasHandle=false;
		} else  draggingCanvasHandle=false;
	
		
		/**does not check the special case of an attached item handle*/
		//not implemented here
		
		return getSelectedHandleNumber();
	}
	


	public void mousePressed() {
		
		areaSelection=null;//starts without a selected area
		CanvasMouseEvent e = getLastMouseEvent();
		pressX= getClickedCordinateX();
		pressY= getClickedCordinateY();
		
		/**first task, determines if a handle has been clicked and
		  stores the handle id number for the handle*/
		establishMovedIntoOrClickedHandle(true);
		

		
		/**second task, determines if there is an object has been clicked
		   if no handle has been clicked this becomes important*/
		LocatedObject2D objectAtPressLocation = getObjectAt(getImageClicked(), pressX, pressY);
		boolean startsSelected=false;
		if(objectAtPressLocation!=null) 
			startsSelected=objectAtPressLocation.isSelected();
	
		
		SmartHandle sh= this.findSelectedSmartHandle(true);
		
		/**what to do in the event of a handle press*/
						if (sh!=null) {
							sh.handlePress(this.getLastMouseEvent());
							this.setPressedSmartHandle(sh);
						} else setPressedSmartHandle(null);
			
		/**in the event that the object pressed does not use smart handles
		 * will soon be obsolete*/				
		if (getSelectedHandleNumber()!=NO_HANDLE && getPrimarySelectedObject() instanceof HasHandles) {
				HasHandles h=(HasHandles) getPrimarySelectedObject();
				h.handlePress(getSelectedHandleNumber(), new Point(getClickedCordinateX(),  getClickedCordinateY()));
				}
					
		/**in the event of a popup trigger, will display either 
		 * the handle's popup menu or the objects popup menu*/
		if (e.isPopupTrigger()) {
			forPopupTrigger(objectAtPressLocation, e,sh);
			popup=true;
			return;
		} else popup=false;
		
		
		if (objectAtPressLocation!=getPrimarySelectedObject()&&getSelectedHandleNumber()==NO_HANDLE) {
			/**If an object was pressed, selects the object*/
			objectAtPressLocation=setPrimarySelectedObject(objectAtPressLocation);
		} else {
			/**If a handle was pressed, makes sure that the primary selected object is in selected mode
			 * TODO: double check the circumstances in which this part required and not redundant. */
			select(getPrimarySelectedObject());
			if (getPrimarySelectedObject()!=null)
				getPrimarySelectedObject().makePrimarySelectedItem(true);
		}
		
		
		/**if neither a handle nor an object was pressed, then no object is selected.
		 * De-selects the primary selected object and set the stored primary selected object to null*/
		if(objectAtPressLocation==null&&getSelectedHandleNumber()==NO_HANDLE) 
			{
				setPrimarySelectedObject(null);
				this.getObjectGroupHandleList();//updates the group handles
			}
		
			
		if (getPrimarySelectedObject()==null) {
			attemptLayerGrab();
			
			//nothing left to do
			return;
		}
			
		
			if (getPrimarySelectedObject() instanceof HasHandles &&getSelectedHandleNumber()>NO_HANDLE) {
				/**informs the object that a mouse press occurred in its handle
				 * some classes of object will do something in this case*/
				getSelectionObjectAshashangles().handleMouseEvent(this.getLastMouseEvent(), handle, getButton(),0, MouseEvent.MOUSE_PRESSED, null);
				
				createUndoForDragHandle() ;//creates a simple undo that will work for certain classes of handle
			}
			
			boolean shift=e.shiftDown();
			boolean copyObjects = this.getLastMouseEvent().altKeyDown();
			
		if (startsSelected && shift && this.handle==NO_HANDLE) {
			/**when holding down the shift key, the user sometimes wants to deselect an item not select it*/
			deselect(objectAtPressLocation);//this implements that case
			if(this.getPrimarySelectedObject()==objectAtPressLocation)
				this.setPrimarySelectedObject(null);
			this.getObjectGroupHandleList();//updates the group handles
		}
					ArrayList<LocatedObject2D> allSelectedrois2 = getAllSelectedItems(false);
					
					if (copyObjects)
						rois2 = getAllSelectedItems(copyObjects);
					else 
						rois2=allSelectedrois2 ;
					
					if (copyObjects) {
						this.setSelectedHandleNumber(NO_HANDLE);//selected handle numbers are not relevant 
						this.deselectAll(allSelectedrois2, null);//sets the orginal as not selected
						selectAll(rois2);//sets the copies as selected
					}
					
			 		currentUndo=new UndoMoveItems(rois2);//establishes the undo
			 		addedToManager=false;
			 		
			 	
			 		innitiateSpecialCaseHandleLists();
	
			 		/**The special case of mouse presses that target text items*/
				if(this.textEditMode() &&!e.isPopupTrigger()&&this.getSelectedHandleNumber()==NO_HANDLE) {
					this.mousePressOnTextCursor((TextGraphic) this.getPrimarySelectedObject());
				}
			
			
				/**if the mouse press starts outside of the primary selected item, needs to store this
				   */
			this.notWithinPrimary=getPrimarySelectedObject()!=null
																&&
																!getPrimarySelectedObject().getOutline().contains(pressX, pressY);
			
			
			
		}

	/**
	Calls methods to ensure that handle lists for special cases are visible
	 */
	public void innitiateSpecialCaseHandleLists() {
		this.getObjectGroupHandleList();//makes sure it is initialized
		this.getCanvasHandleList();//makes sure it is initialized
	}
	

	/**
	not yet implemented, if the press location is barely outside of an object
	selects all objects in the layer
	 */
	private void attemptLayerGrab() {
		// TODO Auto-generated method stub
		//not yet implemented
	}

	/**stores the pressed smart handle
	 * that same object will be accessed when drags and releases occur*/
	protected void setPressedSmartHandle(SmartHandle sh) {
		smartHandle=sh;
	}
	/**return the pressed smart handle
	 * that same object will be accessed when drags and releases occur*/
	protected SmartHandle getPressedSmartHandle() {
		return smartHandle;
	}

	/**Creates a generic handle drag undo that works for certain classes*/
	protected void createUndoForDragHandle() {
		if (getSelectionObjectAshashangles()==null) return;
		currentundoDragHandle=new UndoDragHandle(handle, this.getSelectionObjectAshashangles(), new Point(getClickedCordinateX(),  getClickedCordinateY()));
		if (getPrimarySelectedObject() instanceof BasicGraphicalObject) {
			smartHandleMoveUndo=((BasicGraphicalObject)getPrimarySelectedObject()).provideDragEdit();
		}
		addedcurrentundoDragHandle=false;
	}
	
	/**returns every selected sublayer*/
	static ArrayList<GraphicLayer> getSelectedLayers(GraphicLayer s) {
		
		ArrayList<GraphicLayer> output = new ArrayList<GraphicLayer> ();
		for(GraphicLayer z:s.getSubLayers()) {
			if(isLayerSelected(z)) output.add(z);
		}
		
		return output;
	}
	
	/**returns true if every single item in the layer is selected
	 * @param the layer*/
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
		 otherSelectedItems= getAllSelected(getImageClicked().getTopLevelLayer());
		
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
				primarySelectedItem=out2.get(0);
				return out2;
				}
			
		return out;
	}
	
		/**removes any rois that are hidden if ignorehidded boolean is set to true.
		 * Also removes objects of the excluded class*/
	protected void removeIgnoredAndHidden(ArrayList<LocatedObject2D> rois) {
		if (this.ignorehidden) {
			if (this.ignorehidden) ArraySorter.removeHiddenItemsFrom(rois);
				}
		ArraySorter.removeThoseOfClass(rois, getExcludedClass());
	}
		
	

	
	
	/**what to do if a popup menu must be shown
	 @ return true if a popup is being shown*/
	protected boolean forPopupTrigger(LocatedObject2D roi2, CanvasMouseEvent e, SmartHandle sh ) {
		JPopupMenu menu =null;
		
		/**if the user is selecting more than one item by holding shift*/
		if (shiftDown()) {
			GroupOfObjectPopup menuAll = new GroupOfObjectPopup( new CurrentWorksheetLayerSelector());
			menu=menuAll;
			menuAll.addItemsFromJMenu( obtainUniquePopup(roi2), "this item");
			menuAll.setLastMouseEvent(e);
		}
		if (sh!=null) {
			menu=sh.getJPopup();
		} 
		
		if (menu==null&& roi2 instanceof HasUniquePopupMenu) {
			menu = obtainUniquePopup(roi2);
			
			addAttachmentPositiontoPopup(roi2, menu);
			
			}
		
		/**smart menus must be informed fo their context*/
		if (menu instanceof SmartPopupJMenu) {
			SmartPopupJMenu c = (SmartPopupJMenu) menu;
			c.setUndoManager(this.getImageDisplayWrapperClick().getUndoManager());
			c.setLastMouseEvent(e);
		}
		
		
		 Component c = getLastMouseEvent().getComponent();
		 if (menu!=null) 
			 {menu.show(c, e.getClickedXScreen(), e.getClickedYScreen());
			 return true;
			 }
		 
		 return false;
	}

	/**In the special case in which the object is attached to another object
	 * adds an 'adjust position' option to the popup menu.
	 * @param roi2
	 * @param menu
	 */
	public void addAttachmentPositiontoPopup(LocatedObject2D roi2, JPopupMenu menu) {
		try {
			if ( isAttachedItem(roi2)) {
				AttachmentPositionHandle lockHandle = this.findHandleForLockedItem(roi2);
				
				JMenu attach = new SmartJMenu("Attachment");
				for(MenuElement e: lockHandle.getJPopup().getSubElements()) {
					if(e instanceof JMenuItem)
						attach.add((JMenuItem) e);
				}
				
				if (lockHandle!=null) 
					menu.add(attach);
			}
		} catch (Exception e1) {
		
		}
	}

	/**returns the popup menu for the object
	 * @param roi2 the object selected*/
	protected JPopupMenu obtainUniquePopup(LocatedObject2D roi2) {
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
	public LocatedObject2D getObjectAt(ImageWorkSheet click, int x, int y) {
		ArrayList<LocatedObject2D> therois = getObjectsAtPressLocationWithoutFiltering(click, x, y);
		
		while (getGroupSelectionMode()==SELECT_IN_GROUP&& hasGroups(therois)) {
			replaceGroupsWithContents(therois,  x,  y);
		}
		if (getGroupSelectionMode()>1) {
			int i=getGroupSelectionMode();
			while(i>1&& hasGroups(therois)) {
				replaceGroupsWithContents(therois,  x,  y);
				i--;
			}
			
		}
		
		return getFirstNonhiddenItem(therois);
		
	}

	/**returns the first item on the list that is neither hidden not null*/
	public LocatedObject2D getFirstNonhiddenItem(ArrayList<LocatedObject2D> therois) {
		ArraySorter.removeThoseOfClass(therois, getExcludedClass());
		if (this.ignorehidden)ArraySorter.removeHiddenItemsFrom(therois);
		
		return new ArraySorter<LocatedObject2D>().getFirstNonNull(therois);
	}

	protected ArrayList<LocatedObject2D> getObjectsAtPressLocationWithoutFiltering(ImageWorkSheet click, int x, int y) {
		return getObjecthandler().getAllClickedRoi(click, x, y,this.getSelectOnlyThoseOfClass(), true);
	}
	
	/**iterates through the array. whenever it sees a locatedObjectGroup group, it will place the 
	  contents of the group in the array. This is for the select in group feature*/
	public void replaceGroupsWithContents(ArrayList<LocatedObject2D> therois, int x, int y) {
		ArraySorter<LocatedObject2D> as = new ArraySorter<LocatedObject2D>();
			ArrayList<LocatedObject2D> aa = as.getThoseOfClass(therois, LocatedObjectGroup.class);
			for(LocatedObject2D groupd:aa) {
				LocatedObjectGroup l=(LocatedObjectGroup)  groupd;
				ArrayList<LocatedObject2D> newrois = getObjecthandler().getAllClickedRoi(l.getObjectContainer(), x, y,this.getSelectOnlyThoseOfClass());
				as.replace(therois, groupd, newrois);
			}
	}
	
	/**creates an array that goes down 1 level into the groups */
	public ArrayList<LocatedObject2D> replaceGroupsWithContents2(ArrayList<LocatedObject2D> therois, int x, int y) {
	
			ArrayList<LocatedObject2D> output=new ArrayList<LocatedObject2D>();
			for(LocatedObject2D groupd:therois) {
				if (groupd instanceof LocatedObjectGroup){
				LocatedObjectGroup l=(LocatedObjectGroup)  groupd;
				ArrayList<LocatedObject2D> newrois = getObjecthandler().getAllClickedRoi(l.getObjectContainer(), x, y,this.getSelectOnlyThoseOfClass());
				output.addAll(newrois);
				}
				else output.add(groupd);
			
			}
			return output;
	}
	

	public void mouseMoved() {
		
		
		this.establishMovedIntoOrClickedHandle(false);
		
		updateCursorIfOverhandle();
		
		SmartHandle currentHandle = this.findSelectedSmartHandle(false);
		if (currentHandle!=null)
			currentHandle.mouseMovedOver(this.getLastMouseEvent());
		
		if (currentHandle!=lastMoveOverHandle) {
			if (     currentHandle!=null) 	 currentHandle.mouseEnterHandle(getLastMouseEvent());
			if (lastMoveOverHandle!=null) lastMoveOverHandle.mouseExitHandle(getLastMouseEvent());
		}
		lastMoveOverHandle=currentHandle;
		
		StatusPanel.updateStatus("("+getClickedCordinateX()+", "+getClickedCordinateY()+")");
		
		
		/**for certain subclasses, changes the selected item as the mouse moves*/
		if (!realtimeshow) return;
		
		//TODO: determine if this next part is necesary and delete
				/**
		
		LocatedObject2D roi2 =  getObjectAt(getImageClicked(), getClickedCordinateX(),getClickedCordinateY());
		
		
		
		if (roi2!=getPrimarySelectedObject()&&getSelectedHandleNumber()==-1) {
			setPrimarySelectedObject(roi2);
			IssueLog.log("Obsolete method called");
		} else select(getPrimarySelectedObject());
		if (roi2==null) setPrimarySelectedObject(null);
			
			if (getPrimarySelectedObject()==null) return;
			
			
		*/
			
			
			
			
			getImageClicked().updateDisplay();
		}

	/**changes the mouse cursor depending on there the mouse is*/
	public void updateCursorIfOverhandle() {
		if (super.getImageDisplayWrapperClick()==null) return;
		if (this.getSelectedHandleNumber()==NO_HANDLE) {
			super.getImageDisplayWrapperClick().setCursor(getNormalCursor());
		}
		else super.getImageDisplayWrapperClick().setCursor(getHandleCursor());
		
	}
	
	/**performs an instance check for has handles on the selected item and returns it*/
	private HasHandles getSelectionObjectAshashangles() {
		if (getPrimarySelectedObject() instanceof HasHandles )
			return (HasHandles) getPrimarySelectedObject() ;
		return null;
	}
	
	/**returns the selected SmartHandle based on the handle number*/
	private SmartHandle findSelectedSmartHandle(boolean press) {
		SmartHandle output=null;
		if (getPrimarySelectedObject() instanceof HasSmartHandles){
			HasSmartHandles handetConatiner = (HasSmartHandles) getPrimarySelectedObject();
			output= handetConatiner.getSmartHandleList().getHandleNumber(getSelectedHandleNumber());
		}
		
		
		
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
	
		/**if no smart handle is already found, checks for a 
		 * locked item handle (special the handle for moving an item that is attached to another)*/
		if(output==null && getSelectedHandleNumber()!=NO_HANDLE ) {
					output = findHandleToUseForLockedItem();
			} 
		
		return output;
	}

	/**returns a version of a locked item handle that will appear after 
	  its attached item is clicked and not when the parent item is clicked*/
	protected SmartHandle findHandleToUseForLockedItem() {
		SmartHandle output;
		output=this.findHandleForLockedItem(getPrimarySelectedObject());
		if (output!=null) 
			output=findHandleForLockedItem(getPrimarySelectedObject()).createDemiVersion();
		
		return output;
	}

	/**If multiple objects are selected, creates a group handle list*/
	private ReshapeHandleList getObjectGroupHandleList() {
		if(this.getClass()!=Object_Mover.class) {return null;} //not needed for subclasses
		if(!this.selectionsScale()) {
			getImageDisplayWrapperClick().getImageAsWorksheet().getOverlaySelectionManagger().setSelectionHandles(null);
			return null;
		}
		
		ReshapeHandleList newHandleList = new ReshapeHandleList(this.getAllSelectedItems(false), 5, CODE_FOR_RESHAPE_HANDLE_LIST, selectionsScale2Ways(), 0, false);
		
		/**if the new grouped handle list is much like the old one, just used the old one*/
		if(newHandleList.isSimilarList(lastGroupHandleList)) newHandleList=lastGroupHandleList;
			else if (lastGroupHandleList!=null) {
				lastGroupHandleList.finishEdit();
			}
		
		lastGroupHandleList=newHandleList;
		lastGroupHandleList.updateRectangle();
		getImageDisplayWrapperClick().getImageAsWorksheet().getOverlaySelectionManagger().setSelectionHandles(lastGroupHandleList);
		return lastGroupHandleList;
	}
	
	private SmartHandleList getCanvasHandleList() {
		
		canvasHandleList = this.getImageDisplayWrapperClick().getCanvasHandles();
		
		getImageDisplayWrapperClick().getImageAsWorksheet().getOverlaySelectionManagger().setPermanentHandles(canvasHandleList);
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


	
	public void mouseClicked() {
		
		
		
		
		GraphicItemOptionsDialog.setCurrentImage(getImageClicked());
		if (getPrimarySelectedObject() instanceof HasHandles) {
			getSelectionObjectAshashangles().handleMouseEvent(getLastMouseEvent(), handle, getButton(),clickCount(), MouseEvent.MOUSE_CLICKED, null);
			
		} else
		if (getPrimarySelectedObject() instanceof BasicGraphicalObject) {
			if (getPrimarySelectedObject()==null) return;
			//if (getSelectedObject().dropObject(super.getLastClickMouseEvent(), getClickedCordinateX(), getClickedCordinateY())==null) ;
			
						BasicGraphicalObject b=(BasicGraphicalObject) getPrimarySelectedObject();
						b.handleMouseEvent(this.getLastMouseEvent(), handle, getButton(), clickCount(), MouseEvent.MOUSE_CLICKED);
			
			
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
	
	
	protected static ArrayList<PanelLayout> getPotentialSnapLayouts(ImageWorkSheet imageWrapperClick, double x, double y, boolean ignoreHidden, LocatedObject2D exempt) {
		  
		
		ArrayList<LocatedObject2D> As = imageWrapperClick.getLocatedObjects();
		ArraySorter.removeThoseNotOfClass(As, PanelLayoutGraphic.class);
		if (ignoreHidden) ArraySorter.removeHiddenItemsFrom(As);
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
	public static Rectangle2D getNearestPanelRect(ImageWorkSheet imageWrapperClick, Point2D p, boolean ignoreHidden, LocatedObject2D exempt) {
		
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
		if (getSelectedHandleNumber()>NO_HANDLE&&getPrimarySelectedObject() instanceof HasHandles) {

			HasHandles h=(HasHandles) getPrimarySelectedObject();
			h.handleRelease(getSelectedHandleNumber(), new Point(getClickedCordinateX(),  getClickedCordinateY()), new Point(getDragCordinateX(), getDragCordinateY()));
	
		}
		
		
		
		afterRelease();
		
		
	}
	
	/**called after a mouse release*/
	protected void afterRelease() {
		getSelectionManager().select(null, 0); 
		if (getPrimarySelectedObject() instanceof HasHandles &&handle>NO_HANDLE) {
			getSelectionObjectAshashangles().handleMouseEvent(this.getLastDragOrLastReleaseMouseEvent(), handle, getButton(),this.clickCount(), MouseEvent.MOUSE_RELEASED, null);
			

			return;
		}
		
	
		
		
		/**If the user drags around a rectangle, selects the rois inside*/
		if ((getPrimarySelectedObject()==null ||this.getPrimarySelectedObject()!=null&&!getPrimarySelectedObject().getOutline().contains(pressX, pressY))&&createSelector) {
			selectRoisInDrawnSelector() ;
			findSelectedSmartHandle(false);
		}


		if (this.getPrimarySelectedObject() instanceof PathGraphic&&createSelector &&this.shiftDown()) {
			PathGraphic path=(PathGraphic) getPrimarySelectedObject() ;
			path.selectHandlesInside(	areaSelection);//this part does not help
			
		}
		
		/**sets these field to negative so next mouse action cannot affect last undoer*/
		smartHandleMoveUndo=null;
		 currentundoDragHandle=null;
		addedcurrentundoDragHandle=false;
		
		
		this.setPressedSmartHandle(null);
	}
	
	protected void selectRoisInDrawnSelector() {
		Rectangle2D rect = areaSelection;
		//getImageWrapperClick().getSelectionManagger().select(rect, 0);
		if(rect!=null&&rect.getHeight()>2&&rect.getWidth()>2) {
		//	
			ArrayList<LocatedObject2D> items = this.getObjecthandler().getOverlapOverlaypingItems(rect.getBounds(),this.getImageClicked());
			removeIgnoredAndHidden(items);
			if(items.size()==0) return;
			LocatedObject2D lastItem = items.get(items.size()-1);
			boolean selLast=pressX>this.getLastDragOrLastReleaseMouseEvent().getCoordinatePoint().getX();
			if (selLast)
				this.setPrimarySelectedObject(lastItem);
			else this.setPrimarySelectedObject(items.get(0));
			for(Object i: items) {
				select(i);
				if (i instanceof PathGraphic)
					((PathGraphic) i).selectHandlesInside(rect);
			}
			
		}
		areaSelection=null;
	}

	/**dont remember why i had the button as always 0*/
	private int getButton() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void mouseDragged() {
		
		if (panMode()) 
			return;;
		
		/**selects text if text edit mode */
		if (this.textEditMode()&&this.getSelectedHandleNumber()==NO_HANDLE)  {
			mouseDragForTextCursor();
			return;
		}
		
		if (popup)
			return;//does not drag if a popup is being shown
		 
		
		if (getPrimarySelectedObject()!=null) {			
			mousDragForObjectOrHandle();			
		}  else if (draggingCanvasHandle){
			/**just for the canvas resize handle, that handle drag will only be called here*/
			SmartHandle sh = this.getPressedSmartHandle();
			if (sh!=null) sh.handleDrag(getLastDragOrLastReleaseMouseEvent());
		}
		
		updateSelectingMask();
		
		getImageClicked().updateDisplay();	
		
	}

	/**
	if the user is holding down the keys to navigate the canvas by panning around,
	this performs the task and returns true. 
	 */
	protected boolean panMode() {
		try {
		paneMode=keysForPanModeDown();
		
			if (paneMode ) {
			
					int x = (int) -getXDisplaceMent();
					int y = (int) -getYDisplaceMent();
					
					this.getImageDisplayWrapperClick().scrollPane(x, y);
				
				
				return true;
			} else
				paneMode = false;
		} catch (Exception e) {
			IssueLog.logT(e);
		}
		
		return false;
	}

	/**returns true if the user is holding down the right combination of keys to navigate the page by panning
	 * @return
	 */
	protected boolean keysForPanModeDown() {
		boolean output = this.isMetaOrControlDown()&&this.shiftDown();
		if(KeyDownTracker.isKeyDown(KeyEvent.VK_SPACE) &&UserPreferences.current.spaceBarScrolling)
			output=true;
			return output;
	}

	/**
	 * 
	 */
	protected void mousDragForObjectOrHandle() {
		if (getSelectedHandleNumber()>NO_HANDLE) {
			performHandleDrag();
		}
		else
		if (mode==NORMAL_OBJECT_MOVER) {
			
			 performObjectDrag();
		}
	}

	/**
	 
	 */
	public void performObjectDrag() {
		moveManySelectedObjects(super.getXDisplaceMent(), super.getYDisplaceMent()) ;
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
		drags a handle, depending on what kind of handle or what kind of object,
		may perform additional tasks
	 */
	public void performHandleDrag() {
		/**for objects that are of the has handles interface*/
		if (getPrimarySelectedObject() instanceof HasHandles)
			{HasHandles h=getSelectionObjectAshashangles();
			h.handleMove(getSelectedHandleNumber(), new Point(getClickedCordinateX(),  getClickedCordinateY()), new Point(getDragCordinateX(), getDragCordinateY()));
			}
		
		/**every smart handle implements its own drag*/
		SmartHandle sh = this.getPressedSmartHandle();
		if (sh!=null) sh.handleDrag(getLastDragOrLastReleaseMouseEvent());
		
		
		/**lets the object know its handle is getting a mouse drag*/
		if (getPrimarySelectedObject() instanceof HasHandles)
			getSelectionObjectAshashangles().handleMouseEvent(this.getLastDragOrLastReleaseMouseEvent(), handle, getButton(),0, MouseEvent.MOUSE_DRAGGED, null);
		
		addHandleDragToUndoManager(sh);
	}

	/**Different forms of undo-able edits are created for different handles*/
	void addHandleDragToUndoManager(SmartHandle sh) {
		if (currentundoDragHandle==null) {
				createUndoForDragHandle() ;
				}
		
		if(sh!=null&&sh.handlesOwnUndo()) { currentundoDragHandle=null; currentundoDragHandle=null;}
			
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
	
		if (useSelectorNow() ) {
			Rectangle2D rect = OverlayObjectManager.createRectangleFrom2Points(new Point2D.Double(pressX, pressY), this.draggedCord());
			getSelectionManager().select(rect, 0);
			areaSelection=rect;
			} else {
			
				areaSelection=null;
				
			}
	}

	/**
	 * @return
	 */
	OverlayObjectManager getSelectionManager() {
		return getImageClicked().getOverlaySelectionManagger();
	}

	/**returns true if the region selector is draw under current circumstances
	  */
	public boolean useSelectorNow() {
		if (this.shiftDown()&&createSelector&&
				(this.getPrimarySelectedObject()==null|| 
				notWithinPrimary)) return true;
		return (getAllSelectedItems(false).size()==0)&&createSelector&&getSelectedHandleNumber()<0;
	}
	
	
	public void moveManySelectedObjects(int x, int y) {
		
		
		boolean performSnap=false;
		performSnap = isMetaOrControlDown();
		
		ArrayList<LocatedObject2D> items = getAllSelectedItems(false);
		for(LocatedObject2D roi: items) {
			if (roi.isUserLocked()==ShapeGraphic.LOCKED){
					if (items.size()>1)
					ShowMessage.showOptionalMessage("object is locked ", true, "one of the selected items is locked but can still be moved using its handles");
					return;
					}
		}
		
		for(LocatedObject2D item: items) {
		
			if (item.isUserLocked()==LocatedObject2D.NOT_LOCKED&&!movingAttachedItem())
					moveSingleObject(item, x, y);
			if (movingAttachedItem() &&items.size()==1) {
					
					moveAttachedObject(item, x, y);
				}

					if (performSnap) {
						Rectangle2D r4 = getNearestPanelRect(this.getImageClicked(), RectangleEdges.getLocation(RectangleEdges.CENTER, item.getBounds()), ignorehidden, this.getPrimarySelectedObject());
						snapRoi(item, r4, 8, false);
						}
		}
			
			
	}



	

	protected boolean isMetaOrControlDown() {
		boolean performSnap;
		if ( IssueLog.isWindows()){
			performSnap=this.getLastMouseEvent().isControlDown();
		} else {performSnap=this.getLastDragOrLastReleaseMouseEvent().isMetaDown();}//altKeyDown();
		return performSnap;
	}
	
	/**moves the object x,y units*/
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
			OverlayObjectManager manager = getSelectionManager();
			establishAttachedItemClick(roi1);
			
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
		else establishAttachedItemClick(null);
	}

	/**Check whether the item given is attached to another object
	 * In these cases, there is a handle that is  meant to control its movement.
	 * If so, sets that handle up as the selected handle
	 * @param roi1
	 * @param manager
	 */
	void establishAttachedItemClick(Object roi1) {
		AttachmentPositionHandle sHandle = this.findHandleForLockedItem(roi1);
		
		OverlayObjectManager overlaySelectionManagger = getSelectionManager();
		if(this.getLastMouseEvent()!=null &&this.getLastMouseEvent().shiftDown())
			sHandle=null;
		
		if (sHandle!=null) {
			AttachmentPositionHandle demiVersion = sHandle.createDemiVersion();
			demiVersion.handlePress(getLastMouseEvent());
			
			
				overlaySelectionManagger.setExtraHandle(demiVersion);
			
			if (this.getPressedSmartHandle()==null&&!textEditMode())
				{
				this.setPressedSmartHandle(demiVersion);
				this.setSelectedHandleNumber(demiVersion.getHandleNumber());
				}
		}
	
		else overlaySelectionManagger.setExtraHandle(null);
		
		if(this.textEditMode()) {
			
			overlaySelectionManagger.setExtraHandle(null);
		}
		
		/**The glue handle is used to attach items that are not already attached*/
		if(this.getPrimarySelectedObject() instanceof TextGraphic) {
			 TextGraphic t=(TextGraphic) getPrimarySelectedObject() ;
			 if(t.getGlueHandle()!=null)
				 t.getGlueHandle().setHidden(overlaySelectionManagger.getExtraHandle()!=null);
		}
	}

	protected void setSelectedItemForDisplay(Object roi1) {
		if(getImageDisplayWrapperClick()==null) return;
		getImageDisplayWrapperClick().setSelectedItem((Selectable) roi1);
	}
	
	@Override
	public void mouseEntered() {
		KeyDownTracker.reset();
		super.getImageDisplayWrapperClick().setCursor(getNormalCursor());
		
	}
	

	public void mouseExited() {
		KeyDownTracker.reset();
		if (this.getImageClicked()!=null) getImageClicked().updateDisplay();
		
	}
	
	@Override
	public String getToolName() {
		return selectorToolName;
	}

	/**returns the handle ID number of the selected handle*/
	public int getSelectedHandleNumber() {
		return handle;
	}
	
	/**sets the handle ID number of the selected handle*/
	public void setSelectedHandleNumber(int i) {
		handle=i;
	}

	public LocatedObject2D getPrimarySelectedObject() {
		return primarySelectedItem;
	}



	public IconSet getIconSet() {
		return iconSet;
	}
	
	/**loads icons from the given local resources*/
	public void createIconSet(String... args) {
		iconSet=new IconSet(args);
		
	}

	
	public Class<?> getExcludedClass() {
		return excludedClass;
	}

	public void setExcludedClass(Class<?> excludedClass) {
		this.excludedClass = excludedClass;
	}
	
	/**A user may select an option to ignore certain classes*/
	static Class<?>[] potentialExcludedClasses=new Class<?>[] {null, PanelLayoutGraphic.class, ImagePanelGraphic.class, TextGraphic.class};
	static String[]  namesOfExcludedClasses=new String[] {"none", "Layouts", "ImagePanels", "Text"};
	
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
	
	/**Returns the whether the 'select in group' method of handling groups
	 * is used*/
	public int getGroupSelectionMode() {
		return selectingroup;
	}
	
	/**determines whether the 'select in group' method of handling groups
	 * is used*/
	public void setSelectingroup(int selectingroup) {
		Object_Mover.selectingroup = selectingroup;
	}

	/**returns the cursor for this tool*/
	public Cursor getNormalCursor() {
		return normalCursor;
	}
	/**sets the cursor for this tool*/
	public void setNormalCursor(Cursor normalCursor) {
		this.normalCursor = normalCursor;
	}
	
	/**returns the cursor used when the mouse is over a handle*/
	public Cursor getHandleCursor() {
		return handleCursor;
	}
	/**set the cursor used when the mouse is over a handle*/
	public void setHandleCursor(Cursor handleCursor) {
		this.handleCursor = handleCursor;
		
	}
	
	

	/**This class of dialog changes the options for this tool*/
	public static class MoverDialog extends StandardDialog {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Object_Mover mover;
		
		public MoverDialog(Object_Mover mover) {
			setModal(true);
			this.mover=mover;
			add("excludedClass", new ChoiceInputPanel("Select which class to ignore", namesOfExcludedClasses, mover.getindexofExcluded()));
			String[] groupops=new String[] {"Don't", "Do", "1 Level Down", "2 Level Down"};
			add("groupsel", new ChoiceInputPanel("Reach into Groups",groupops, mover.getGroupSelectionMode()));
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

	/**responds to keystrokes by the user*/
	public boolean keyPressed(KeyEvent arg0) {
		if(this.textEditMode()) {
			this.keyPressOnSelectedTextItem(arg0);
			return false;
		}

		if (!arg0.isShiftDown()) {
			DisplayedImage imageDisplayWrapperClick = getImageDisplayWrapperClick();
			switch (arg0.getKeyCode()) {
			
			
			case KeyEvent.VK_LEFT: {moveManySelectedObjects(-2,0) ; ;break;}
			case KeyEvent.VK_RIGHT: {moveManySelectedObjects(2,0); ;break;}
			case KeyEvent.VK_UP: {moveManySelectedObjects(0,-2); ;break;}
			case KeyEvent.VK_DOWN: {moveManySelectedObjects(0,2) ;break;}
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
				this.getImageClicked().getTopLevelLayer().remove((ZoomableGraphic) getPrimarySelectedObject());
				this.primarySelectedItem=null;
				imageDisplayWrapperClick.setSelectedItem(null);
				;break;}
			case KeyEvent.VK_A: {
				ArrayList<LocatedObject2D> all = imageDisplayWrapperClick.getImageAsWorksheet().getLocatedObjects();
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
					GraphicLayer cc = getImageClicked().getTopLevelLayer().getSelectedContainer();
					ImagePanelGraphic image = (ImagePanelGraphic) new ClipboardAdder(false).add(cc);
					image.setRelativeScale(ImageDPIHandler.ratioForIdealDPI());
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
	private void deleteSelectedItemsAndLayers(DisplayedImage imageDisplayWrapperClick) {
		ArrayList<LocatedObject2D> rois3 = getAllSelectedItems(false);
		ArrayList<GraphicLayer> rois4 = getSelectedLayers(this.getImageClicked().getTopLevelLayer());
		CombinedEdit undoableEdit=new CombinedEdit();
		imageDisplayWrapperClick.getUndoManager().addEdit(undoableEdit);
		for (LocatedObject2D roi: rois3){
			undoableEdit.addEditToList(new UndoAbleEditForRemoveItem(null,(ZoomableGraphic)roi ));
			this.getImageClicked().getTopLevelLayer().remove((ZoomableGraphic) roi);
		}
		for (GraphicLayer roi: rois4){
			undoableEdit.addEditToList(new UndoAbleEditForRemoveItem(null,(ZoomableGraphic)roi ));
			this.getImageClicked().getTopLevelLayer().remove((ZoomableGraphic) roi);
		}
		this.primarySelectedItem=null;
		imageDisplayWrapperClick.setSelectedItem(null);
	}


	public DragAndDropHandler getDragAndDropHandler() {
		return new NormalToolDragHandler(this);
	}
	
	
	@Override
public String getToolTip() {
		
		return "Select, Move and Manipulate Objects";
	}
	
	/**returns true if this tool is in text edit mode*/
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
	
	/**called when a mouse press is done on a text item*/
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

	/**Called when a user presses a key, will alter the text of the text item accordingly*/
	public void keyTypedOnTextItem(KeyEvent arg0) {
		if(this.textEditMode()) {
			TextGraphic textob=(TextGraphic) getPrimarySelectedObject();
			textob.handleKeyTypedEvent(arg0);
			return;
		}
		
		
	}
	
	/**Called when the given object is selected in the layers window */
	@Override
	public boolean treeSetSelectedItem(Object o) {
		if (o!=getPrimarySelectedObject() && o instanceof LocatedObject2D) {
			setPrimarySelectedObject((LocatedObject2D) o);
			return true;
		}
		return false;
	}
	
	
	/**looks through a list of objects
	 * items and returns the one that has a given objet
	 * @return 
	 */
	public static TakesAttachedItems getLockContainterForObject(LocatedObject2D object, ArrayList<?> list) {
		for(Object t: list) try {
			if (t==null||!(t instanceof TakesAttachedItems)) continue;
			TakesAttachedItems taker=(TakesAttachedItems) t;
			if (taker.hasLockedItem(object)) return taker;
		} catch (Throwable t2) {
			IssueLog.logT(t2);
		}
		return null;
	}
	
	/**gets all the objects in a particular image that can take on an attached item.*/
	public static ArrayList<?> getPotentialLockAcceptors(ImageWorkSheet gmp) {
		ArrayList<LocatedObject2D> aRoi;
		aRoi=gmp.getLocatedObjects();
		ArraySorter<LocatedObject2D> as = new ArraySorter<LocatedObject2D>();
		aRoi=as.getThoseOfClass(aRoi, TakesAttachedItems.class);
		
		ArrayList<Object> output = new ArrayList<Object>();
		output.addAll(aRoi);
		
		ArrayList<ZoomableGraphic> carriers = new ArraySorter<ZoomableGraphic>().getThoseOfClass(gmp.getTopLevelLayer().getObjectsAndSubLayers(), CarriesLockTaker.class);
		for(ZoomableGraphic carrier: carriers) {
			CarriesLockTaker c=(CarriesLockTaker) carrier;
			output.add(c.getLockTaker());
		}
		
		return output;
	}
	
	/**finds what object holds the attached item
	 * @param object2 the attached item
	 * @return the item that object2 is attached to*/
	public TakesAttachedItems findLockContainer(LocatedObject2D object2) {
		ImageWorkSheet imageClicked = getImageClicked();
		return findLockContainer(object2, imageClicked);
	}

	/**finds what object holds the attached item. Searches within the given worksheet
	 * @param object2
	 * @param imageClicked
	 * @return
	 */
	public static TakesAttachedItems findLockContainer(LocatedObject2D object2, ImageWorkSheet imageClicked) {
		return getLockContainterForObject(object2, getPotentialLockAcceptors(imageClicked));
	}
	
	/**returns true if the user is trying to move an item that is attached to another object*/
	private boolean movingAttachedItem() {
		return isAttachedItem(this.getPrimarySelectedObject());
	}
	/**returns true if the item is attached to another object*/
	private boolean isAttachedItem(LocatedObject2D object) {
		if(object instanceof BarGraphic.BarTextGraphic) {
			BarGraphic.BarTextGraphic b=(BarTextGraphic) object;
			if (b.locationAutoMatic()) return true;
		}
		if(object instanceof HasSmartHandles) {
			
			SmartHandle overrideHandle = ((HasSmartHandles) object).getSmartHandleList().getOverrideHandle();
			
			if(overrideHandle!=null) {
				
				return true;
			}
		}
		return findLockContainer(object)!=null;
	}
	
	/**called if the user is trying to move an item that is attached to another object*/
	private void moveAttachedObject(LocatedObject2D roi, int x, int y) {
		
		if(roi instanceof BarGraphic.BarTextGraphic) {
			BarGraphic.BarTextGraphic b=(BarTextGraphic) roi;
			if (b.locationAutoMatic()) b.getLocationHandleForBarText().handleDrag(getLastDragOrLastReleaseMouseEvent());;
		}
		
		AttachmentPositionHandle lockedItemHandle = findHandleForLockedItem(roi);
		if(lockedItemHandle!=null) {
			lockedItemHandle=lockedItemHandle.createDemiVersion();
			lockedItemHandle.handleDrag(getLastDragOrLastReleaseMouseEvent());
		}
		
		if(roi instanceof HasSmartHandles) {
			SmartHandle h = ((HasSmartHandles) roi).getSmartHandleList().getOverrideHandle();
			if(h!=null) {
				h.handleDrag(getLastDragOrLastReleaseMouseEvent());
			}
		}

	}

	
	/**A user user clicks on an item that is attached to another, 
	 * returns the locked item handle for that attachment*/
	public AttachmentPositionHandle findHandleForLockedItem(Object r) {
		if (r instanceof LocatedObject2D) {
			LocatedObject2D object=(LocatedObject2D) r;
			TakesAttachedItems tk = findLockContainer(object);
			if(tk==null) return null;
			if (tk.getSmartHandleList()==null) return null;
			AttachmentPositionHandle lockedItemHandle = tk.getSmartHandleList().getAttachmentPositionHandle(object);
			return lockedItemHandle;
		}
		if(r instanceof HasSmartHandles) {
			SmartHandle overrideHandle = ((HasSmartHandles) r).getSmartHandleList().getOverrideHandle();
			if(overrideHandle!=null)
				IssueLog.log("will return handle for object");
			//return overrideHandle;
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
				primarySelectedItem= lastToolsSelectedItem;
				lastToolsSelectedItem=null;
			}
		}
		
	}
	

	/**If the tool is restricted to instances of a specific class, this returns that class,
	  if it returns object, the tool will recognize any  located object as selectable*/
	public Class<?> getSelectOnlyThoseOfClass() {
		return onlySelectThoseOfClass;
	}

	/**called to make the tool restricted to instances of one class
	  If it is set to anything other than Object.class, the tool will ignore items
	  not of those classes*/
	public void setSelectOnlyThoseOfClass(Class<?> onlySelectThoseOfClass) {
		this.onlySelectThoseOfClass = onlySelectThoseOfClass;
		if (this.onlySelectThoseOfClass==null) this.onlySelectThoseOfClass=Object.class;
	}

	public boolean isPaneMode() {
		return paneMode;
	}



	/**The tool icon*/
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

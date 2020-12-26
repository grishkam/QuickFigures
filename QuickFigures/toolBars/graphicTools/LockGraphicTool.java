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
package graphicTools;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import applicationAdapters.ImageWrapper;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_LayoutObjects.ObjectDefinedLayoutGraphic;
import sUnsortedDialogs.ObjectListChoice;
import selectedItemMenus.SnappingSyncer;
import undo.CombinedEdit;
import undo.UndoTakeLockedItem;
import utilityClasses1.ArraySorter;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.LockedItemList;
import utilityClassesForObjects.TakesLockedItems;

/**A tool for moving attached items. no longer included in the toolbars but the methods
 * in this class are accessed via handles that can be clicked on without the use of this tool*/
public class LockGraphicTool extends GraphicTool {
	protected CombinedEdit undoer;
	private ArrayList<LocatedObject2D> allRoi;
	boolean askIfMultiple=true;
	
	{createIconSet("icons2/lockGraphic2.jpg","icons2/RectangleIconPress.jpg","icons2/LockItemIcon.jpg");
	}
	
	public static Class<?>[] neverLock=new Class<?>[] {ObjectDefinedLayoutGraphic.class};
	
	
	
	public void onPress(ImageWrapper gmp, LocatedObject2D roi2) {
		if (this.clickCount()>1||this.getMouseButtonClick()==2||getLastClickMouseEvent().isPopupTrigger()) {
			
			SnappingSyncer.createFromArray(allRoi, true);
			
			return;
		}
		
		
		allRoi=getPotentialLockAcceptors(getImageClicked());
		
		removeFromAlltakers(getPrimarySelectedObject(), allRoi, undoer);
		
		
		
	}
	
	/**goes through a list of lock taking items and removes the selected item*/
	static void removeFromAlltakers(LocatedObject2D sel, ArrayList<LocatedObject2D> allRoi, CombinedEdit undoer) {
		LockedItemList.removeFromAlltakers(sel, allRoi, undoer);
	}
	

	public static LocatedObject2D getPotentialLockAcceptorAtPoint(Point2D pt, LocatedObject2D item, ImageWrapper gmp) {
		ArrayList<LocatedObject2D> list=getPotentialLockAcceptors( gmp);
		ArrayList<LocatedObject2D> list2=new ArrayList<LocatedObject2D>();
		for(LocatedObject2D l: list) {
			TakesLockedItems t=(TakesLockedItems) l;
			if(t.hasLockedItem(item)) continue;
			
			if(l.getOutline().contains(pt)) list2.add(l);
		}
		if(list2.size()==0) return null;
		
		return list2.get(list2.size()-1);
	}
	

	

	
	
	

	
	@Override
	public void onRelease(ImageWrapper gmp, LocatedObject2D roi2) {
		if (roi2==null) return;
if (this.clickCount()>1||this.getMouseButtonClick()==2||getLastClickMouseEvent().isPopupTrigger()) {
			
			
			return;
		}
		
		
		allRoi=getAllPotentialLocks();
		if (allRoi.size()<1) return;
		
		onLockAdd(allRoi, this.getPrimarySelectedObject());
		
		getImageClicked().updateDisplay();
	}
	
	protected ArrayList<LocatedObject2D> getAllPotentialLocks() {
		ArrayList<LocatedObject2D> allRoi=this.getObjecthandler().getAllClickedRoi(getImageClicked(), getReleaseCordinateX(), getReleaseCordinateY(), TakesLockedItems.class);
		ArraySorter<LocatedObject2D> as = new ArraySorter<LocatedObject2D>();
		if (ignorehidden)ArraySorter.removehideableItems(allRoi);
		allRoi=as.getThoseOfClass(allRoi, TakesLockedItems.class);
		allRoi.remove(getPrimarySelectedObject());
		return allRoi;
	}
	
	



	/**adds t to one potential locked item*/
	protected void onLockAdd(ArrayList<LocatedObject2D> allRoi, LocatedObject2D t) {
		for(Class<?> c: neverLock) {
			if (c.isInstance(t)) return;
		}
		TakesLockedItems tl=	(TakesLockedItems) allRoi.get(0);
		if (allRoi.size()>1&&askIfMultiple) {
			if (allRoi.size()==2 &&allRoi.get(0) instanceof ImagePanelGraphic &&allRoi.get(1) instanceof PanelLayoutGraphic) {
				tl=(TakesLockedItems)allRoi.get(0) ;
			} else
				if (allRoi.size()==2 &&allRoi.get(1) instanceof ImagePanelGraphic &&allRoi.get(0) instanceof PanelLayoutGraphic) {
					tl=(TakesLockedItems)allRoi.get(1) ;
				} else
			tl=(TakesLockedItems) new ObjectListChoice<LocatedObject2D>("").select("Chose Where to put", allRoi);
			}
		
		 onLock(tl,t);
	}
	
	protected void onLock(TakesLockedItems tl, LocatedObject2D t) {
		UndoTakeLockedItem lockingUndo = new UndoTakeLockedItem(tl, t, false);
		if (undoer!=null) {
			
			undoer.addEditToList(lockingUndo);
		}
		tl.addLockedItem(t);
		lockingUndo.establishFinalState();
	}
	
	@Override
	public String getToolTip() {
			return "Move Attached Items";
		}
	@Override
	public String getToolName() {
			return "Mover For Attached Items";
		}
	
	@Override
	public String getToolSubMenuName() {
		return "Expert Tools";
	}

}

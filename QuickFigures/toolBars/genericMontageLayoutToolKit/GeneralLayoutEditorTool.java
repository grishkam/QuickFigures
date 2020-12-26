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
package genericMontageLayoutToolKit;
import applicationAdapters.ImageWrapper;
import externalToolBar.DragAndDropHandler;
import genericMontageKit.*;
import genericTools.BasicToolBit;
import genericTools.MoverDragHandler;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import imageDisplayApp.OverlayObjectManager;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.GenericMontageEditor;
import layout.basicFigure.LayoutEditorDialogs;
import layout.basicFigure.LayoutSpaces;
import logging.IssueLog;
import undo.UndoLayoutEdit;
import utilityClasses1.ArraySorter;
import utilityClassesForObjects.LocatedObject2D;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**A superclass used for a variety of tool bits. Has methods to access Grid layout*/
public class GeneralLayoutEditorTool extends BasicToolBit implements LayoutSpaces, ActionListener{


	
	protected DefaultLayoutGraphic layoutGraphic;
	boolean removalPermissive=false;
	boolean usesMain=false;
	boolean resetClickPointOnDrag=true;
	GenericMontageEditor editor=new GenericMontageEditor();
	
	private Cursor currentCursor;
	private BasicLayout editingLayout;
	private UndoLayoutEdit currentUndo;
	
	public GenericMontageEditor getLayoutEditor() {
		if ( editor==null) editor=new GenericMontageEditor();
		
		return  editor;
	}
	
	/**returns the current layout. also sets up the ImageWrapper for that layout
	  to be a shell. This will be called with each click to set up the layout
	  used by the tool's getCurrentLayout function.*/
	private BasicLayout setUpCurrentLayout() {
		if (layoutGraphic==null) {
			return null;
			}
		layoutGraphic.generateStandardImageWrapper();
		
		if (removalPermissive) {layoutGraphic.generateRemovalPermissiveImageWrapper();}
		else {
				ImageWrapper wrapper = layoutGraphic.getPanelLayout().getEditedImage();
			ArrayList<LocatedObject2D> excluded = layoutGraphic.getEditor().getObjectHandler().getExcludedRois(layoutGraphic.getPanelLayout().getBoundry().getBounds(), wrapper);
			for(LocatedObject2D e: excluded) {
			wrapper.takeFromImage(e);
			} 
		}
		this.setEditor(layoutGraphic.getEditor());
		return layoutGraphic.getPanelLayout();
		
	}
	
	public void setEditor(GenericMontageEditor editor2) {
		editor=editor2;
		
	}

	protected boolean hasALayoutBeenClicked() {
		setupClickedLayout();
		if (layoutGraphic!=null && layoutGraphic.getBounds().contains(getClickedCordinateX(), getClickedCordinateY())) {
			BasicLayout lay = getCurrentLayout();
			
			setRowColClickForLayout(lay);
			return true;
		} 
		return false;
	}
	
	public void mousePressed() {
		setupClickedLayout();
		
		if (!hasALayoutBeenClicked()) {
		//	IssueLog.log("layout has not been pressed");
			return;
			}	
		//IssueLog.log("layout has been pressed");
	
		
		
		try {
			currentUndo = new UndoLayoutEdit(layoutGraphic);
			
			performPressEdit();
			
		} catch (Throwable e) {
			IssueLog.logT(e);
		}
	}
	
	
	
	protected void performPressEdit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved() {
		setupClickedLayout();
		getImageClicked().getOverlaySelectionManagger().setSelection(markerRoi(), 0);
	}
	
	@Override
	public void mouseExited() {
		this.removeMarkerRoi(); 
	}
	
	/**Identifies the layout at the clickpoint and sets it as the current layout. 
	  */
	public void setupClickedLayout() {
		ArrayList<LocatedObject2D> list = super.getObjecthandler().getAllClickedRoi(getImageClicked(), this.getClickedCordinateX(), this.getClickedCordinateY(), DefaultLayoutGraphic.class);
		ArraySorter.removehideableItems(list);//removes hidden layouts
		
		LocatedObject2D layoutGraphic0 = getObjecthandler().nearest(list, new Point(this.getClickedCordinateX(), this.getClickedCordinateY()));
		if (layoutGraphic0 instanceof DefaultLayoutGraphic) {
			layoutGraphic=(DefaultLayoutGraphic) layoutGraphic0;
			//IssueLog.log("clicked on layout "+ layoutGraphic);
			editingLayout=setUpCurrentLayout();
			//IssueLog.log("clicked on layout "+ editingLayout);
		} else {
			layoutGraphic=null;
			editingLayout=null;
		}
		
	}
	
	
	
	@Override
	public void mouseDragged() {
		//if (!isMontage(imp)) return;
		
		if (getCurrentLayout()!=null) getCurrentLayout().resetPtsPanels();
		
		
		setStoredMouseDisplacement();
		
		
		if (layoutGraphic!=null&& layoutGraphic.getBounds().contains(getDragCordinateX(), getDragCordinateY())) {
			//IssueLog.log(getCurrentLayout().report());
			BasicLayout lay = getCurrentLayout();
			super.setRowColDragForLayout(lay);
		} else {
			if (getCurrentLayout()==null /**!super.doesClickedImageHAveMaintMontageLayout() */){
				IssueLog.log("no longer inside of layout");
				return;
				}
		}
		
		
	try{	performDragEdit(this.getLastMouseEvent().shfitDown());} catch (Throwable t) {IssueLog.logT(t);}
	
	if (resetClickPointOnDrag) {
	
	
		int xd = getMouseXdrag();
		
		
		setMouseXClick(xd);
		setMouseYClick(getMouseYdrag());
		
		}
			//if (this.getImageWrapperClick().createLayout()!=null)
				//getImageWrapperClick().createLayout().setMontageProperties();
		setMarkerRoi();
		if (currentUndo!=null) currentUndo.establishFinalLocations();
		
		getImageClicked().updateDisplay();
		
}


	

	
	
	@Override
	public void mouseReleased() {
		try {
			performReleaseEdit(shiftDown());
			removeMarkerRoi();
			if (currentUndo!=null) currentUndo.establishFinalLocations();
		} catch (Throwable e) {
			IssueLog.logT(e);
		}
		if (currentUndo!=null) getImageDisplayWrapperClick().getUndoManager().addEdit(currentUndo);
		
		super.resizeCanvas();
		
		this.getImageClicked().updateDisplay();
	}
	
	protected void performReleaseEdit(boolean b) {
		// TODO Auto-generated method stub
		
	}

	/**Shows a dialog window if user clicks twice, may be overriden*/
	protected void doubleClickDialog() {
			new LayoutEditorDialogs().showDialogBasedOnLocation( getLayoutEditor(), getCurrentLayout(), getClickedCordinateX(), getClickedCordinateY());
		getImageClicked().updateDisplay();
	}
	
	@Override
	public void mouseClicked() {
		if (super.clickCount()==2) try{
			doubleClickDialog();
			} catch (Throwable t) {
				IssueLog.logT(t);
				}
	//if (e.isPopupTrigger()) {IssueLog.log("popup window not finished yet");}
	}
	
	public void performDragEdit(boolean shiftDown) {
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {

		
		onActionPerformed(arg0.getSource(), arg0.getActionCommand());
		 currentlyInFocusWindowImage().updateDisplay();
		//getMainLayout().updateImageDisplay();
	}
	
    public void onActionPerformed(Object source, String st) {
		// TODO Auto-generated method stub
		
	}
    
    public int markerType() {
    	return MONTAGE;
    }
    
	
	
	public void setMarkerRoi() {
		getImageClicked().getTopLevelLayer();
			try {
				OverlayObjectManager select = this.getImageClicked().getOverlaySelectionManagger();
				
				select.setSelection(markerRoi(), 0);
				select.setSelection(MarkerRoi2(), 1);
			} catch (Throwable e) {
				IssueLog.logT(e);
			}
		
	} 
	
	
	
	public LocatedObject2D markerRoi() {
	//	if (usesMain) return null;
	
		if (getCurrentLayout()==null) return null;
		
		if (this.markerType()==MONTAGE) {
			DefaultLayoutGraphic m= new DefaultLayoutGraphic(getCurrentLayout());
			m.setStrokeWidth(4);
			return m;
		}
		
		
		return RectangularGraphic.blankRect(this.getCurrentLayout().getSelectedSpace(this.getClickedCordinateX(), this.getClickedCordinateY(), markerType()).getBounds(), Color.blue);
		//return null;
	}
	
	
	
	public LocatedObject2D MarkerRoi2() {
		//if (usesMain) return null;
		if (markerType()==MONTAGE) return null;
		return RectangularGraphic.blankRect(this.getCurrentLayout().getSelectedSpace(this.getDragCordinateX(), this.getDragCordinateY(), markerType()).getBounds(), Color.green);
		//return null;
	}

	public void removeMarkerRoi()  {
		
		getImageClicked().getOverlaySelectionManagger().removeSelections();
		
	}
	
	public void setCursorIcon(Image cursorIcon) {
		currentCursor=Toolkit.getDefaultToolkit().createCustomCursor(cursorIcon, new Point(0,0), "");
		super.setCursor(currentCursor);
	}

	
	/**A montage layout object for the clicked graphic with a shell.*/
	public BasicLayout getCurrentLayout() {
		return editingLayout;
	}
	
	@Override
	public boolean keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
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
		}
		return false;
	}
	public DragAndDropHandler getDragAndDropHandler() {
		return new MoverDragHandler(this);
	}
    
}

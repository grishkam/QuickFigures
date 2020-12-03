package genericMontageLayoutToolKit;
import applicationAdapters.ImageWrapper;
import externalToolBar.DragAndDropHandler;
import genericMontageKit.*;
import genericMontageUIKit.BasicToolBit;
import genericMontageUIKit.MoverDragHandler;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;
import gridLayout.GenericMontageEditor;
import gridLayout.MontageEditorDialogs;
import gridLayout.LayoutSpaces;
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


	
	protected MontageLayoutGraphic layoutGraphic;
	boolean removalPermissive=false;
	boolean usesMain=false;
	boolean resetClickPointOnDrag=true;
	GenericMontageEditor editor=new GenericMontageEditor();
	private Image cursorIcon;
	private Cursor currentCursor;
	private BasicMontageLayout editingLayout;
	private UndoLayoutEdit currentUndo;
	
	public GenericMontageEditor getEditor() {
		if ( editor==null) editor=new GenericMontageEditor();
		
		return  editor;
	}
	
	/**returns the current layout. also sets up the ImageWrapper for that layout
	  to be a shell. This will be called with each click to set up the layout
	  used by the tool's getCurrentLayout function.*/
	private BasicMontageLayout setUpCurrentLayout() {
		if (layoutGraphic==null) {
			//usesMain=true;
			//return this.getImageWrapperClick().createLayout();
			return null;
			}
		layoutGraphic.generateStandardImageWrapper();
		
		if (removalPermissive) {layoutGraphic.generateRemovalPermissiveImageWrapper();}
		else {
			//if removal non permissive, takes the unneeded. this was written to solve a BUG but eventually caused more bugs.
			ImageWrapper wrapper = layoutGraphic.getPanelLayout().getWrapper();
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
			BasicMontageLayout lay = getCurrentLayout();
			
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
		getImageWrapperClick().getOverlaySelectionManagger().setSelection(MarkerRoi(), 0);
	}
	
	@Override
	public void mouseExited() {
		this.removeMarkerRoi(); 
	}
	
	/**Identifies the layout at the clickpoint and sets it as the current layout. 
	  */
	public void setupClickedLayout() {
		ArrayList<LocatedObject2D> list = super.getObjecthandler().getAllClickedRoi(getImageWrapperClick(), this.getClickedCordinateX(), this.getClickedCordinateY(), MontageLayoutGraphic.class);
		ArraySorter.removehideableItems(list);//removes hidden layouts
		
		LocatedObject2D layoutGraphic0 = getObjecthandler().nearest(list, new Point(this.getClickedCordinateX(), this.getClickedCordinateY()));
		if (layoutGraphic0 instanceof MontageLayoutGraphic) {
			layoutGraphic=(MontageLayoutGraphic) layoutGraphic0;
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
			BasicMontageLayout lay = getCurrentLayout();
			super.setRowColDragForLayout(lay);
		} else {
			if (getCurrentLayout()==null /**!super.doesClickedImageHAveMaintMontageLayout() */){
				IssueLog.log("no longer inside of layout");
				return;
				}
		}
		
		
	try{	performDragEdit(this.getLastClickMouseEvent().shfitDown());} catch (Throwable t) {IssueLog.logT(t);}
	
	if (resetClickPointOnDrag) {
	
	
		int xd = getMouseXdrag();
		
		
		setMouseXClick(xd);
		setMouseYClick(getMouseYdrag());
		
		}
			//if (this.getImageWrapperClick().createLayout()!=null)
				//getImageWrapperClick().createLayout().setMontageProperties();
		setMarkerRoi();
		if (currentUndo!=null) currentUndo.establishFinalLocations();
		
		getImageWrapperClick().updateDisplay();
		
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
		
		this.getImageWrapperClick().updateDisplay();
	}
	
	protected void performReleaseEdit(boolean b) {
		// TODO Auto-generated method stub
		
	}

	/**Shows a dialog window if user clicks twice, may be overriden*/
	protected void doubleClickDialog() {
			new MontageEditorDialogs().showDialogBasedOnLocation( getEditor(), getCurrentLayout(), getClickedCordinateX(), getClickedCordinateY());
		getImageWrapperClick().updateDisplay();
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
		getImageWrapperClick().getGraphicLayerSet();
			try {
				OverlayObjectManager select = this.getImageWrapperClick().getOverlaySelectionManagger();
				
				select.setSelection(MarkerRoi(), 0);
				select.setSelection(MarkerRoi2(), 1);
			} catch (Throwable e) {
				IssueLog.logT(e);
			}
		
	} 
	
	
	
	public LocatedObject2D MarkerRoi() {
	//	if (usesMain) return null;
	
		if (getCurrentLayout()==null) return null;
		
		if (this.markerType()==MONTAGE) {
			MontageLayoutGraphic m= new MontageLayoutGraphic(getCurrentLayout());
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
		
		getImageWrapperClick().getOverlaySelectionManagger().removeSelections();
		
	}
	
	public void setCursorIcon(Image cursorIcon) {
		this.cursorIcon = cursorIcon;
		currentCursor=Toolkit.getDefaultToolkit().createCustomCursor(cursorIcon, new Point(0,0), "");
		super.setCursor(currentCursor);
		//this.getToolCore().setCursor(currentCursor, Cursor.CUSTOM_CURSOR);
	}

	
	/**A montage layout object for the clicked graphic with a shell.*/
	public BasicMontageLayout getCurrentLayout() {
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

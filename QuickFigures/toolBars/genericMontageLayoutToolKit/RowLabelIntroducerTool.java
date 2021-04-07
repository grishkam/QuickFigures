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
package genericMontageLayoutToolKit;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.undo.UndoableEdit;

import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.ImageWorkSheet;
import figureFormat.LabelExamplePicker;
import figureOrganizer.FigureLabelOrganizer;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.CursorFinder;
import graphicalObjects_SpecialObjects.TextGraphic;
import icons.IconSet;
import icons.ToolIconWithText;
import imageDisplayApp.OverlayObjectManager;
import layout.BasicObjectListHandler;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import objectDialogs.MultiTextGraphicSwingDialog;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialogListener;
import undo.CombinedEdit;
import undo.UndoAddItem;
import undo.UndoManagerPlus;
import undo.UndoAddOrRemoveAttachedItem;

/**introduces Row and Column labels to a montage layout
  and locked them to the layout graphic. Also alows user
  to easily edit the row labels as a group*/
public class RowLabelIntroducerTool extends RowAndColumnSwapperTool{

	
	
	private LabelExamplePicker picker;
	TextGraphic item;// the current text item
	private BasicLayout lastusedLayout;
	private UndoableEdit lastEdit;
	
	public RowLabelIntroducerTool(int mode) {
		super(mode);
		this.picker=new LabelExamplePicker(new ComplexTextGraphic(), mode);
		
	
	}
	
	@Override
	public void showOptionsDialog() {
		
		 try {
			 picker.getModelItem().showOptionsDialog();;
			
		} catch (Throwable e) {
			IssueLog.logT(e);
		}
		 
	}
	

	
	/**returns the type of area that will be marked*/
	 public int markerType() {
		 if (mode==LayoutSpaces.ROW_OF_PANELS) return LayoutSpaces.ROWS;
			if (mode==LayoutSpaces.COLUMN_OF_PANELS) return  LayoutSpaces.COLS;
			 return LayoutSpaces.PANELS;
	    }
	 
	 /**returns the type of label added as a string*/
	 protected String getTextBase() {
		 if (mode==LayoutSpaces.ROW_OF_PANELS) return "Row";
			if (mode==LayoutSpaces.COLUMN_OF_PANELS) return  "Column";
			 return "Panel";
	    
	}
	
	void setUpIconSets() {
		columnSwapIcons=new IconSet(new ToolIconWithText(0, mode),
				new ToolIconWithText(1, mode),
				new ToolIconWithText(2, mode));
		rowSwapIcons=columnSwapIcons;
		panelSwapIcons=columnSwapIcons;
			;
		
	}
	
	@Override
	public
	IconSet getIconSet() {
		setUpIconSets();
		return super.getIconSet();
	}
	public void performReleaseEdit(boolean b) {
		
	}
	
	/**returns the text graphic relevant to the clickpoint if there is one*/
	private TextGraphic getDesiredGraphic(Rectangle boundsForThisRowsLabel, ImageWorkSheet wp ) {
		ArrayList<LocatedObject2D> rois = new BasicObjectListHandler().getOverlapOverlaypingItems(boundsForThisRowsLabel, wp);
		
		ArrayList<BasicGraphicalObject> array = picker.getDesiredItemsAsGraphicals(rois);
		if (array.size()>0) 
		{
			
				TextGraphic text = (TextGraphic) array.get(0);
				new  CursorFinder().setCursorFor(text, new Point(this.getClickedCordinateX(), this.getClickedCordinateY()));
				return text;
			}
		return null;
	}
	

	/**generates a text item for the given index
	 * @param index
	 * @return
	 */
	public TextGraphic generateItemFor(int index) {
		item=picker.getModelItem().copy();
		TextGraphic i=item;
		
		i.setText("Text");
	
		i.setAttachmentPosition(picker.getDefaultAttachmentPosition());
		
		
		ComplexTextGraphic c=(ComplexTextGraphic) i;
		c.getParagraph().get(0).get(0).setText(getTextBase()+" "+index);
		if (markerType()==LayoutSpaces.PANELS) {
			c.setTextColor(Color.white);
		}
		if (getTextBase().equals("Row")) {
			c.getParagraph().setJustification(2);
		}
		if (getTextBase().equals("Column")) {
			c.getParagraph().setJustification(1);
			
		}
		selectItem();
		return i;
	}

	/**
	 returns the starting index for attachments
	 */
	int placeItemAtStartingPositionForAttachment(int x, int y) {
		/**puts the item in the correct location*/
		BasicLayout alteredLayout = this.getCurrentLayout().makeAltered(markerType());
		int index = alteredLayout.getPanelIndex(x,y);
		
		Rectangle2D r = alteredLayout.getPanel(index);
		item.setLocationUpperLeft(r.getX()+2, r.getY()+2);
		return index;
	}
	
	

	
	protected void performPressEdit() {
		
		CombinedEdit undoGroup = new CombinedEdit();
		
		CanvasMouseEvent mm = super.getLastMouseEvent();
		if (mm.isPopupTrigger()) {
			new popupMenuForRowLabels(lastusedLayout).show(mm.getComponent(), mm.getClickedXScreen(), mm.getClickedYScreen());
			return;
		}
		
		ImageWorkSheet wp = getCurrentLayout().getVirtualWorksheet();
		
		
		item=getDesiredGraphic(markerRoi().getBounds(), wp);
		if (item==null) {
			GraphicLayer layerFor=getImageClicked().getTopLevelLayer();
			DefaultLayoutGraphic montageLayoutGraphic = super.layoutGraphic;
			
			if (montageLayoutGraphic!=null)
				layerFor=layoutGraphic.getParentLayer();
			int index = getCurrentLayout().makeAltered(markerType()).getPanelIndex(mm.getCoordinateX(), mm.getCoordinateY());//placeItemAtStartingPositionForAttachment(mm.getCoordinateX(), mm.getCoordinateY());
			
			
			item=FigureLabelOrganizer.addLabelOfType(markerType(), index, layerFor,  layoutGraphic);
		
			layerFor.add(item);
				UndoAddItem addingEdit = new 	UndoAddItem(layerFor, item);
					undoGroup.addEditToList(addingEdit);
			
		
					lastusedLayout=getCurrentLayout();
					
		if (layoutGraphic!=null&&item!=null) {
			
			layoutGraphic.getEditor().expandSpacesToInclude(getCurrentLayout(), item.getBounds());
			layoutGraphic.addLockedItem(item);
			
			layoutGraphic.mapPanelLocationsOfLockedItems();
			layoutGraphic.snapLockedItems();
			expandLabelSapces();
			
			/**if item location is no longer inthe layout or was not mapped properlu*/
			 placeItemAtStartingPositionForAttachment(mm.getCoordinateX(), mm.getCoordinateY());
			 layoutGraphic.mapPanelLocationsOfLockedItems();
			 layoutGraphic.snapLockedItems();
			
			undoGroup.addEditToList(new UndoAddOrRemoveAttachedItem(layoutGraphic, item, false));
		}
		
		
	
		
		
		if (layoutGraphic!=null) {layoutGraphic.snapLockedItems();}
	
		
			lastEdit=undoGroup;
			 getImageClicked().getUndoManager().addEdit(lastEdit);
		
		
		} 
		
		if (item!=null) {
			selectItem();
		}
		
	}
	
	void expandLabelSapces() {
		getLayoutEditor().expandSpacesToInclude(lastusedLayout, item.getBounds());
		
	}
	
	class popupMenuForRowLabels extends SmartPopupJMenu implements ActionListener{
		
		
		
		/**
		 * 
		 */
		ArrayList<LocatedObject2D> rois;
		private static final long serialVersionUID = 1L;
		JMenuItem showDialog=new JMenuItem("edit labels");
		private BasicLayout lastusedLayout;
		
		popupMenuForRowLabels(BasicLayout lastusedLayout) {
			add(showDialog);
			showDialog.addActionListener(this);
			this.lastusedLayout=lastusedLayout;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource()==showDialog) {
				ImageWorkSheet wp = getCurrentLayout().getVirtualWorksheet();
				Shape bb = getCurrentLayout().getSelectedSpace(1, LayoutSpaces.ALL_MONTAGE_SPACE);
				rois = new BasicObjectListHandler().getOverlapOverlaypingItems(bb.getBounds(), wp);
				
				ArrayList<BasicGraphicalObject> rois2 = picker.getDesiredItemsAsGraphicals(rois);
				
				showLabelEditDialog(rois2);
			}
			
		}

		public void showLabelEditDialog(ArrayList<BasicGraphicalObject> rois2) {
			MultiTextGraphicSwingDialog dd = new MultiTextGraphicSwingDialog(rois2, true);

			dd.addDialogListener(new StandardDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
						// TODO Auto-generated method stub
						
			for(LocatedObject2D item: rois) {
				getLayoutEditor().expandSpacesToInclude(lastusedLayout, item.getBounds());
			}
						
			}});
			
			dd.showDialog();
		}
	}
	
	@Override
	public void mouseMoved() {
		lastEdit=null;
		removeMarkerRoi();
		findClickedLayout(false);
		if (markerRoi()==null) return;
		getImageClicked().getOverlaySelectionManagger().setSelection(markerRoi(), 0);
		
		TextGraphic dd = this.getDesiredGraphic(markerRoi().getBounds(),this.getImageDisplayWrapperClick().getImageAsWorksheet());
		if (dd==null) {
			//dd=createNewGraphic(getCurrentLayout(),this.getImageDisplayWrapperClick().getImageAsWrapper(), this.getClickedCordinateX(), this.getClickedCordinateY());
		//	Rectangle r2 = this.getCurrentLayout().getSelectedSpace(this.getClickedCordinateX(), this.getClickedCordinateY(), mode).getBounds();
			//dd.getAttachmentPosition().snapObjectToRectangle(dd, r2);
		
		}
		item=dd;
		if(dd!=null)
		 {
			OverlayObjectManager sel = getImageClicked().getOverlaySelectionManagger();
					sel.setSelection(dd, 1);
			selectItem();	
		 }
		
	}
	
	
	@Override
	public boolean keyPressed(KeyEvent arg0) {
		
		if (item instanceof TextGraphic) {
			
			;
			TextGraphic textob=(TextGraphic) item;
			selectItem();
			 textob.handleKeyPressEvent(arg0);
			
		} else
		super.keyPressed(arg0);
		expandLabelSapces();
	this.updateClickedDisplay();
	
	return true;
	}

	public void selectItem() {
		item.select();
		item.setEditMode(true);
	}
	
	@Override
	public void mouseReleased() {
		UndoManagerPlus man = getImageDisplayWrapperClick().getUndoManager();
		UndoableEdit editPrior = man.getLastEditFromList();
		
		super.mouseReleased();//this introduces an undo for the layout change
		
		/**Combines the layout edit unto with the text addition undo*/
		if (lastEdit==editPrior )man.mergeLastNEdits(2); else 
			man.undo(); //if no label was introduced, then we dont need the layout edit
		
	}
	
	@Override
	public String getToolTip() {
			return "Create "+ getTextBase()+" Labels";
		}

}

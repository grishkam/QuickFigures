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

import applicationAdapters.CanvasMouseEventWrapper;
import applicationAdapters.ImageWrapper;
import externalToolBar.IconSet;
import externalToolBar.ToolIconWithText;
import figureTemplates.RowLabelPicker;
import genericMontageKit.BasicOverlayHandler;
import genericMontageKit.SelectionManager;
import graphicalObjects.CursorFinder;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import gridLayout.BasicMontageLayout;
import gridLayout.MontageSpaces;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import objectDialogs.MultiTextGraphicSwingDialog;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialog;
import standardDialog.SwingDialogListener;
import undo.CompoundEdit2;
import undo.UndoAddItem;
import undo.UndoManagerPlus;
import undo.UndoSelectionSet;
import undo.UndoTakeLockedItem;
import utilityClassesForObjects.LocatedObject2D;

/**introduces Row and Column labels to a montage layout
  and locked them to the layout graphic. Also alows user
  to easily edit the row labels as a group*/
public class RowLabelIntroducerTool extends RowColSwapperTool2{

	
	
	private RowLabelPicker picker;
	TextGraphic item;
	private BasicMontageLayout lastusedLayout;
	private UndoableEdit lastEdit;
	
	
	@Override
	public void showOptionsDialog() {
		
		 try {
			 picker.getModelItem().showOptionsDialog();;
			
		} catch (Throwable e) {
			IssueLog.log(e);
		}
		 
	}
	
	public RowLabelIntroducerTool(int mode) {
		super(mode);
		this.picker=new RowLabelPicker(new ComplexTextGraphic(), mode);
		
		// TODO Auto-generated constructor stub
	}
	
	
	 public int markerType() {
		 if (mode==MontageSpaces.ROW_OF_PANELS) return MontageSpaces.ROWS;
			if (mode==MontageSpaces.COLUMN_OF_PANELS) return  MontageSpaces.COLS;
			
			 return MontageSpaces.PANELS;
	    }
	 
	 protected String getTextBase() {
		 if (mode==MontageSpaces.ROW_OF_PANELS) return "Row";
			if (mode==MontageSpaces.COLUMN_OF_PANELS) return  "Column";
			
			 return "Panel";
	    
	}
	
	void setUpIconSets() {
		;
		set1=new IconSet(new ToolIconWithText(0, mode),
				new ToolIconWithText(1, mode),
				new ToolIconWithText(2, mode));
		set2=set1;
		set3=set1;
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
	private TextGraphic getDesiredGraphic(Rectangle boundsForThisRowsLabel, ImageWrapper wp ) {
		ArrayList<LocatedObject2D> rois = new BasicOverlayHandler().getOverlapOverlaypingrois(boundsForThisRowsLabel, wp);
		
		ArrayList<BasicGraphicalObject> array = picker.getDesiredItemsAsGraphicals(rois);
		if (array.size()>0) 
		{
			
				TextGraphic text = (TextGraphic) array.get(0);
				new  CursorFinder().setCursorFor(text, new Point(this.getClickedCordinateX(), this.getClickedCordinateY()));
				return text;
			}
		return null;
	}
	
	/**returns the text graphic relevant to the clickpoint if there is one*/
	private TextGraphic createNewGraphic(Rectangle boundsForThisRowsLabel, ImageWrapper wp, int x, int y ) {
		item=picker.getModelItem().copy();
		item.setText("Text");
	
		item.setSnappingBehaviour(picker.getDefaultSnapping());
		
		/**puts the item in the correct location*/
		BasicMontageLayout alteredLayout = this.getCurrentLayout().makeAltered(markerType());
		int index = alteredLayout.getPanelIndex(x,y);//mm.getClickedXImage(), mm.getClickedYImage());
		//IssueLog.log("Clicked panel index "+index);
		Rectangle2D r = alteredLayout.getPanel(index);
		item.setLocationUpperLeft(r.getX()+2, r.getY()+2);
		ComplexTextGraphic c=(ComplexTextGraphic) item;
		c.getParagraph().get(0).get(0).setText(getTextBase()+" "+index);
		if (markerType()==MontageSpaces.PANELS) {
			c.setTextColor(Color.white);
		}
		if (getTextBase().equals("Row")) {
			c.getParagraph().setJustification(2);
		}
		if (getTextBase().equals("Column")) {
			c.getParagraph().setJustification(1);
			
		}
		selectItem();
		return item;
	}
	
	

	
	protected void performPressEdit() {
		
		CompoundEdit2 undoGroup = new CompoundEdit2();
		
		CanvasMouseEventWrapper mm = super.getLastClickMouseEvent();
		if (mm.isPopupTrigger()) {
			new popupMenuForRowLabels(lastusedLayout).show(mm.getComponent(), mm.getClickedXScreen(), mm.getClickedYScreen());
			return;
		}
		
		ImageWrapper wp = getCurrentLayout().getWrapper();
		
		Rectangle boundsForThisRowsLabel=MarkerRoi().getBounds();
		
		
		
		
		item=getDesiredGraphic(boundsForThisRowsLabel, wp);
		if (item==null) {
			GraphicLayer layerFor=getImageWrapperClick().getGraphicLayerSet();
			MontageLayoutGraphic montageLayoutGraphic = super.layoutGraphic;
			
			item=createNewGraphic(boundsForThisRowsLabel, wp,mm.getClickedXImage(), mm.getClickedYImage());
	
			
			if (montageLayoutGraphic!=null)
				layerFor=layoutGraphic.getParentLayer();
				
				layerFor.add(item);
				UndoAddItem addingEdit = new 	UndoAddItem(layerFor, item);
					undoGroup.addEditToList(addingEdit);
			
		
		
		if (layoutGraphic!=null) {
			layoutGraphic.addLockedItem(item);
			layoutGraphic.snapLockedItems();
			layoutGraphic.mapPanelLocationsOfLockedItems();
			
			undoGroup.addEditToList(new UndoTakeLockedItem(layoutGraphic, item, false));
		}
		
		lastusedLayout=getCurrentLayout();
		
		StandardDialog td = item.getOptionsDialog();
		td.addDialogListener(new SwingDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
				// TODO Auto-generated method stub
				expandLabelSapces();
				
				
			}});
		
		if (this.clickCount()>1)td.showDialog();
		
		expandLabelSapces();
		
		
		
		
			SelectionManager sel = getImageWrapperClick().getSelectionManagger();
			sel.setSelection(item, 1);
			
			undoGroup.addEditToList(new UndoSelectionSet(sel));
			
			lastEdit=undoGroup;
			 getImageWrapperClick().getUndoManager().addEdit(lastEdit);
		
		
		} 
		
		if (item!=null) {
			selectItem();
		}
	}
	
	void expandLabelSapces() {
		getEditor().expandSpacesToInclude(lastusedLayout, item.getBounds());
		
	}
	
	class popupMenuForRowLabels extends SmartPopupJMenu implements ActionListener{
		
		
		
		/**
		 * 
		 */
		ArrayList<LocatedObject2D> rois;
		private static final long serialVersionUID = 1L;
		JMenuItem showDialog=new JMenuItem("edit labels");
		private BasicMontageLayout lastusedLayout;
		
		popupMenuForRowLabels(BasicMontageLayout lastusedLayout) {
			add(showDialog);
			showDialog.addActionListener(this);
			this.lastusedLayout=lastusedLayout;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource()==showDialog) {
				ImageWrapper wp = getCurrentLayout().getWrapper();
				Shape bb = getCurrentLayout().getSelectedSpace(1, MontageSpaces.ALL_MONTAGE_SPACE);
				rois = new BasicOverlayHandler().getOverlapOverlaypingrois(bb.getBounds(), wp);
				
				ArrayList<BasicGraphicalObject> rois2 = picker.getDesiredItemsAsGraphicals(rois);
				
				showLabelEditDialog(rois2);
			}
			
		}

		public void showLabelEditDialog(ArrayList<BasicGraphicalObject> rois2) {
			MultiTextGraphicSwingDialog dd = new MultiTextGraphicSwingDialog(rois2, true);

			dd.addDialogListener(new SwingDialogListener() {

			@Override
			public void itemChange(DialogItemChangeEvent event) {
						// TODO Auto-generated method stub
						
			for(LocatedObject2D item: rois) {
				getEditor().expandSpacesToInclude(lastusedLayout, item.getBounds());
			}
						
			}});
			
dd.showDialog();
		}
	}
	
	@Override
	public void mouseMoved() {
		lastEdit=null;
		removeMarkerRoi();
		setupClickedLayout();
		if (MarkerRoi()==null) return;
		getImageWrapperClick().getSelectionManagger().setSelection(MarkerRoi(), 0);
		
		TextGraphic dd = this.getDesiredGraphic(MarkerRoi().getBounds(),this.getImageDisplayWrapperClick().getImageAsWrapper());
		if (dd==null) {
			dd=createNewGraphic(MarkerRoi().getBounds(),this.getImageDisplayWrapperClick().getImageAsWrapper(), this.getClickedCordinateX(), this.getClickedCordinateY());
			Rectangle r2 = this.getCurrentLayout().getSelectedSpace(this.getClickedCordinateX(), this.getClickedCordinateY(), mode).getBounds();
			dd.getSnappingBehaviour().snapObjectToRectangle(dd, r2);
		
		}
		item=dd;
		if(dd!=null)
		 {
			SelectionManager sel = getImageWrapperClick().getSelectionManagger();
					sel.setSelection(dd, 1);
			selectItem();
		//	this.getImageDisplayWrapperClick().getUndoManager().mergeInedit(new UndoSelectionSet(sel));
			
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

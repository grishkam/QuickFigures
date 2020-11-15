package genericMontageLayoutToolKit;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import genericMontageKit.PanelContentExtract;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;
import logging.IssueLog;
import undo.UndoAddItem;
import undo.UndoLayoutEdit;
import utilityClassesForObjects.LocatedObject2D;


/**A tool to change the number of rows and cols in a montage. Created Feb 1.
   it can also be used to create a new layout graphic if one applies it to a selection*/
public class MontageLayoutRowColNumberTool extends GeneralLayoutEditorTool {

	
	boolean movePanelContent=false;
	MontageLayoutGraphic ml;
	BasicMontageLayout bm;
	private LocatedObject2D r;
	private List<PanelContentExtract> extrapanel;
	private UndoLayoutEdit undo;
	private UndoAddItem undo2;
	
	{createIconSet("icons/GirdDimensionIcon.jpg",
			"icons/GirdDimensionPressIcon.jpg",
			"icons/GirdDimensionRolloverIcon.jpg"
			);}
	
	
	public void mousePressed() {
		setupClickedLayout();
		extrapanel=null;
		if (this.hasALayoutBeenClicked() ) {
			
			ml=layoutGraphic;
			bm=super.getCurrentLayout();//ml.getPanelLayout();
			undo=new UndoLayoutEdit(layoutGraphic);
			undo2=null;
		}
		else
		{
		Rectangle bounds = this.getImageWrapperClick().getSelectionManagger().getSelectionBounds1();
		if (bounds==null) return;
		if (bounds.getWidth()==0) return;
		
		bm=new BasicMontageLayout();
		bm.setLayoutBasedOnRect(bounds);
		
		 ml = new MontageLayoutGraphic(bm);
		this.getImageWrapperClick().getGraphicLayerSet().add(ml);
		super.layoutGraphic=ml;
		undo2=new UndoAddItem(getImageWrapperClick().getGraphicLayerSet(), ml);
		undo=null;
		}
		
		
		getImageWrapperClick().updateDisplay();
	
		
	}
	
	public void performDragEdit(boolean b) {
		int[] rowcol=new int[] {};
		int dragCordinateY = this.getDragCordinateY();
		int dragCordinateX = this.getDragCordinateX();
		BasicMontageLayout bm = getCurrentLayout();
		rowcol = findAddedRowsCols(dragCordinateX, dragCordinateY, bm);
		
		ArrayList<PanelContentExtract> panels=null;
		if (movePanelContent) {
			panels = getEditor().cutStack(bm);
			
		}
		if (rowcol[0]+bm.nRows()>=1)getEditor().addRows(bm, rowcol[0]);
		if (rowcol[1]+bm.nColumns()>=1)getEditor().addCols(bm, rowcol[1]);
		
		if (movePanelContent&&panels!=null) {
			
						/**handles loose panels*/
						if(bm.nPanels()<panels.size()) {
							List<PanelContentExtract> oldextra = extrapanel;//if former extra panels exits best not to forget them
							extrapanel=panels.subList(bm.nPanels(), panels.size());//stores extra panels
							if (oldextra!=null)extrapanel.addAll(oldextra);//completes the list to include new and former extra
						}
						else {
							if(bm.nPanels()>panels.size()&&extrapanel!=null) {
								int nneeded = bm.nPanels()-panels.size();
								if (nneeded>extrapanel.size()) nneeded=extrapanel.size();//in case the extra panel list is too short
								panels.addAll(extrapanel.subList(0, nneeded ));//adds as many extra panels as needed or available
										if(nneeded<extrapanel.size()) {
											extrapanel=extrapanel.subList(nneeded, extrapanel.size());
										} else
														extrapanel=null;
							}
						}
						
			
			getEditor().pasteStack(bm, panels );//puts the panels back
		}
		if (undo!=null) {
			undo.establishFinalLocations();
			this.getImageDisplayWrapperClick().getUndoManager().addEdit(undo);
		}
		if (undo2!=null) {
			this.getImageDisplayWrapperClick().getUndoManager().addEdit(undo2);
		}
	}

	public static int[] findAddedRowsCols(int dragCordinateX, int dragCordinateY, BasicMontageLayout bm) {
		int[] rowcol;
		Rectangle bound = bm.getBoundry().getBounds();
		
		
		double col=dragCordinateX-bound.getWidth()-bound.getX();
	
		double row=dragCordinateY-bound.getHeight()-bound.getY();
		
		col/=bm.getStandardPanelWidth();
		row/=bm.getStandardPanelHeight();
		int row2=(int)Math.ceil(row);
		int col2=(int)Math.ceil(col);
		rowcol=new int[] {row2, col2};
		return rowcol;
	}
	
	public void mouseEntered() {
		r=this.getImageWrapperClick().getSelectionManagger().getSelection(0);
	}
	
	
	public LocatedObject2D MarkerRoi() { 
		LocatedObject2D mroi = super.MarkerRoi();
		if (mroi==null)
			return r;
		
		return mroi;
	}
	
	@Override
	public boolean keyPressed(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			IssueLog.log("Shift down, will include panel contends in move");
			movePanelContent=true;
		}
		return false;
	}

	@Override
	public boolean keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
			movePanelContent=false;
		}
		return false;
	}
	
	@Override
	public String getToolTip() {
			return "Change the number of Rows and Columns (Try holding SHIFT to move content)";
		}
	
	@Override
	public String getToolName() {
			return "Panel Number Change Tool";
		}
	
	
	
}

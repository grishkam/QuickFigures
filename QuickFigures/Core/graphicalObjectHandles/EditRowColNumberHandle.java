package graphicalObjectHandles;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import applicationAdapters.CanvasMouseEventWrapper;
import applicationAdapters.DisplayedImage;
import genericMontageLayoutToolKit.MontageLayoutRowColNumberTool;

import java.awt.geom.Rectangle2D;

import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import gridLayout.BasicMontageLayout;
import gridLayout.GenericMontageEditor;
import gridLayout.MontageSpaces;
import imageMenu.CanvasAutoResize;
import logging.IssueLog;
import undo.UndoLayoutEdit;

/**a handle that allows the user to pack panels into a different number of rows andcolors*/
public class EditRowColNumberHandle extends SmartHandle implements MontageSpaces{

	protected MontageLayoutGraphic layout;
	protected int type;
	protected int index;
	private int endIndex;
	private DisplayedImage wrap;
	private int nRow;
	private int nCols;

	public EditRowColNumberHandle(int x, int y) {
		super(x, y);
	}

	public EditRowColNumberHandle(MontageLayoutGraphic montageLayoutGraphic) {
		this(0,0);
		
		this.layout=montageLayoutGraphic;
		
		
	//this.setLocation(50,50);
		setHandleColor(Color.LIGHT_GRAY);
		
		setupSpecialShape();
		Rectangle2D space = layout.getPanelLayout().getSelectedSpace(1, ALL_OF_THE+PANELS).getBounds();
		
		this.setHandleNumber(PanelLayoutGraphic.RepackPanelsHandle);
		
		double y2 = space.getMaxY()+20;
		
		double x2 = space.getMaxX()+20;
		
		this.setCordinateLocation(new Point2D.Double(x2, y2));
		nRow=layout.getPanelLayout().nRows();
		nCols=layout.getPanelLayout().nColumns();
	}
	
	protected boolean hasSpecialShape() {;
	
		setupSpecialShape();
		return specialShape!=null;
	}

	/**sets up the arrow shapes*/
	public void setupSpecialShape() {
	
	}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public boolean containsClickPoint(Point2D p) {
		return super.containsClickPoint(p);
	}
	
	
	
	public void handleRelease(CanvasMouseEventWrapper canvasMouseEventWrapper) {
		UndoLayoutEdit currentUndo = new UndoLayoutEdit(layout);
		
		endIndex = this.getCurrentLayout().makeAltered(type).getNearestPanelIndex(canvasMouseEventWrapper.getCoordinatePoint());
		
		canvasMouseEventWrapper.getAsDisplay().getUndoManager().addEdit(currentUndo);
		canvasMouseEventWrapper.getAsDisplay().getImageAsWrapper().getSelectionManagger().setSelectionstoNull();
		
		
		
		
	}
	
	public void handlePress(CanvasMouseEventWrapper canvasMouseEventWrapper) {
		wrap=canvasMouseEventWrapper.getAsDisplay();
		
		if(canvasMouseEventWrapper.clickCount()<2) return;
		
		
		

		
	}
	
	public void handleDrag(CanvasMouseEventWrapper lastDragOrRelMouseEvent) {
	
		Point p2 = lastDragOrRelMouseEvent.getCoordinatePoint();
		BasicMontageLayout bm = layout.getPanelLayout();
		GenericMontageEditor edit = layout.getEditor();
		int[] rowcol = MontageLayoutRowColNumberTool.findAddedRowsCols((int)p2.getX(), (int)p2.getY(), bm);
		
		int r=bm.nRows();
		int c=bm.nColumns();
		if (rowcol[0]+bm.nRows()>=1 &&type==ROWS)r= rowcol[0];
		if (rowcol[1]+bm.nColumns()>=1 &&type==COLS)c= rowcol[1];
		if(r*c>-nRow*nCols) {
			IssueLog.log("will try to repack the panels");
			edit.repackagePanels(layout.getPanelLayout(), r, c);
		}
		
		new CanvasAutoResize().performActionDisplayedImageWrapper(lastDragOrRelMouseEvent.getAsDisplay());

	}
	/**What to do when a handle is moved from point p1 to p2*/
	public void handleMove(Point2D p1, Point2D p2) {
		
	}

	private BasicMontageLayout getCurrentLayout() {
		return layout.getPanelLayout();
		
	}
	
	

	private GenericMontageEditor getEditor() {
		this.layout.generateCurrentImageWrapper();
		return layout.getEditor();
	}

}

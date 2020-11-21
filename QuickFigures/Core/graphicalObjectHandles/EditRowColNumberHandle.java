package graphicalObjectHandles;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import applicationAdapters.CanvasMouseEventWrapper;
import genericMontageLayoutToolKit.MontageLayoutRowColNumberTool;

import java.awt.geom.Rectangle2D;

import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import gridLayout.BasicMontageLayout;
import gridLayout.GenericMontageEditor;
import gridLayout.MontageSpaces;
import imageMenu.CanvasAutoResize;
import undo.UndoLayoutEdit;

/**a handle that allows the user to pack the layout panels into a different number of rows and columns 
  For example, user can drag handle to easily transform a 1*6 layout into 2*3, 3*2 or6*1*/
public class EditRowColNumberHandle extends SmartHandle implements MontageSpaces{

	private static final int PLUS_SIZE = 3;
	protected MontageLayoutGraphic layout;
	protected int type;
	protected int index;
	private UndoLayoutEdit currentUndo;

	public EditRowColNumberHandle(int x, int y) {
		super(x, y);
	}

	public EditRowColNumberHandle(MontageLayoutGraphic montageLayoutGraphic) {
		this(0,0);
		
		this.layout=montageLayoutGraphic;
		setHandleColor(Color.red);
		this.specialShape=addSubtractShape(PLUS_SIZE, false);
		
		setupSpecialShape();
		Rectangle2D space = layout.getPanelLayout().getSelectedSpace(1, ALL_OF_THE+PANELS).getBounds();
		
		this.setHandleNumber(PanelLayoutGraphic.RepackPanelsHandle);
		
		double y2 = space.getMaxY()+25;
		
		double x2 = space.getMaxX()+25;
		
		this.setCordinateLocation(new Point2D.Double(x2, y2));
	
	}
	
	protected boolean hasSpecialShape() {;
	
		setupSpecialShape();
		return specialShape!=null;
	}

	/**sets up the arrow shapes*/
	public void setupSpecialShape() {
	
	}

	private static final long serialVersionUID = 1L;
	
	public boolean containsClickPoint(Point2D p) {
		return super.containsClickPoint(p);
	}
	
	
	/***/
	public void handleRelease(CanvasMouseEventWrapper canvasMouseEventWrapper) {
		if(currentUndo!=null) currentUndo.establishFinalState();
		canvasMouseEventWrapper.getAsDisplay().getUndoManager().addEdit(currentUndo);
		canvasMouseEventWrapper.getAsDisplay().getImageAsWrapper().getSelectionManagger().setSelectionstoNull();
	
	}
	
	public void handlePress(CanvasMouseEventWrapper canvasMouseEventWrapper) {
		currentUndo = new UndoLayoutEdit(layout);//establishes the undo
	}
	
	public void handleDrag(CanvasMouseEventWrapper lastDragOrRelMouseEvent) {
		Point p2 = lastDragOrRelMouseEvent.getCoordinatePoint();
		BasicMontageLayout current = layout.getPanelLayout();
		GenericMontageEditor edit = layout.getEditor();
		int[] proposedRowColChange = MontageLayoutRowColNumberTool.findAddedRowsCols((int)p2.getX(), (int)p2.getY(), current);
		
		int r=current.nRows();
		int c=current.nColumns();
		if (proposedRowColChange[0]+current.nRows()>=1 )r= proposedRowColChange[0]+current.nRows();
		if (proposedRowColChange[1]+current.nColumns()>=1 )c= proposedRowColChange[1]+current.nColumns();
		
			edit.repackagePanels(layout.getPanelLayout(), r, c);
		
		
		new CanvasAutoResize().performActionDisplayedImageWrapper(lastDragOrRelMouseEvent.getAsDisplay());

	}




}

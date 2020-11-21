package graphicalObjectHandles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEventWrapper;
import applicationAdapters.DisplayedImage;
import figureFormat.ChannelLabelPicker;
import figureFormat.RowLabelPicker;
import genericMontageKit.BasicObjectListHandler;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_FigureSpecific.FigureLabelOrganizer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.MontageSpaces;
import menuUtil.SmartPopupJMenu;
import undo.CombinedEdit;
import undo.UndoAddItem;
import undo.UndoLayoutEdit;
import utilityClassesForObjects.LocatedObject2D;

public class RowLabelHandle extends MoveRowHandle {

	private RowLabelPicker picker;
	private int mode=PANELS;
	
	public RowLabelHandle(MontageLayoutGraphic montageLayoutGraphic, int y,  int index) {
		super(montageLayoutGraphic, y, false, index);
		 if (type==ROWS) mode=MontageSpaces.ROW_OF_PANELS ;
			if (type==COLS)mode=MontageSpaces.COLUMN_OF_PANELS ;
			
			
		specialShape=null;
		Rectangle2D space = getSpaceForHandle(index);
		
	if(type==ROWS) 
	this.setCordinateLocation(new Point2D.Double(space.getMinX(), space.getCenterY()));
	else 
		this.setCordinateLocation(new Point2D.Double(space.getCenterX(), space.getMinY()));
	this.setHandleNumber(90000+100*type+index);
	
	
	this.message="Add Label";
	
	int width=60;
	int hight=20;
	this.setHandleColor(new Color(0,0,0,0));
	if(type==ROWS) 
	this.specialShape=new Rectangle(-width,-hight/2, width, hight);
	else this.specialShape=new Rectangle(-width/2,-hight, width, hight);
	

	
	hideIfNotNeeded(montageLayoutGraphic, index, getPicker(mode));
	hideIfNotNeeded(montageLayoutGraphic, index, new ChannelLabelPicker(new TextGraphic()));
	}

	private void hideIfNotNeeded(MontageLayoutGraphic montageLayoutGraphic, int index, RowLabelPicker pick) {
		Rectangle boundsForThisRowsLabel=getSpaceForLabel(index).getBounds();
		ArrayList<LocatedObject2D> rois = new BasicObjectListHandler().getOverlapOverlaypingItems(boundsForThisRowsLabel, montageLayoutGraphic.getPanelLayout().getWrapper());
		
		ArrayList<BasicGraphicalObject> array = pick.getDesiredItemsAsGraphicals(rois);
		
		if(array.size()>0) this.setHidden(true);
	}
	
	private Rectangle2D getSpaceForLabel(int index) {
		Rectangle2D space = layout.getPanelLayout().makeAltered(MontageSpaces.COLUMN_OF_PANELS).getSelectedSpace(index, MontageSpaces.LABEL_ALLOTED_TOP).getBounds();
	if(type==ROWS)  space = layout.getPanelLayout().makeAltered(MontageSpaces.ROW_OF_PANELS).getSelectedSpace(index, MontageSpaces.LABEL_ALLOTED_LEFT).getBounds();
		return space;
	}
	
	private Rectangle2D getSpaceForHandle(int index) {
		Rectangle2D space = layout.getPanelLayout().getSelectedSpace(index, mode).getBounds();
		return space;
	}
	
	
	
	public String menuCall() {
		String message="";
		if(type==ROWS) 
		message="Add Row Label";
		else message="Add Col Label";
		return message;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void handlePress(CanvasMouseEventWrapper canvasMouseEventWrapper) {
		
		if (canvasMouseEventWrapper.isPopupTrigger()) {return;}
		
		TextGraphic label = FigureLabelOrganizer.addLabelOfType(type, index, layout.getParentLayer(), layout);
		
		addLabel(canvasMouseEventWrapper, label);
		
	}

	protected void addLabel(CanvasMouseEventWrapper canvasMouseEventWrapper, TextGraphic label) {
		setUpMatchingLocation(label);
		
		
		DisplayedImage d = canvasMouseEventWrapper.getAsDisplay();
		
		CombinedEdit cEdit=new CombinedEdit();
		UndoAddItem anEdit = new UndoAddItem(layout.getParentLayer(), label);
		cEdit.addEditToList(anEdit);
		d.getUndoManager().addEdit(cEdit);
		d.updateDisplay();
		
		UndoLayoutEdit undo2 = new UndoLayoutEdit(layout);
		layout.getEditor().expandSpacesToInclude(layout.getPanelLayout(), label.getBounds());
		undo2.establishFinalLocations();
		
		cEdit.addEditToList(undo2);
	}

	private void setUpMatchingLocation(TextGraphic label) {
		Rectangle2D space = layout.getPanelLayout().getSelectedSpace(1, ALL_OF_THE+MontageSpaces.LABEL_ALLOTED_TOP).getBounds();
		if (type==ROWS) space = layout.getPanelLayout().makeAltered(MontageSpaces.BLOCK_OF_PANELS).getSelectedSpace(1, MontageSpaces.LABEL_ALLOTED_LEFT).getBounds();
		ArrayList<LocatedObject2D> rois = new BasicObjectListHandler().getOverlapOverlaypingItems(space.getBounds(), layout.getPanelLayout().getWrapper());
		ArrayList<BasicGraphicalObject> array =this.getPicker(mode).getDesiredItemsAsGraphicals(rois);
		if(array.size()>0) label.setSnapPosition(array.get(0).getSnapPosition());
	
	}
	public void handleDrag(CanvasMouseEventWrapper canvasMouseEventWrapper) {}
	public void handleRelease(CanvasMouseEventWrapper canvasMouseEventWrapper) {}
	
	protected void drawMessage(Graphics2D graphics, Shape s) {
		if(message!=null) {
			graphics.setColor(Color.BLACK);
			graphics.setFont(getMessageFont());
			graphics.drawString(message, (int)s.getBounds().getMinX()+3, (int)s.getBounds().getCenterY()+4);
		}
	}

	
	protected Font getMessageFont() {
		return new Font("Arial", 0, 11);
	}
	

	
	public RowLabelPicker getPicker(int mode) {
		
		this.picker=new RowLabelPicker(new ComplexTextGraphic(), mode);
		return picker;
	}

	class AddRowMenu extends SmartPopupJMenu {
		
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public void addManyLabels() {
			for(int i=1; i<=index; i++)
			{TextGraphic label = FigureLabelOrganizer.addLabelOfType(type, i, layout.getParentLayer(), layout);
			
			addLabel(super.getMemoryOfMouseEvent(), label);
			}
		}
	}


	
}

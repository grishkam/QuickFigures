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
package handles.layoutHandles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.DisplayedImage;
import figureFormat.ChannelLabelExamplePicker;
import figureFormat.LabelExamplePicker;
import figureOrganizer.FigureLabelOrganizer;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import layout.BasicObjectListHandler;
import layout.basicFigure.LayoutSpaces;
import locatedObject.LocatedObject2D;
import undo.CombinedEdit;
import undo.UndoAddItem;
import undo.UndoLayoutEdit;

/**A handle that is used to add a label to a default layout*/
public class AddLabelHandle extends MoveRowHandle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private LabelExamplePicker picker;//determines what labels are already considered as already present
	private int mode=PANELS;
	
	public AddLabelHandle(DefaultLayoutGraphic montageLayoutGraphic, int y,  int index) {
		super(montageLayoutGraphic, y, false, index);
		 if (type==ROWS) mode=LayoutSpaces.ROW_OF_PANELS ;
			if (type==COLS)mode=LayoutSpaces.COLUMN_OF_PANELS ;
			
			
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
	hideIfNotNeeded(montageLayoutGraphic, index, new ChannelLabelExamplePicker(new TextGraphic()));
	}

	private void hideIfNotNeeded(DefaultLayoutGraphic montageLayoutGraphic, int index, LabelExamplePicker pick) {
		boolean needLabel = labelSpaceNotAvailable(montageLayoutGraphic, index, pick);
		if(needLabel) this.setHidden(true);
	}

	/**returns true if the space for the label of the given index is occupied by another label
	 * @param montageLayoutGraphic
	 * @param index
	 * @param pick
	 * @return
	 */
	public boolean labelSpaceNotAvailable(DefaultLayoutGraphic montageLayoutGraphic, int index, LabelExamplePicker pick) {
		Rectangle boundsForThisRowsLabel=getSpaceForLabel(index).getBounds();
		ArrayList<LocatedObject2D> rois = new BasicObjectListHandler().getOverlapOverlaypingItems(boundsForThisRowsLabel, montageLayoutGraphic.getPanelLayout().getVirtualWorksheet());
		
		ArrayList<BasicGraphicalObject> array = pick.getDesiredItemsAsGraphicals(rois);
		
		boolean needLabel = array.size()>0;
		return needLabel;
	}
	
	private Rectangle2D getSpaceForLabel(int index) {
		Rectangle2D space = layout.getPanelLayout().makeAltered(LayoutSpaces.COLUMN_OF_PANELS).getSelectedSpace(index, LayoutSpaces.LABEL_ALLOTED_TOP).getBounds();
	if(type==ROWS)  space = layout.getPanelLayout().makeAltered(LayoutSpaces.ROW_OF_PANELS).getSelectedSpace(index, LayoutSpaces.LABEL_ALLOTED_LEFT).getBounds();
		return space;
	}
	
	private Rectangle2D getSpaceForHandle(int index) {
		Rectangle2D space = layout.getPanelLayout().getSelectedSpace(index, mode).getBounds();
		return space;
	}
	

	/**Adds a label in response to a handle press*/
	public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
		
		if (canvasMouseEventWrapper.isPopupTrigger()) {return;}
		
		TextGraphic label = FigureLabelOrganizer.addLabelOfType(type, index, layout.getParentLayer(), layout);
		
		addLabel(canvasMouseEventWrapper, label);
		
	}

	protected void addLabel(CanvasMouseEvent canvasMouseEventWrapper, TextGraphic label) {
		setUpMatchingLocation(label);
		
		
		DisplayedImage d = canvasMouseEventWrapper.getAsDisplay();
		
		CombinedEdit cEdit=new CombinedEdit();
		UndoAddItem anEdit = new UndoAddItem(layout.getParentLayer(), label);
		cEdit.addEditToList(anEdit);
		d.getUndoManager().addEdit(cEdit);
		d.updateDisplay();
		
		UndoLayoutEdit undo2 = new UndoLayoutEdit(layout);
		expandLabelSpace(label);
		undo2.establishFinalLocations();
		
		cEdit.addEditToList(undo2);
	}

	/**
	 * @param label
	 */
	public void expandLabelSpace(TextGraphic label) {
		layout.getEditor().expandSpacesToInclude(layout.getPanelLayout(), label.getBounds());
	}

	private void setUpMatchingLocation(TextGraphic label) {
		Rectangle2D space = layout.getPanelLayout().getSelectedSpace(1, ALL_OF_THE+LayoutSpaces.LABEL_ALLOTED_TOP).getBounds();
		if (type==ROWS) space = layout.getPanelLayout().makeAltered(LayoutSpaces.BLOCK_OF_PANELS).getSelectedSpace(1, LayoutSpaces.LABEL_ALLOTED_LEFT).getBounds();
		ArrayList<LocatedObject2D> rois = new BasicObjectListHandler().getOverlapOverlaypingItems(space.getBounds(), layout.getPanelLayout().getVirtualWorksheet());
		ArrayList<BasicGraphicalObject> array =this.getPicker(mode).getDesiredItemsAsGraphicals(rois);
		if(array.size()>0) label.setAttachmentPosition(array.get(0).getAttachmentPosition());
	
	}
	public void handleDrag(CanvasMouseEvent canvasMouseEventWrapper) {}
	public void handleRelease(CanvasMouseEvent canvasMouseEventWrapper) {}
	
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
	
	/**returns the label picker that is used to determine that kinds of labels are treated as already 
	  occupying the label location*/ 
	private LabelExamplePicker getPicker(int mode) {
		this.picker=new LabelExamplePicker(new ComplexTextGraphic(), mode);
		return picker;
	}

	



	
}

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
 * Date Modified: Dec 16, 2021
 * Version: 2021.2
 */
package handles.layoutHandles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPopupMenu;

import addObjectMenus.LaneLabelAdder;
import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.DisplayedImage;
import figureFormat.ChannelLabelExamplePicker;
import figureFormat.LabelExamplePicker;
import figureOrganizer.FigureLabelOrganizer;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.FigureType;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import imageMenu.CanvasAutoResize;
import layout.BasicObjectListHandler;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import locatedObject.LocatedObject2D;
import menuUtil.BasicSmartMenuItem;
import menuUtil.SmartPopupJMenu;
import messages.ShowMessage;
import standardDialog.StandardDialog;
import ultilInputOutput.FileChoiceUtil;
import undo.CombinedEdit;
import undo.UndoAddItem;
import undo.UndoLayoutEdit;

/**A handle that is used to add a label to a default layout*/
public class AddLabelHandle extends MoveRowHandle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**set to true if opposite side*/
	boolean opposite=false;
	
	private LabelExamplePicker picker;//determines what labels are already considered as already present
	private int mode=PANELS;

	private String defaultText=null;
	
	/**The constructor
	 * @param montageLayoutGraphic the layouts
	 * @param index the row or column index
	 * @param newType the code indicating whether it is a row of column label @see LayoutSpaces
	 * @param otherSide indicates whether the label should be added opposite to the normal side*/
	public AddLabelHandle(DefaultLayoutGraphic montageLayoutGraphic, int newType,  int index, boolean otherSide) {
		super(montageLayoutGraphic, newType, false, index);
		this.opposite= otherSide;
		
		 if (type==ROWS) mode=LayoutSpaces.ROW_OF_PANELS ;
			if (type==COLS)mode=LayoutSpaces.COLUMN_OF_PANELS ;
			
			
		specialShape=null;
		Rectangle2D space = getSpaceForHandle(index);
		
	if(type==ROWS) 
		{
		this.setCordinateLocation(new Point2D.Double(space.getMinX(), space.getCenterY()));
		if(otherSide) 
			this.setCordinateLocation(new Point2D.Double(space.getMaxX(), space.getCenterY()));
		}
	else {
		this.setCordinateLocation(new Point2D.Double(space.getCenterX(), space.getMinY()));
		if(otherSide) 
			setCordinateLocation(new Point2D.Double(space.getCenterX(), space.getMaxY()));
		
		}
	
	
	this.setHandleNumber(90000+100*type+index+(opposite?900000:0));
	
	
	this.message="Add Label";
	

	
	int width=60;
	int hight=20;
	this.setHandleColor(new Color(0,0,0,0));
	if(type==ROWS) 
		this.specialShape=new Rectangle(-width,-hight/2, width, hight);
	else this.specialShape=new Rectangle(-width/2,-hight, width, hight);
	
	if(otherSide) {
		if(type==ROWS) 
			this.specialShape=new Rectangle(0,-hight/2, width, hight);
		else this.specialShape=new Rectangle(-width/2,hight, width, hight);
	}

	if(type==COLS&&montageLayoutGraphic.getFigureType()==FigureType.WESTERN_BLOT) {
		this.message="Add Lane Labels";
		width=width*3/2;
		this.specialShape=new Rectangle(-width/2,-hight, width, hight);
	}
	
	hideIfNotNeeded(montageLayoutGraphic, index, getPicker(mode));
	hideIfNotNeeded(montageLayoutGraphic, index, new ChannelLabelExamplePicker(new TextGraphic()));
	}

	private void hideIfNotNeeded(DefaultLayoutGraphic montageLayoutGraphic, int index, LabelExamplePicker pick) {
		boolean labelShouldBeHidden = labelSpaceNotAvailable(montageLayoutGraphic, index, pick);
		if(labelShouldBeHidden) this.setHidden(true);
	}

	/**returns true if the space for the label of the given index is occupied by another label
	 * @param montageLayoutGraphic
	 * @param index
	 * @param pick
	 * @return
	 */
	public boolean labelSpaceNotAvailable(DefaultLayoutGraphic montageLayoutGraphic, int index, LabelExamplePicker pick) {
		Rectangle boundsForThisRowsLabel=getSpaceForLabel(index, opposite).getBounds();
		ArrayList<LocatedObject2D> rois = new BasicObjectListHandler().getOverlapOverlaypingItems(boundsForThisRowsLabel, montageLayoutGraphic.getPanelLayout().getVirtualWorksheet());
		
		ArrayList<BasicGraphicalObject> array = pick.getDesiredItemsAsGraphicals(rois);
		ArrayList<BasicGraphicalObject> spaceFillerList = pick.getDesiredItemsAsGraphicals(rois);
		
		boolean spaceFilled = array.size()>0;
		
		
		
		/**account for the circumstance where there is only a partial overlap between a label in a nearby column. Also considers if the space meant for   */
		for (BasicGraphicalObject currentObject : array) {
			
			Rectangle b2 = currentObject.getBounds();
			Rectangle2D intersection = b2.createIntersection(boundsForThisRowsLabel);
			if(intersection.getHeight()*intersection.getWidth()*2.5<b2.height*b2.width)
				{
				spaceFillerList.remove(currentObject);
				}
			
			if(currentObject.getTagHashMap().get("Index")!=null&&(int)currentObject.getTagHashMap().get("Index")==index)// what to do if the tag on the object says it belongs to this locaiton
				spaceFillerList.add(currentObject);
			if(currentObject.getTagHashMap().get("Index")!=null&&(int)currentObject.getTagHashMap().get("Index")!=index)// what to do if the tag on the object says it belongs to another location
				spaceFillerList.remove(currentObject);
			
		}
		spaceFilled =spaceFillerList.size()>0;
		
		return spaceFilled;
	}
	
	/**returns the section the layout where the label belongs*/
	private Rectangle2D getSpaceForLabel(int index, boolean opposite) {
		Rectangle2D space = layout.getPanelLayout().makeAltered(LayoutSpaces.COLUMN_OF_PANELS).getSelectedSpace(index, LayoutSpaces.LABEL_ALLOTED_TOP).getBounds();
	if(type==ROWS) 
		space = layout.getPanelLayout().makeAltered(LayoutSpaces.ROW_OF_PANELS).getSelectedSpace(index, LayoutSpaces.LABEL_ALLOTED_LEFT).getBounds();
	if(type==ROWS&&opposite)  
		space = layout.getPanelLayout().makeAltered(LayoutSpaces.ROW_OF_PANELS).getSelectedSpace(index, LayoutSpaces.LABEL_ALLOTED_RIGHT).getBounds();
	if(type==PANELS)  
		space = layout.getPanelLayout().getSelectedSpace(index, LayoutSpaces.PANELS).getBounds();
	
	
	return space;
	}
	
	private Rectangle2D getSpaceForHandle(int index) {
		Rectangle2D space = layout.getPanelLayout().getSelectedSpace(index, mode).getBounds();
		return space;
	}
	

	/**Adds a label in response to a handle press*/
	public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
		
		if (canvasMouseEventWrapper.isPopupTrigger()) 
			{return;}
		
		
		CombinedEdit cEdit = new CombinedEdit();
		
		/**conditional for the special circumstance of this being a western blot figure*/
		if (mode==LayoutSpaces.COLUMN_OF_PANELS &&layout.getParentLayer() instanceof FigureOrganizingLayerPane) {
			FigureOrganizingLayerPane figure=(FigureOrganizingLayerPane) layout.getParentLayer();
			if(figure.getFigureType()==FigureType.WESTERN_BLOT) {
				boolean decision = FileChoiceUtil.yesOrNo("Do you want to add multiple lane labels? This feature is meant for blots and gels");
				if (decision) {
					addLaneLabels(canvasMouseEventWrapper);
					return;
					}
				
			}
		}
		
		if(canvasMouseEventWrapper.shiftDown()) {
			performSingleAllLabelAddition(canvasMouseEventWrapper, cEdit);
		}else
		
		performSingleLabelAddition(canvasMouseEventWrapper, cEdit);
		
		cEdit.addEditToList(
				new CanvasAutoResize(false).performUndoableAction(canvasMouseEventWrapper.getAsDisplay())
		);
		
		canvasMouseEventWrapper.addUndo(cEdit);
		
	}
	
	

	/**Adds labels to all the single label locations that are empty.
	 * Prompts the user to input text
	 * @param canvasMouseEventWrapper
	 * @param cEdit
	 */
	private void performSingleAllLabelAddition(CanvasMouseEvent canvasMouseEventWrapper, CombinedEdit cEdit) {
		String s1=FigureLabelOrganizer.getDefaultNameOfType(type, opposite)+FigureLabelOrganizer.getNumberCode();
		String[] dText = new String[] {s1};
		dText=StandardDialog.getStringArrayFromUser("Label Text", s1, 10);
		
		performAllLabelAddition(canvasMouseEventWrapper, cEdit, dText);
		
	}

	/**Adds labels to all the single label locations that are empty
	 * @param canvasMouseEventWrapper
	 * @param cEdit
	 * @param dText
	 */
	public void performAllLabelAddition(CanvasMouseEvent canvasMouseEventWrapper, CombinedEdit cEdit, String[] dText) {
		int count=0;
		int n=layout.getPanelLayout().nPanels();
		if (type==BasicLayout.ROWS) 
			n=layout.getPanelLayout().nRows();
		else 
			if (type==BasicLayout.COLS) 
				n=layout.getPanelLayout().nColumns();
		for(int i=0; i<n; i++) {
			AddLabelHandle current = new AddLabelHandle(layout, type, i+1, opposite);
			if(i<dText.length)
				current.defaultText=dText[i];
			else 
				current.defaultText=dText[dText.length-1];
			
			if(!current.isHidden()) {
				current.performSingleLabelAddition(canvasMouseEventWrapper, cEdit);
				count++;
			}
		}
		if(count==0) {
			ShowMessage.showOptionalMessage("Label space is occupied", false, "It appears that there are already labels in those locations");
		}
	}

	/**Adds a single label
	 * @param canvasMouseEventWrapper
	 * @param cEdit
	 */
	protected void performSingleLabelAddition(CanvasMouseEvent canvasMouseEventWrapper, CombinedEdit cEdit) {
		TextGraphic label = FigureLabelOrganizer.addLabelOfType(defaultText,type, index, layout.getParentLayer(), layout, opposite, null);

		afterAddLabel(canvasMouseEventWrapper, label, cEdit);
	}

	/**Adds lane labels to the column i. This feature is a work in progress
	 * @param canvasMouseEventWrapper
	 */
	private void addLaneLabels(CanvasMouseEvent canvasMouseEventWrapper) {
		
		DefaultLayoutGraphic layout2 = layout;
		int index2 = this.index;
		
		CombinedEdit undo = createLaneLabelsFor(canvasMouseEventWrapper, layout2, index2);
			
		canvasMouseEventWrapper.addUndo(undo);
	}

	/**
	 * @param canvasMouseEventWrapper
	 * @param layout2
	 * @param index2
	 * @return
	 */
	public static CombinedEdit createLaneLabelsFor(CanvasMouseEvent canvasMouseEventWrapper, DefaultLayoutGraphic layout2,
			int index2) {
		CombinedEdit undo = new CombinedEdit();
		LaneLabelAdder laneLabelAdder = new LaneLabelAdder();
		TextGraphic textItem = laneLabelAdder. createTextItem();
		
		
		
		
		
		Rectangle box = layout2.getPanelLayout().getSelectedSpace(index2, LayoutSpaces.PANELS).getBounds();
		
		ArrayList<TextGraphic> labelList = new ArrayList<TextGraphic>();
		DefaultLayoutGraphic laneLabelLayout = laneLabelAdder.addLaneLabel(textItem, true, labelList, layout2.getParentLayer(), box, undo);
		
		if(laneLabelLayout ==null)
			return null;//if user clicks cancel will not return a layout
		
		Rectangle laneLabelBounds = laneLabelLayout.getBounds();
		
		if(labelList.size()>0)
			textItem=labelList.get(0);
		if( laneLabelBounds.getMaxY()>box.getMinY()+1) {
			
			undo.addEditToList(new UndoLayoutEdit(layout2));
			double height = laneLabelBounds.getMaxY()-box.getMinY();
			
			layout2.moveLayoutAndContents(0, height);//to make sure lane labels are above
			laneLabelLayout.moveLayoutAndContents(0, -height);
		}
		
		/**expands the figure label pace for new lane labels*/
			undo.addEditToList(performLabelSpaceExpansion( laneLabelLayout, layout2));
		
			undo.addEditToList(
					new CanvasAutoResize(false).performUndoableAction(canvasMouseEventWrapper.getAsDisplay())
			);
		return undo;
	}

	/**Formats the label and adds an undo to the undo manager*/
	protected void afterAddLabel(CanvasMouseEvent canvasMouseEventWrapper, TextGraphic label, CombinedEdit cEdit) {
		
		setUpMatchingLocation(label, true);
		
		
		DisplayedImage d = canvasMouseEventWrapper.getAsDisplay();
		
		
		UndoAddItem anEdit = new UndoAddItem(layout.getParentLayer(), label);
		cEdit.addEditToList(anEdit);
		
		d.updateDisplay();
		
		
		
		cEdit.addEditToList(performLabelSpaceExpansion(label, layout));
		
		
	}

	/**
	 * @param label
	 * @return
	 */
	protected static UndoLayoutEdit performLabelSpaceExpansion(BasicGraphicalObject label,  DefaultLayoutGraphic layout) {
		UndoLayoutEdit undo2 = new UndoLayoutEdit(layout);
		expandLabelSpace(label, layout);
		undo2.establishFinalLocations();
		return undo2;
	}

	/**
	 * @param label
	 */
	public static void expandLabelSpace(BasicGraphicalObject label,  DefaultLayoutGraphic layout) {
		layout.getEditor().expandSpacesToInclude(layout.getPanelLayout(), label.getBounds());
	}

	/**sets the attachment position */
	private void setUpMatchingLocation(TextGraphic label, boolean font) {
		Rectangle2D space = layout.getPanelLayout().getSelectedSpace(1, ALL_OF_THE+LayoutSpaces.LABEL_ALLOTED_TOP).getBounds();
		
		if (type==COLS &&opposite)  {
			layout.getPanelLayout().getSelectedSpace(1, ALL_OF_THE+LayoutSpaces.LABEL_ALLOTED_BOT).getBounds();
		}
		if (type==ROWS) 
			space = layout.getPanelLayout().makeAltered(LayoutSpaces.BLOCK_OF_PANELS).getSelectedSpace(1, LayoutSpaces.LABEL_ALLOTED_LEFT).getBounds();
		if (type==ROWS &&opposite) 
			space = layout.getPanelLayout().makeAltered(LayoutSpaces.BLOCK_OF_PANELS).getSelectedSpace(1, LayoutSpaces.LABEL_ALLOTED_RIGHT).getBounds();
		if (type==PANELS ) 
			space = layout.getPanelLayout().makeAltered(LayoutSpaces.BLOCK_OF_PANELS).getSelectedSpace(1, ALL_OF_THE+PANELS).getBounds();
		
		
		ArrayList<LocatedObject2D> rois = new BasicObjectListHandler().getOverlapOverlaypingItems(space.getBounds(), layout.getPanelLayout().getVirtualWorksheet());
		ArrayList<BasicGraphicalObject> array =this.getPicker(mode).getDesiredItemsAsGraphicals(rois);
		if(array.size()>0) {
				BasicGraphicalObject seniorTextItem = array.get(array.size()-1);
				 label.setAttachmentPosition(seniorTextItem.getAttachmentPosition());
				
				if(font && seniorTextItem instanceof TextGraphic) {
					TextGraphic t = (TextGraphic) seniorTextItem;
					label.setFont(t.getFont());
					label.setAngle(t.getAngle());
					if(t.getTagHashMap().get("Index")!=null)//if an existing label has an address, assigns that address to the current label
						label.getTagHashMap().put("Index", this.index);
					label.setTextColor(t.getTextColor());
				}
		}
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

	
	/**returns the font that will be used for the 'add label' message */
	protected Font getMessageFont() {
		return new Font("Arial", 0, 11);
	}
	
	/**returns the label picker that is used to determine that kinds of labels are treated as already 
	  occupying the label location*/ 
	private LabelExamplePicker getPicker(int mode) {
		this.picker=new LabelExamplePicker(new ComplexTextGraphic(), mode);
		return picker;
	}

	
	/**returns the popup menu for this handle. */
	public JPopupMenu getJPopup() {
		SmartPopupJMenu menu=new SmartPopupJMenu();
		
		BasicSmartMenuItem[] b = new BasicSmartMenuItem[] {
				new AddAllLabelsMenuItem(LayoutSpaces.ROWS, false),
				new AddAllLabelsMenuItem(LayoutSpaces.COLS, false),
				new AddAllLabelsMenuItem(LayoutSpaces.PANELS, false)
				};
		for(BasicSmartMenuItem m: b) {menu.add(m);}
		
		
		menu.add(new BasicSmartMenuItem("Add Lane Labels") {
				private static final long serialVersionUID = 1L;
				@Override
				public void actionPerformed(ActionEvent e) {
					addLaneLabels(me);
				}	
			}) ;
		
		return menu;
	}

	/**A popup meny item that adds many labels instead of the default single label*/
	class AddAllLabelsMenuItem extends BasicSmartMenuItem  {

		private AddLabelHandle handle;

		/**
		 * @param rows
		 */
		public AddAllLabelsMenuItem(int rows, boolean opposite) {
		
			
			handle=new AddLabelHandle(layout, rows, 1, opposite);
			this.setText("Add labels to "+stringDescriptors[rows]+"s");
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			CombinedEdit cEdit = new CombinedEdit();
			handle.performSingleAllLabelAddition(me, cEdit);
			this.getUndoManager().addEdit(cEdit);
		}
		
	}
	
}

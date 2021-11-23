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
 * Date Created: May 2, 2021
 * Date Modified: Nov 23, 2021
 * Version: 2021.2
 */
package addObjectMenus;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.Icon;

import figureOrganizer.FigureLabelOrganizer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import locatedObject.AttachmentPosition;
import locatedObject.RectangleEdges;
import messages.ShowMessage;
import standardDialog.strings.StringInputPanel;
import storedValueDialog.StoredValueDilaog;
import undo.CombinedEdit;
import undo.UndoAddItem;

/**Adds text objects to a selected image panel
 * Meant to represent lane labels*/
public class LaneLabelAdder extends BasicGraphicAdder {
	
	/**
	 * 
	 */
	private static final String LABEL_PASTE_TEXT_AREA_KEY = "Custom";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	LaneLabelCreationOptions options=new LaneLabelCreationOptions();
	private String[] labelList;
	
	public LaneLabelAdder() {
		
	}
	
	private CombinedEdit undo;
	
	/**A text item to show as an icon. along with the menu item*/
	TextGraphic iconText=new TextGraphic();

	
	 {
		iconText.setFont(iconText.getFont().deriveFont((float) 42));	
		iconText.setFillBackGround(true);
		iconText.getBackGroundShape().copyAttributesFrom(RectangularGraphic.blankRect(new Rectangle(),iconText.getTextColor()));
		iconText.getBackGroundShape().setStrokeColor(iconText.getTextColor());
		iconText.setText("text");
		iconText.setLocationUpperLeft(0, 0);
		
	}
	
	 /**Adds one or more text items, will add one to each selected image panel if possible
	   if not, just adds one text item to the figure*/
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		boolean proceed = ShowMessage.showOptionalMessage("Lane labels are an experimental feature for western blot and gel images", false, "Lane labels for western blots are a work in progress, are you sure you want to proceed?");
		if(!proceed)
			return null;
		TextGraphic out = createTextItem();
		 addLockedItemToSelectedImage(out);
		
		
		return out;
		
	}

	/**Creates the text item that is the model for all of the lane labels
	 * @return
	 */
	public TextGraphic createTextItem() {
		TextGraphic out=new FigureLabelOrganizer.ColumnLabelTextGraphic();
		out.setLocationUpperLeft(50, 50);
		out.setAttachmentPosition(AttachmentPosition.defaultLaneLabel());
		return out;
	}

	/**Adds many copies of the text item to the selected images. Attaches each text to 
	  an image panel. font size is decreased if panels are too small*/
	public ArrayList<TextGraphic> addLockedItemToSelectedImage(TextGraphic ag) {
		
		undo=new CombinedEdit();
		
		
		ArrayList<ZoomableGraphic> possibleTargets = selector.getSelecteditems();
		
		ArrayList<TextGraphic> added=new ArrayList<TextGraphic>();
		
		
		for(ZoomableGraphic item :possibleTargets) 
				if (item instanceof ImagePanelGraphic) {
					ImagePanelGraphic it = (ImagePanelGraphic) item;
					Rectangle b = it.getBounds();
					
					
					addLaneLabel(ag, false, added, it.getParentLayer(), b, undo);
					break;
				}
		
		
		if (added.size()==0) undo=null;
		return added;
	}

	/**
	 * @param ag
	 * @param output
	 * @param added
	 * @param it
	 * @param b
	 * @return
	 */
	public DefaultLayoutGraphic addLaneLabel(TextGraphic ag, boolean output, ArrayList<TextGraphic> added, GraphicLayer parentLayer,
			Rectangle b, CombinedEdit undo) {
		GraphicLayerPane addedLayer = new GraphicLayerPane("lane labels");
		showLaneLabelDialog();
		int count = 1;
		
		int nLanes=(int) options.nLanes;
		
		
		
		int border = 5;
		
		/**calculates the column width needed to fill tne space*/
		int wCol = b.width/nLanes-border+border/(nLanes-1);
		
		
		BasicLayout layout = new BasicLayout(nLanes, 1, wCol, b.height/5, border, border, true);
		layout.move(b.getX(), b.getY());
		DefaultLayoutGraphic roi = new DefaultLayoutGraphic(layout);
		addedLayer.add(roi);
		
		undo.addEditToList(new UndoAddItem(parentLayer, addedLayer));
		parentLayer.add(addedLayer);
		
		for(int f=1; f<=nLanes; f++){
			TextGraphic  ag2 = ag;
			ag2.setFontSize((int) (wCol/2));
			ag2.setAngle(45);
			
			if (output) {
				ag2=ag.copy();
				ag2.setAttachmentPosition(ag.getAttachmentPosition());
			} else {
				ag.setAttachmentPosition(AttachmentPosition.defaultLaneLabel());
				while (ag.getBounds().width>0.8*b.getWidth()) {ag.setFontSize(ag.getFont().getSize()-1);}
			}
			Rectangle2D panel = layout.makeAltered(LayoutSpaces.COLUMN_OF_PANELS).getPanel(f);
			ag2.setLocation(panel.getCenterX(), panel.getMinY());
			
			/**Sets the text of the label*/
			String text_for_label = options.prefix+count+options.suffix;
			if(labelList!=null &&labelList.length>=f) {
				text_for_label=labelList[f-1];
			}
			ag2.setContent(text_for_label);
			
			
			
			count++;
			ag2.setTextColor(Color.black);
			
			ag2.getTagHashMap().put("Index",f);
			
			added.add(ag2);
			addedLayer.add(ag2);
			roi.addLockedItem(ag2);
			output=true;
			
			GraphicLayer p = addedLayer;
			
			undo.addEditToList(new UndoAddItem(p, ag2));
		}
		
		double height = 10;
		for(TextGraphic a: added)height=a.getBounds().getHeight();
		roi.getPanelLayout().labelSpaceWidthTop=height;
		roi.moveLayoutAndContents(0, -height);
		
		
		roi.setLocation(RectangleEdges.getLocation(RectangleEdges.UPPER_LEFT, b));
		return roi;
	}

	/**
	 * Shos the dialog which allods the user to choose how many lane labels to create
	 */
	protected void showLaneLabelDialog() {
		StoredValueDilaog storedValueDilaog = new StoredValueDilaog(options);
		storedValueDilaog .setModal(true);
		 storedValueDilaog.setTitle("How many lane labels?");
		 labelList=null;
		 storedValueDilaog.add(LABEL_PASTE_TEXT_AREA_KEY, new StringInputPanel("Paste label list below", null, 15, 20));
		 
		storedValueDilaog.showDialog();
		
		labelList=storedValueDilaog.getLinesFromString(LABEL_PASTE_TEXT_AREA_KEY);
	}
	
	@Override
	public String getCommand() {
		return "addText";
	}

	@Override
	public String getMenuCommand() {
		return "Lane labels";
	}
	
	public Icon getIcon() {
		return  ComplexTextGraphic.createImageIcon();
		
	}
	
	/**performs the action.*/
	public void run() {
		GraphicLayer l = null;
		if(selector!=null &&selector.getSelectedLayer()!=null)l=selector.getSelectedLayer();
	
		ZoomableGraphic item = this.add(l);
		
		if(undo!=null) getUndoManager().addEdit(undo) ;
		else
		if(item!=null) {
			this.getUndoManager().addEdit(new UndoAddItem(l, item));
		}
	}
	
	@Override
	public String getMenuPath() {
		return "To selected panels";
	}

	
}

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
 * Date Modified: May 2, 2021
 * Version: 2021.1
 */
package addObjectMenus;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.Icon;

import figureOrganizer.LabelCreationOptions;
import figureOrganizer.MultichannelDisplayLayer;
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
import standardDialog.StandardDialog;
import storedValueDialog.StoredValueDilaog;
import undo.CombinedEdit;
import undo.UndoAddItem;

/**Adds text objects to a selected image panel
 * Meant to represent lane labels*/
class LaneLabelAdder extends BasicGraphicAdder {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean simple=false;
	LaneLabelCreationOptions options=new LaneLabelCreationOptions();
	
	public LaneLabelAdder(boolean isSimple) {
		simple=isSimple;
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
		TextGraphic out = new TextGraphic();
		if(!simple) out=new ComplexTextGraphic();
		out.setLocationUpperLeft(50, 50);
		out.setAttachmentPosition(AttachmentPosition.defaultLaneLabel());
		ArrayList<TextGraphic> list = addLockedItemToSelectedImages(out);
		
		
		return out;
		
	}

	/**Adds many copies of the text item to the selected images. Attaches each text to 
	  an image panel. font size is decreased if panels are too small*/
	public ArrayList<TextGraphic> addLockedItemToSelectedImages(TextGraphic ag) {
		undo=new CombinedEdit();
		GraphicLayerPane addedLayer = new GraphicLayerPane("lane labels");
		
		ArrayList<ZoomableGraphic> possibleTargets = selector.getSelecteditems();
		boolean output=false;//true if at least one object has been added
		ArrayList<TextGraphic> added=new ArrayList<TextGraphic>();
		int count = 1;
		
		
		for(ZoomableGraphic item :possibleTargets) 
				if (item instanceof ImagePanelGraphic) {
					ImagePanelGraphic it = (ImagePanelGraphic) item;
					Rectangle b = it.getBounds();
					
					
					
					StoredValueDilaog storedValueDilaog = new StoredValueDilaog(options);storedValueDilaog .setModal(true);
					storedValueDilaog.showDialog();
					
					int nLanes=(int) options.nLanes;
					
					
					
					int border = 2;
					
					/**calculates the column width needed to fill tne space*/
					int wCol = b.width/nLanes-border+border/(nLanes-1);
					
					
					BasicLayout layout = new BasicLayout(nLanes, 1, wCol, b.height/5, border, border, true);
					layout.move(it.getLocationUpperLeft().getX(), it.getLocationUpperLeft().getY());
					DefaultLayoutGraphic roi = new DefaultLayoutGraphic(layout);
					addedLayer.add(roi);
					
					undo.addEditToList(new UndoAddItem(it.getParentLayer(), addedLayer));
					it.getParentLayer().add(addedLayer);
					
					for(int f=1; f<=nLanes; f++){
						TextGraphic  ag2 = ag;
						ag2.setFontSize(wCol/2);
						ag2.setAngle(45);
						
						if (output) {
							ag2=ag.copy();
							ag2.setAttachmentPosition(ag.getAttachmentPosition());
						} else {
							ag.setAttachmentPosition(AttachmentPosition.defaultColLabel());
							while (ag.getBounds().width>0.9*it.getObjectWidth()) {ag.setFontSize(ag.getFont().getSize()-1);}
						}
						Rectangle2D panel = layout.makeAltered(LayoutSpaces.COLUMN_OF_PANELS).getPanel(f);
						ag2.setLocation(panel.getCenterX(), panel.getMinY());
						ag2.setText(options.prefix+count+options.suffix); count++;
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
					
					
					roi.setLocation(it.getLocation());
					break;
				}
		
		
		if (added.size()==0) undo=null;
		return added;
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
		if(!simple) return  ComplexTextGraphic.createImageIcon();
		return TextGraphic.createImageIcon();
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
		return "to selected panels";
	}

	
}

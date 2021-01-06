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
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package addObjectMenus;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.Icon;

import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import locatedObject.AttachmentPosition;
import undo.CombinedEdit;
import undo.UndoAddItem;

/**Adds text objects to selected image panels*/
class TextItemAdder extends BasicGraphicAdder {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean simple=false;
	
	public TextItemAdder(boolean isSimple) {
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
		ArrayList<TextGraphic> list = addLockedItemToSelectedImages(out);
		if (list.size()<1
				) {
				gc.add(out);
				out.showOptionsDialog();
		}
		
		return out;
		
	}

	/**Adds many copies of the text item to the selected images. Attaches each text to 
	  an image panel. font size is decreased if panels are too small*/
	public ArrayList<TextGraphic> addLockedItemToSelectedImages(TextGraphic ag) {
		undo=new CombinedEdit();
		ArrayList<ZoomableGraphic> possibleTargets = selector.getSelecteditems();
		boolean output=false;//true if at least one object has been added
		ArrayList<TextGraphic> added=new ArrayList<TextGraphic>();
		int count = 1;
		for(ZoomableGraphic item :possibleTargets) 
				if (item instanceof ImagePanelGraphic) {
					ImagePanelGraphic it = (ImagePanelGraphic) item;
					TextGraphic  ag2 = ag;
					
					if (output) {
						ag2=ag.copy();
						ag2.setAttachmentPosition(ag.getAttachmentPosition());
					} else {
						ag.setAttachmentPosition(AttachmentPosition.defaultPanelLabel());
						while (ag.getBounds().width>0.9*it.getObjectWidth()) {ag.setFontSize(ag.getFont().getSize()-1);}
					}
					ag2.setText("Label "+count); count++;
					ag2.setTextColor(Color.white);
					added.add(ag2);
					it.addLockedItem(ag2);
					output=true;
					GraphicLayer p = it.getParentLayer();
					if (p instanceof MultichannelDisplayLayer) {p=p.getParentLayer();}
					p.addItemToLayer(ag2);
					undo.addEditToList(new UndoAddItem(p, ag2));
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
		if(!simple) return "Add Rich Text";
		return "Add Text";
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

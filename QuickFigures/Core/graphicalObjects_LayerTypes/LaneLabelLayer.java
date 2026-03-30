/*******************************************************************************
 * Copyright (c) 2026 Gregory Mazo
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
 * Date Created: March 30, 2026
 * Date Modified: March 30, 2026
 * Version: 2023.2
 */
package graphicalObjects_LayerTypes;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import addObjectMenus.LaneLabelCopy;
import fLexibleUIKit.MenuItemExecuter;
import fLexibleUIKit.MenuItemMethod;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import popupMenusForComplexObjects.DonatesMenu;
import textObjectProperties.TextPattern;
import undo.SimpleTraits;
import undo.UndoAddItem;

/**
 Soemtimes a user wants an shape displayed over a few different parent panels
 but at the equivalent location in each panel.
 A special layer that contains items whose location is determined by a parent item
 Whenever a user changes the location of the parent item, the reflections will be updated.
 
 
 */
public class LaneLabelLayer extends GraphicLayerPane implements  HasUniquePopupMenu, DonatesMenu, SimpleTraits<LaneLabelLayer>{

	/**the list of which text items correspond to which panel*/
	//private HashMap<ZoomableGraphic, TextGraphic> records=new  HashMap<ZoomableGraphic, TextGraphic>();

	/**the pattern that the labels take*/
	TextPattern textPattern=new  TextPattern(TextPattern.PatternType.ABC);
	
	
	
	boolean continuouseUpdate=true;
	
	
	/**
	 * @param name
	 */
	public LaneLabelLayer() {
		super("Lane labels");
	}
	
	
	/**creates a single duplicate*/
	public LaneLabelLayer copy() {
		LaneLabelLayer output = new LaneLabelLayer();
		giveTraitsTo(output);
		
		return output;
	}


	/**
	 * @param output
	 */
	public void giveTraitsTo(LaneLabelLayer output) {
		output.continuouseUpdate=this.continuouseUpdate;
		output.textPattern=this.textPattern.copy();
	}
	

	@MenuItemMethod(menuActionCommand = "Select All", menuText ="Select All", orderRank=10)
	public void selectAll() {
		for(ZoomableGraphic g: this.getAllGraphics()) {
			if(g instanceof TextGraphic) {
				((TextGraphic) g).select();
			}
		}
	}
	
	/**Shows a modal options dialog for the item, and returns an undoable edit*/
	@MenuItemMethod(menuActionCommand = "duplicate_lane_labels", menuText ="Copy lane labels", orderRank=4)
	public UndoAddItem showAddLaneLabels() {
		ZoomableGraphic addition = new LaneLabelCopy().add(this);
		return new UndoAddItem(addition.getParentLayer(), addition);
	
	}
	
	
	public boolean isContinuouseUpdate() {
		return continuouseUpdate;
	}

	public void setContinuouseUpdate(boolean continuouseUpdate) {
		this.continuouseUpdate = continuouseUpdate;
	}
	
	
	
	@Override
	public LaneLabelLayer self() {
		return this;
	}


	private static final long serialVersionUID = 1L;


	@Override
	public JMenu getDonatedMenuFor(Object requestor) {
		if(requestor instanceof TextGraphic)
		{
			JMenu jMenu = new MenuItemExecuter(this).getJMenu();
			jMenu.setText("Lane labels");
			return jMenu;
		}
		
		return null;
	}


	@Override
	public PopupMenuSupplier getMenuSupplier() {
		
		return (new PopupMenuSupplier() {

			@Override
			public JPopupMenu getJPopup() {
				
				return new JPopupMenu();
			}

			
	});

}
	
}	

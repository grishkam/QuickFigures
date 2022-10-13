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
 * Date Modified: Oct 13, 2022
 * Version: 2022.1
 */
package popupMenusForComplexObjects;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.undo.AbstractUndoableEdit;

import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import locatedObject.LocatedObject2D;
import locatedObject.ScalededItem;
import logging.IssueLog;
import locatedObject.AttachedItemList;
import menuUtil.SmartPopupJMenu;
import messages.ShowMessage;
import multiChannelFigureUI.ChannelPanelEditingMenu;
import popupMenusForComplexObjects.MenuForMultiChannelDisplayLayer.SmartMenuItem2;
import sUnsortedDialogs.ScaleSettingDialog;
import menuUtil.BasicSmartMenuItem;
import menuUtil.PopupMenuSupplier;
import menuUtil.SmartJMenu;
import undo.AbstractUndoableEdit2;
import undo.CombinedEdit;
import undo.UndoAddOrRemoveAttachedItem;
import utilityClasses1.ArraySorter;

/**A popup menu for a scale bar*/
public class BarGraphicMenu extends SmartPopupJMenu implements ActionListener,
PopupMenuSupplier  {

	/**
	 * 
	 */
	
	static final String OPTIONS_DIALOG="Options", HIDE_TEXT="Hide Text", SHOW_TEXT="Show Text";//, backGroundShap="Outline Shape";
	
	BarGraphic barG;
	public BarGraphicMenu(BarGraphic textG) {
		super();
		this.barG = textG;
		add(createItem(OPTIONS_DIALOG));
		TextGraphicMenu textGraphicMenu = new TextGraphicMenu(textG.getBarText());		
		JMenu jMenu = textGraphicMenu.getJMenu("Bar Text");
		if (barG.isShowText())
			jMenu.add(createItem(HIDE_TEXT));
		else 
			jMenu.add(createItem(SHOW_TEXT));
		add(jMenu);
		add(new SwitchBarToOtherPanelMenu());
		
		
		addScaleChangeMenuItem();
		
	}

	/**
	 * adds a scale change menu item. This is a rough draft
	 */
	public void addScaleChangeMenuItem() {
		SmartJMenu unitsMenu = new SmartJMenu("units");
		BasicSmartMenuItem out=new UnitChangeMenuItem("Set Pixel Size (Set Scale...)") ;
		unitsMenu.add(out);
		this.add(unitsMenu);
	}
	
	
	class UnitChangeMenuItem extends  BasicSmartMenuItem {
		private static final long serialVersionUID = 1L;
		/**
		 * @param string
		 */
		public UnitChangeMenuItem(String string) {
			super(string);
		}
		@Override
		public AbstractUndoableEdit2 performAction() {
			ImagePanelGraphic sp = findImagePanel(barG);
			double oWidth= barG.getBarWidthBasedOnUnits();
			
			if(sp instanceof ImagePanelGraphic) {
				ChannelPanelEditingMenu cpem = new ChannelPanelEditingMenu((ImagePanelGraphic) sp);
				MultichannelDisplayLayer presseddisplay = cpem.getPresseddisplay();
				if(presseddisplay==null) {
					ShowMessage.showOptionalMessage("could not identify source image");
					return null;
				}
				SetImagePixelSize dialog = new SetImagePixelSize(presseddisplay);
				AbstractUndoableEdit2 edit2 = barG.provideUndoForDialog();
				
				ScaleSettingDialog showPixelSizeSetDialog = dialog. showPixelSizeSetDialog();
				
				/**What to do if the bar becomes too small to see*/
				sp.updateBarScale();
				double fWidth= barG.getBarWidthBasedOnUnits();
				
				//if(fWidth<5||fWidth>300) {
					double factor = 10.0;
					if(fWidth>0) {
						factor=oWidth/fWidth;
					}
					barG.setLengthInUnits((int) (barG.getLengthInUnits()*factor));
					
				//}
				CombinedEdit cEdit = new CombinedEdit(showPixelSizeSetDialog.getUndo(), edit2);
				return cEdit;
			} else {
				ShowMessage.showOptionalMessage("the image panel does not appear to be connected to a source image");
				return null;
			}
		}
	}
	
	/**temporary method to find the image that the bar is attached to 
	 * @param barG2
	 * @return
	 */
	protected ImagePanelGraphic findImagePanel(BarGraphic barG2) {
		for(ZoomableGraphic item: barG2.getParentLayer().getTopLevelParentLayer().getAllGraphics()) {
			if(item instanceof ImagePanelGraphic) {
				BarGraphic bar = ((ImagePanelGraphic) item).getScaleBar();
				if(bar==barG2)
					return (ImagePanelGraphic) item;
			}
		}
		return null;
	}

	public JMenuItem createItem(String st) {
		JMenuItem o=new JMenuItem(st);
		o.addActionListener(this);
		o.setActionCommand(st);
		
		return o;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public JPopupMenu getJPopup() {
		return this;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		String com=arg0.getActionCommand();
		
		if (com.equals(OPTIONS_DIALOG)) {
			barG.showOptionsDialog();
		}
		else {
		
			
				if (com.equals(HIDE_TEXT)) {
					barG.setShowText(false);
				}
				if (com.equals(SHOW_TEXT)) {
					barG.setShowText(true);
				}
		
		}
		
	}
	
	/**a menu that switches the bar location to another panel*/
	class SwitchBarToOtherPanelMenu extends SelectItemJMenu {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		SwitchBarToOtherPanelMenu() {
			super("Switch Panels");
			ArrayList<ZoomableGraphic> localItems = barG.getParentLayer().getAllGraphics();
			ArraySorter.removeThoseNotOfClass(localItems, ImagePanelGraphic.class);//a bar graphic can only be attached to an image panel
			createMenuItemsForList2(localItems);
		}
		
		/**Adds bar to selected ImagePanel
		 * @return */
		@Override
		public AbstractUndoableEdit performAction(LocatedObject2D target) {
			ImagePanelGraphic image=(ImagePanelGraphic) target;
			CombinedEdit output = new CombinedEdit();
			
			AttachedItemList.removeFromAlltakers( barG, barG.getParentLayer().getTopLevelParentLayer().getAllGraphics(), output);
			
			UndoAddOrRemoveAttachedItem undo = new UndoAddOrRemoveAttachedItem(image, barG, false);
	
			image.addLockedItem(barG);//does the deed
			
			undo.establishFinalState();
			output.addEditToList(undo);
			image.updateDisplay();
			return output;
		}
		}
	
}

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
 * Version: 2022.0
 */
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import channelLabels.ChannelLabelTextGraphic;
import fLexibleUIKit.ObjectAction;
import figureOrganizer.FigureLabelOrganizer.ColumnLabelTextGraphic;
import figureOrganizer.FigureLabelOrganizer.PanelLabelTextGraphic;
import figureOrganizer.FigureLabelOrganizer.RowLabelTextGraphic;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.PanelManager;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.BarGraphic.BarTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import menuUtil.PopupMenuSupplier;
import menuUtil.SmartPopupJMenu;
import messages.ShowMessage;
import objectDialogs.TextInsetsDialog;
import undo.AbstractUndoableEdit2;
import undo.UndoAddItem;

/**A menu for text graphics*/
public class TextGraphicMenu extends SmartPopupJMenu implements ActionListener,
PopupMenuSupplier  {

	/**
	 * 
	 */
	
	static final String TEXT_INSETS="Inset text", OPTIONS_DIALOG="Text Options", FORMAT_BACKGROUND="Fill Background With Colored Shape", DUPLICATE_TEXT="Duplicate", EDIT_MODE="Enter Text Edit Move";
	
	TextGraphic textG;

	private boolean excludeExpertOptions=true;
	
	public TextGraphicMenu(TextGraphic textG) {
		super();
		this.textG = textG;
		for(JMenuItem i: getItems() ) {add(i);}
		DonatesMenu.MenuFinder.addDonatedMenusTo(this, textG);
	}
	
	public ArrayList<JMenuItem> getItems() {
		ArrayList<JMenuItem> jm=new ArrayList<JMenuItem>();
		jm.add(createItem(OPTIONS_DIALOG));
		//if (textG!=null && textG.isSelected())jm.add(createItem(EDIT_MODE));
		addExpertOptions(jm);
		jm.add(createDuplicatorAction(false).createJMenuItem("Duplicate"));
		
		
		FigureOrganizingLayerPane f = FigureOrganizingLayerPane.findFigureOrganizer(textG);
		if(f!=null && !(textG instanceof ChannelLabelTextGraphic)) {
			EditLabels menuItem = f.getMenuSupplier().getLabelEditorMenuItemFor(textG);
			if (menuItem==null ) 
				menuItem=new EditLabels(textG);//TODO: determine if this is ever called
			if (menuItem!=null)
				jm.add(menuItem);
			
		}
		
		return jm;
	}

	/**an action that duplicates a text item
	 * @return
	 */
	public ObjectAction<TextGraphic> createDuplicatorAction(boolean rowLabel) {
		return new ObjectAction<TextGraphic>(textG) {

			public AbstractUndoableEdit2  performAction() {
				TextGraphic c = textG.copy();
				GraphicLayer targetLayer = textG.getParentLayer();
				c.moveLocation(5, 2);
				
				
				
				
				/**Determines if the original is attached to a layout and attaches a duplicate*/
				DefaultLayoutGraphic layout = PanelManager.getGridLayout(textG.getParentLayer());
				if(layout!=null&&layout.hasLockedItem(textG)) {
					
					boolean isARowLabel = textG instanceof RowLabelTextGraphic;
					boolean isAColLabel = textG instanceof ColumnLabelTextGraphic;
					boolean isAPanelLabel= textG instanceof PanelLabelTextGraphic;
					
						int increment = 1;
						if(layout.getPanelLayout().rowmajor&&isARowLabel ) 
							increment=layout.getPanelLayout().nColumns();
						if(!layout.getPanelLayout().rowmajor&&isAColLabel ) 
							increment=layout.getPanelLayout().nRows();
						int panelIndex = layout.getPanelForObject(textG)+increment;
						c.getTagHashMap().put("Index", panelIndex);
						layout.addLockedItem(c);
						layout.snapLockedItem(c);
						if(textG.getTag("Index")==null) {
							c.getTagHashMap().put("Index", null);
						}
					
					c.setAttachmentPosition(textG.getAttachmentPosition());
					ShowMessage.showOptionalMessage("Label was attached to layout", true, "Label started out attached to layout", "A duplicate is now attached to the layout and placed in next row, coloumn or panel", "You have the option to release the attached label in the Attachment submenu (right click)");
				}
				
				if(targetLayer==null) return null;
				targetLayer.add(c);
				
				return new UndoAddItem(targetLayer, c);
				
			}};
	}


	/**
	 * @param jm
	 */
	void addExpertOptions(ArrayList<JMenuItem> jm) {
		if(excludeExpertOptions) return;
		jm.add(createItem(TEXT_INSETS));
		jm.add(createItem(FORMAT_BACKGROUND));
	}
	
	public JMenu getJMenu(String st) {
		JMenu out=new JMenu(st);
		for(JMenuItem i: getItems() ) {out.add(i);}
		return out;
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
		if (com.equals(FORMAT_BACKGROUND)) {
			textG.getBackGroundShape().showOptionsDialog();
		}
		if (com.equals(OPTIONS_DIALOG)) {
			textG.showOptionsDialog();
		}
		if (com.equals(TEXT_INSETS)) {
			TextInsetsDialog id = new TextInsetsDialog(textG);
			id.showDialog();
		}
		
		
		if (com.equals(EDIT_MODE)&& !(textG instanceof BarTextGraphic)) {
			ShowMessage.showOptionalMessage("There is a better way", true, "You can enter text edit move by double cliking on a text item");
			textG.select();
			textG.setEditMode(true);
			
		}
	}
	
	
	
}

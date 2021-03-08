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
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fLexibleUIKit.ObjectAction;
import figureOrganizer.FigureOrganizingLayerPane;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.TextGraphic;
import menuUtil.SmartPopupJMenu;
import menuUtil.PopupMenuSupplier;
import objectDialogs.TextInsetsDialog;
import undo.UndoAddItem;

/**A menu for text graphics*/
public class TextGraphicMenu extends SmartPopupJMenu implements ActionListener,
PopupMenuSupplier  {

	/**
	 * 
	 */
	
	static final String TEXT_INSETS="Inset text", OPTIONS_DIALOG="Text Options", FORMAT_BACKGROUND="Fill Background With Colored Shape", DUPLICATE_TEXT="Duplicate";
	
	TextGraphic textG;

	private boolean excludeExpertOptions=true;
	
	public TextGraphicMenu(TextGraphic textG) {
		super();
		this.textG = textG;
		for(JMenuItem i: getItems() ) {add(i);}
	}
	
	public ArrayList<JMenuItem> getItems() {
		ArrayList<JMenuItem> jm=new ArrayList<JMenuItem>();
		jm.add(createItem(OPTIONS_DIALOG));
		addExpertOptions(jm);
		jm.add(createDuplicatorAction(false).createJMenuItem("Duplicate"));
		
		FigureOrganizingLayerPane f = FigureOrganizingLayerPane.findFigureOrganizer(textG);
		if(f!=null) {
			EditLabels menuItem = f.getMenuSupplier().getLabelEditorMenuItemFor(textG);
			if (menuItem==null ) menuItem=new EditLabels(textG);
			if (menuItem!=null)jm.add(menuItem);
			
		}
		
		return jm;
	}

	/**an action that duplicates a text item
	 * @return
	 */
	public ObjectAction<TextGraphic> createDuplicatorAction(boolean rowLabel) {
		return new ObjectAction<TextGraphic>(textG) {

			public void actionPerformed(ActionEvent e) {
				TextGraphic c = textG.copy();
				c.moveLocation(5, 2);
				
				
				
				GraphicLayer targetLayer = textG.getParentLayer();
				if(targetLayer==null) return;
				targetLayer.add(c);
				this.addUndo(new UndoAddItem(targetLayer, c));
				
				
				
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
	}
	
	
	
}

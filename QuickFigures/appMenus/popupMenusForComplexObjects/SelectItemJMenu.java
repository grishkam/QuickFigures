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
 * Version: 2023.2
 */
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JMenuItem;
import javax.swing.undo.AbstractUndoableEdit;

import layersGUI.HasTreeLeafIcon;
import layout.BasicObjectListHandler;
import locatedObject.LocatedObject2D;
import menuUtil.SmartJMenu;
import undo.CombinedEdit;

/**A JMenu that that displays the names of many objects that the user may chose
 * @seeLocatedObject2D the types of objects*/
public abstract class SelectItemJMenu extends SmartJMenu  implements ActionListener {
	protected ArrayList<LocatedObject2D> o=new ArrayList<LocatedObject2D>();
	protected ArrayList<JMenuItem> oi=new ArrayList<JMenuItem>();;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SelectItemJMenu(String st) {
		super(st);
	}

	/**When given a list of objects, generates a set of menu items with the object names and 
	  tree icons*/
	public void createMenuItemsForList(ArrayList<LocatedObject2D> arr) {
		for(LocatedObject2D l: arr) {
			if (l==null) continue;
			JMenuItem menuitem = new JMenuItem(l.toString()) ;
			menuitem.addActionListener(this);
			this.add(menuitem);
			if (l instanceof HasTreeLeafIcon) {
				HasTreeLeafIcon i=(HasTreeLeafIcon) l;
				menuitem.setIcon(i.getTreeIcon());
			}
			o.add(l);
			oi.add(menuitem);
		}
	}
	
	public void createMenuItemsForList2(Collection<?> localItems) {
		createMenuItemsForList(BasicObjectListHandler.getAs2DObjects( localItems));
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		int index = oi.indexOf(arg0.getSource());
		LocatedObject2D itemtoremove = o.get(index);
		addUndo(performAction(itemtoremove));
	}
	
	/**A menu item for */
	protected class ComplexAdder extends JMenuItem implements ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ArrayList<LocatedObject2D> list;
		
		public ComplexAdder(String name, ArrayList<LocatedObject2D> list) {
			this.setText(name);
			this.setActionCommand(name);
			this.list=list;
			this.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			CombinedEdit undo = new CombinedEdit();
			for(LocatedObject2D l:list) {
				undo.addEditToList(performAction(l));
			}
			addUndo(undo);
		}}
	

	
	public abstract AbstractUndoableEdit performAction(LocatedObject2D target) ;
}

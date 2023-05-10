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

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fLexibleUIKit.MenuItemExecuter;
import locatedObject.AttachedItemList;
import locatedObject.TakesAttachedItems;
import menuUtil.SmartPopupJMenu;
import utilityClasses1.ArraySorter;
import menuUtil.PopupMenuSupplier;

/**A menu for items that function as attachment sites for other objects
 * @see TakesAttachedItems
 * @see AttachedItemList*/
public class AttachedItemMenu extends SmartPopupJMenu implements ActionListener, PopupMenuSupplier{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TakesAttachedItems lockedItem;
	JMenuItem snap=new JMenuItem("Snap Locked Items"); {snap.addActionListener(this);}
	JMenuItem release=new JMenuItem("Release All Locked Items");
	private AttachedItemList list;
	
	
	public AttachedItemMenu() {}
	public AttachedItemMenu(TakesAttachedItems tak, AttachedItemList list) {
		
		setLockedItem(tak);
		this.setList(list);
		ArraySorter.removeDeadItems(tak.getLockedItems()) ;
		addLockedItemMenus();
	}
	
	public void addLockedItemMenus() {
			//this.add(snap);
			if (list==null) list=getLockedItemTaker().getLockedItems();
		ReleaseLockedMenu removeLockedItemenu = new ReleaseLockedMenu(getLockedItemTaker(), list);
		if (list.size()>0) add(removeLockedItemenu);
		JMenu men = new MenuItemExecuter(getLockedItemTaker()).getJMenu();
		men.setText("Other Options");
		if (men.getItemCount()>0) this.add(men);
		try {
			AddLockedMenu addLockedItemMenu = new AddLockedMenu(getLockedItemTaker());
			if (addLockedItemMenu.getNItems()>0)add(addLockedItemMenu);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	@Override
	public JPopupMenu getJPopup() {
		return this;
	}

	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource()==snap) getLockedItemTaker().snapLockedItems();
		
		
	}
	public TakesAttachedItems getLockedItemTaker() {
		return lockedItem;
	}
	public void setLockedItem(TakesAttachedItems lockedItem) {
		this.lockedItem = lockedItem;
	}
	public AttachedItemList getList() {
		return list;
	}
	public void setList(AttachedItemList list) {
		this.list = list;
	}

}

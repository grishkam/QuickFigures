/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package fLexibleUIKit;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.undo.UndoableEdit;

import graphicActionToolbar.CurrentFigureSet;
import menuUtil.SmartJMenu;
import menuUtil.SmartPopupJMenu;
import menuUtil.MenuSupplier;
import undo.UndoManagerPlus;
import utilityClassesForObjects.RectangleEdges;

/**this will generate working popup menus from the annotated methods in
  an object. I wrote it because I wanted to sometimes write the 
  code for popup menus with a little less complexity each time*/
public class MenuItemExecuter implements ActionListener, MenuSupplier {
	private Object o;

	private HashMap<MenuItemMethod, Method> map=new HashMap<MenuItemMethod, Method>();
	private HashMap<String, Method> mapComms=new HashMap<String, Method>();
	private MenuSupplier partner=null;

	private SmartPopupJMenu popupMenu;
	private UndoManagerPlus undoManager;
	
	public MenuItemExecuter(Object o) {
		this.o=o;
		innitiallizeMap();
	}

	public ArrayList<MenuItem> findItems() {
		//this.o=o;
		
		ArrayList<MenuItem> output = new ArrayList<MenuItem>();
		for(MenuItemMethod k: map.keySet()) {
			output.add(generateMenuItemFrom(k, map.get(k)));
		}	return output;
	}
	
	
	public ArrayList<JMenuItem> findJItems() {
		return findJItems(null);
	}
	public ArrayList<JMenuItem> findJItems(JMenu menu) {
		//this.o=o;
		Set<MenuItemMethod> keySet = map.keySet();
		
		/**need a sorted list*/
		ArrayList<MenuItemMethod> allKeys= new ArrayList<MenuItemMethod>();
		allKeys.addAll(keySet);
		
		Collections.sort(allKeys, new Comparator<MenuItemMethod>() {

			@Override
			public int compare(MenuItemMethod arg0, MenuItemMethod arg1) {
				return arg0.orderRank()-arg1.orderRank();
			}});
		
		
		HashMap<String, JMenu> submenus=new HashMap<String, JMenu>();
		ArrayList<JMenuItem> output = new ArrayList<JMenuItem>();
		for(MenuItemMethod k: allKeys ) {
			JMenuItem item = generateJMenuItemFrom(k, map.get(k));
			
			
			if (!k.permissionMethod().equals("")) try{
				Method pMethod = o.getClass().getMethod(k.permissionMethod());
				if (pMethod!=null) {
					Object b = pMethod.invoke(o);
				/**will not include this menu item in the list if the permission method is null or false*/
					if (b==null||b.toString().equals("false")) continue;
					
					
				}
				
			}catch (Throwable t) {t.printStackTrace();}
			
			
			
			if (k.subMenuName().equals(""))
				output.add(item);
			else {
				
				String submenuName = k.subMenuName();
				if (submenuName.contains("<")) submenuName=submenuName.split("<")[0];//if divideed, we need to put the first submenu into the hashmap
		
				JMenu submen = submenus.get(submenuName);
				if (submen==null) {
						{
								submen=new JMenu(submenuName);
								submenus.put(submenuName, submen);
								output.add(submen);
						}
				}
				
				if (k.subMenuName().contains("<"))
						submen=SmartJMenu. getOrCreateSubmenuFromPath(submen, k.subMenuName().split("<"), 1);
				
				submen.add(item);
			}
		}	
		
		if(this.getPartner()!=null) {
			output.addAll(getPartner().findJItems());
		}
		
		return output;
	}
	
	public JPopupMenu getJPopup() {
		SmartPopupJMenu p=new SmartPopupJMenu();
		ArrayList<JMenuItem> arr = findJItems();
		for(JMenuItem a:arr) {
			p.add(a);
			}
		this.popupMenu=p;
		return p;
	}
	
	public JMenu getJMenu() {
		JMenu p=new SmartJMenu("");
		addToJMenu(p);
		
		return p;
	}

	/**
	 * @param p
	 */
	public void addToJMenu(JMenu p) {
		ArrayList<JMenuItem> arr = findJItems();
		for(JMenuItem a:arr) {p.add(a);}
	}
	
	
	private void innitiallizeMap() {
		map.clear();
		mapComms.clear();
		if(o==null) return;
		Method[] methods = o.getClass().getMethods();
		for(Method m:methods) {
		MenuItemMethod anns = m.getAnnotation(MenuItemMethod.class);
		if(anns==null ) continue;
			map.put(anns, m);
			mapComms.put(anns.menuActionCommand(), m);
		}
	}
	
	public MenuItem generateMenuItemFrom(MenuItemMethod anns, Method m) {
		MenuItem mi = new MenuItem(anns.menuText());
		mi.addActionListener(this);
		mi.setActionCommand(anns.menuActionCommand());
		return mi;
	}
	public JMenuItem generateJMenuItemFrom(MenuItemMethod anns, Method m) {
		JMenuItem mi = new JMenuItem(anns.menuText());
		mi.addActionListener(this);
		mi.setActionCommand(anns.menuActionCommand());
		return mi;
	}
	
	

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Method m = mapComms.get(arg0.getActionCommand());
		try {
			
			Object item = m.invoke(o, new Object[] {});
			if (getUndoManager()!=null &&item instanceof UndoableEdit) {
				getUndoManager().addEdit((UndoableEdit) item);
			}
			
			new CurrentFigureSet().getCurrentlyActiveDisplay().updateDisplay();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@MenuItemMethod(menuActionCommand = RectangleEdges.RIGHT_SIDE_BOTTOM+"", menuText = "")
	void go() {}



	public MenuSupplier getPartner() {
		return partner;
	}

	public void setPartner(MenuSupplier partner) {
		this.partner = partner;
	}
	
	public UndoManagerPlus getUndoManager() {
		if (undoManager==null & popupMenu!=null && this.popupMenu.getUndoManager()!=null) {
			this.undoManager=popupMenu.getUndoManager();
		}
		return undoManager;
	}
}

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
 * Date Modified: Dec 17, 2021
 * Version: 2021.2
 */
package fLexibleUIKit;

import java.awt.Color;
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
import iconGraphicalObjects.CheckBoxIcon;
import iconGraphicalObjects.ColorIcon;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import menuUtil.SmartPopupJMenu;
import menuUtil.BasicSmartMenuItem;
import menuUtil.MenuSupplier;
import undo.UndoManagerPlus;

/**this will generate working popup menus from the annotated methods in
  an object. It allows a programmer to build complex popup menus with a little less complexity each time*/
public class MenuItemExecuter implements ActionListener, MenuSupplier {
	
	/**Object containing the method calls that are the basis of menu items*/
	private Object sourceObject;

	/**Maps that link method calls to items within this menu */
	private HashMap<MenuItemMethod, Method> map=new HashMap<MenuItemMethod, Method>();
	private HashMap<String, Method> mapComms=new HashMap<String, Method>();
	
	private MenuSupplier partner=null;

	private SmartPopupJMenu popupMenu;
	private SmartJMenu theSmartMenu;
	
	private UndoManagerPlus undoManager;

	
	
	/**creates a menu item execuer for the object*/
	public MenuItemExecuter(Object o) {
		this.sourceObject=o;
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
	
	/**builds a list of JMenu items*/
	public ArrayList<JMenuItem> findJItems(JMenu menu) {
		//this.o=o;
		ArrayList<MenuItemMethod> allKeys = getOrderedListOfMenuMethods();
		
		
		HashMap<String, JMenu> submenus=new HashMap<String, JMenu>();
		ArrayList<JMenuItem> output = new ArrayList<JMenuItem>();
		for(MenuItemMethod k: allKeys ) {
			JMenuItem item = generateJMenuItemFrom(k, map.get(k));
			
			
			if (!k.permissionMethod().equals("")) try{
				Method pMethod = sourceObject.getClass().getMethod(k.permissionMethod());
				if (pMethod!=null) {
					Object b = pMethod.invoke(sourceObject);
				/**will not include this menu item in the list if the permission method is null or false*/
					if (b==null||b.toString().equals("false")) continue;
					
					
				}
				
			}catch (Throwable t) {IssueLog.log(t);}
			
			
			/**sets up the icon*/
			if (!k.iconMethod().equals("")) try{
				Method pMethod = sourceObject.getClass().getMethod(k.iconMethod());
				if (pMethod!=null) {
					Object b = pMethod.invoke(sourceObject);
				
					if (b==null||b.toString().equals("false")) {
						item.setIcon(new CheckBoxIcon(Color.black, false));
					};
					if(b.toString().equals("true")) {
						item.setIcon(new CheckBoxIcon(Color.black, true));
					}
					
					
				}
				
				
			}catch (Throwable t) {IssueLog.log(t);}
			
			
			/**determines whether to add the item to the main menu or find/create a submenu*/
			if (k.subMenuName().equals(""))
				output.add(item);
			else {
				
				String submenuName = k.subMenuName();
				if (submenuName.contains("<")) submenuName=submenuName.split("<")[0];//if divideed, we need to put the first submenu into the hashmap
		
				JMenu targetSubmenu = submenus.get(submenuName);
				if (targetSubmenu==null) {
						{
								targetSubmenu=new SmartJMenu(submenuName);
								submenus.put(submenuName, targetSubmenu);
								output.add(targetSubmenu);
						}
				}
				
				if (k.subMenuName().contains("<"))
						targetSubmenu=SmartJMenu. getOrCreateSubmenuFromPath(targetSubmenu, k.subMenuName().split("<"), 1);
				
				targetSubmenu.add(item);
			}
		}	
		
		if(this.getPartner()!=null) {
			output.addAll(getPartner().findJItems());
		}
		
		return output;
	}

	/**organizes the list of menu item methods into their specified order and returns the result
	 * @return
	 */
	public ArrayList<MenuItemMethod> getOrderedListOfMenuMethods() {
		Set<MenuItemMethod> keySet = map.keySet();
		
		/**need a sorted list*/
		ArrayList<MenuItemMethod> allKeys= new ArrayList<MenuItemMethod>();
		allKeys.addAll(keySet);
		
		Collections.sort(allKeys, new Comparator<MenuItemMethod>() {

			@Override
			public int compare(MenuItemMethod arg0, MenuItemMethod arg1) {
				return arg0.orderRank()-arg1.orderRank();
			}});
		return allKeys;
	}
	
	/**creates a j popup menu*/
	public JPopupMenu getJPopup() {
		SmartPopupJMenu p=new SmartPopupJMenu();
		ArrayList<JMenuItem> arr = findJItems();
		for(JMenuItem a:arr) {
			p.add(a);
			}
		this.popupMenu=p;
		return p;
	}
	
	/**Creates a JMenu*/
	public JMenu getJMenu() {
		SmartJMenu p=new SmartJMenu("");
		addToJMenu(p);
		theSmartMenu=p;
		return p;
	}

	/**Adds all the items to the target j menu
	 * @param p
	 */
	public void addToJMenu(SmartJMenu p) {
		ArrayList<JMenuItem> arr = findJItems();
		for(JMenuItem a:arr) {p.add(a);}
		this.theSmartMenu=p;
	}
	
	/**sets up a map linking a list of action commands to to specific method calls within the class*/
	private void innitiallizeMap() {
		map.clear();
		mapComms.clear();
		if(sourceObject==null) return;
		Method[] methods = sourceObject.getClass().getMethods();
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
	
	/**Creates a JMenu item for invoking the method call*/
	public JMenuItem generateJMenuItemFrom(MenuItemMethod anns, Method m) {
		JMenuItem mi = new BasicSmartMenuItem(anns.menuText());
		mi.addActionListener(this);
		mi.setActionCommand(anns.menuActionCommand());
		return mi;
	}
	
	

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Method m = mapComms.get(arg0.getActionCommand());
		try {
			
			Object item = m.invoke(sourceObject, new Object[] {});
			UndoManagerPlus manager1 = getUndoManager();
			if (manager1!=null &&item instanceof UndoableEdit) {
				manager1.addEdit((UndoableEdit) item);
			}
			
			new CurrentFigureSet().getCurrentlyActiveDisplay().updateDisplay();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	



	public MenuSupplier getPartner() {
		return partner;
	}

	public void setPartner(MenuSupplier partner) {
		this.partner = partner;
	}
	
	/**returns the undo manager that will be used to  */
	public UndoManagerPlus getUndoManager() {
		/**first determines if this executor is using a popup menu that knows its undo manager*/
		if (undoManager==null & popupMenu!=null && this.popupMenu.getUndoManager()!=null) {
			this.undoManager=popupMenu.getUndoManager();
		}
		/**next determines if this executor is using a normalmenu that knows its undo manager*/
		if (undoManager==null & theSmartMenu!=null && this.theSmartMenu.getUndoManager()!=null) {
			this.undoManager=theSmartMenu.getUndoManager();
		}
		return undoManager;
	}
}

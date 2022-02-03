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
 * Date Modified: Jan 31, 2022
 * Version: 2022.0
 */
package fLexibleUIKit;

import java.awt.Color;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
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

import applicationAdapters.CanvasMouseEvent;
import graphicActionToolbar.CurrentFigureSet;
import iconGraphicalObjects.CheckBoxIcon;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import menuUtil.SmartPopupJMenu;
import messages.ShowMessage;
import menuUtil.BasicSmartMenuItem;
import menuUtil.MenuSupplier;
import undo.UndoManagerPlus;

/**this will generate working popup menus from the annotated methods in
  an object. It allows a programmer to build complex popup menus with a little less complexity each time*/
public class MenuItemExecuter implements  MenuSupplier {
	
	/**Object containing the method calls that are the basis of menu items*/
	private Object sourceObject;

	/**Maps that link method calls to items within this menu */
	private HashMap<MenuItemMethod, Method> map=new HashMap<MenuItemMethod, Method>();
	
	private MenuSupplier partner=null;

	private SmartPopupJMenu popupMenu;
	private SmartJMenu theSmartMenu;
	
	private UndoManagerPlus undoManager;

	
	
	/**creates a menu item execuer for the object*/
	public MenuItemExecuter(Object o) {
		this.sourceObject=o;
		innitiallizeMap();
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
			JMenuItem[] items = generateJMenuItemFrom(k, map.get(k));
			
			for(JMenuItem item : items)
			addItemToList(submenus, output, k, item);
		}	
		
		if(this.getPartner()!=null) {
			output.addAll(getPartner().findJItems());
		}
		
		return output;
	}

	/**Adds the given J meny item to the list
	 * @param submenus a map that keeps track of every submenu
	 * @param output the list of JMenu items
	 * @param k the annotation that describes where the menu item should be placed
	 * @param item
	 */
	public void addItemToList(HashMap<String, JMenu> submenus, ArrayList<JMenuItem> output, MenuItemMethod k,
			JMenuItem item) {
		if (!k.permissionMethod().equals("")) try{
			Method pMethod = sourceObject.getClass().getMethod(k.permissionMethod());
			if (pMethod!=null) {
				Object b = pMethod.invoke(sourceObject);
			/**will not include this menu item in the list if the permission method is null or false*/
				if (b==null||b.toString().equals("false"))
					return;
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




	/**
	 * @param k
	 * @param item
	 * @param trueCheckBoxObject 
	 */
	public void setupIcon(MenuItemMethod k, JMenuItem item, Object trueCheckBoxObject) {
		/**sets up the icon*/
		if (!k.iconMethod().equals("")) try{
			Method pMethod = sourceObject.getClass().getMethod(k.iconMethod());
			if (pMethod!=null) {
				Object b = pMethod.invoke(sourceObject);
			
				if (b==null||b.toString().equals("false")||trueCheckBoxObject!=b) {
					item.setIcon(new CheckBoxIcon(Color.black, false));
				};
				if(b.toString().equals("true")||b==trueCheckBoxObject) {
					item.setIcon(new CheckBoxIcon(Color.black, true));
				}
				
				
			}
			
			
		}catch (Throwable t) {IssueLog.log(t);}
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
		addToJPopupMenu(p);
		this.popupMenu=p;
		return p;
	}

	/**adds the menu items for this executor to the JPopup menu
	 * @param p
	 */
	public void addToJPopupMenu(SmartPopupJMenu p) {
		ArrayList<JMenuItem> arr = findJItems();
		for(JMenuItem a:arr) {
			p.add(a);
			}
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
		
		if(sourceObject==null) return;
		Method[] methods = sourceObject.getClass().getMethods();
		for(Method m:methods) {
		MenuItemMethod anns = m.getAnnotation(MenuItemMethod.class);
		if(anns==null ) continue;
			map.put(anns, m);
			
		}
	}
	

	
	/**Creates a JMenu item(s) for invoking the method call*/
	public JMenuItem[] generateJMenuItemFrom(MenuItemMethod anns, Method m) {
		
		/**if the method returns its own JMenu item (that item would handle adding the undo)*/
		if(isSubclass(m.getReturnType(),SmartJMenu.class)) try {
			m.getReturnType().getSuperclass();
			return new JMenuItem[] {(JMenuItem) m.invoke(sourceObject, new Object[] {})};
		} catch (Throwable t) {
			IssueLog.log(t);
		}
		
		String suffix="";
		
		Annotation[][] parameternotes = m.getParameterAnnotations();
		
		/**if the method takes an enum argument, then this will create a series of versions with each possible enum*/
		if(m.getParameterCount()==1&&m.getParameterTypes()[0].isEnum()) {
			
			Object[] eConstants=m.getParameterTypes()[0].getEnumConstants();
			JMenuItem[] output=new JMenuItem[eConstants.length] ;
			int i=0;
			for(Object constant: eConstants) {
				String menuText = anns.menuText()+" "+enumNameToText(constant);
				if(anns.menuText().contains("ENUM"))
					menuText=anns.menuText().replace("ENUM", enumNameToText(constant));
				
				JMenuItem mi = new BasicSmartMenuItem(menuText);
				mi.addActionListener(new MenuItemListener(m, new Object[] {constant}));
				setupIcon(anns, mi,constant);
				output[i]=mi;
				i++;
			}
			return output;
		}
		else 
		
		{
			JMenuItem mi = new BasicSmartMenuItem(anns.menuText()+suffix);
			mi.addActionListener(new MenuItemListener(m, new Object[] {}));
			mi.setActionCommand(anns.menuActionCommand());
			setupIcon(anns, mi,null);
			return new JMenuItem[] {mi};
		}
		
		
		
	}




	/**Returns true if the first class is a subclass of the second
	 * @param possibleSubclassType
	 * @param class1
	 * @return
	 */
	private boolean isSubclass(Class<?> possibleSubclassType, Class<SmartJMenu> class1) {
		while(possibleSubclassType!=null&&possibleSubclassType.getSuperclass()!=Object.class) {
			if(class1.equals(possibleSubclassType))
				return true;
			possibleSubclassType=possibleSubclassType.getSuperclass();
		}
		return false;
	}




	/**returns text that reflects the enum name
	 * @param constant
	 * @return
	 */
	public String enumNameToText(Object constant) {
		return constant.toString().toLowerCase().replace("_", " ");
	}
	
	/**An action listener that calls an item*/
	class MenuItemListener implements ActionListener {
		
			private Method targetMethod;
			private Object[] arguments;

			/**
		 * @param m
		 */
		public MenuItemListener(Method m, Object[] args) {
			this.targetMethod=m;
			this.arguments=args;
		}

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					
					Object[] args = new Object[targetMethod.getParameterCount()];
					Class<?>[] types = targetMethod.getParameterTypes();
					
					
					args = fillArguments(args, types);
					
					Object item = targetMethod.invoke(sourceObject, args);
					
					/**If the output is an undoable edit, adds it to the undo manager*/
					UndoManagerPlus manager1 = getUndoManager();
					if (manager1!=null &&item instanceof UndoableEdit) {
						manager1.addEdit((UndoableEdit) item);
					}
					
					/**updates the display*/
					new CurrentFigureSet().getCurrentlyActiveDisplay().updateDisplay();
					
					
				} catch (Exception e) {
					IssueLog.logT(e);
				}
			}
		
			/**fills the array of arguments to contain the parameter types
			 * @param args
			 * @param types
			 * @return a version of the array in which objects are filled
			 */
			public Object[] fillArguments(Object[] args, Class<?>[] types) {
				if((types.length==1 )&& types[0]==CanvasMouseEvent.class) {
					CanvasMouseEvent event = getEvent();
					if(event==null) {
						ShowMessage.showOptionalMessage("This menu option can only be used if one clicks directly on the worksheet");
						return args;
					}
					args= new Object[] {event};
					
					
				}
				
				/**fills in with the arguments */
				for(int count=0; count<args.length; count++) {
					if(arguments[count].getClass()==types[count]) {
						args[count]=arguments[count];
						
					}
					else IssueLog.log("failed at filling in parameter "+count);
				}
				
				
				return args;
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
	
	/**returns the undo manager that will be used to  */
	public CanvasMouseEvent getEvent() {
		/**the popup had this event*/
		if (popupMenu!=null) {
			return popupMenu.getMemoryOfMouseEvent();
		}
		/**next determines if this event is in the */
		if (theSmartMenu!=null) {
			return theSmartMenu.getMemoryOfMouseEvent();
		}
		return null;
	}
}

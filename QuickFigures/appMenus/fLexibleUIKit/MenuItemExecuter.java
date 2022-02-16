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
 * Date Modified: Feb 4, 2022
 * Version: 2022.0
 */
package fLexibleUIKit;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.undo.UndoableEdit;

import applicationAdapters.CanvasMouseEvent;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects.BasicGraphicalObject;
import graphicalObjects.ZoomableGraphic;
import iconGraphicalObjects.CheckBoxIcon;
import logging.IssueLog;
import menuUtil.BasicSmartMenuItem;
import menuUtil.HasUniquePopupMenu;
import menuUtil.MenuSupplier;
import menuUtil.PopupMenuSupplier;
import menuUtil.SmartJMenu;
import menuUtil.SmartPopupJMenu;
import messages.ShowMessage;
import undo.CombinedEdit;
import undo.UndoManagerPlus;

/**this will generate working popup menus from the annotated methods in
  an object. It allows a programmer to build complex popup menus with a little less complexity each time.
  @see MenuItemMethod to understand the annotation*/
public class MenuItemExecuter implements  MenuSupplier {
	
	/**Object containing the method calls that are the basis of menu items*/
	private Object sourceObject;

	/**Maps that link method calls to items within this menu */
	private HashMap<MenuItemMethod, Method> map=new HashMap<MenuItemMethod, Method>();
	
	private MenuSupplier partner=null;

	private SmartPopupJMenu popupMenu;
	private SmartJMenu theSmartMenu;
	private BasicSmartMenuItem lastItem;
	
	private UndoManagerPlus undoManager;

	/**set to true if methods should be applied to all selected items with that method call*/
	private boolean propagate=false;


	
	
	/**creates a menu item execuer for the object
	 * @parm progagate determines if the menu items will apply to multiple selected objects*/
	public MenuItemExecuter(Object o, boolean propagate) {
		this.propagate=propagate;
		this.sourceObject=o;
		innitiallizeMap();
	}


	
	
	/**creates a menu item execuer for the object*/
	public MenuItemExecuter(Object basicObject) {
		this(basicObject, false);
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




	/**determines what sort of ison belongs 
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
				if(b==null)
					return;
				
				/**if the icon method returns an object of class icon*/
				for(Class<?> c: b.getClass().getClasses()) {
					if(c==Icon.class)
						item.setIcon((Icon) b);
					return;
				}
				/**if the icon method returns a null object or false*/
				if (b==null||b.toString().equals("false")||trueCheckBoxObject!=b) {
					item.setIcon(new CheckBoxIcon(Color.black, false));
				};
				/**if the icon method returns true*/
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
		
		
		
		/**if the method takes an enum argument, then this will create a series of versions with each possible enum*/
		if(m.getParameterCount()==1&&m.getParameterTypes()[0].isEnum()) {
			
			return createEnumVersions(anns, m);
		}
		else 
			if(m.getParameterCount()==1&&m.getParameterTypes()[0]==Boolean.class) {
				
				
				return createTwoBooleanVersions(anns, m);
			}
			else
		{
			BasicSmartMenuItem mi = new BasicSmartMenuItem(anns.menuText()+suffix);
			mi.addActionListener(new MenuItemListener(m, new Object[] {}, mi));
			mi.setActionCommand(anns.menuActionCommand());
			setupIcon(anns, mi,null);
			return new JMenuItem[] {mi};
		}
		
		
		
	}




	/**
	 * @param anns
	 * @param m
	 * @return
	 */
	private JMenuItem[] createEnumVersions(MenuItemMethod anns, Method m) {
		Object[] eConstants=m.getParameterTypes()[0].getEnumConstants();
		JMenuItem[] output=new JMenuItem[eConstants.length] ;
		int i=0;
		for(Object constant: eConstants) {
			String menuText = anns.menuText()+" "+enumNameToText(constant);
			if(anns.menuText().contains("ENUM"))
				menuText=anns.menuText().replace("ENUM", enumNameToText(constant));
			
			BasicSmartMenuItem mi = new BasicSmartMenuItem(menuText);
			Object[] parameterArgs = new Object[] {constant};
			mi.addActionListener(new MenuItemListener(m, parameterArgs, mi));
			setupIcon(anns, mi,constant);
			output[i]=mi;
			i++;
		}
		return output;
	}




	/**Creates two menu items, one for an argument of true and another for an argument of false
	 * If the parameter has an annotation attached that informs which method to call regarding the current status, 
	 * will only return one menu item (that which changes the status between true and false).
	 * @param anns
	 * @param m
	 * @return
	 */
	private JMenuItem[] createTwoBooleanVersions(MenuItemMethod anns, Method m) {
		JMenuItem[] output=new JMenuItem[2] ;
		int i=0;
		for(boolean constant:  new boolean[] {true, false}) {
			String menuText = anns.menuText();
			if(menuText.contains(":")) {
				String[] text = menuText.split(":");
				if(constant==Boolean.TRUE)
						{menuText=text[0];}
				else {menuText=text[1];}
			}
			
			BasicSmartMenuItem mi = new BasicSmartMenuItem(menuText);
			Object[] parameterArgs = new Object[] {constant};
			mi.addActionListener(new MenuItemListener(m, parameterArgs, mi));
			
			output[i]=mi;
			i++;
		}
		Annotation[][] parameternotes = m.getParameterAnnotations();
		for(Annotation[] a: parameternotes) {
			for(Annotation b: a) {
				if (b instanceof MenuChoiceAnnotation) {
					MenuChoiceAnnotation menuB=(MenuChoiceAnnotation) b;
					if(!menuB.findCurrent().equals("")) {
						Method pMethod;
						try {
							pMethod = sourceObject.getClass().getMethod(menuB.findCurrent());
							if (pMethod!=null) {
								{
									Object currentStatus = pMethod.invoke(sourceObject);
									
									if(currentStatus==Boolean.FALSE)
										{return new JMenuItem[] {output[0]};}
									else {return new JMenuItem[] {output[1]};}
									}
								}
							
						} catch (Exception e) {
							IssueLog.logT(e);
						}
						
					}
				}
			}
		}
		
		return output;
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
			private BasicSmartMenuItem menuItem;
			

			/**
		 * @param m
			 * @param mi 
		 */
		public MenuItemListener(Method m, Object[] args, BasicSmartMenuItem mi) {
			this.targetMethod=m;
			this.arguments=args;
			this.menuItem=mi;
		}

			@Override
			public void actionPerformed(ActionEvent arg0) {
				lastItem=menuItem;
				try {
					
					Object[] args = new Object[targetMethod.getParameterCount()];
					Class<?>[] types = targetMethod.getParameterTypes();
					
					
					args = fillArguments(args, types);
					
					Object item = targetMethod.invoke(sourceObject, args);
					
					if(propagate)
						item=propateToSelectedObjects(item, targetMethod, args);
					
					/**If the output is an undoable edit, adds it to the undo manager*/
					UndoManagerPlus manager1 = getUndoManager();
					if (manager1!=null &&item instanceof UndoableEdit) {
						
						UndoableEdit item2 = (UndoableEdit) item;
						
						manager1.addEdit(item2);
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
					Class<? extends Object> argumentClass = arguments[count].getClass();
					Class<?> requiredArgumentClass = types[count];
					if(argumentClass==requiredArgumentClass) {
						args[count]=arguments[count];
						
					} else if (requiredArgumentClass.isPrimitive()) {
						//argumentClass.get
					}
					else IssueLog.log("failed at filling in parameter "+count+" due to class "+argumentClass+" not matching "+requiredArgumentClass);
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
		
		if(this.lastItem!=null&&lastItem.getUndoManager()!=null)
			this.undoManager=lastItem.getUndoManager();
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
		if(lastItem!=null)
			return lastItem.getLastMouseEvent();
		return null;
	}
	
	/**Multiple object may be selected. if multiple objects are the targets. Will invoke the method for all of them
	 * @param args 
	 * @param targetMethod 
	 * @return */
	public UndoableEdit propateToSelectedObjects(Object item2, Method targetMethod, Object[] args) {
		UndoableEdit output=null;
		if(item2 instanceof UndoableEdit)
			output=(UndoableEdit) item2;
		CanvasMouseEvent theEvent = getEvent();
		
		if(theEvent==null)
			return output;
		
		if(!propagate)
			return output;;
	
		CombinedEdit editAll=new CombinedEdit();
		editAll.addEditToList(output);
		ArrayList<ZoomableGraphic> items = theEvent.getSelectionSystem().getSelecteditems();
		for(Object currentItem: items) try {
			Object invovationObject=currentItem;
			if(currentItem==this.sourceObject) {
				continue;
			}
			if(this.getPartner()!=null &&(this.getPartner()instanceof MenuItemExecuter)&& ((MenuItemExecuter)this.getPartner()).sourceObject==currentItem) {
				continue;
			}
			
			if(!hasMethod(currentItem, targetMethod))
				invovationObject=findAlternativeObject(currentItem, targetMethod);
			if(invovationObject==this.sourceObject||this.sourceObject.equals(invovationObject))
				continue;
			if(!hasMethod(invovationObject, targetMethod))
				continue;
			
			Object item = targetMethod.invoke(invovationObject, args);
			
			if(item instanceof UndoableEdit)
				editAll.addEditToList((UndoableEdit) item);
		} catch (Throwable t ) {
			IssueLog.logT(t);
		}
		
		
		return editAll;
	}




	/**
	 * Checks the popup menus of the target object to see if any of them have the given method.
	 * If 
	 * @param currentItem
	 * @param targetMethod
	 * @return
	 */
	private Object findAlternativeObject(Object currentItem, Method targetMethod) {
		
		if(currentItem instanceof HasUniquePopupMenu) {
			
			PopupMenuSupplier menuSource = ((HasUniquePopupMenu) currentItem).getMenuSupplier();
			
			if(hasMethod(menuSource, targetMethod))
					{
				
				return menuSource;
				}
			if(menuSource instanceof MenuItemExecuter) {
				MenuItemExecuter m2 = (MenuItemExecuter) menuSource;
				Object m = m2.sourceObject;
				
				if(hasMethod(m, targetMethod))
					{
						return m;
					}
				
			}
		}
		return currentItem;
	}




	/**returns true if the item given has the method or an equivalent method
	 * @param currentItem
	 * @param targetMethod
	 * @return
	 */
	private boolean hasMethod(Object currentItem, Method targetMethod) {
		try {
			Method theNewMethod = currentItem.getClass().getMethod(targetMethod.getName(), targetMethod.getParameterTypes());
			
		} catch (NoSuchMethodException | SecurityException e) {
			return false;
		}
		return true;
	}




	
}

/**
 * Author: Greg Mazo
 * Date Modified: Sep 29, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package popupMenusForComplexObjects;

import javax.swing.JComponent;
import javax.swing.JMenu;

import graphicalObjects.BasicGraphicalObject;
import locatedObject.LocationChangeListener;

/**
 interface for objects that may provide a menu under certain contexts
 */
public interface DonatesMenu {
	
	JMenu getDonatedMenuFor(Object requestor);
	
	/**static methods used in multiple contexts to obtain submenus*/
	public static class MenuFinder {
		
		/**adds the donated menu as a submenu to the parent menu*/
		public static JMenu addMenuFor(JComponent addto, Object donator, Object recipeint) {
			JMenu output=null;
			if(donator instanceof DonatesMenu)
				output=((DonatesMenu)donator).getDonatedMenuFor(recipeint);
			if(addto!=null&&output!=null)
				addto.add(output);
			return output;
		}
		
		/**
		 * @param targetMenu
		 * @param targetObject
		 * @return 
		 */
		public static JMenu addDonatedMenusTo(JComponent targetMenu,  BasicGraphicalObject targetObject) {
			JMenu output = DonatesMenu.MenuFinder.addMenuFor(targetMenu, targetObject.getParentLayer(), targetObject);
			for(LocationChangeListener list1:targetObject.getListenerList()) {
				if(output==null)
					output=DonatesMenu.MenuFinder.addMenuFor(targetMenu, list1, targetObject);
			}
			return output;
		}
	}
}

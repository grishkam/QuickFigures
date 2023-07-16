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
 * Date Modified: July 8, 2023
 * Version: 2023.2
 */
package popupMenusForComplexObjects;

import javax.swing.JComponent;
import javax.swing.JMenu;

import graphicalObjects.BasicGraphicalObject;
import locatedObject.LocationChangeListener;

/**
 interface for objects that may provide a menu to others under certain contexts
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

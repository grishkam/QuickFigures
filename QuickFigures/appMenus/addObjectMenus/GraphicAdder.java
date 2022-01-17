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
package addObjectMenus;

import javax.swing.Icon;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import selectedItemMenus.LayerSelectionSystem;
import selectedItemMenus.MenuItemInstall;

/**This interface is for items that are included in the adding menu
 * (@see ObjectAddingMenu). 
 * In general, these items can also appear in other menus*/
public interface GraphicAdder extends MenuItemInstall {
		/**Adds an object to the layer and returns it*/
		public ZoomableGraphic add(GraphicLayer gc);
		public String getCommand();
		public String getMenuCommand();
		public Icon getIcon();

		/**Certain implementations of this use a layer selector to obtain more inforamtion*/
		
		/**returns a keyboard shortcut*/
		public Character getKey();
		
		/**sets the selection system*/
		public void setSelector(LayerSelectionSystem selector);
		public void run();
	
	
}

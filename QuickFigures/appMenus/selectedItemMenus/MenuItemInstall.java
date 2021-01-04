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
package selectedItemMenus;

import java.awt.Font;

import javax.swing.Icon;

/**This interface is for any class that specifies the traits of a menu item*/
public interface MenuItemInstall {
	/**if the item has a special menu path, this returns a string to help find it*/
	public String getMenuPath();
	/**The menu text that appears in the JMenu for this item command for this item*/
	public String getMenuCommand();
	/**The icon for the item. this may appear either as part of the menu item or on a button*/
	public Icon getIcon();
	/**if the menu item must have a non-default font, returns it*/
	public Font getMenuItemFont();
	
	/**returns true if the adder will work for the layer selector given*/
	public boolean canUseObjects(LayerSelector graphicTreeUI);
	
	
}

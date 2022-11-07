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
 * Date Created: Nov 29, 2021
 * Date Modified:Dec 4, 2021
 * Version: 2022.2
 */
package popupMenusForComplexObjects;

import graphicalObjects_Shapes.PathGraphic;

/**
 
 * 
 */
public class ConnectorMenu extends PathGraphicMenu {

	

	/**
	 * @param textG
	 */
	public ConnectorMenu(PathGraphic textG) {
		super(textG);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**Adds every meny item to the menu
	 * @param textG
	 */
	public void addAllMenuItems() {
		
		this.addAllMenuItems(new ShapeGraphicMenu(targetPathForMenu).createMenuItems());
		
		String subMenuName = "Expert options";
		
		 
		addArrowHeadOptions(subMenuName);
	}

}

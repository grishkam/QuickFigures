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
 * Date Modified: Jan 5, 2021
 * Version: 2023.2
 */
package icons;

import java.awt.Component;
import java.awt.Graphics;

import layersGUI.HasTreeLeafIcon;

/**A tool icon that displays the same icon as the tree icon for a particular object*/
public class TreeIconWrappingToolIcon  extends GraphicToolIcon{

	private HasTreeLeafIcon treeIcon;
	
	int x_shift=5;
	int y_shift=5;
	

	public TreeIconWrappingToolIcon(HasTreeLeafIcon treeIcon, int type) {
		super(type);
		this.treeIcon = treeIcon;
	}
	
	public TreeIconWrappingToolIcon(HasTreeLeafIcon treeIcon, int type, int x, int y) {
		super(type);
		this.treeIcon = treeIcon;
		x_shift=x;
		y_shift=y;
	}
	
	protected void paintObjectOntoIcon(Component arg0, Graphics g, int arg2,
			int arg3) {
		treeIcon.getTreeIcon().paintIcon(arg0, g, arg2+x_shift, arg3+y_shift );
		
	}
	
	public static IconSet createIconSet(HasTreeLeafIcon treeIcon, int x, int y) {
	return	new IconSet(
				new TreeIconWrappingToolIcon(treeIcon, 0, x, y),
				new TreeIconWrappingToolIcon(treeIcon, 1, x, y),
				new TreeIconWrappingToolIcon(treeIcon, 2, x, y)
				);
	
	}
	
	public static IconSet createIconSet(HasTreeLeafIcon treeIcon) {
		return	createIconSet(treeIcon, 5, 5);
	
	}

	@Override
	public
	GraphicToolIcon copy(int type) {
		return new TreeIconWrappingToolIcon(treeIcon, type);
	}

}

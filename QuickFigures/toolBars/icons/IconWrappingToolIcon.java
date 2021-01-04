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
package icons;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**A tool icon that draws another kind of icon on top of it
 Allows reuse of icons designed for menus and JTrees to be used 
 for certain tools*/
public class IconWrappingToolIcon  extends GraphicToolIcon{

	private Icon treeIcon;

	public IconWrappingToolIcon(Icon treeIcon, int type) {
		super(type);
		this.treeIcon = treeIcon;
	}
	
	protected void paintObjectOntoIcon(Component arg0, Graphics g, int arg2,
			int arg3) {
		treeIcon.paintIcon(arg0, g, arg2+2, arg3+2 );
		
	}
	
	public static IconSet createIconSet(Icon treeIcon) {
			return	new IconSet(
						new IconWrappingToolIcon(treeIcon, 0),
						new IconWrappingToolIcon(treeIcon, 1),
						new IconWrappingToolIcon(treeIcon, 2)
						
						);}
			
	public static IconSet createIconSet(Icon treeIcon0, Icon treeIcon1) {
			return	new IconSet(
						new IconWrappingToolIcon(treeIcon0, 0),
						new IconWrappingToolIcon(treeIcon1, 1),
						new IconWrappingToolIcon(treeIcon1, 2)
						);}

	@Override
	public
	GraphicToolIcon copy(int type) {
		return new IconWrappingToolIcon(treeIcon, type);
	}

	
	

}

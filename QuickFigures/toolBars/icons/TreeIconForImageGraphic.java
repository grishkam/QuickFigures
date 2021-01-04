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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**An icon that consists of a rectangle with a tiny drawing in it*/
public class TreeIconForImageGraphic extends GenericTreeIcon {


	ColorBlotchForIcon blotch1=new ColorBlotchForIcon(new Rectangle(4,4, 5,5), Color.white);
	ColorBlotchForIcon blotch3=new ColorBlotchForIcon(new Rectangle(3,3, 3,3), Color.white);
	ColorBlotchForIcon blotch2=new ColorBlotchForIcon(new Rectangle(10,7, 2,2), Color.white);

	/**constructs an icon with the first 2 letters of the string in the colors given*/
	public TreeIconForImageGraphic(Color c) {
		 blotch1.blotchColor=c;
	}
	

	
	
	
	/**draws two letters in a black rectangle*/
	@Override
	public void paintIcon(Component arg0, Graphics arg1, int arg2, int arg3) {
		super.paintIcon(arg0, arg1, arg2, arg3);
		if (arg1 instanceof Graphics2D) {
			
			Graphics2D g2d=(Graphics2D) arg1;
			blotch1.paintBlotch(g2d, arg2, arg3);
			blotch2.paintBlotch(g2d, arg2, arg3);
			blotch3.paintBlotch(g2d, arg2, arg3);
		}
	
	}
}
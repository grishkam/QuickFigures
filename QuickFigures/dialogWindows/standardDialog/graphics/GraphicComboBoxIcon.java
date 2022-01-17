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
 * Date Modified: Jan 16, 2021
 * Date Created: Jan 16, 2021
 * Version: 2022.0
 */
package standardDialog.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.Icon;
import graphicalObjects.BasicCoordinateConverter;
import graphicalObjects_Shapes.SimpleGraphicalObject;

/**An icon that displays a graphical object
 * Created to render list cells in a combo box 
 * Displays the object in the same way regardless of its xy position
  @see SimpleGraphicalObject
  */
public class GraphicComboBoxIcon implements Icon{


	int inset=10;
	 
	 private SimpleGraphicalObject object;
	 boolean selected=false;
	Color selectionBackground = new Color(50,50, 230);

	public GraphicComboBoxIcon(SimpleGraphicalObject s) { 
		this.object=s;
	 }
	
	public GraphicComboBoxIcon(SimpleGraphicalObject s, boolean selected) { 
		this(s);
		this.selected=selected;
	 }
	
	public GraphicComboBoxIcon(SimpleGraphicalObject s, boolean selected, Color background) { 
		this(s,selected);
		selectionBackground =background;
	 }

	 

	

@Override
public int getIconWidth() {
	if(object!=null)
		return (int) (object.getExtendedBounds().getWidth()+2*inset);
	return 20;
}


@Override
public int getIconHeight() {
	if(object!=null)
		return (int) object.getExtendedBounds().getHeight()+2*inset;
	return 20;
}



@Override
public void paintIcon(Component c, Graphics g, int x, int y) {
	Font ifont = g.getFont();
	Color iColor = g.getColor();
	if(object==null)
		return;
	Rectangle b = object.getExtendedBounds();
	if(selected) {
		
		g.setColor(selectionBackground);
		g.fillRect(x, y, this.getIconWidth(), this.getIconHeight());
	}
	object.draw((Graphics2D) g, new BasicCoordinateConverter(x+b.getX()-inset, y+b.getY()-inset, 1));
	g.setFont(ifont);
	g.setColor(iColor);
	
}


	
}

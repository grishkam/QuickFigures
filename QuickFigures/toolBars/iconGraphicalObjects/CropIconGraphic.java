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
 * Version: 2023.1
 */
package iconGraphicalObjects;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import locatedObject.AttachmentPosition;
import standardDialog.graphics.GraphicObjectDisplayBasic;

/**A class for rendering my own design for cropping icon*/
public class CropIconGraphic extends GraphicGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color iconColor=Color.black;
	private Color iconColor2=Color.gray;
	boolean open;
	private RectangularGraphic spacefilled;

	
	ArrayList<ShapeGraphic> iconParts=new ArrayList<ShapeGraphic>();
	
	public  CropIconGraphic() {
	
		createItems() ;
		
		addItems();
	}
	
	
	
	/**creates the graphical objects that will compose the shapes in the icon */
	public void createItems() {
		spacefilled = new RectangularGraphic();
		spacefilled.setRectangle(getR1rect());
		spacefilled.setAntialize(true);
		
		
	
		Rectangle fullRect=new Rectangle(3,3, 12, 12);
		for(int i=0; i<8; i++) {
			int place = i%4;
			Rectangle partRect=new Rectangle(0,0, 2, 15);
			if(i<4) partRect=new Rectangle(0,0, 15, 2);
			if(place==1||place==3) continue;
			AttachmentPosition p=new AttachmentPosition();
			p.setLocationTypeInternal(place);
			p.setLocationCategory(AttachmentPosition.INTERNAL);
			p.snapRects(partRect, fullRect);
			
			ShapeGraphic bar = RectangularGraphic.filledRect(partRect);
			
			bar.setFillColor(iconColor);
			if(place==2) bar.setFillColor(iconColor2);
			
			iconParts.add(bar);
		}
		
		
	}
	
	
	/**returns the shape of Rectangle 1. this will depend on the circumstances*/
	private Rectangle getR1rect() {
		 {
			return new Rectangle(0,2,14,10);
		}
	}

	
	public void addItems() {
		
		for(ShapeGraphic d:iconParts) {	getTheInternalLayer().add(d);}
	}



	public Color getIconColor() {
		if (iconColor==null) {
			iconColor=Color.black;
		}
		return iconColor;
	}

	/**
	 * @return
	 */
	public static Icon createsCropIcon() {
		return new GraphicObjectDisplayBasic<CropIconGraphic>(new 	CropIconGraphic());
	}

	
}
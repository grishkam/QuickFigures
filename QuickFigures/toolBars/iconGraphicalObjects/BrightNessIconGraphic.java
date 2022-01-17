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
 * Version: 2022.0
 */
package iconGraphicalObjects;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.CircularGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import locatedObject.DefaultPaintProvider;
import locatedObject.RectangleEdgePositions;
import locatedObject.RectangleEdges;

/**A class for rendering of a brightness icon
  */
public class BrightNessIconGraphic extends GraphicGroup  implements  RectangleEdgePositions {

	/**
	 * 
	 */
	public static final int NORMAL_FORM=0, OPEN_FORM=1;
	private static final long serialVersionUID = 1L;
	private Color iconColor=Color.black;
	int form=NORMAL_FORM;
	private CircularGraphic greyToBlackPart;
	private CircularGraphic whiteHalf;
	
	ArrayList<ShapeGraphic> spokeDots=new ArrayList<ShapeGraphic>();
	
	public BrightNessIconGraphic() {
		
		createItems() ;
		setItemColors();
		
		addItems();
	}
	
	 DefaultPaintProvider getPaintProvider() {
		 DefaultPaintProvider d=new  DefaultPaintProvider(iconColor);
		 d.setColor(Color.white);
		 d.setFillColor2( Color.black);
		 d.setFe1(RectangleEdges.LEFT);
		 d.setFe2(RectangleEdges.RIGHT);
		 d.setType(DefaultPaintProvider.SHAPE_GRADIENT_PAINT);
		 return d;
	 }
	
	
	public void createItems() {
		Rectangle rectsize = new Rectangle(4,5,10,10);
		
		
		greyToBlackPart = CircularGraphic.filledCircle(rectsize);
		
		whiteHalf= CircularGraphic.halfCircle(rectsize);
		whiteHalf.setFillColor(Color.white);
		greyToBlackPart.setStrokeColor(Color.black);
		greyToBlackPart.setFillPaintProvider(getPaintProvider());
		greyToBlackPart.setStrokeWidth(2);
	
		/**creates a ring of dots around the black/white circle*/
		for(int i=0; i<8; i++) {
			ShapeGraphic dot = CircularGraphic.filledCircle(new Rectangle(0,0,2,2));
			if(form==1) dot = RectangularGraphic.filledRect(new Rectangle(-2,0,4,2));
			dot.setLocation(8+8*Math.cos(Math.PI/4*i), 9+8*Math.sin(Math.PI/4*i));
			dot.setFillColor(Color.black);
			dot.setAngle(-Math.PI/4*i);
			spokeDots.add(dot);
		}
		
		 setItemColors() ;
	}
	

	public void setOpen(int o) {
		this.form=o;
		this.setItemColors();
	}
	
	public void setColor(Color c) {
		setItemColors();
	}
	public void setItemColors() {
		 greyToBlackPart.setFillColor(getIconColor());
		whiteHalf.setFillColor(Color.white);
		greyToBlackPart.setFillPaintProvider(getPaintProvider());
	}
	
	/**adds the graphic items for this icon to the layer*/
	public void addItems() {
		getTheInternalLayer().add(greyToBlackPart);
		getTheInternalLayer().add(whiteHalf);
		for(ShapeGraphic d:spokeDots) {	getTheInternalLayer().add(d);}
	}


	public Color getIconColor() {
		if (iconColor==null) {
			iconColor=Color.black;
		}
		return iconColor;
	}



	
	
}
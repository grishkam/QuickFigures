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
 * Date Created: Jan 10, 2026
 * Date Modified: Jan 10, 2026
 * Version: 2026.1
 */
package iconGraphicalObjects;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.Icon;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_Shapes.TrapezoidGraphic;
import standardDialog.graphics.GraphicObjectDisplayBasic;

/**A class for rendering my own design for trash icon*/
public class TrashIconGraphic extends GraphicGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color iconColor=Color.black;
	private Color iconColor2=Color.gray;
	boolean open;

	
	ArrayList<ShapeGraphic> iconParts=new ArrayList<ShapeGraphic>();
	
	public  TrashIconGraphic() {
	
		createItems() ;
		
		addItems();
	}
	
	
	
	/**creates the graphical objects that will compose the shapes in the icon */
	public void createItems() {
	
	
		
		Rectangle fullRect2=new Rectangle(2,2, 14, 15);
		TrapezoidGraphic tg = new TrapezoidGraphic(fullRect2);
		tg.setStrokeColor(Color.black);
		tg.getParameter().setRatioToMaxLength(0.15);
		tg.setFillColor(iconColor2);
		tg.setAngle(Math.PI);
		tg.setAntialize(true);
		iconParts.add(tg);
		
		RectangularGraphic rectangularGraphic = new RectangularGraphic(new Rectangle(5,-1, 7, 5));
		rectangularGraphic.setFillColor(null);
		rectangularGraphic.setFilled(false);
		rectangularGraphic.setStrokeColor(Color.black);
		rectangularGraphic.setStrokeWidth(1);
		rectangularGraphic.setStrokeJoin("round");
		rectangularGraphic.setAntialize(true);
		iconParts.add(rectangularGraphic);
		
		Rectangle fullRect3=new Rectangle(0,2, 18, 4);
		TrapezoidGraphic tg3 = new TrapezoidGraphic(fullRect3);
		tg3.setStrokeColor(Color.black);
		tg3.setFillColor(iconColor2);
		tg3.setAntialize(true);
		tg3.getParameter().setRatioToMaxLength(0.15);
		iconParts.add(tg3);
		tg3.setStrokeJoin("round");
		
		
		for(int i=0; i<3; i++) {
			
			Rectangle partRect=new Rectangle(fullRect2.x+3*i+3,fullRect2.y+4, 2, 9);
			
			
			ShapeGraphic bar = RectangularGraphic.filledRect(partRect);
			bar.setStrokeWidth(0);
			
			bar.setFillColor(iconColor2.darker().darker());
			
			
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
	public static Icon createAnIcon() {
		return new GraphicObjectDisplayBasic<TrashIconGraphic>(new 	TrashIconGraphic());
	}

	
}
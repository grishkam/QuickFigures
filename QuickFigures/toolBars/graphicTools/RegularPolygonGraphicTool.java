/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package graphicTools;

import java.awt.Color;
import java.awt.Rectangle;

import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.RegularPolygonGraphic;
import icons.TreeIconWrappingToolIcon;

/**A tool bit for drawing regular polygons*/
public class RegularPolygonGraphicTool extends RectGraphicTool {
	
	private RegularPolygonGraphic m;

	public  RegularPolygonGraphicTool(int nVertex) {
		this(new RegularPolygonGraphic(new Rectangle(0,0,10,10), nVertex));

	}
	
	public  RegularPolygonGraphicTool(RegularPolygonGraphic m) {
		this.m=m;
		model=m;
		getModel().setStrokeColor(Color.black);
		super.iconSet=TreeIconWrappingToolIcon.createIconSet(getModel());
	}
	
	
	public RectangularGraphic createShape(Rectangle r) {
		RectangularGraphic out = getModel().copy();
		out.setRectangle(r);
		return out;
	}
	
	
	/**returns the name of the model shape */
	public String getShapeName() {
		if(m!=null) return ""+m.getPolygonType();
		return "Shape";
	}
	
		
}

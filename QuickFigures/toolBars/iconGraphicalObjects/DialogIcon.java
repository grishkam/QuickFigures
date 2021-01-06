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
 * Date Modified: Dec 7, 2020
 * Version: 2021.1
 */
package iconGraphicalObjects;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.Icon;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import standardDialog.graphics.GraphicDisplayComponent;

/**An generates an icon that appears as a small dialog*/
public class DialogIcon {
	
	
	public static Icon getIcon() {
		return new GraphicDisplayComponent(createIcon() );
	}
	
	static GraphicGroup createIcon() {
		GraphicGroup gg = new GraphicGroup();
		addRect(gg, new Rectangle(2,2,18,16), new Color(0,0,0,0), null);
		
		addRect(gg, new Rectangle(2,2,16,14), Color.BLACK, null);
		addRect(gg, new Rectangle(2,2,16,3), Color.BLACK, Color.gray);
		
		
		addRect(gg, new Rectangle(5,8,11,1), Color.DARK_GRAY, null);
		addRect(gg, new Rectangle(5,12,11,1), Color.DARK_GRAY, null);
		
		return gg;
	}
	

	
	public static ShapeGraphic addRect(GraphicGroup g, Rectangle r, Color c, Color cFill) {
		ShapeGraphic out = RectangularGraphic.blankRect(r, c);
		if (cFill!=null) {out.setFillColor(cFill); out.setFilled(true);}
		out.setAntialize(true);
		out.setStrokeWidth(1);
		
		out.makeNearlyDashLess();
		g.getTheInternalLayer().add(out);
		return out;
	}

}

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
 * Version: 2022.2
 */
package iconGraphicalObjects;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.Icon;

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import standardDialog.graphics.GraphicObjectDisplayBasic;

/**this class consists of static methods for creating a few of the icons used*/
public class IconUtil {

	public static Icon createFolderIcon(boolean open, Color folderColor) {
		
		return new folderIcon( open,folderColor);
	}
	
	public static class folderIcon extends GraphicObjectDisplayBasic<FolderIconGraphic> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public folderIcon(boolean open, Color c) {
			this.setCurrentDisplayObject(new FolderIconGraphic(c,open));
		}
		
	}


	/**returns the brightness/contrast icon*/
public static Icon createBrightnessIcon() {
	
	return new BrightnessIcon( );
}
	
	public static class BrightnessIcon extends GraphicObjectDisplayBasic<BrightNessIconGraphic> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public BrightnessIcon() {
			this.setCurrentDisplayObject(new BrightNessIconGraphic());
		}
		
	
		
	}
	
	/**returns an icon that displays a short String*/
	public static GraphicGroup createAllIcon(String all) {
		GraphicGroup gg = new GraphicGroup();
		addRect(gg, new Rectangle(2,2,18,16), new Color(0,0,0,0), null);
		
		TextGraphic z = new TextGraphic(all);
		z.moveLocation(2, 15);
		gg.getTheInternalLayer().add(z);
		
		return gg;
	}
	
	
	/**Adds a rectangular graphic to the given group*/
	private static ShapeGraphic addRect(GraphicGroup g, Rectangle r, Color c, Color cFill) {
		ShapeGraphic out = RectangularGraphic.blankRect(r, c);
		if (cFill!=null) {out.setFillColor(cFill); out.setFilled(true);}
		out.setAntialize(true);
		out.setStrokeWidth(1);
		
		out.makeNearlyDashLess();
		g.getTheInternalLayer().add(out);
		return out;
	}
	
}

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
package imageDisplayApp;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import applicationAdapters.ObjectCreator;
import applicationAdapters.PixelWrapper;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import utilityClassesForObjects.LocatedObject2D;

/**an implementation of the object creator interface*/
public class BasicGraphicalObjectCreator implements ObjectCreator {

	
	/**returns null for now but will eventually create Labels. its optional to provide font metrics*/
	@Override
	public LocatedObject2D createTextObject(String label, Color c, Font font, FontMetrics f, int lx,
			int ly, double angle, boolean antialiasedText) {
		TextGraphic tg = new TextGraphic(label);
		tg.setTextColor(c);
		tg.setFont(font);
		tg.setLocation(lx, ly);
		tg.setAngle(angle);
		return tg;
	}

	@Override
	public LocatedObject2D createImageObject(String name, PixelWrapper pix,
			int x, int y) {
		ImagePanelGraphic output = new ImagePanelGraphic((BufferedImage) pix.image());
		output.setName(name);
		output.setLocationUpperLeft(new Point(x,y));
		return output;
	}

	@Override
	public LocatedObject2D createRectangularObject(Rectangle r) {
		RectangularGraphic output = new RectangularGraphic();
		output.setRectangle(r);
		return output;
	}

}

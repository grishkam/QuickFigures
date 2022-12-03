/**
 * Author: Greg Mazo
 * Date Modified: Nov 27, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package render;

import java.awt.Graphics2D;
import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;

/**
 
 * 
 */
public class GraphicsRenderQF {
	public static ImagePanelGraphic lastImage;
	public static double lastImageX;
	public static double lastImageY;

	/***/
	public static Graphics2D getGraphics2D(ArrayList<ZoomableGraphic> g) {
		return new QFGraphics2D(g);
	}
}

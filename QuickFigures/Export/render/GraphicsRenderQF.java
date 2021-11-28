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

import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayer;

/**
 
 * 
 */
public class GraphicsRenderQF {
	/***/
	public static Graphics2D getGraphics2D(GraphicLayer g) {
		return new QFGraphics2D(g);
	}
}

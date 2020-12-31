/**
 * Author: Greg Mazo
 * Date Modified: Dec 30, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package icons;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**

 */
public class ExtractedIcon implements Icon {

	
	private GraphicToolIcon innerIcon;

	public ExtractedIcon(GraphicToolIcon icon) {
		innerIcon=icon;
	}
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		innerIcon.paintObjectOntoIcon(c, g, x, y);

	}

	@Override
	public int getIconWidth() {
		return innerIcon.getIconWidth();
	}

	@Override
	public int getIconHeight() {
		return innerIcon.getIconHeight();
	}

}

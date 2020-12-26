/**
 * Author: Greg Mazo
 * Date Modified: Dec 19, 2020
 * Copyright (C) 2020 Gregory Mazo
 * Version: 2021
 */
/**
 
 * 
 */
package genericMontageLayoutToolKit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import utilityClassesForObjects.RectangleEdges;

/**
 an icon for the border adjuster tool. written to replace an ImageIcon that looked great on some computers and
 terrible on others.
 */
public class BorderAdjustToolIcon extends GeneralLayoutToolIcon implements LayoutSpaces {

	/**
	 * 
	 */
	
	/**
	 * @param type
	 */
	public BorderAdjustToolIcon(int type) {
		super(type);
		this.paintBoundry=false;
		panelColor    = new Color[] {YELLOW_TONE};
	}
	
	protected void paintObjectOntoIcon(Component arg0, Graphics g, int arg2, int arg3) { 
		super.paintObjectOntoIcon(arg0, g, arg2, arg3);
		
		Rectangle2D[] panels = getDrawnLayout().getPanels();
		int count=0;
		for(Rectangle2D p:panels) {
			if(count%2==0) {count++; continue;}
			count++;
			Point2D r = RectangleEdges.getLocation(RectangleEdges.RIGHT, p);
			if ((g instanceof Graphics2D) &&type==NORMAL_ICON_TYPE)
				super.paintArrow((Graphics2D)g, (int)r.getX()+arg2-2, (int)r.getY()+arg3, 5, RectangleEdges.RIGHT, 1);
		}
	}
	
	
	/**
	creates a layout for drawing and icon
	 */
	protected BasicLayout createSimpleIconLayout( int type) {
		BasicLayout layout = new BasicLayout(2, 2, 8, 8, 2,2, true);
		layout.setLabelSpaces(2, 2,2,2);
		if (type==ROLLOVER_ICON_TYPE) {layout.setHorizontalBorder(5);}
		layout.move(-1,1);
		return layout;
	}
	
	protected GeneralLayoutToolIcon generateAnother(int type) {
		return new BorderAdjustToolIcon(type);
	}
	
	/**
	returns a list of panel colors. 
	 */
	protected Color[] getPanelColors() {
		if (type==PRESSED_ICON_TYPE) return new Color[] {Color.orange.darker().darker()};
		return panelColor;
		}
	
	
}

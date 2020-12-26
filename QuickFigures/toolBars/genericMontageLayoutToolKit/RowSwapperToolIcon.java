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

import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import utilityClassesForObjects.RectangleEdges;

/**
 an icon for the swapper tool. written to replace an ImageIcon (derived from jpeg) that looked great on some computers and
 terrible on others.
 */
public class RowSwapperToolIcon extends GeneralLayoutToolIcon implements LayoutSpaces {

	private int form=LayoutSpaces.ROWS;

	/**
	 * @param type
	 */
	public RowSwapperToolIcon(int type, int form) {
		super(type);
		this.form=form;
		this.paintBoundry=false;
		
		if(form==LayoutSpaces.PANELS) {
			panelColor    = new Color[] {RED_TONE,GREEN_TONE, GREEN_TONE, BLUE_TONE};
			if(type==PRESSED_ICON_TYPE|| type== ROLLOVER_ICON_TYPE)  {
				panelColor[0]=BLUE_TONE;
				panelColor[3]=RED_TONE;
			}
		}
		
		else {
			panelColor    = new Color[] {RED_TONE,BLUE_TONE};
			if(type==PRESSED_ICON_TYPE|| type== ROLLOVER_ICON_TYPE) {
				panelColor    = new Color[] {BLUE_TONE, RED_TONE};
			}
		}
	}
	
	protected void paintObjectOntoIcon(Component arg0, Graphics g, int arg2, int arg3) { 
		super.paintObjectOntoIcon(arg0, g, arg2, arg3);
		
		if (form==ROWS) {
			Point2D r = new Point2D.Double(arg2+5,arg3+12);
			if (g instanceof Graphics2D) {
				int size=2;
				super.paintArrow((Graphics2D)g, (int)r.getX(), (int)r.getY(), 6, RectangleEdges.TOP, size);
				super.paintArrow((Graphics2D)g, (int)r.getX(), (int)r.getY(), 6, RectangleEdges.BOTTOM, size);
			}
		}
		
		if (form==COLS) {
			Point2D r = new Point2D.Double(arg2+12,arg3+5);
			if (g instanceof Graphics2D) {
				int size=2;
				super.paintArrow((Graphics2D)g, (int)r.getX(), (int)r.getY(), 6, RectangleEdges.LEFT, size);
				super.paintArrow((Graphics2D)g, (int)r.getX(), (int)r.getY(), 6, RectangleEdges.RIGHT, size);
			}
		}
		
		if (form==PANELS) {
			Point2D r = new Point2D.Double(arg2+12,arg3+12);
			if (g instanceof Graphics2D) {
				int size=2;
				super.paintArrow((Graphics2D)g, (int)r.getX(), (int)r.getY(), 6, RectangleEdges.UPPER_LEFT, size);
				super.paintArrow((Graphics2D)g, (int)r.getX(), (int)r.getY(), 6, RectangleEdges.LOWER_RIGHT, size);
			}
		}
	}
	
	
	/**
	creates a layout for drawing and icon
	 */
	protected BasicLayout createSimpleIconLayout( int type) {
		
		if(form==LayoutSpaces.ROWS) {
			BasicLayout layout = new BasicLayout(1, 2, 12, 8, 2,2, true);
			layout.setLabelSpaces(2, 2,8,2);
			layout.move(2,2);
			return layout;
		}
		else if (form==LayoutSpaces.COLS) {
			BasicLayout layout = new BasicLayout(2, 1, 8, 12, 2,2, true);
			layout.setLabelSpaces(8, 2,2,2);
			layout.move(1,2);
			return layout;
		}
		else  {
			BasicLayout layout = new BasicLayout(2, 2, 8, 8, 2,2, true);
			layout.setLabelSpaces(2, 2,2,2);
			layout.move(1,1);
			return layout;
		}
		
	}
	
	protected GeneralLayoutToolIcon generateAnother(int type) {
		return new RowSwapperToolIcon(type, this.form);
	}

	public GeneralLayoutToolIcon copy(int type) {
		GeneralLayoutToolIcon another = generateAnother(type);
		return another;
	}
	
}

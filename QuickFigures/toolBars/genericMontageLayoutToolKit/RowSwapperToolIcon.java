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
 * Version: 2022.1
 */
package genericMontageLayoutToolKit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import locatedObject.RectangleEdges;

/**
 an icon for the swapper tool. written to replace an ImageIcon (derived from jpeg) that looked great on some computers and
 terrible on others.
 */
public class RowSwapperToolIcon extends GeneralLayoutToolIcon implements LayoutSpaces {

	private int form=LayoutSpaces.ROWS;

	/**
	creates an icon for the 
	@param type determines if the icon is a pressed, rollover or normal icon
	@param form determines if the icon is a row, column or panel version
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
	creates a layout for drawing an icon
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

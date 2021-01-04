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
import locatedObject.RectangleEdges;

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

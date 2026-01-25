/*******************************************************************************
 * Copyright (c) 2023 Gregory Mazo
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
 * Date Modified: Jan 24, 2026
 * Version: 2023.2
 */
package graphicalObjects_FlowChart;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import addObjectMenus.RectangleAdder;
import applicationAdapters.CanvasMouseEvent;
import genericTools.ToolBit;
import graphicTools.RectGraphicTool;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_FlowChart.ShapeSwitchMenu.ShapeSwitchMenuItem;
import graphicalObjects_Shapes.RectangularGraphic;
import includedToolbars.ObjectToolset1;
import logging.IssueLog;
import menuUtil.BasicSmartMenuItem;
import menuUtil.SmartJMenu;
import undo.AbstractUndoableEdit2;

/**
 
 * 
 */
public class ShapeSwitchMenu extends SmartJMenu {

	

	private FlowChart flowChart;
	ChartNexus clickedNexus;

	/**
	 * @param string
	 */
	public ShapeSwitchMenu(FlowChart f, ChartNexus cn) {
		super("Change shape");
		this.flowChart=f;
		addShapeSwitchMenuItems();
		clickedNexus=cn;
	}
	
	public void addShapeSwitchMenuItems() {
		
		addShapeCategory( ObjectToolset1.getRectangularShapeGraphicBits(), "Rectangular");
		addShapeCategory( ObjectToolset1.getCircularShapeGraphicBits(), "Circular");
		addShapeCategory( ObjectToolset1.getRegularPolygonShapeTools(), "Polygon");
	}

	/**
	 * @param list
	 */
	public void addShapeCategory(ArrayList<ToolBit> list, String submenu) {
		SmartJMenu sm = new SmartJMenu(submenu);
		for(ToolBit i: list) {
			if (i instanceof RectGraphicTool) {
				RectangleAdder rectangleAdder = new RectangleAdder((RectGraphicTool) i, "", i==list.get(0));
				ShapeSwitchMenuItem menuItem = new ShapeSwitchMenuItem(rectangleAdder);
				menuItem.setIcon(rectangleAdder.getIcon());
				sm.add(menuItem);
			
			}
		}
		this.add(sm);
	}

	/**
	 
	 * 
	 */
public class ShapeSwitchMenuItem extends BasicSmartMenuItem{

		private RectangleAdder rectangleAdder;

		/**
		 * @param rectangleAdder
		 */
		public ShapeSwitchMenuItem(RectangleAdder rectangleAdder) {
			this.rectangleAdder=rectangleAdder;
			this.setText(rectangleAdder.getShapeName());
		}
		
		
		/**performs the shape switch for the nexus*/
		public void performShapeSwitch(ChartNexus cn) {
			HashMap<AnchorAttachment, Point2D> locations = new HashMap<AnchorAttachment, Point2D>();
			for (AnchorAttachment a: cn.getFlowChart().attachments) {
				if(a.getAnchorSite()==cn) {
					locations.put(a, a.getAttachmentLocation());
				}
			}
			
			RectangularGraphic r = rectangleAdder.createRectangularShape();
			RectangularGraphic shapeAsRectangle = cn.getShapeAsRectangle();
			r.copyAttributesFrom(shapeAsRectangle);
			r.setRectangle(shapeAsRectangle.getRectangle());
			r.copyColorsFrom(shapeAsRectangle);
			cn.setShape(r);
			
			/**when shape is changed, the points are different, this sets them to as similar as possible a location*/
			for(AnchorAttachment a: locations.keySet()) {
				a.changeAttachmentLocation(locations.get(a));
			}
		}
		
		/**May be overwritten by subclasses. Does some task and returns an undo*/
		public AbstractUndoableEdit2 performAction() {
			
			for(ZoomableGraphic f: flowChart.getAllGraphics()) {
				if(f instanceof ChartNexus) {
					ChartNexus cn=(ChartNexus) f;
					if(!cn.getShape().isSelected()&&cn!=clickedNexus)
						continue;
					
					
					performShapeSwitch(cn);
				}
			}
			
			CanvasMouseEvent lastMouseEvent = super.getLastMouseEvent();
			if(lastMouseEvent!=null)
				lastMouseEvent.getAsDisplay().updateDisplay();
			return null;
		}
		

}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

}

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
package genericMontageLayoutToolKit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import genericTools.Object_Mover;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import icons.IconSet;
import layout.PanelLayout;
import layout.basicFigure.BasicLayout;
import locatedObject.LocatedObject2D;
import locatedObject.RectangleEdges;
import utilityClasses1.ArraySorter;

/**A tool that moves a selected item from its current location to the equivalent location in another panel*/
public class Shift_Object_Between_Panels extends Object_Mover {
	
//	{createIconSet("icons/RoiShifterIcon.jpg","icons/RoiShifterIconPressed.jpg","icons/RoiShifterRolloverIcon.jpg");}
	
	public IconSet getIconSet() {
		IconSet set1 = new  ShiftToolIcon(0).generateIconSet();
		setIconSet(set1);
		return set1;
	}
	
	@Override
	public void mouseDragged() {
		if (super.altKeyDown()) {
			super.mouseDragged(); return;
		}
		
		if (getPrimarySelectedObject()!=null) {
		PanelLayoutGraphic layout = getClickContainingLayout();
		if (layout==null) return;
		if (layout==this.getPrimarySelectedObject()) return;
		Rectangle2D l1 = layout.getPanelLayout().getNearestPanel(this.getPrimarySelectedObject().getBounds().getCenterX(), getPrimarySelectedObject().getBounds().getCenterY());
		Rectangle2D l2 = layout.getPanelLayout().getNearestPanel(getDragCordinateX(), getDragCordinateY());
			if (l1.equals(l2)) return;
			this.getPrimarySelectedObject().moveLocation(l2.getX()-l1.getX(), l2.getY()-l1.getY());
		}
		
		if (currentUndo!=null) {
			 currentUndo.establishFinalLocations();
			 if (!this.addedToManager)
				 {this.getImageDisplayWrapperClick().getUndoManager().addEdit(currentUndo);
				 this.addedToManager=true;
				 }
	}
		
	}
	
	
	public PanelLayoutGraphic getClickContainingLayout() {
		ArrayList<LocatedObject2D> layouts = this.getObjecthandler().getAllClickedRoi(getImageClicked(), getClickedCordinateX(), getClickedCordinateY(), PanelLayoutGraphic.class);
		if (this.ignorehidden) ArraySorter.removeHiddenItemsFrom(layouts);
		if (layouts.size()>0) return (PanelLayoutGraphic) new ArraySorter<LocatedObject2D>().getFirstNonNull(layouts);
		return null;
	}
	
	@Override
	public String getToolTip() {
			
			return "Move Objects Between Panels";
		}
	
	
	@Override
	public String getToolName() {
			
			return "Shift Object Between Panels";
		}

{this.setIconSet(new  ShiftToolIcon(0).generateIconSet());}
	
	class ShiftToolIcon extends GeneralLayoutToolIcon {

		/**
		 * @param type
		 */
		public ShiftToolIcon(int type) {
			super(type);
			super.paintBoundry=false;
			super.panelColor=new Color[] {Color.black};
		}
		
		/**
		creates a layout for drawing and icon
		 */
		protected PanelLayout createSimpleIconLayout( int type) {
			BasicLayout layout = new BasicLayout(2, 1, 9, 9, 2,2, true);
			layout.setLabelSpaces(2, 2,2,2);
			layout.move(1,6);
			return layout;
		}
		
		protected void drawPanel(Graphics2D g2d, Rectangle2D p, int count) { 
			super.drawPanel(g2d, p, count);
			Point2D loc = RectangleEdges.getLocation(RectangleEdges.LEFT, p);
			boolean draw=  (type==NORMAL_ICON_TYPE&&count==0)  ||  (type!=NORMAL_ICON_TYPE&&count==1);
			if(draw) {
				Ellipse2D e = new Ellipse2D.Double(loc.getX()+2, loc.getY()-2, 5, 5);
				g2d.setColor(Color.blue);
				g2d.setStroke(new BasicStroke(1));
				g2d.draw(e);
			}
			if (count==0&&type==NORMAL_ICON_TYPE) {
				super.paintArrow(g2d, (int)loc.getX()+7, (int)loc.getY(), 9, RectangleEdges.RIGHT, 2);
			}
		}
		
		/**
		 * @param type
		 * @return
		 */
		protected GeneralLayoutToolIcon generateAnother(int type) {
			return new ShiftToolIcon(type);
		}
		
		/**given the base color of a panel, returns the fill color used to give the panel a light tint
		 * @param panelColor2
		 * @return
		 */
		protected Color deriveFillColor(Color panelColor2) {
			Color fillColor=new Color(0,0,0,0);
			return fillColor;
		}
	}
	
	
}

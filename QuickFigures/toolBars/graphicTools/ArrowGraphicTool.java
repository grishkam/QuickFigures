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
 * Date Modified: Nov 28, 2021
 * Version: 2023.2
 */
package graphicTools;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.Icon;

import applicationAdapters.ImageWorkSheet;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.ArrowGraphic;
import icons.TreeIconWrappingToolIcon;
import locatedObject.LocatedObject2D;
import standardDialog.StandardDialog;

/**A tool to draw an arrow. If the number of arrow heads is set to 0, this is just a tool to draw a line (arrow without heads)*/
public class ArrowGraphicTool extends GraphicTool implements ShapeAddingTool{
	
	ArrowGraphic model = new ArrowGraphic(); {} {super.temporaryTool=true;}
	
	
	void setUpModel() {super.iconSet=TreeIconWrappingToolIcon.createIconSet(model);model.setStrokeColor(Color.black);}
		{setUpModel(); }
	public ArrowGraphicTool() {
	}
	
	public ArrowGraphicTool(int head) {
		this(head, false);
	}
	
	/**constructs a tool
	 * @param tail determines if this tool creates arrow with a distinct head and tail*/
	public ArrowGraphicTool(int nHead, boolean tail) {
		
		if(tail) {nHead=2; }
		ArrowGraphic arrow = new ArrowGraphic();
		{
			model = arrow;
			model.setNumerOfHeads(nHead);
			model.getHead().setArrowStyle(ArrowGraphic.NORMAL_HEAD);
			
			if(tail) {
				arrow.setHeadsSame(false);
				arrow.getHead(ArrowGraphic.SECOND_HEAD).setArrowStyle(ArrowGraphic.FEATHER_TAIL);
			}
		}
		setUpModel();
	}
	
	
	
	
	public void onPress(ImageWorkSheet gmp, LocatedObject2D roi2) {
	
		if (getPrimarySelectedObject() instanceof ArrowGraphic) return;
		int cx = getClickedCordinateX();
		int cy = getClickedCordinateY();
		ArrowGraphic bg = createArrow(cx, cy);
		setPrimarySelectedObject(bg);
		setSelectedHandleNumber(1);
		super.setPressedSmartHandle(bg.getSmartHandleList().getHandleNumber(1));;
		
		GraphicLayer layer = findLayerForObjectAddition(gmp, bg);
				layer.add(bg);
				addUndoerForAddItem(gmp, layer, bg);
				
		gmp.updateDisplay();
		
		
	}
	
	/**
	creates the arrow at the given point
	 */
	public ArrowGraphic createArrow(double d, double e) {
		ArrowGraphic bg = new ArrowGraphic();
		
		bg.setHeadsSame(model.headsAreSame());
		bg.copyAttributesFrom(model);
		bg.copyArrowAtributesFrom(model);
		bg.copyColorsFrom(model);
		
		
		bg.setLocation(d, e);
		return bg;
	}
	
	/**creates an arrow that stretches from one end of the rectangle to another*/
	public ArrowGraphic createShape(Rectangle r) {
		ArrowGraphic createArrow = createArrow(r.getX(), r.getY());
		createArrow.setPoints(new Point2D.Double(r.getX(), r.getY()), new Point2D.Double(r.getMaxX(), r.getMaxY()));
		return createArrow;
	}
	
	

	
	
	@Override
	public String getToolTip() {
			return "Draw an Arrow";
		}
	

	@Override
	public String getToolName() {
		return "Draw "+ getShapeName();
	}
	
	public String getShapeName() {
		if(model.getNHeads()==0)
			return "Line";
		return "Arrow";
	}
	public Icon getIcon() {
		return model.getTreeIcon();
	}

	public ArrowGraphic getModelArrow() {
		return model;
	}

	@Override
	protected StandardDialog getOptionsDialog() {
		return model.getOptionsDialog();
	}

}

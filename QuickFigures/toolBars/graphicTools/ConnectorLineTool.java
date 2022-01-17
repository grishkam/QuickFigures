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
 * Version: 2022.0
 */
package graphicTools;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.Icon;

import applicationAdapters.ImageWorkSheet;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.ConnectorGraphic;
import handles.ConnectorHandleList;
import icons.TreeIconWrappingToolIcon;
import locatedObject.LocatedObject2D;
import standardDialog.StandardDialog;

/**A tool to draw an arrow. If the number of arrow heads is set to 0, this is just a tool to draw a line (arrow without heads)*/
public class ConnectorLineTool extends GraphicTool implements ShapeAddingTool{
	
	ConnectorGraphic model = new ConnectorGraphic(true, new Point2D[] {new Point2D.Double()}); {} {super.temporaryTool=true;}
	
	
	void setUpModel() {super.iconSet=TreeIconWrappingToolIcon.createIconSet(model);model.setStrokeColor(Color.black);}
		{setUpModel(); }
	public ConnectorLineTool() {
	}
	
	
	
	/**constructs a tool
	 * @param tail determines if this tool creates arrow with a distinct head and tail*/
	public ConnectorLineTool(int nAnchors, boolean horizontalStart) {
		
		
		
		Point2D.Double[] anchors=new Point2D.Double[nAnchors];
		for(int i=0; i<nAnchors; i++) anchors[i]=new Point2D.Double();
		setUpModel();
		model.setAnchors(anchors);
		model.setHorizontal(horizontalStart);
	}
	
	
	
	
	public void onPress(ImageWorkSheet gmp, LocatedObject2D roi2) {
	
		if (getPrimarySelectedObject() instanceof ConnectorGraphic) return;
		int cx = getClickedCordinateX();
		int cy = getClickedCordinateY();
		ConnectorGraphic bg = createLine(cx, cy, 15);
		
		setPrimarySelectedObject(bg);
		int handle = ConnectorHandleList.getHandleIDForAnchor(bg.getAnchors().length-1);
		setSelectedHandleNumber(handle);
		super.setPressedSmartHandle(bg.getSmartHandleList().getHandleNumber(handle));;
		
		GraphicLayer layer = findLayerForObjectAddition(gmp, bg);
				layer.add(bg);
				addUndoerForAddItem(gmp, layer, bg);
				
		gmp.updateDisplay();
		
		
	}
	
	/**
	creates a horizontal line at the given point
	 */
	public ConnectorGraphic createLine(double lx, double ly, double length) {
		ConnectorGraphic bg = new ConnectorGraphic(true, new Point2D.Double(lx,ly), new Point2D.Double(lx+length,ly-length/2), new Point2D.Double(lx+length*2,ly));
		if(!model.isHorizontal())
			 bg= new ConnectorGraphic(false, new Point2D.Double(lx,ly), new Point2D.Double(lx+length/2,ly+length), new Point2D.Double(lx,ly+length*2));
		
		if(model.nAnchors()==2) {
			bg = new ConnectorGraphic(true, new Point2D.Double(lx,ly), new Point2D.Double(lx+length,ly-length/2));
		}
		if(model.nAnchors()==2&&!model.isHorizontal()) {
			bg = new ConnectorGraphic(false, new Point2D.Double(lx,ly), new Point2D.Double(lx+length/2,ly+length));
		}
		
		bg.copyAttributesFrom(model);
		bg.copyColorsFrom(model);
		
		return bg;
	}
	
	/**creates an arrow that stretches from one end of the rectangle to another*/
	public ConnectorGraphic createShape(Rectangle r) {
		ConnectorGraphic createArrow = createLine(r.getX(), r.getY(), r.width);
		
		return createArrow;
	}
	
	

	
	@Override
	public String getToolTip() {
			return "Draw an horizontal or vertical line";
		}
	

	@Override
	public String getToolName() {
		return "Draw "+ getShapeName();
	}
	
	public String getShapeName() {
		return this.getModelObject().getShapeName();
	}
	public Icon getIcon() {
		return model.getTreeIcon();
	}

	public ConnectorGraphic getModelObject() {
		return model;
	}



	@Override
	protected StandardDialog getOptionsDialog() {
		return model.getOptionsDialog();
	}

}

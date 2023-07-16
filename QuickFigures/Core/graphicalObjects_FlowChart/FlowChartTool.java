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
package graphicalObjects_FlowChart;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

import javax.swing.Icon;

import applicationAdapters.ImageWorkSheet;
import graphicTools.GraphicTool;
import graphicTools.ShapeAddingTool;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import icons.TreeIconWrappingToolIcon;
import locatedObject.LocatedObject2D;
import standardDialog.StandardDialog;
import undo.Edit;

/**A tool to draw the first part of a flow chart. work in progress*/
public class FlowChartTool extends GraphicTool {
	
	ArrowGraphic model = new ArrowGraphic(); {} {super.temporaryTool=true;}
	
	
	void setUpModel() {super.iconSet=TreeIconWrappingToolIcon.createIconSet(model);model.setStrokeColor(Color.black);}
		{setUpModel(); }
	public FlowChartTool() {
	}
	
	
	/**constructs a tool
	
	
	
	

	
	/**
	creates the arrow at the given point
	 */
	public FlowChart createFlowChartWithArrow(Rectangle2D r) {
		FlowChart fc=new FlowChart("Flow Chart");
		
		
		
		RectangularGraphic r1 = new RectangularGraphic(new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(), r.getHeight()/3));
		r1.setStrokeColor(Color.PINK);
		RectangularGraphic r2 = new RectangularGraphic(new Rectangle2D.Double(r.getX(), r.getY()+r.getHeight()*2/3, r.getWidth(), r.getHeight()/3));
		r2.setStrokeColor(Color.green);
		
		
		ChartNexus cn = new ChartNexus(r2);
		ChartNexus cn2 = new ChartNexus(r1);
		fc.addItemToLayer(cn);
		fc.addItemToLayer(cn2);
	
		
		AnchorObjectGraphic line = new AnchorObjectGraphic(cn, cn2, null);
		
		Edit.addItem(cn.getParentLayer(),(ZoomableGraphic) line);
		return fc;
	}
	
	public void onPress(ImageWorkSheet gmp, LocatedObject2D roi2) {
		
	
		int cx = getClickedCordinateX();
		int cy = getClickedCordinateY();
		Rectangle2D.Double r = new Rectangle2D.Double(cx, cy, 40, 120);
		FlowChart bg = createFlowChartWithArrow(r);
		
		GraphicLayer layer = findLayerForObjectAddition(gmp, bg);
				layer.add(bg);
				addUndoerForAddItem(gmp, layer, bg);
				
		gmp.updateDisplay();
		
		
	}
	
	

	
	
	@Override
	public String getToolTip() {
			return "Draw a Flow Chart";
		}
	

	@Override
	public String getToolName() {
		return "Draw "+ getShapeName();
	}
	
	public String getShapeName() {
	
		return "Flow Chart";
	}
	public Icon getIcon() {
		return model.getTreeIcon();
	}

	public ArrowGraphic getModelArrow() {
		return model;
	}

	@Override
	protected StandardDialog getOptionsDialog() {
		return null; 
	}


	

}

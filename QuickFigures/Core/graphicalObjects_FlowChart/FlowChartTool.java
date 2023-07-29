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
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import icons.TreeIconWrappingToolIcon;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import standardDialog.StandardDialog;
import standardDialog.graphics.GraphicDisplayComponent;
import undo.Edit;

/**A tool to draw the first part of a flow chart. work in progress*/
public class FlowChartTool extends GraphicTool {
	
	ArrowGraphic model = new ArrowGraphic();
	private int cx;
	private int cy;
	private int cx2;
	private int cy2; {} {super.temporaryTool=true;}
	
	
	void setUpModel() {super.iconSet=TreeIconWrappingToolIcon.createIconSet(new ChartNexus(new RectangularGraphic()), 2, 4);
		model.setStrokeColor(Color.black);
		}
		{setUpModel(); }
		
		
		
	public FlowChartTool() {
	}
	
	
	/**returns the icon*/
	public Icon getTreeIcon() {
		return new GraphicDisplayComponent(ChartNexus.createIcon() );
	}
	

	
	public int determineSteps(double ratio) {
		
		int output=1;
		
		output=((int)ratio)-1;
		if(output<1)
			output=1;
		
		return output;
	}
	
	

	
	/**
	creates the arrow at the given point
	 */
	public FlowChart createFlowChartWithArrow(Rectangle2D r) {
		FlowChart fc=new FlowChart("Flow Chart");
		
		//3.0 for boxes that take up one third of the drawn area
		int nSteps=1;
		boolean widthIsLoingAxis = r.getWidth()>r.getHeight();
		
		if(widthIsLoingAxis)
			nSteps=determineSteps(r.getWidth()/r.getHeight());
		if(!widthIsLoingAxis)
			nSteps=determineSteps( r.getHeight()/r.getWidth());
		
		

		double divisionSize=1.0+nSteps*2.0;//3.0;
		
		double hratio=1.0/divisionSize;
		double wratio=1.0;
		
		
		
		if(widthIsLoingAxis) {
			 hratio=1;
			 wratio=1.0/divisionSize;
		}
		
		
		RectangularGraphic r1 =null;
		RectangularGraphic r2 =null;
		
		ChartNexus cn=null;
		ChartNexus cn2 =null;
		
		for(int i=1; i<=nSteps; i++) {
			
			double boxWidth = r.getWidth()*wratio;
			double boxHeight = r.getHeight()*hratio;
			if(r1==null)
				 r1 = new RectangularGraphic(new Rectangle2D.Double(r.getX(), r.getY(), boxWidth, boxHeight));
			else 
				r1=r2;
			
			//if(r2==null)
				 r2 = new RectangularGraphic(new Rectangle2D.Double(r.getX()+boxWidth*i*2*(1-wratio), r.getY()+boxHeight*(i)*2*(1-hratio), boxWidth, boxHeight));
				
				r1.setStrokeColor(Color.PINK);
				r2.setStrokeColor(Color.green);
				
				
				if(cn==null) {
					cn = new ChartNexus(r1);
					fc.addItemToLayer(cn);
					}
				else cn=cn2;
				
				
				cn2 = new ChartNexus(r2);
				
				fc.addItemToLayer(cn2);
			
				
				AnchorObjectGraphic line = new AnchorObjectGraphic(cn, cn2, null);
				ChartNexusSmartHandle.formatArrowForFlowChart(line);
		
				
				Edit.addItem(cn.getParentLayer(),(ZoomableGraphic) line);
		}
		
		
		return fc;
	}
	
	public void onPress(ImageWorkSheet gmp, LocatedObject2D roi2) {
		
	
		cx = getClickedCordinateX();
		cy = getClickedCordinateY();
		
		
		
	}
	
	

	@Override
	public void mouseDragged() { 
		super.mouseDragged();
		if(this.getImageDisplayWrapperClick().getSelectedItem() instanceof ChartNexus) {
			return;
		}
		
		FlowChart bg = prepareFlowChartBasedOnMouseDrag(this.getDragCordinateX(), this.getDragCordinateY());
		this.getImageDisplayWrapperClick().setSelectedItem(new GraphicGroup(bg));
		
		this.getImageDisplayWrapperClick().getImageAsWorksheet().getOverlaySelectionManagger().setSelectionGraphic(bg);
	}
	
	
	
	public void onRelease(ImageWorkSheet gmp, LocatedObject2D roi2) {
		if(gmp.getSelectionObject() instanceof ChartNexus) {
			return;
		}
		
		FlowChart bg = prepareFlowChartBasedOnMouseDrag(this.getReleaseCordinateX(), this.getReleaseCordinateY());
		
		GraphicLayer layer = findLayerForObjectAddition(gmp, bg);
				layer.add(bg);
				addUndoerForAddItem(gmp, layer, bg);
				
		gmp.updateDisplay();
		
		
	}


	/**
	 * @return
	 */
	public FlowChart prepareFlowChartBasedOnMouseDrag(double cx2, double cy2) {
		
		double hx = cx2-cx;
		double hy = cy2-cy;
		
		double w=30;
		double h=40;
		if(hy>h)
			h=hy;
		if(hx>w)
			w=hx;
		
		Rectangle2D.Double r = new Rectangle2D.Double(cx, cy, w, h);
		FlowChart bg = createFlowChartWithArrow(r);
		return bg;
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

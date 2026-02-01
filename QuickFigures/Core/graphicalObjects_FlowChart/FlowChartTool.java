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
 * Date Modified: Jan 31, 2026
 * Version: 2026.2
 */
package graphicalObjects_FlowChart;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

import applicationAdapters.ImageWorkSheet;
import graphicTools.GraphicTool;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.RegularPolygonGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import icons.TreeIconWrappingToolIcon;
import locatedObject.LocatedObject2D;
import locatedObject.RectangleEdges;
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
	private int cy2;
	private boolean press_on_chart;
	private FlowChart currentChart;
	private Rectangle2D currentSize; {}
	{super.temporaryTool=true;}
	
	
	void setUpModel() {super.iconSet=TreeIconWrappingToolIcon.createIconSet(new ChartNexus(new RectangularGraphic(), ""), 2, 4);
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
	 * @param allowDiagnol 
	 */
	public FlowChart createFlowChartWithArrow(Rectangle2D totalAreaForNewParts, boolean allowDiagnol) {
		FlowChart fc=new FlowChart("Flow Chart");
		
		//3.0 for boxes that take up one third of the drawn area
		int nSteps=1;
		double whRatio=totalAreaForNewParts.getWidth()/totalAreaForNewParts.getHeight();
		boolean widthIsLoingAxis = whRatio>1;
		double tolerance_for_square = 0.3;
		double tolerance_for__almost_gold_rect = 0.6;
		
		boolean almostSquare = (whRatio<1+tolerance_for_square) &&(whRatio>1-tolerance_for_square);
		boolean almostGoldRect = (whRatio<1+tolerance_for__almost_gold_rect) &&(whRatio>1-tolerance_for__almost_gold_rect) &&!almostSquare;
		
		if(widthIsLoingAxis)
			nSteps=determineSteps(totalAreaForNewParts.getWidth()/totalAreaForNewParts.getHeight());
		if(!widthIsLoingAxis)
			nSteps=determineSteps( totalAreaForNewParts.getHeight()/totalAreaForNewParts.getWidth());
		if(almostSquare)
			nSteps=1;
		if(almostGoldRect)
			nSteps=2;

		double divisionSize=1.0+nSteps*2.0;//3.0;
		
		double hratio=1.0/divisionSize;
		double wratio=1.0;
		
		double hDirection=1;
		double vDirection=1;
		
		if(widthIsLoingAxis) {
			 hratio=1;
			 wratio=1.0/divisionSize;
		}
		
		if(allowDiagnol ) {
			 hratio=1.0/divisionSize;
			 wratio=1.0/divisionSize;
		} else if(widthIsLoingAxis) {
			vDirection=0;
		} else {
			hDirection=0;
		}
		
		if(almostSquare ) {
			 hratio=0.333333333333;
			 wratio=hratio;
		}
		
		if(almostGoldRect ) {
			 hratio=0.2;
			 wratio=hratio;
		}
		
		
		
		
		ShapeGraphic previousNodeShape =null;
		ShapeGraphic r2 =null;
		
		ChartNexus cn=null;
		ChartNexus cn2 =null;
		
		for(int i=1; i<=nSteps; i++) {
			
			double boxWidth = totalAreaForNewParts.getWidth()*wratio;
			double boxHeight = totalAreaForNewParts.getHeight()*hratio;
			if(previousNodeShape==null) {
				 previousNodeShape = new RegularPolygonGraphic(new Rectangle2D.Double(totalAreaForNewParts.getX(), totalAreaForNewParts.getY(), boxWidth, boxHeight),8);
			if(almostSquare || (almostGoldRect&&i==1)) {
				if(widthIsLoingAxis) {
					previousNodeShape.setLocationType(RectangleEdges.LEFT);
					previousNodeShape.setLocation(RectangleEdges.getLocation(RectangleEdges.LEFT, totalAreaForNewParts));
				} else {
				previousNodeShape.setLocationType(RectangleEdges.TOP);
				previousNodeShape.setLocation(RectangleEdges.getLocation(RectangleEdges.TOP, totalAreaForNewParts));
				}
			}
			
			}else 
				previousNodeShape=r2;
			
			
			
			//if(r2==null)
				 //r2 = new RectangularGraphic(new Rectangle2D.Double(totalAreaForNewParts.getX()+boxWidth*i*2*(1-wratio), totalAreaForNewParts.getY()+boxHeight*(i)*2*(1-hratio), boxWidth, boxHeight));
				 r2 = new RegularPolygonGraphic(new Rectangle2D.Double(totalAreaForNewParts.getX()+boxWidth*i*2*hDirection, totalAreaForNewParts.getY()+boxHeight*(i)*2*vDirection, boxWidth, boxHeight), 8);
					
				 
				 if(almostSquare) {
					 previousNodeShape.setStrokeColor(Color.magenta);
				 } else if(almostGoldRect) {
					 previousNodeShape.setStrokeColor(Color.yellow.darker());
				 } else 
				previousNodeShape.setStrokeColor(Color.PINK);
				r2.setStrokeColor(Color.green);
				
				
				if(cn==null) {
					cn = new ChartNexus(previousNodeShape, "new node");
					fc.addItemToLayer(cn);
					}
				else cn=cn2;
				
				if ((!almostSquare && !almostGoldRect) ){
						cn2 = new ChartNexus(r2, "node");
						
						fc.addItemToLayer(cn2);
					
						
						AnchorObjectGraphic line = new AnchorObjectGraphic(cn, cn2, null);
						ChartNexusSmartHandle.formatArrowForFlowChart(line);
				
						
						Edit.addItem(cn.getParentLayer(),(ZoomableGraphic) line);
						cn=cn2;
				} else {
					if(cn!=null) {
						int count = almostSquare?3:i==2?3:1;
						
						new ChartNexusSmartHandle(cn).createNewNexus(count, widthIsLoingAxis? RectangleEdges.RIGHT: RectangleEdges.BOTTOM);
					}
					cn2=fc.getLastNexus();
					
					cn2.getShape().setStrokeColor(Color.cyan);
					cn=cn2;
					
					
				}
		}
		
		
		return fc;
	}
	
	public void onPress(ImageWorkSheet gmp, LocatedObject2D roi2) {
		
	
		cx = getClickedCordinateX();
		cy = getClickedCordinateY();
		super.onPress(gmp, roi2);
		
		if( FlowChart.isChartPart((ZoomableGraphic) gmp.getSelectionObject()) ) {
			press_on_chart=true;
			currentChart=FlowChart.findFlowChartfor( (ZoomableGraphic) gmp.getSelectionObject());
			if(currentChart!=null)
					{currentSize=currentChart.getNexusSize();}
			IssueLog.log(currentChart);
			IssueLog.log(currentSize);
		} else {
			
			press_on_chart=false;
			currentChart=null;
			currentSize=null;
			
		}
	}
	
	

	@Override
	public void mouseDragged() { 
		super.mouseDragged();
		
		if(press_on_chart){
			
			//return;
				}
		
	
		
		FlowChart bg = prepareFlowChartBasedOnMouseDrag(this.getDragCordinateX(), this.getDragCordinateY(), this.shiftDown());
		this.getImageDisplayWrapperClick().setSelectedItem(new GraphicGroup(bg));
		
		this.getImageDisplayWrapperClick().getImageAsWorksheet().getOverlaySelectionManagger().setSelectionGraphic(bg);
	}
	
	
	
	public void onRelease(ImageWorkSheet gmp, LocatedObject2D roi2) {
		if(press_on_chart)	{
			//return;
	
		}
		
		FlowChart bg = prepareFlowChartBasedOnMouseDrag(this.getReleaseCordinateX(), this.getReleaseCordinateY(), this.shiftDown());
		
		GraphicLayer layer = findLayerForObjectAddition(gmp, bg);
				layer.add(bg);
				addUndoerForAddItem(gmp, layer, bg);
				
		gmp.updateDisplay();
		
		
	}


	/**
	 * @return
	 */
	public FlowChart prepareFlowChartBasedOnMouseDrag(double cx2, double cy2, boolean allowDiagnol) {
		
		double hx = cx2-cx;
		double hy = cy2-cy;
		
		double w=30;
		double h=40;
		if(hy>h)
			h=hy;
		if(hx>w)
			w=hx;
		
		Rectangle2D.Double r = new Rectangle2D.Double(cx, cy, w, h);
		FlowChart bg = createFlowChartWithArrow(r, allowDiagnol);
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

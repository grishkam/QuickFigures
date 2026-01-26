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
 * Date Created: July 7, 2023
 * Date Modified: July 7, 2023
 * Version: 2023.2
 */
package graphicalObjects_FlowChart;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_Shapes.ShapeGraphic;
import locatedObject.PathPoint;

/**
 
 * 
 */
public class FlowChart extends GraphicLayerPane {

	/**
	 * 
	 */
	public static final String FLOW_CHART_PART = "FlowChartPart";
	public static final String FLOW_CHART_NEXUS = "ChatNexus";
	public ArrayList<AnchorAttachment> attachments=new ArrayList<AnchorAttachment>();
	
	
	/**
	 * @param name
	 */
	public FlowChart(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	
	@Override
	public void draw(Graphics2D graphics, CordinateConverter cords) {
			
			for(AnchorAttachment a: attachments)
				a.updateLocation();
		
			 updatePathsFromPoints();
		
		super.draw(graphics, cords);

	}
	
	
	/**
	 * @param aa
	 */
	public void addAttachment(AnchorAttachment aa) {
		
		attachments.add(aa);
		
	}
	
	/**
	 * updates the paths to reflect the changed in points
	 */
	private void updatePathsFromPoints() {
		for(AnchorAttachment a: attachments) {
			a.getPath().updatePathFromPoints();
		}
		
	}
	
	/**returns the attachment object with the same index as this path point
	 * @param pathPoint
	 * @return 
	 */
	public AnchorAttachment getAttachmentforPoint(PathPoint pathPoint) {
		for(AnchorAttachment a: attachments) {
		
			boolean match = a.getPathPointIndex()==a.getPath().getPoints().indexOf(pathPoint);
			if(match)
				return a;
		}
		
		return null;
		
	}
	
	
	public static boolean isChartPart(ZoomableGraphic z) {
			if(z==null)
				return false;
			if(z instanceof ChartNexus) {
				return true;
			}
			if(z.getParentLayer() instanceof ChartNexus) {
				return true;
			}
			if(z instanceof ShapeGraphic) {
				ShapeGraphic shape=(ShapeGraphic) z;
				if(shape.getTag(FLOW_CHART_PART)==Boolean.TRUE)
					return true;
				//shape.getTagHashMap().put("FlowChartPart", true);
			}
		
		return false;
	}
	
	public static FlowChart findFlowChartfor(ZoomableGraphic z) {
		if(z==null)
			return null;
		if(z instanceof ChartNexus) {
			return ((ChartNexus) z).getFlowChart();
		}
		if(z.getParentLayer() instanceof FlowChart) {
			return ((FlowChart) z.getParentLayer());
		}
		if(z instanceof ShapeGraphic) {
			ShapeGraphic shape=(ShapeGraphic) z;
			GraphicLayer l = z.getParentLayer();
			ChartNexus cn=(ChartNexus) shape.getTag(FLOW_CHART_NEXUS);
			return cn.getFlowChart();
		}
	
	return null;
}
	
	public Rectangle2D getNexusSize() {
		for(ZoomableGraphic i: this.getAllGraphics()) {
			if(i instanceof ChartNexus) {
				return ((ChartNexus) i).getShape().getBounds();
			}
		}
		
		return null;
	}
	
}

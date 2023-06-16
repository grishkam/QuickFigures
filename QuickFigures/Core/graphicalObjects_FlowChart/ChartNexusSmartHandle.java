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
 * Date Created: May 27, 2023
 * Date Modified: May 27, 2023
 * Version: 2023.2
 */
package graphicalObjects_FlowChart;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEvent;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import handles.SmartHandle;
import imageDisplayApp.OverlayObjectManager;
import layout.BasicObjectListHandler;
import locatedObject.LocatedObject2D;

/**
 A handle on a flow shar nexus that allows one to draw new connections
 */
public class ChartNexusSmartHandle extends SmartHandle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Point pressLocation;
	private ChartNexus nexus;
	
	public ChartNexusSmartHandle(ChartNexus nexus) {
		this.nexus=nexus;
		
	}
	
	/**called when a user drags a handle */
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		Point2D l1 = this.getCordinateLocation();
		Point2D l2 = lastDragOrRelMouseEvent.getCoordinatePoint();
		OverlayObjectManager sel = super.getSelectionMangerForEvent(lastDragOrRelMouseEvent);
		ArrowGraphic ag = new ArrowGraphic(l1, l2);
		ag.setStrokeColor(Color.pink);
		sel.setSelectionGraphic(ag);
	}
	
	/**called when a user pressed a handle */
	public void handlePress(CanvasMouseEvent lastDragOrRelMouseEvent) {
		pressLocation = lastDragOrRelMouseEvent.getCoordinatePoint();
		
	}
	
	
	/**called when a user releases a handle */
	public void handleRelease(CanvasMouseEvent lastDragOrRelMouseEvent) {
		double d=pressLocation.distance(lastDragOrRelMouseEvent.getCoordinatePoint());
		
		if(d>30) {
			int rectSize=40;
			RectangularGraphic r1 = new RectangularGraphic(new Rectangle(lastDragOrRelMouseEvent.getCoordinateX()-rectSize/2,lastDragOrRelMouseEvent.getCoordinateY()-rectSize/2, rectSize, rectSize));
			r1.setStrokeColor(Color.black);
			
			ChartNexus cn2 = new ChartNexus(r1);
			BasicObjectListHandler oh=new BasicObjectListHandler();
			ArrayList<LocatedObject2D> therois = oh.getAllClickedRoi(lastDragOrRelMouseEvent.getAsDisplay().getImageAsWorksheet(),lastDragOrRelMouseEvent.getCoordinatePoint(), ChartNexus.class);
			if(therois.size()>0) {
				cn2=(ChartNexus) therois.get(0);//use an existing roi if possible
			 }else
			    lastDragOrRelMouseEvent.getAsDisplay().getImageAsWorksheet().addItemToImage(cn2);
			
			
			Point2D m = PathGraphic.midPoint(pressLocation,lastDragOrRelMouseEvent.getCoordinatePoint());
			AnchorObjectGraphic line = new AnchorObjectGraphic(nexus, cn2, m);
			line.addArrowHeads(2);
			line.getArrowHead2().getHead().setArrowHeadSize(16);;
			line.setStrokeWidth(4);
			
			lastDragOrRelMouseEvent.getAsDisplay().getImageAsWorksheet().addItemToImage(line);
		}
		
	}

}

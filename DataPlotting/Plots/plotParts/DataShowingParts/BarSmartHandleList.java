/**
 * Author: Greg Mazo
 * Date Modified: Dec 27, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package plotParts.DataShowingParts;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEvent;
import genericPlot.BasicPlot;
import handles.SmartHandle;
import handles.SmartHandleList;
import utilityClassesForObjects.RectangleEdges;

/**
 
 * 
 */
public class BarSmartHandleList extends SmartHandleList {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataBarShape bar;

	/**
	 * @param dataBarShape
	 */
	public BarSmartHandleList(DataBarShape dataBarShape) {
		this.bar=dataBarShape;
		this.add(new BarWidthSmartHandle());
		
	}
	
	/**
	 
	 * 
	 */
public class BarWidthSmartHandle extends SmartHandle {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**location of the handle. this determines where in the figure the handle will actually appear
		   overwritten in many subclasses*/
		public Point2D getCordinateLocation() {
			Point2D location = RectangleEdges.getLocation(RectangleEdges.RIGHT, bar.getBounds());
			return location;
		}
		
		
		/**called when a user drags a handle, changes the width of the data bars in the plot
		  if shift is down, only alters one data bar */
		public void handleDrag(CanvasMouseEvent mouse) {
			Point2D location = RectangleEdges.getLocation(RectangleEdges.CENTER, bar.getBounds());
			double shift = mouse.getCoordinateX()-location.getX();
			bar.setBarWidth(shift);
			bar.requestShapeUpdate();
			
			BasicPlot plot = BasicPlot.findPlot(bar);
			ArrayList<DataBarShape> bars = plot.getMeanBars();
			if (!mouse.shfitDown())for(DataBarShape b: bars) {
				b.setBarWidth(shift);
				b.requestShapeUpdate();
			}
			bar.updatePlotArea();
			
			
		}
		
	
}


}

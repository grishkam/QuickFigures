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
 * Date Modified: Jan 7, 2021
 * Version: 2021.1
 */
package plotParts.DataShowingParts;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEvent;
import genericPlot.BasicPlot;
import handles.SmartHandle;
import handles.SmartHandleList;
import locatedObject.RectangleEdges;

/**
A handle list with a special handle for the width of bars
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
	 a handle used to alter the width of data bars
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

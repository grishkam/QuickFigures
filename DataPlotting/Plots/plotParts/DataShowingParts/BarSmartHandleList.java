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
 * Date Modified: Nov 12, 2021
 * Version: 2021.2
 */
package plotParts.DataShowingParts;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEvent;
import genericPlot.BasicPlot;
import handles.SmartHandle;
import handles.SmartHandleList;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import standardDialog.StandardDialog;
import undo.CombinedEdit;
import undoForPlots.DataShapeUndo;
import undoForPlots.PlotAreaChangeUndo;

/**
A handle list with a special handle for the width of bars
 */
public class BarSmartHandleList extends SmartHandleList {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataShowingShape bar;

	/**
	 * @param dataBarShape
	 */
	public BarSmartHandleList(DataShowingShape dataBarShape) {
		this.bar=dataBarShape;
		this.add(new BarWidthSmartHandle(bar));
		
	}
	
	/**
	 a handle used to alter the width of data bars
	 */
public static class BarWidthSmartHandle extends SmartHandle {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private DataShowingShape theBar;
		private transient CombinedEdit undo;
		
		/**
		 * @param bar
		 */
		public BarWidthSmartHandle(DataShowingShape bar) {
			this.theBar=bar;
		}


		/**location of the handle. this determines where in the figure the handle will actually appear
		   overwritten in many subclasses*/
		public Point2D getCordinateLocation() {
			Point2D location = RectangleEdges.getLocation(RectangleEdges.RIGHT, theBar.getBounds());
			return location;
		}
		
		
		/**called when a user drags a handle, changes the width of the data bars in the plot
		  if shift is down, only alters one data bar */
		public void handleDrag(CanvasMouseEvent mouse) {
			Point2D location = RectangleEdges.getLocation(RectangleEdges.CENTER, theBar.getBounds());
			double shift = mouse.getCoordinateX()-location.getX();
			boolean shiftDown = mouse.shiftDown();
			
			setBarWidths(theBar, shift, shiftDown);
			
			///Update plot
			
		}


		/**sets the width of the shape
		 * @param newBarWidth
		 * @param shiftDown
		 */
		protected void setBarWidths(DataShowingShape theTargetShape, double newBarWidth, boolean shiftDown) {
			ArrayList<? extends DataShowingShape> bars = this.getTargets(theTargetShape, shiftDown);
			for(DataShowingShape b: bars) {
				b.setBarWidth(newBarWidth);
				b.requestShapeUpdate();
			}
		}
		
		
		/**returns all targets shapes
		 * @param theTargetShape the primary target shape
		 * */
		protected ArrayList<? extends DataShowingShape> getTargets(DataShowingShape theTargetShape, boolean shiftDown) {
			ArrayList<DataShowingShape> output = new ArrayList<DataShowingShape> ();
			output.add(theTargetShape);
			
			/**sets the width of every bar*/
			BasicPlot plot = BasicPlot.findPlot(theTargetShape);
			ArrayList<? extends DataShowingShape> bars = plot.getDataShape(theTargetShape.getClass());
			
			
			if (!shiftDown) {
				output.addAll(bars);
			}
			return output;
		}
		
	
		/**Called when a handle is pressed*/
		public void handlePress(CanvasMouseEvent mouse) {
			
			
			/**shows a dialog for this handle*/
			if (mouse.clickCount()==2) {
				Double n = StandardDialog.getNumberFromUser("Input Width", theBar.getBarWidth());
				prepareUndoForHandle();
				if(n!=null && n>0.1)
					{
					setBarWidths(theBar, n, mouse.shiftDown());
					mouse.addUndo(undo);
					}
			}
			
			
				prepareUndoForHandle();
				
			
			
		}


		/**
		 * Creates an undoable edit for changes to the bar width
		 */
		protected void prepareUndoForHandle() {
			ArrayList<? extends DataShowingShape> targets = this.getTargets(theBar, false);
			CombinedEdit undo=new CombinedEdit();
			undo.addEditToList(new PlotAreaChangeUndo(BasicPlot.findPlot(theBar)));
			for(DataShowingShape t: targets) {
				undo.addEditToList(new DataShapeUndo(t));
			}
			this.undo=undo;
		}
		
		/**Called when a handle is pressed*/
		public void handleRelease(CanvasMouseEvent mouse) {
			
			super.handleRelease(mouse);
			if(undo!=null && !undo.empty()) {
			
				mouse.addUndo(undo);
			}
			
		}
		
}


}

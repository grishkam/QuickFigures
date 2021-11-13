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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEvent;
import genericPlot.BasicPlot;
import handles.SmartHandle;
import handles.SmartHandleList;
import locatedObject.RectangleEdges;
import plotTools.ColumnSwapTool;
import standardDialog.StandardDialog;
import undo.CombinedEdit;
import undoForPlots.DataShapeUndo;
import undoForPlots.PlotAreaChangeUndo;

/**
A handle list with a special handle for the width of bars
 */
public class DataBarSmartHandleList extends SmartHandleList {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataShowingShape bar;

	/**
	 * @param dataBarShape
	 */
	public DataBarSmartHandleList(DataShowingShape dataBarShape) {
		this.bar=dataBarShape;
		this.add(new BarWidthSmartHandle(bar));
		
		
		this.add(new OrderSwapSmartHandle(bar));
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
			Rectangle bounds = getRectangleBar();
			Point2D location = RectangleEdges.getLocation(RectangleEdges.RIGHT, bounds);
			if(!theBar.onVertical()) {
				location = RectangleEdges.getLocation(RectangleEdges.BOTTOM, bounds);
			}
			return location;
		}
		
		/**returns the rectangle that has the shape attached to it*/
		public Rectangle getRectangleBar() {
			Rectangle bounds = theBar.getBounds();
			Shape partial = theBar.getLastPartialShape();
			if(partial!=null)
				bounds=partial.getBounds();
			return bounds;
		}
		
		/**called when a user drags a handle, changes the width of the data bars in the plot
		  if shift is down, only alters one data bar */
		public void handleDrag(CanvasMouseEvent mouse) {
			
			Rectangle barBounds = getRectangleBar();
					
			Point2D location = RectangleEdges.getLocation(RectangleEdges.CENTER, barBounds);
			double shift = mouse.getCoordinateX()-location.getX();
			if(!theBar.onVertical()) {
				shift = mouse.getCoordinateY()-location.getY();
			}
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
		
		
		
		
		/**returns all targets shapes that will be modifed along with the primary shape
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

/**A handle that allows one to reorder the data series*/
public static class OrderSwapSmartHandle extends SmartHandle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataShowingShape theShape;
	private ColumnSwapTool tool;
	
	/**
	 * @param shape
	 */
	public OrderSwapSmartHandle(DataShowingShape shape) {
		this.theShape=shape;
		tool=new ColumnSwapTool();
		tool.setPressShape(theShape);
		this.setHandleNumber(879023);
		this.setEllipseShape(true);
		this.setHandleColor(shape.getFillColor());
		setupSpecialShape() ;
	}
	
	/**called when a user drags a handle */
	public void handlePress(CanvasMouseEvent m) {
		tool.setPressShape(theShape);
		tool.alternativeMouseEvent=m;
		setupSpecialShape() ;
	}
	
	/**called when a user drags a handle */
	public void handleDrag(CanvasMouseEvent m) {
		tool.onDragWithinImage(m.getCoordinateX(), m.getCoordinateY(), m.getAsDisplay().getImageAsWorksheet());
		
	}
	
	/**called when a user drags a handle */
	public void handleRelease(CanvasMouseEvent m) {
		m.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger().clear();
		tool.performSwap();
	}
	
	/**location of the handle. this determines where in the figure the handle will actually appear
	   overwritten in many subclasses*/
	public Point2D getCordinateLocation() {
		Point2D location = RectangleEdges.getLocation(RectangleEdges.TOP, theShape.getBounds());
		double y = location.getY()-20;
		double x = location.getX();
		return new Point2D.Double(x, y);
	}
	
	
	/**sets up the arrow shape that will indicate what the handle does*/
	public void setupSpecialShape() {
		
			
		if (specialShape==null) {
			if(!theShape.onVertical()) {
				specialShape=super.getUpDownArrowShape(4, 3);
			}
			else if (this.theShape.onVertical()) {
				specialShape=super.createLeftRightArrow(4,3);
			}
			else 
									{
					specialShape=getAllDirectionArrows(3, 2, false);
					}
		
		}
	}
	
}


}

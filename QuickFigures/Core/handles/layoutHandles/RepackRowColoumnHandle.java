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
 * Date Modified: Jan 5, 2021
 * Version: 2022.0
 */
package handles.layoutHandles;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import applicationAdapters.CanvasMouseEvent;
import genericMontageLayoutToolKit.RowColNumberTool;

import java.awt.geom.Rectangle2D;

import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import handles.SmartHandle;
import imageDisplayApp.CanvasOptions;
import imageMenu.CanvasAutoResize;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.BasicLayoutEditor;
import layout.basicFigure.LayoutSpaces;
import undo.CanvasResizeUndo;
import undo.CombinedEdit;
import undo.UndoLayoutEdit;

/**a handle that allows the user to pack the layout panels into a different number of rows and columns 
  For example, user can drag handle to easily transform a 1*6 layout into 2*3, 3*2 or6*1*/
public class RepackRowColoumnHandle extends SmartHandle implements LayoutSpaces{

	private static final int PLUS_SIZE = 3;
	protected DefaultLayoutGraphic layout;
	protected int type;
	protected int index;
	private UndoLayoutEdit currentUndo;
	private CombinedEdit combinedUndo;

	

	public RepackRowColoumnHandle(DefaultLayoutGraphic montageLayoutGraphic) {
		
		this.layout=montageLayoutGraphic;
		setHandleColor(Color.red);
		this.specialShape=addOrSubtractSymbol(PLUS_SIZE, false);
		
		setupSpecialShape();
		Rectangle2D space = layout.getPanelLayout().getSelectedSpace(1, ALL_OF_THE+PANELS).getBounds();
		
		this.setHandleNumber(PanelLayoutGraphic.RepackPanelsHandle);
		
		double y2 = space.getMaxY()+25;
		
		double x2 = space.getMaxX()+25;
		
		this.setCordinateLocation(new Point2D.Double(x2, y2));
	
	}
	
	protected boolean hasSpecialShape() {;
	
		setupSpecialShape();
		return specialShape!=null;
	}

	/**sets up the arrow shapes*/
	public void setupSpecialShape() {
	
	}

	private static final long serialVersionUID = 1L;
	
	public boolean containsClickPoint(Point2D p) {
		return super.containsClickPoint(p);
	}
	
	
	/***/
	public void handleRelease(CanvasMouseEvent canvasMouseEventWrapper) {
		if(currentUndo!=null) currentUndo.establishFinalState();
		canvasMouseEventWrapper.getAsDisplay().getUndoManager().addEdit(combinedUndo);
		canvasMouseEventWrapper.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger().setSelectionstoNull();
	
	}
	
	public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
		currentUndo = new UndoLayoutEdit(layout);//establishes the undo
		combinedUndo=new CombinedEdit(currentUndo);
	}
	
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		Point p2 = lastDragOrRelMouseEvent.getCoordinatePoint();
		BasicLayout current = layout.getPanelLayout();
		BasicLayoutEditor edit = layout.getEditor();
		int[] proposedRowColChange = RowColNumberTool.findAddedRowsCols((int)p2.getX(), (int)p2.getY(), current);
		
		int r=current.nRows();
		int c=current.nColumns();
		if (proposedRowColChange[0]+current.nRows()>=1 )r= proposedRowColChange[0]+current.nRows();
		if (proposedRowColChange[1]+current.nColumns()>=1 )c= proposedRowColChange[1]+current.nColumns();
		
			edit.repackagePanels(layout.getPanelLayout(), r, c);
		
		
		if (CanvasOptions.current.resizeCanvasAfterEdit)
			{
			CanvasResizeUndo undoCanvas = new CanvasAutoResize(false).performUndoableAction(lastDragOrRelMouseEvent.getAsDisplay());
			combinedUndo.addEditToList(undoCanvas);
			}
	}




}

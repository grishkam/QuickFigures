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
 * Date Created: Jan 5, 2021
 * Version: 2023.2
 */
package handles.layoutHandles;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;

import applicationAdapters.CanvasMouseEvent;
import figureOrganizer.FigureScaler;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import handles.ReshapeHandleList;
import handles.ReshapeHandleList.ReshapeSmartHandle;
import handles.SmartHandle;
import layout.basicFigure.LayoutSpaces;
import locatedObject.LocatedObject2D;
import locatedObject.RectangleEdges;
import messages.ShowMessage;
import undo.CombinedEdit;
import undo.UndoLayoutEdit;

/**Work in progress a handle that allows the user to scale all objects that are part of the layout
 */
public class ScaleLayoutHandle extends SmartHandle implements LayoutSpaces{

	protected DefaultLayoutGraphic layout;
	
	private UndoLayoutEdit currentUndo;
	private CombinedEdit combinedUndo;

	private ReshapeHandleList reshapeer;//the reshape handle list used
	private ReshapeSmartHandle handleOfType;//the reshape handle being used

	private ArrayList<LocatedObject2D> locatedObjects;//list of objects being scaled

	
	
	boolean dragStarted=false;

	

	public ScaleLayoutHandle(DefaultLayoutGraphic montageLayoutGraphic) {
		this.layout=montageLayoutGraphic;
		updateLocation();
		this.setHandleNumber(PanelLayoutGraphic.SCALE_HANDLE);
		
		locatedObjects = montageLayoutGraphic.generateStandardImageWrapper().getLocatedObjects();
		locatedObjects.add(0, montageLayoutGraphic);
		reshapeer=new ReshapeHandleList(locatedObjects, false) ;
		
		this.setHandleColor(Color.pink.darker());
		this.handlesize=2;
	}

	/**
	 * 
	 */
	private void updateLocation() {
		Rectangle b = layout.getOutline().getBounds();
		this.setCordinateLocation(new Point2D.Double(b.getMaxX(), b.getMaxY()));
	}
	



	private static final long serialVersionUID = 1L;
	

	
	
	/***/
	public void handleRelease(CanvasMouseEvent canvasMouseEventWrapper) {
		if(!dragStarted) return;
		double factor = handleOfType.getWorkingScaleFactor();
		Rectangle b = layout.getBounds();
		Double loc = new Point2D.Double(b.getX(), b.getY());
		combinedUndo.addEditToList(
								new FigureScaler(false).scaleFigure(layout, factor,loc)
								);
		if(currentUndo!=null) currentUndo.establishFinalState();
		canvasMouseEventWrapper.getAsDisplay().getUndoManager().addEdit(combinedUndo);
		canvasMouseEventWrapper.getAsDisplay().getImageAsWorksheet().getOverlaySelectionManagger().setSelectionstoNull();
		
		updateLocation();
	}
	
	public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
		dragStarted=false;
		
		ShowMessage.showOptionalMessage("Scale: this handle is a work in progress", true, "Dragging this handle will resize all objects in the layout");
		
		currentUndo = new UndoLayoutEdit(layout);//establishes the undo
		combinedUndo=new CombinedEdit(currentUndo);
		
		
		handleOfType = reshapeer.getHandleOfType(RectangleEdges.LOWER_RIGHT);
		handleOfType.handlePress(canvasMouseEventWrapper);;
		updateLocation();
		
	}
	
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		
		handleOfType.handleDrag(lastDragOrRelMouseEvent);;
		dragStarted=true;
		
	}




}

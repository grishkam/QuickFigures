/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package graphicalObjectHandles;

import java.awt.Color;
import java.awt.geom.Point2D;
import applicationAdapters.CanvasMouseEvent;
import java.awt.geom.Rectangle2D;

import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import gridLayout.BasicMontageLayout;
import gridLayout.GenericMontageEditor;
import gridLayout.LayoutSpaces;
import undo.UndoLayoutEdit;

/**a handle that moves the rows and columns of the layout around by swapping their contents*/
public class MoveRowHandle extends SmartHandle implements LayoutSpaces{

	protected MontageLayoutGraphic layout;
	protected int type;
	private boolean left=false;
	protected int index;
	private int endIndex;

	public MoveRowHandle(int x, int y) {
		
		// TODO Auto-generated constructor stub
	}

	public MoveRowHandle(MontageLayoutGraphic montageLayoutGraphic, int y, boolean sub, int index) {
		this(0,0);
		this.left=sub;
		this.layout=montageLayoutGraphic;
		this.type=y;
		this.index=index;
		this.handlesize=8;
		int offset=0;
		if(left) offset=-offset;
		int xoffset=0;
		int yoffset = 0;
		if (type==ROWS) yoffset=offset;
				  else xoffset=offset;
		
		Rectangle2D space = layout.getPanelLayout().getSelectedSpace(index, type).getBounds();
		double x2 = space.getMaxX()+xoffset;
		double y2 = space.getCenterY()+yoffset;
		if(type==COLS) {
			y2 = space.getMaxY()+yoffset;
			x2 = space.getCenterX()+xoffset;
		}
		
		if(type==PANELS) {
			y2 = space.getCenterY()+yoffset;
			x2 = space.getCenterX()+xoffset;
		}
		
		
		this.setCordinateLocation(new Point2D.Double(x2, y2));
	
		setHandleColor(Color.LIGHT_GRAY);
		
		setupSpecialShape();
		
		this.setHandleNumber(PanelLayoutGraphic.handleIDFactor*20+type*1000+offset+1*index);
		
	}
	
	protected boolean hasSpecialShape() {
		setupSpecialShape();
		return specialShape!=null;
	}

	/**sets up the arrow shapes*/
	public void setupSpecialShape() {
		if (specialShape==null) {
				if (type==COLS)
				specialShape=createLeftRightArrow(this.handlesize, 2);
				else if(type==ROWS) specialShape=getUpDownArrowShape(handlesize, 2);
				else if(type==PANELS)
					{
					specialShape=getAllDirectionArrows(5, 4, true);
					}
		
		}
	}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public boolean containsClickPoint(Point2D p) {
		return super.containsClickPoint(p);
	}
	
	public void handleDrag(CanvasMouseEvent canvasMouseEventWrapper) {
		super.handleDrag(canvasMouseEventWrapper);
		
		BasicMontageLayout makeAltered = layout.getPanelLayout().makeAltered(type);
		int startIndex = index;
		endIndex = makeAltered.getNearestPanelIndex(canvasMouseEventWrapper.getCoordinatePoint());
		
		Rectangle2D startingPanel = makeAltered.getPanel(startIndex);
		Rectangle2D ending = makeAltered.getPanel(endIndex);
		;
		canvasMouseEventWrapper.getAsDisplay().getImageAsWrapper().getOverlaySelectionManagger().setSelection(RectangularGraphic.blankRect(startingPanel, Color.blue, true, true), 0);
		canvasMouseEventWrapper.getAsDisplay().getImageAsWrapper().getOverlaySelectionManagger().setSelection(RectangularGraphic.blankRect(ending, Color.green, true, true), 1);
		
	}
	
	public void handleRelease(CanvasMouseEvent canvasMouseEventWrapper) {
		UndoLayoutEdit currentUndo = new UndoLayoutEdit(layout);
		
		endIndex = this.getCurrentLayout().makeAltered(type).getNearestPanelIndex(canvasMouseEventWrapper.getCoordinatePoint());
		
		if (type==PANELS) {  
			getEditor().swapMontagePanels(getCurrentLayout(), index, endIndex);
		
		}
		
		if (type==COLS) {
			 getEditor().swapColumn(getCurrentLayout(), index,endIndex);
		
		}
		
		if (type==ROWS) {
			 getEditor().swapRow(getCurrentLayout(), index,endIndex);
			
			
		}
		
		layout.mapPanelLocationsOfLockedItems();
		currentUndo.establishFinalLocations();
		canvasMouseEventWrapper.getAsDisplay().getUndoManager().addEdit(currentUndo);
		canvasMouseEventWrapper.getAsDisplay().getImageAsWrapper().getOverlaySelectionManagger().setSelectionstoNull();
		
		
		if (layout.getParentLayer() instanceof FigureOrganizingLayerPane) {
			FigureOrganizingLayerPane parentLayer = (FigureOrganizingLayerPane) layout.getParentLayer();
			 parentLayer.updateChannelOrder(type);
		}
		
	}

	private BasicMontageLayout getCurrentLayout() {
		return layout.getPanelLayout();
		
	}

	private GenericMontageEditor getEditor() {
		this.layout.generateCurrentImageWrapper();
		return layout.getEditor();
	}

}

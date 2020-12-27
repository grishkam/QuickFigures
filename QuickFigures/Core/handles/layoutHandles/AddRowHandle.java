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
package handles.layoutHandles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

import applicationAdapters.CanvasMouseEvent;
import applicationAdapters.DisplayedImage;
import genericMontageLayoutToolKit.MontageLayoutRowColNumberTool;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import handles.SmartHandle;

import java.awt.geom.Rectangle2D;

import imageDisplayApp.CanvasOptions;
import imageMenu.CanvasAutoResize;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.GenericMontageEditor;
import layout.basicFigure.LayoutSpaces;

/**A handle that adds rows/cols to the end of the layout or */
public class AddRowHandle extends SmartHandle implements LayoutSpaces{

	private DefaultLayoutGraphic layout;
	private int type;
	private boolean subtract=false;
	boolean dragType=true;
	private DisplayedImage wrap;
	int a=0;
	int plusSize = 5;



	public AddRowHandle(DefaultLayoutGraphic montageLayoutGraphic, int y, boolean sub) {

		this.subtract=sub;
		this.layout=montageLayoutGraphic;
		this.type=y;
		int offset = -a; if(subtract) offset=-offset;
		Rectangle2D space = layout.getPanelLayout().getSelectedSpace(1, ALL_OF_THE+PANELS).getBounds();
		
		double x2 = space.getCenterX()+offset;
		double y2 = space.getMaxY()+20;
		if(type==COLS) {
			offset = -a; if(!subtract) offset=a;
			y2 = space.getCenterY()+offset;
			x2 = space.getMaxX()+20;
		}
		this.setCordinateLocation(new Point2D.Double(x2, y2));
	//this.setLocation(50,50);
		
		super.handlesize=4;
		
		
		Area a = addSubtractShape(plusSize, subtract);
		specialShape=a;//AffineTransform.getTranslateInstance(x2,y2).createTransformedShape(a);
		if (subtract)this.setHandleColor(Color.red);
		else setHandleColor(Color.green);
		
		if(type==COLS) this.setHandleNumber(PanelLayoutGraphic.AddColHandle); else
		this.setHandleNumber(PanelLayoutGraphic.AddRowHandle);
		
		if(dragType)return;
		message="Add ";
		if(subtract) message="Remove ";
		if(type==COLS) message+="Column"; else message+="Row";
		
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public boolean containsClickPoint(Point2D p) {
		return super.containsClickPoint(p);
	}
	
	public void handlePress(CanvasMouseEvent canvasMouseEventWrapper) {
		wrap=canvasMouseEventWrapper.getAsDisplay();
		
		if(dragType&&canvasMouseEventWrapper.clickCount()<2) return;
		if (this.subtract) {
			if(type==COLS&&layout.getPanelLayout().nColumns()>1) 
				layout.getEditor().addCols(layout.getPanelLayout(), -1);
			if (type==ROWS&&layout.getPanelLayout().nRows()>1) layout.getEditor().addRows(layout.getPanelLayout(), -1);
			
		} else {
		if(type==COLS) layout.getEditor().addCols(layout.getPanelLayout(), 1);
			else layout.getEditor().addRows(layout.getPanelLayout(), 1);
		}
		
		if (CanvasOptions.current.resizeCanvasAfterEdit)
			new CanvasAutoResize().performActionDisplayedImageWrapper(wrap);

		
	}
	
	public void handleDrag(CanvasMouseEvent lastDragOrRelMouseEvent) {
		if(!dragType) return;
		Point p2 = lastDragOrRelMouseEvent.getCoordinatePoint();
		BasicLayout bm = layout.getPanelLayout();
		GenericMontageEditor edit = layout.getEditor();
		int[] rowcol = MontageLayoutRowColNumberTool.findAddedRowsCols((int)p2.getX(), (int)p2.getY(), bm);
		
		if (rowcol[0]+bm.nRows()>=1 &&type==ROWS)edit.addRows(bm, rowcol[0]);
		if (rowcol[1]+bm.nColumns()>=1 &&type==COLS)edit.addCols(bm, rowcol[1]);
		
		if (CanvasOptions.current.resizeCanvasAfterEdit)
			new CanvasAutoResize().performActionDisplayedImageWrapper(lastDragOrRelMouseEvent.getAsDisplay());

	}
	/**What to do when a handle is moved from point p1 to p2*/
	public void handleMove(Point2D p1, Point2D p2) {
		
	}
	
	protected Font getMessageFont() {
		return new Font("Arial", 0, 6);
	}

	
}
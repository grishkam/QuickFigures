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
 * Date Created: Nov 6, 2022
 * Date Modified: Nov 6, 2022
 * Copyright (C) 2022 Gregory Mazo
 * Version: 2023.2
 */
package objectDialogs;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;

import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWorkSheet;
import graphicalObjects.CordinateConverter;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import handles.SmartHandleList;
import imageDisplayApp.MiniToolBarPanel;
import imageDisplayApp.StandardWorksheet;
import locatedObject.Selectable;
import standardDialog.graphics.GraphicComponent;
import undo.UndoManagerPlus;

/**
 An implementation of the DiaplayedImage interface that allows many options to be used inside of a crop dialog.
 Does not work with every option. Supposed to be a limited implementation
 */
public class CropDialogAssist implements DisplayedImage {

	private CroppingDialog cropDialog;
	private GraphicComponent panel;

	public CropDialogAssist(CroppingDialog c) {
		this.cropDialog=c;
		this.panel=c.panel;
	}
	
	
	@Override
	public void updateDisplay() {
		this.cropDialog.repaint();

	}

	/**returns a worksheet that will allow access to the overlay object list*/
	@Override
	public ImageWorkSheet getImageAsWorksheet() {
		
		StandardWorksheet gs = new StandardWorksheet( this.cropDialog.objectList);
		if(cropDialog.selectedObject==cropDialog.cropAreaRectangle) {
			GraphicLayerPane pane = new GraphicLayerPane("");
			pane.add(cropDialog.cropAreaRectangle);
			gs = new StandardWorksheet( pane);
		}
		ImagePanelGraphic item = this.cropDialog.dialogDisplayImage;
		gs.setTitle(this.cropDialog.getTitle());
		gs.getBasics().setWidth( item.getUnderlyingImageWidth());
		gs.getBasics().setHeight(item.getUnderlyingImageHeight());
		gs.setDisplayGroup(this);
		return gs;
	}

	/***/
	@Override
	public CordinateConverter getConverter() {
		// TODO Auto-generated method stub
		return panel.getCord();
	}

	@Override
	public Window getWindow() {
		return this.cropDialog;
	}

	@Override
	public void updateWindowSize() {
		

	}

	@Override
	public UndoManagerPlus getUndoManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCursor(Cursor c) {

	}

	@Override
	public void zoomOutToDisplayEntireCanvas() {
		cropDialog.setScaleToDisplay(cropDialog.dialogDisplayImage);

	}

	/**
	 * 
	 */
	private static final String ZOOM_OUT = "Out", ZOOM_IN = "In";
	/**Zooms the canvas in or out depending on the string*/
	public void zoom(String st) {
		if (st==null) return;
			if (st.contains(ZOOM_IN)) this.ZoomIn();
			if (st.contains(ZOOM_OUT)) this.ZoomOut();
	}

	/**
	 * 
	 */
	private void ZoomOut() {
		this.cropDialog.zoomOut();
		
	}


	/**
	 * 
	 */
	private void ZoomIn() {
		this.cropDialog.zoomIn();
		
	}


	@Override
	public double getZoomLevel() {
		return this.cropDialog.displayMagnification;

	}

	@Override
	public void setZoomLevel(double z) {
		this.cropDialog.setDisplayScale(z);

	}

	@Override
	public void scrollPane(double d, double e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setScrollCenter(double dx, double dy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEndFrame(int frame) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getEndFrame() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCurrentFrame() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCurrentFrame(int currentFrame) {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeWindowButKeepObjects() {
		// TODO Auto-generated method stub

	}

	@Override
	public Selectable getSelectedItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSelectedItem(Selectable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public SmartHandleList getCanvasHandles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension getPageSize() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setSidePanel(MiniToolBarPanel miniToolBarPanel) {
		// TODO Auto-generated method stub
		
	}

}

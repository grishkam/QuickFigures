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
 * Date Modified: April 7, 2021
 * Version: 2021.1
 */
package undo;

import java.awt.geom.Rectangle2D;

import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;

/**An undoable edit for changes to the scale of an inset definer
 * not used (nor need) yet but part of work in progress
 * plan to use with a new scale change dialog*/
public class UndoInsetDefChange extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelGraphicInsetDefiner item;
	
	/**stores the starting and ending parameters*/
	private double iScale;
	private double fScale;
	private Rectangle2D.Double fRect;
	private Rectangle2D.Double iRect;
	
	boolean update=false;

	/**creates the undo, */
	public UndoInsetDefChange(PanelGraphicInsetDefiner insetDefiner, boolean updateImagePanels) {
		this.item=insetDefiner;
		iScale=item.getBilinearScale();
		iRect=item.getRectangle();
		update=updateImagePanels;
	}
	

	public void establishFinalState() {
		fScale=item.getBilinearScale();
		fRect=item.getRectangle();
	}
	
	public void redo() {
		item.setBilinearScale(fScale);
		item.setRectangle(fRect);
		if(update) item.updateImagePanels();
	}
	
	public void undo() {
		item.setBilinearScale(iScale);
		item.setRectangle(iRect);
		if(update) item.updateImagePanels();
	}
	
	/**creates an un do that undoes changes to panel size and layout that occur when one alters*/
	public static CombinedEdit createRescale(PanelGraphicInsetDefiner insetDefiner) {
		CombinedEdit output = new CombinedEdit();
		output.addEditToList(new UndoLayoutEdit(insetDefiner.personalLayout));
		output.addEditToList(new UndoInsetDefChange(insetDefiner, false));
		
		
		for(ImagePanelGraphic p: insetDefiner.getPanelManager().getPanelList().getPanelGraphics()) {
			output.addEdit(p.provideDragEdit());
		}
		
		output.addEditListener(new EditListener() {

			@Override
			public void afterEdit() {
				insetDefiner.updateImagePanels();
				
			}});
		
		
		return output;
	}

}

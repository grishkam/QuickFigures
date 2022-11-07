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
 * Date Modified: Oct 13, 2022
 * Version: 2022.2
 */
package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;

import applicationAdapters.HasScaleInfo;
import fLexibleUIKit.ObjectAction;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicActionToolbar.CurrentFigureSet;
import locatedObject.ScaleInfo;
import logging.IssueLog;
import sUnsortedDialogs.ScaleResetListener;
import sUnsortedDialogs.ScaleSettingDialog;
import undo.AbstractUndoableEdit2;

/**Similar to ImageJ's 'Set Scale' Displays a user interface that allows the user to set the pixel size of an image.
 * Updates the scale bars*/
public class SetImagePixelSize extends ObjectAction<MultichannelDisplayLayer> {

	MultichannelDisplayLayer display;
	
	public  SetImagePixelSize(MultichannelDisplayLayer d) {
		super(d);
		display=d;
	}
	
	/**shows the dialog*/
	public ScaleSettingDialog usePixelSizeSetDialog(String mold) {
		
		ScaleSettingDialog ssd = new  ScaleSettingDialog(display.getSlot(), null, true);
		ssd.setWindowCentered(true);
		ssd.setModal(true);
		ssd.setScaleResetListen(createScaleResetListener());
		ssd.undo=new ImageScaleUndo(display);
		if(mold!=null)
			ssd.switchto(mold); 
		else
			ssd.showDialog();
		return ssd;
	}

	
	/**A listener that updates the panels and scale bars after a scale reset*/
	public ScaleResetListener createScaleResetListener() {
		return new ScaleResetListener() {

			@Override
			public void scaleReset(HasScaleInfo scaled, ScaleInfo info) {
				display.updateFromOriginal();
				display.updatePanels();
				
				for (PanelGraphicInsetDefiner i: display.getInsets()) {
					i.updateDisplayPanelImages();
				}
				
			new CurrentFigureSet().updateDisplayCurrent();
			
			}
			
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		usePixelSizeSetDialog(null);
	}
	
	
	
	/**An undoable edit for this action*/
	class ImageScaleUndo extends AbstractUndoableEdit2 {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private MultichannelDisplayLayer layer;
		private ScaleInfo iScale;
		private ScaleInfo fScale;
		
		public ImageScaleUndo(MultichannelDisplayLayer d) {
			this.layer=d;
			this.iScale=layer.getSlot().getScaleInfo().copy();
		}

		public void establishFinalState() {this.fScale=layer.getSlot().getScaleInfo().copy();}
		public void redo() {
			display.getSlot().setScaleInfo(fScale);
			createScaleResetListener().scaleReset(display.getSlot(), fScale);
		}
		
		public void undo() {
			display.getSlot().setScaleInfo(iScale);
			createScaleResetListener().scaleReset(display.getSlot(), iScale);
		}
		
		public double getRatio() {return iScale.getPixelWidth()/fScale.getPixelWidth();}
	}
}

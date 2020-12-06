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
package figureEditDialogs;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import logging.IssueLog;
import objectDialogs.CroppingDialog;
import undo.CombinedEdit;
import undo.PreprocessChangeUndo;

public class RecropButton extends JButton implements ActionListener {

	private PanelStackDisplayOptions dialog;

	/**implements a recrop button*/
	public RecropButton(PanelStackDisplayOptions panelStackDisplayOptions) {
		this.addActionListener(this);
		this.setText("Re-Crop");
		dialog=panelStackDisplayOptions;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		new CurrentFigureSet().addUndo(
		showRecropDialog()						);
		
	}

	public  CombinedEdit showRecropDialog() {
		MultichannelDisplayLayer mainDisplayItem = dialog.getMainDisplayItem();
		CombinedEdit undo = new CombinedEdit(new PreprocessChangeUndo(mainDisplayItem));
		CroppingDialog.showCropDialog(mainDisplayItem.getSlot(), null, 0);
		mainDisplayItem .setFrameSliceUseToViewLocation();
		dialog.afterEachItemChange();
		undo.establishFinalState();
		Rectangle r = mainDisplayItem.getSlot().getModifications().getRectangle();
		
		for(MultichannelDisplayLayer dp:dialog.getAdditionalDisplayLayers()) try {
			if (dp==null||dp==mainDisplayItem) continue;
			PreprocessChangeUndo undo2 = new PreprocessChangeUndo(dp);
			CroppingDialog.showCropDialogOfSize(dp.getSlot(), new Dimension(r.width, r.height));
			dp.setFrameSliceUseToViewLocation();
			dialog.afterEachItemChange();
			undo2.establishFinalState();
			undo.addEditToList(undo2);
		}catch (Throwable t) {
			IssueLog.logT(t);
		}
		
		undo.establishFinalState();
		return undo;
	}

}

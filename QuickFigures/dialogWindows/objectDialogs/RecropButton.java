package objectDialogs;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import logging.IssueLog;
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
			IssueLog.log(t);
		}
		
		undo.establishFinalState();
		return undo;
	}

}

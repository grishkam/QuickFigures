package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;

import applicationAdapters.HasScaleInfo;
import fLexibleUIKit.ObjectAction;
import graphicActionToombar.CurrentSetInformerBasic;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDef;
import sUnsortedDialogs.ScaleResetListener;
import sUnsortedDialogs.ScaleSettingDialog;
import utilityClassesForObjects.ScaleInfo;

/**Similar to ImageJ's 'Set Scale' Displays a user interface that allows the user to set the pixel size of an image.
 * Updates the scale bars*/
public class SetImageScale extends ObjectAction<MultichannelDisplayLayer> {

	MultichannelDisplayLayer display;
	
	public  SetImageScale(MultichannelDisplayLayer d) {
		super(d);
		display=d;
	}
	
	public void showPixelSizeSetDialog() {
		ScaleSettingDialog ssd = new  ScaleSettingDialog(display.getSlot(), null, true);
		ssd.setWindowCentered(true);
		ssd.setModal(true);
		ssd.setScaleResetListen(new ScaleResetListener() {

			@Override
			public void scaleReset(HasScaleInfo scaled, ScaleInfo info) {
				display.updateFromOriginal();
				display.updatePanels();
				
				for (PanelGraphicInsetDef i: display.getInsets()) {
					i.updateDisplayPanelImages();
				}
				
			new CurrentSetInformerBasic().updateDisplayCurrent();
			}
			
		});
		ssd.showDialog();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		showPixelSizeSetDialog();
	}
}

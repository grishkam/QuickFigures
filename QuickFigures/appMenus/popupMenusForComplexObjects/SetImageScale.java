package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;

import applicationAdapters.HasScaleInfo;
import fLexibleUIKit.ObjectAction;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDef;
import sUnsortedDialogs.ScaleResetListener;
import sUnsortedDialogs.ScaleSettingDialog;
import undo.AbstractUndoableEdit2;
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
		ssd.setScaleResetListen(createScaleResetListener());
		ssd.undo=new ImageScaleUndo(display);
		ssd.showDialog();
		
	}

	public ScaleResetListener createScaleResetListener() {
		return new ScaleResetListener() {

			@Override
			public void scaleReset(HasScaleInfo scaled, ScaleInfo info) {
				display.updateFromOriginal();
				display.updatePanels();
				
				for (PanelGraphicInsetDef i: display.getInsets()) {
					i.updateDisplayPanelImages();
				}
				
			new CurrentFigureSet().updateDisplayCurrent();
			
			}
			
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		showPixelSizeSetDialog();
	}
	
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
	}
}

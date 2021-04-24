/**
 * Author: Greg Mazo
 * Date Modified: Apr 24, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package testing;

import java.awt.Rectangle;
import java.util.ArrayList;

import channelLabels.ChannelLabelManager;
import channelLabels.MergeLabelStyle;
import channelMerging.PreProcessInformation;
import figureOrganizer.FigureOrganizingLayerPane;
import graphicActionToolbar.QuickFigureMaker;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageMenu.CanvasAutoResize;
import imageScaling.Interpolation;
import imageScaling.ScaleInformation;

/**
 a test that shows images with a few different interpolation methods
 */
public class InterpolationText extends FigureTester {
	
	public static void main(String[] args) {
		
		FigureOrganizingLayerPane.suppressCropDialog=true;
		setup();
		 FigureTester.ignoreTemplate=true;
		FigureTester figureTester = new FigureTester();
		
		
		 PreProcessInformation p = new PreProcessInformation(new Rectangle(44,55, 25,25), 0, new ScaleInformation(10, Interpolation.BICUBIC));
		 QuickFigureMaker maker = figureTester.example1BFigureMaker();
		 maker.figureCreationOptions.ignoreSavedTemplate=true;
		
		ImageWindowAndDisplaySet diw = ImageWindowAndDisplaySet.createAndShowNew("New Image", 40, 30);
		
		int factor=0;
		for(Interpolation inter: Interpolation.values()) {
			figureTester.createMock(1, true);
			PreProcessInformation newp = new PreProcessInformation(p, p.getScaleInformation().getAtDifferentIterpolation(inter));
			FigureOrganizingLayerPane f = maker.createFigure(diw, figureTester.getMockFilePath(1), newp);
			f.transform().move(factor*100, 0);
			ChannelLabelManager labels = f.getPrincipalMultiChannel().getChannelLabelManager();
			labels.getChannelLabelProp().setMergeLabelStyle(MergeLabelStyle.SIMPLY_LABEL_AS_MERGE);
			labels.getChannelLabelProp().setMergeText(""+inter.name());
			f.updatePanelsAndLabelsFromSource();
			
			removeClass(f, BarGraphic.class);
			factor++;
		}
		new CanvasAutoResize(true).performUndoableAction(diw);
	}

	/**
	 * @param f
	 * @param classout
	 */
	protected static void removeClass(FigureOrganizingLayerPane f, Class<BarGraphic> classout) {
		ArrayList<ZoomableGraphic> all = f.getAllGraphics();
		for(ZoomableGraphic a:all) {
			if (classout.isInstance(a))
				f.remove(a);
		}
	}

}

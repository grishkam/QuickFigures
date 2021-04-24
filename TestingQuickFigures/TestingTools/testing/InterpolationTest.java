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
import imageMenu.ZoomFit;
import imageScaling.Interpolation;
import imageScaling.ScaleInformation;
import standardDialog.choices.ChoiceInputPanel;

/**
 a test that shows images with a few different interpolation methods.
 Differences between them should be visualy obvious.
 
 */
public class InterpolationTest extends FigureTester {
	
	private boolean mock;

	/**
	 * @param b
	 */
	public InterpolationTest(boolean b) {
		mock=b;
	}

	public static void main(String[] args) {
		
		new InterpolationTest(true).showInterpolationTest();
		new InterpolationTest(false).showInterpolationTest();
	}

	/**
	 * 
	 */
	protected void showInterpolationTest() {
		FigureOrganizingLayerPane.suppressCropDialog=true;
		setup();
		 FigureTester.ignoreTemplate=true;
		FigureTester figureTester = new FigureTester();
		
		
		 PreProcessInformation p = new PreProcessInformation(getCropROI(), 0, new ScaleInformation(10, Interpolation.BICUBIC));
		 QuickFigureMaker maker = figureTester.example1BFigureMaker();
		 maker.figureCreationOptions.ignoreSavedTemplate=true;
		
		ImageWindowAndDisplaySet diw = ImageWindowAndDisplaySet.createAndShowNew("New Image", 40, 30);
		
		int location =0;
		int factor=0;
		for(Interpolation inter: Interpolation.values()) {
			String mockFilePath2 = getFilePath(figureTester);
			
			PreProcessInformation newp = new PreProcessInformation(p, p.getScaleInformation().getAtDifferentIterpolation(inter));
			
			FigureOrganizingLayerPane f = maker.createFigure(diw, mockFilePath2, newp);
			
			ChannelLabelManager labels = f.getPrincipalMultiChannel().getChannelLabelManager();
			labels.getChannelLabelProp().setMergeLabelStyle(MergeLabelStyle.SIMPLY_LABEL_AS_MERGE);
			labels.getChannelLabelProp().setMergeText(ChoiceInputPanel.titleCase(inter.name()));
			f.updatePanelsAndLabelsFromSource();
			f.transform().move(location, 0);
			removeClass(f, BarGraphic.class);
			factor++;
			location+=f.getMontageLayoutGraphic().getBounds().width+10;
		}
		new CanvasAutoResize(true).performUndoableAction(diw);
		new ZoomFit(ZoomFit.SCREEN_FIT).performActionDisplayedImageWrapper(diw);;
	}

	/**returns the crop area used for this test
	 * @return
	 */
	protected Rectangle getCropROI() {
		if(!mock)
			return new Rectangle(450, 400, 60,60);
			
		return new Rectangle(44,55, 25,25);
	}

	/**
	 * @param figureTester
	 * @return
	 */
	protected  String getFilePath(FigureTester figureTester) {
		if(!mock) {
			return FigureTester.getTest1ImagePath(1, 1);
		}
		
		figureTester.createMock(1, true);
		String mockFilePath2 = figureTester.getMockFilePath(1);
		return mockFilePath2;
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

/**
 * Author: Greg Mazo
 * Date Modified: Apr 6, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package figureFormat;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import applicationAdapters.DisplayedImage;
import channelMerging.ChannelUseInstructions;
import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;
import testing.TestExample;
import utilityClasses1.ArraySorter;
import testing.FigureTester.FigureProvider;

/**
 
 * 
 */
public class FigureTemplateTest {

	@Test
	public void test() {
		DisplayedImage uniform = new FigureProvider(TestExample.MANY_SPLIT_CHANNEL).createExample();
		DisplayedImage non_uniform = new FigureProvider(TestExample.MANY_SPLIT_CHANNEL_SCRAMBLE).createExample();
		
		
		
		
		
		
		testColorModeChange(uniform, non_uniform);
		
		/**pauses long enough for the user to observe a change. Changes will visibly affect, panels, labels,layouts and scale bars*/
		IssueLog.waitSeconds(50);
	}

	/**
	 * @param uniform
	 * @param non_uniform
	 * @param colorMode
	 */
	protected void testColorModeChange(DisplayedImage uniform, DisplayedImage non_uniform) {
		/**sets up the multichannel display layer for the template*/
		ArrayList<GraphicLayer> sublayers = uniform.getImageAsWorksheet().getTopLevelLayer().getSubLayers();
		ArraySorter.removeThoseNotOfClass(sublayers, MultichannelDisplayLayer.class);
		
		FigureTemplate template = new FigureTemplate();
		template.getMultiChannelPicker().setModelItem(sublayers.get(0));
		
		template.applyTemplateTo(non_uniform.getImageAsWorksheet());
		
		ArrayList<GraphicLayer> sublayers2 = uniform.getImageAsWorksheet().getTopLevelLayer().getSubLayers();
		ArraySorter.removeThoseNotOfClass(sublayers2, MultichannelDisplayLayer.class);
		for(GraphicLayer onelayer: sublayers2) {
			
			/**Check to makde sure the color mode was changes*/
			MultichannelDisplayLayer m=(MultichannelDisplayLayer) onelayer;
			int mode = m.getPanelList().getChannelUseInstructions().channelColorMode;
			int expectedMode = template.getMultiChannelPicker().getModelItem().getPanelList().getChannelUseInstructions().channelColorMode;
			assert(mode==expectedMode);
		}
		
		non_uniform.updateDisplay();
	}

}

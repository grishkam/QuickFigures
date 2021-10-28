/**
 * Author: Greg Mazo
 * Date Modified: April 7, 2021
 * Version: 2021.2
 */
package figureFormat;

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
 work in progress, runs a test of the figure template.
 contains both an automated and a visual test
 */
public class FigureTemplateTest {

	@Test
	public void test() {
		DisplayedImage uniform = new FigureProvider(TestExample.MANY_SPLIT_CHANNEL).createExample();
		DisplayedImage non_uniform = new FigureProvider(TestExample.MANY_SPLIT_CHANNEL_SCRAMBLE).createExample();
		
		
		
		
		
		
		testColorModeChange(uniform, non_uniform, ChannelUseInstructions.CHANNELS_IN_GREYSCALE);
		
		testColorModeChange(uniform, non_uniform, ChannelUseInstructions.CHANNELS_IN_COLOR);
		
		testMergeHandleChange(uniform, non_uniform, ChannelUseInstructions.MERGE_FIRST);
		testMergeHandleChange(uniform, non_uniform, ChannelUseInstructions.ONLY_MERGE_PANELS);
		testMergeHandleChange(uniform, non_uniform, ChannelUseInstructions.NO_MERGE_PANELS);
		testMergeHandleChange(uniform, non_uniform, ChannelUseInstructions.MERGE_LAST);
		
		for(boolean b2: new boolean[] {true, false})
		for(boolean b: new boolean[] {true, false})
			for(int chan: new int[] {1,2,3})
				testChannelExcludedChange(uniform, non_uniform, b, chan, b2);
		
		
		/**pauses long enough for the user to observe a change. Changes will visibly affect, panels, labels,layouts and scale bars*/
		IssueLog.waitSeconds(5);
	}

	/**Tests to ensure that a new color mode is applied
	 * @param exampleForTemplate the figure that contains the original example object for this test
	 * @param inconsistentFigure the figure that contains a variety of objects and does not start uniform. this one will be altered to fit the template
	 * @param colorMode
	 */
	protected void testColorModeChange(DisplayedImage exampleForTemplate, DisplayedImage inconsistentFigure, int newmode) {
		FigureTemplate template = setTemplateToFirstMultichannel(exampleForTemplate);
		template.getMultiChannelPicker().getModelItem().getPanelList().getChannelUseInstructions().channelColorMode=newmode;
		int expectedMode = template.getMultiChannelPicker().getModelItem().getPanelList().getChannelUseInstructions().channelColorMode;
		
		
		
		template.applyTemplateTo(inconsistentFigure.getImageAsWorksheet());
		
		/**Checks to determine if the colormode of each */
		ArrayList<GraphicLayer> sublayers2 = inconsistentFigure.getImageAsWorksheet().getTopLevelLayer().getSubLayers();
		ArraySorter.removeThoseNotOfClass(sublayers2, MultichannelDisplayLayer.class);
		for(GraphicLayer onelayer: sublayers2) {
			
			/**Check to makde sure the color mode was changes*/
			MultichannelDisplayLayer m=(MultichannelDisplayLayer) onelayer;
			int mode = m.getPanelList().getChannelUseInstructions().channelColorMode;
		

			assert(mode==expectedMode);
		}
		
		inconsistentFigure.updateDisplay();
		showUser(inconsistentFigure);
	}
	
	
	/**Tests to ensure that new insturctions on which panels to create are set to match the model
	 * this does not chang the appearance of the figure but is important if a new figure is created. 
	 * I manually tested to see if the effect was applied to new figures
	 * @param exampleForTemplate the figure that contains the original example object for this test
	 * @param inconsistentFigure the figure that contains a variety of objects and does not start uniform. this one will be altered to fit the template
	 * @param colorMode
	 */
	protected void testMergeHandleChange(DisplayedImage exampleForTemplate, DisplayedImage inconsistentFigure, int newmode) {
		FigureTemplate template = setTemplateToFirstMultichannel(exampleForTemplate);
		template.getMultiChannelPicker().getModelItem().getPanelList().getChannelUseInstructions().MergeHandleing=newmode;
		int expectedMode = template.getMultiChannelPicker().getModelItem().getPanelList().getChannelUseInstructions().MergeHandleing;
		
		
		
		template.applyTemplateTo(inconsistentFigure.getImageAsWorksheet());
		
		/**Checks to determine if the colormode of each */
		ArrayList<GraphicLayer> sublayers2 = inconsistentFigure.getImageAsWorksheet().getTopLevelLayer().getSubLayers();
		ArraySorter.removeThoseNotOfClass(sublayers2, MultichannelDisplayLayer.class);
		for(GraphicLayer onelayer: sublayers2) {
			/**Check to makde sure the color mode was changes*/
			MultichannelDisplayLayer m=(MultichannelDisplayLayer) onelayer;
			int mode = m.getPanelList().getChannelUseInstructions().MergeHandleing;
		

			assert(mode==expectedMode);
		}
		
		
	}
	
	
	/**Tests that the template affects which channels are set to be included or excluded from the figure
	 * @param exampleForTemplate the figure that contains the original example object for this test
	 * @param inconsistentFigure the figure that contains a variety of objects and does not start uniform. this one will be altered to fit the template
	 * @param colorMode
	 */
	protected void testChannelExcludedChange(DisplayedImage exampleForTemplate, DisplayedImage inconsistentFigure, boolean newmode, int chan, boolean excludeChan) {
		/**sets up the multichannel display layer for the template*/
		FigureTemplate template = setTemplateToFirstMultichannel(exampleForTemplate);
		template.getMultiChannelPicker().getModelItem().getPanelList().getChannelUseInstructions().setMergeExcluded(chan, newmode);
		if (excludeChan)template.getMultiChannelPicker().getModelItem().getPanelList().getChannelUseInstructions().setChannelPanelExcluded(chan, newmode);
		
		template.applyTemplateTo(inconsistentFigure.getImageAsWorksheet());
		
		/**Checks to determine if the colormode of each */
		ArrayList<GraphicLayer> sublayers2 = inconsistentFigure.getImageAsWorksheet().getTopLevelLayer().getSubLayers();
		ArraySorter.removeThoseNotOfClass(sublayers2, MultichannelDisplayLayer.class);
		for(GraphicLayer onelayer: sublayers2) {
			
			/**Check to makde sure the color mode was changes*/
			MultichannelDisplayLayer m=(MultichannelDisplayLayer) onelayer;
			boolean mode = m.getPanelList().getChannelUseInstructions().isMergeExcluded(chan);
		

			assert(mode==newmode);
			
			if (excludeChan) {
				mode = m.getPanelList().getChannelUseInstructions().isChanPanExcluded(chan);
				assert(mode==newmode);
			}
			
		}
		
		inconsistentFigure.updateDisplay();
		
		if (!excludeChan)showUser(inconsistentFigure);//if the exclusion of channel panels is being tested then it will not be visible to the user unless a new figure is created, that was confimred manually
	}

	/**Finds a @see MultichannelDisplayLayer within the target image and sets it as the example object for the template
	 * @param exampleForTemplate
	 * @return
	 */
	protected FigureTemplate setTemplateToFirstMultichannel(DisplayedImage exampleForTemplate) {
		ArrayList<GraphicLayer> sublayers = exampleForTemplate.getImageAsWorksheet().getTopLevelLayer().getSubLayers();
		ArraySorter.removeThoseNotOfClass(sublayers, MultichannelDisplayLayer.class);
		
		FigureTemplate template = new FigureTemplate();
		template.getMultiChannelPicker().setModelItem(sublayers.get(0));
		return template;
	}

	/**
	 * @param inconsistentFigure
	 */
	protected void showUser(DisplayedImage inconsistentFigure) {
		inconsistentFigure.updateDisplay();
		IssueLog.waitSeconds(2);// pause for long enough that the user can see and notice the color change
	}

}

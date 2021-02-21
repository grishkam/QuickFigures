/**
 * Author: Greg Mazo
 * Date Modified: Feb 20, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package channelLabels;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import advancedChannelUseGUI.FigureTest;
import channelMerging.ImageDisplayLayer;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.PanelListElement;
import logging.IssueLog;

/**
 
 * 
 */
public class ChannelLabelManagerTest extends FigureTest {

	@Test
	public void test() {
		FigureOrganizingLayerPane f = createFirstExample();
		super.chooseCurrentWindow();
		
		ImageDisplayLayer displayLayer = f.getPrincipalMultiChannel();
		ChannelLabelManager manager = displayLayer.getChannelLabelManager();
		
		/**example 1 is known to have 5 labels, dapi, egfp, texasred, and merge */
		assert(manager.getAllLabels().size()==5);
		
		ArrayList<ChannelLabelTextGraphic> allLabels = manager.getAllLabels();
		
		/**tests removal of channel labels from parent layer*/
		for(ChannelLabelTextGraphic channelLabel: allLabels) {
			assert(channelLabel.getParentLayer()!=null);
		}
		manager.eliminateChanLabels();
		for(ChannelLabelTextGraphic channelLabel: allLabels) {
			assert(channelLabel.getParentLayer()==null);
		}
		
		PanelListElement mergePanel = displayLayer.getPanelList().getMergePanel();
		
		ChannelLabelTextGraphic cl = manager.generateChanelLabel(mergePanel);
		assert(cl.isThisMergeLabel());
		
		cl.updateChannelLabelPropertiesToLabelText();
		assert(cl.getParagraph().getText().toLowerCase().equals("merge"));
		
		/**channel names are known for example 1, the expected names are tested*/
		testChannelLabelName(displayLayer, 1, "dapi");
		testChannelLabelName(displayLayer, 2, "egfp");
		testChannelLabelName(displayLayer, 3, "texasred");
		testChannelLabelName(displayLayer, 4, "cy5");
		
		manager.nameChannels();
		
		IssueLog.waitSeconds(25);

	}

	/**
	 * @param displayLayer
	 * @param c
	 * @param expected
	 */
	public void testChannelLabelName(ImageDisplayLayer displayLayer, int c, String expected) {
		ChannelLabelTextGraphic cl;
		PanelListElement panel = displayLayer.getPanelList().getChannelPanelFor(c, 1,1);
		cl = displayLayer.getChannelLabelManager().generateChanelLabel(panel);
		assert(!cl.isThisMergeLabel());
		assert(cl.getParagraph().getText().toLowerCase().equals(expected));
	}

	

}

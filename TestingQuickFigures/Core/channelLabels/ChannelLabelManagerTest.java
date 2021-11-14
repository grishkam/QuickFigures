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
 
 * 
 */
package channelLabels;

import java.util.ArrayList;

import org.junit.Test;

import channelMerging.ImageDisplayLayer;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.PanelListElement;
import testing.FigureTest;

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
		
		
		super.closeCurrentWindow();
		

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

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
 * Author: Greg Mazo
 * Date Modified: April 7, 2021
 * Version: 2022.0
 */
package advancedChannelUseGUI;

import java.awt.geom.Point2D;

import org.junit.Test;

import channelMerging.ChannelEntry;
import channelMerging.ImageDisplayLayer;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelList;
import figureOrganizer.PanelListElement;
import logging.IssueLog;
import testing.FigureTest;
import testing.FigureTester;
import undo.PanelManagerUndo;

/**
 this is a partly manual test. After automatic tests are done, user will be able to edit the figure 
 with the dialog for 2 minutes.
 Have not devised ways to automatically test the every feature of the advanced channel use dialog
 so this class just displays the dialog for manual edits to occur
 */
public class AdvancedChannelUseGUITest extends FigureTest {

	
/**how long to wait between steps so user cansee*/
	private int waitTime=00;



	@Test
	public void test() {
		FigureOrganizingLayerPane f = createFirstExample();
		testForFigureIncurrentImage(f);
		
		
		f = super.createMockExample();
		testForFigureIncurrentImage(f);
		
		
		FigureTester.closeAllWindows();
	}



	/**
	 * @param f
	 */
	public void testForFigureIncurrentImage(FigureOrganizingLayerPane f) {
		chooseCurrentWindow();
		ImageDisplayLayer mm = f.getPrincipalMultiChannel();
		if (mm instanceof MultichannelDisplayLayer) {
			MultichannelDisplayLayer m=(MultichannelDisplayLayer) mm;
			AdvancedChannelUseGUI dialog = new AdvancedChannelUseGUI( mm.getPanelManager(), m.getChannelLabelManager());
			
			dialog.setVisible(true);
			PanelListDisplay jListOfPanels = dialog.getPanelJList();
			
			PanelList panelList = mm.getPanelManager().getPanelList();
			int count =  panelList.getSize();
			
			for(int i=0; i<count;i++) 
				for(int j=0; j<count;j++){
				PanelListElement panelJ = panelList.getPanels().get(j);
				PanelListElement panelI = panelList.getPanels().get(i);
				Point2D pJstart = panelJ.getImageDisplayObject().getLocationUpperLeft();
				Point2D pIstart = panelI.getImageDisplayObject().getLocationUpperLeft();
				
				PanelManagerUndo undo = jListOfPanels.swapItems(panelI, panelJ);
				
				/**makes sure the panels locations are switched*/
				assert(panelJ== panelList.getPanels().get(i));
				assert(panelI== panelList.getPanels().get(j));
				assert(pJstart.equals(panelI.getImageDisplayObject().getLocationUpperLeft()));
				assert(pIstart.equals(panelJ.getImageDisplayObject().getLocationUpperLeft()));
				
				/**tests undo*/
				undo.undo();
				assert(panelJ== panelList.getPanels().get(j));
				assert(panelI== panelList.getPanels().get(i));
				assert(pJstart.equals(panelJ.getImageDisplayObject().getLocationUpperLeft()));
				assert(pIstart.equals(panelI.getImageDisplayObject().getLocationUpperLeft()));
				IssueLog.waitMiliseconds(waitTime);
				}
			
			PanelListElement mergePanel = panelList.getMergePanel();
			dialog.getPanelJList().setSelectedValue(mergePanel, true);
			assert(jListOfPanels.getSelectedValue()==mergePanel);
			
			 count=mergePanel.getChannelEntryList().size();
			 for(int i=0; i<count;i++) 
					for(int j=0; j<count;j++){
						ChannelListDisplay lc = dialog.getJListForChannels();
						ChannelEntry chanI=mergePanel.getChannelEntryList().get(i);
						ChannelEntry chanJ=mergePanel.getChannelEntryList().get(j);
						 lc.swapItems(chanI, chanJ);
						/**Checks to determine if channel switch occured*/
						assert(chanI==mergePanel.getChannelEntryList().get(j));
						assert(chanJ==mergePanel.getChannelEntryList().get(i));
					
					
						
					}
			
			gg.updateDisplay();
		}
	}



	
	

}

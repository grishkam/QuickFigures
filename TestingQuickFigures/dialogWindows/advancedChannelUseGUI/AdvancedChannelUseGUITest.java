/**
 * Author: Greg Mazo
 * Date Modified: Dec 31, 2020
 * Copyright (C) 2020 Gregory Mazo
 * Version 2021.1
 */
/**
 
 * 
 */
package advancedChannelUseGUI;

import java.awt.geom.Point2D;

import org.junit.Test;

import applicationAdapters.DisplayedImage;
import channelMerging.ChannelEntry;
import channelMerging.ImageDisplayLayer;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelList;
import figureOrganizer.PanelListElement;
import graphicActionToolbar.CurrentFigureSet;
import logging.IssueLog;
import testing.FigureTest;
import testing.FigureTester;
import testing.TestingOptions;
import undo.CombinedEdit;
import undo.PanelManagerUndo;

/**
 this is a partly manual test. After automatic tests are done, user will be able to edit the figure 
 with the dialog for 2 minutes.
 Have not devised ways to automatically test the every feature of the advanced channel use dialog
 so this class just displays the dialog for manual edits to occur
 */
public class AdvancedChannelUseGUITest extends FigureTest {

	

	@Test
	public void test() {
		FigureOrganizingLayerPane f = createFirstExample();
		testForFigureIncurrentImage(f);
		
		if (TestingOptions.performManualTests)IssueLog.waitSeconds(120);
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
						CombinedEdit undo = lc.swapItems(chanI, chanJ);
						/**Checks to determine if channel switch occured*/
						assert(chanI==mergePanel.getChannelEntryList().get(j));
						assert(chanJ==mergePanel.getChannelEntryList().get(i));
					
					
						
					}
			
			gg.updateDisplay();
		}
	}



	
	

}

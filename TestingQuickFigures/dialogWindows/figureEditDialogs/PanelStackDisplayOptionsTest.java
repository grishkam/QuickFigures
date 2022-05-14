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
 * Date Modified: Dec 6, 2020
 * Version: 2022.1
 */
package figureEditDialogs;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.ArrayList;

import org.junit.Test;

import channelMerging.CSFLocation;
import channelMerging.ChannelUseInstructions;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.PanelListElement;
import graphicActionToolbar.CurrentFigureSet;
import graphicActionToolbar.QuickFigureMakerTest;
import logging.IssueLog;
import messages.ShowMessage;
import testing.DialogTester;
import testing.TestingOptions;
import testing.TestingUtils;

/**
 Tests the recreate panels dialog.
 This actually takes a long time since 
 */
public class PanelStackDisplayOptionsTest extends  DialogTester  {
	

	

	@Test
	public void test() {
		
		IssueLog.sytemprint=true;
		IssueLog.windowPrint=false;
		IssueLog.countExceptions=0;
		/**For the multichannel case*/
		FigureOrganizingLayerPane qf = QuickFigureMakerTest.generateQuickFigure(3, 2, 1);
		
		PanelStackDisplayOptions dialog = PanelStackDisplayOptions.recreateFigurePanels(qf, false);
		
		/**tests to see if certain changes are being made*/
		performTestsForMergePanelChoice(qf, dialog);
		checkChannelExclusion(qf, dialog);
		checkChannelMergeExclusion(qf, dialog);
		checkEachMergeChannel(qf, dialog);
		checkPreprocessScaleField(qf, dialog);
		checkIdealNColumns(qf, dialog);
		checkPanelPixelDensityfield(qf, dialog);
		
		checkColorMode(qf, dialog);
		DialogTester.testInputPanelApperance(dialog, 1);
		
		/** these tests take a long time, so a conditional */
		if (TestingOptions.performSlowTestsForExceptions)askAboutManualTests(dialog);
		
		dialog.setVisible(false);
	}

	/**
	 * @param dialog
	 */
	public void askAboutManualTests(PanelStackDisplayOptions dialog) {
		if (TestingOptions.performManualTests && ShowMessage.showOptionalMessage("next step", false, "Automated tests done for recreate panels dialog, do you want to check combinations of settings and then perform manual tests?"))
			performCombinationsAndManual(dialog);
	}

	/**
	 * @param qf
	 * @param dialog
	 */
	public void checkColorMode(FigureOrganizingLayerPane qf, PanelStackDisplayOptions dialog) {
		dialog.setChoiceIndex(PanelStackDisplayOptions.COLOR_MODE_KEY, ChannelUseInstructions.CHANNELS_IN_GREYSCALE);
		qf.updateDisplay();
		checkChannelPanelsForGreyscaleStatus(qf, true);
		
		dialog.setChoiceIndex(PanelStackDisplayOptions.COLOR_MODE_KEY, ChannelUseInstructions.CHANNELS_IN_COLOR);
		qf.updateDisplay();
		checkChannelPanelsForGreyscaleStatus(qf, false);
	}

	/**
	 * @param qf
	 */
	public void checkChannelPanelsForGreyscaleStatus(FigureOrganizingLayerPane qf, boolean expectGreyscale) {
		ArrayList<PanelListElement> chanPanels = qf.getAllPanelLists().getPanelsWith(CSFLocation.channelLocation(1), PanelListElement.CHANNEL_IMAGE_PANEL);
		IssueLog.log("Checking for greyscale status expect "+expectGreyscale);
		for(PanelListElement panel:  chanPanels )
		{
			if(panel.getChannelEntryList().size()==1 ) {
				Color color = panel.getChannelEntries().get(0).getColor();
				if(color.equals(Color.white))
					continue; //white panels always look greyscale
				if(color.equals(Color.black))
					continue; //black panels always look greyscale
			}
			IssueLog.log("Checking for greyscale" +panel);
				assert(expectGreyscale==isGreyScale(panel));
				}
	}

	/**Checks to determine if the image panel created is a greyscale image or a colored one
	 * Precondition: assuming that the panel is a channel panel with only one color
	 * @param chanPanels
	 * @return 
	 */
	public boolean isGreyScale(PanelListElement chanPanels) {
		BufferedImage bb = chanPanels.getImageDisplayObject().getBufferedImage();
		IssueLog.log(" width "+bb.getWidth()+" height "+bb.getHeight());
		for(int i=1; i<bb.getWidth(); i++)
			for(int j=1; j<bb.getHeight(); j++)
					{
				ColorModel colorModel = bb.getColorModel();
				int pixel = bb.getRGB(i, j);
				int blue = colorModel.getBlue(pixel);
				int red = colorModel.getRed(pixel);
				int green = colorModel.getGreen(pixel);
				
				
				if(blue!=red)
					return false;
				if(blue!=green)
					return false;
				if(red!=green)
					return false;
				;
				;
				
					}
		;
		return true;
	}

	/**
	 * @param qf
	 * @param dialog
	 */
	public void checkIdealNColumns(FigureOrganizingLayerPane qf, PanelStackDisplayOptions dialog) {
		this.changeNumber(dialog, PanelStackDisplayOptions.IDEAL_LAYOUT_SIZE_KEY, 2);
		assert(qf.getLayout().nColumns()==2);
		
		this.changeNumber(dialog, PanelStackDisplayOptions.IDEAL_LAYOUT_SIZE_KEY, 5);
		assert(qf.getLayout().nColumns()!=2);
	}

	/**
	 * @param dialog
	 */
	public void performCombinationsAndManual(PanelStackDisplayOptions dialog) {
		FigureOrganizingLayerPane qf;
		/**Changes the dialog options to many combinations of states. During this test, one can observe the panels being recreated
		 * some of these result in all channel/panels being excluded from the
		 * figure and result in exceptions (that are caught). Although no user would purposely 
		 * try to remove every single channel, it may help to avoid allowing the user to do this.
		 * It appears that when non valid numbers like -1 and 0 are put in, the code just ignores them
		 * In certain circunstances, the dialog stops updating the figure itself*/
		testCombinations(dialog);
		dialog.undo.undo();
		dialog.revertAll();
		new CurrentFigureSet().getCurrentlyActiveDisplay().setZoomLevel(2);// to fix any excessive zooming that may have occred
		TestingUtils.askUser("Dialog has cycled through combinations of settings. For 30 seconds.Manually determine that the dialog still works ");
		IssueLog.waitSeconds(30);
		TestingUtils.askUser("were you still able to edit the figure with the dialog? ");
		
		
		 dialog.setVisible(false);
		 /**for the test case with only 1 channel.The dialog should show the frames and slices
		   tab first*/
		 qf = QuickFigureMakerTest.generateQuickFigure(1, 4, 1);
		  dialog = PanelStackDisplayOptions.recreateFigurePanels(qf, false);
		 int g= dialog.getOptionDisplayTabs().getSelectedIndex();
		assert( dialog.getOptionDisplayTabs().getTitleAt(g)==PanelStackDisplayOptions.FRAMES_AND_SLICES_TAB_NAME);
		 ShowMessage.showMessages("now for 30 seconds, user may manually test dialog windows frames and slices tab by typing in a number (1 or 2)");
		 IssueLog.waitSeconds(30);
		 
		 dialog.setVisible(false);
	}

	/**checks to determine if changes to the panel pixel density work
	 * @param qf
	 * @param dialog
	 */
	public void checkPanelPixelDensityfield(FigureOrganizingLayerPane qf, PanelStackDisplayOptions dialog) {
		double oSize = qf.getAllPanelLists().getMergePanel().getPanelGraphic().getRelativeScale();
		
		changeNumber(dialog, PanelStackDisplayOptions.PANEL_SIZE_KEY, 95);
		ArrayList<PanelListElement> panels = qf.getAllPanelLists().getPanels();
		for(PanelListElement panel: panels  )
			{
				double nSize = panel.getPanelGraphic().getRelativeScale();
				
				assert(nSize!=oSize);
				
			}
		changeNumber(dialog, PanelStackDisplayOptions.PANEL_SIZE_KEY, 300);
		
	}

	/** check the scale key
	 * @param qf
	 * @param dialog
	 */
	public void checkPreprocessScaleField(FigureOrganizingLayerPane qf, PanelStackDisplayOptions dialog) {
		double oScale = qf.getPrincipalMultiChannel().getPreprocessScale().getScale();
		Dimension oSize = qf.getAllPanelLists().getMergePanel().getPanelGraphic().getDimensions();
		changeNumber(dialog, ScaleLevelInputDialog.SCALE_KEY, oScale*2);
		IssueLog.log("scale starts at "+oScale);
		/**makes sure the scale has change*/
		double nScale = qf.getPrincipalMultiChannel().getPreprocessScale().getScale();
		IssueLog.log("scale ends at "+nScale);
		assert(nScale==2*oScale);
		//makes sure the size of the panel object has changed
		Dimension dimensions = qf.getAllPanelLists().getMergePanel().getPanelGraphic().getDimensions();
		assert(!oSize.equals(dimensions));
		
		/**return to original size*/
		changeNumber(dialog, ScaleLevelInputDialog.SCALE_KEY, oScale);
		dimensions = qf.getAllPanelLists().getMergePanel().getPanelGraphic().getDimensions();
		assert(oSize.equals(dimensions));
		
	}

	/**
	 * @param dialog
	 * @param key 
	 * @param oScale
	 */
	public void changeNumber(PanelStackDisplayOptions dialog, String key, double oScale) {
		dialog.setNumber(key,oScale);
		IssueLog.log(key+" number was artificially set to "+oScale);
		dialog.afterEachItemChange();
	}
	
	/**check to make sure that changes to the each merge channel choice
	 * channel panels option result in the
	 * figure lacking those channel panels
	 * @param qf
	 * @param dialog
	 */
	public void checkEachMergeChannel(FigureOrganizingLayerPane qf, PanelStackDisplayOptions dialog) {
		int nChan = qf.getPrincipalMultiChannel().getMultiChannelImage().nChannels();
		for(int i=1; i<=nChan; i++) {
			ArrayList<PanelListElement> p = getChannelPanels(qf, i);
			assert(p.size()>0);//check to make sure the figure starts with that channel panel
			
			/**sets the channel as excluded and check to see if the channel panels are still there*/
			dialog.setChoiceIndex(PanelStackDisplayOptions.MERGE_TO_EACH_CHANNEL_PANEL_KEY, i);
			p = getChannelPanels(qf, i);
			assert(p.size()==0);//checks to make sure the channel is now absent
			
			/**Checks to make sure that every panel has the channel*/
			for(PanelListElement panel: qf.getAllPanelLists().getPanels()) {
				if(panel.isTheMerge()) continue;//merge panel should have channel but need not be checked
				assert(panel.getChannelEntryList().hasChannelWithIndex(i));
			}
			
			dialog.setChoiceIndex(PanelStackDisplayOptions.MERGE_TO_EACH_CHANNEL_PANEL_KEY, 0);
			p = getChannelPanels(qf, i);
			assert(p.size()>0);//check to make sure the figure starts with that channel
			
				}
		
		
		
	}

	/**check to make sure that changes to the excluded channel panels option result in the
	 * figure lacking those channel panels
	 * @param qf
	 * @param dialog
	 */
	public void checkChannelExclusion(FigureOrganizingLayerPane qf, PanelStackDisplayOptions dialog) {
		int nChan = qf.getPrincipalMultiChannel().getMultiChannelImage().nChannels();
		for(int i=1; i<=nChan; i++) {
			ArrayList<PanelListElement> p = getChannelPanels(qf, i);
			assert(p.size()>0);//check to make sure the figure starts with that channel
			
			/**sets the channel as excluded and check to see if the channel panels are still there*/
			dialog.setChannelChoices(PanelStackDisplayOptions.EXCLUDE_CHANNEL_KEY, i);
			p = getChannelPanels(qf, i);
			assert(p.size()==0);//checks to make sure the channel is now absent
			
			
			if(nChan>1) {
				/**attempts to exclude 2 channels*/
				int otherChan=1;
				if(i==1) otherChan=2;
				ArrayList<Integer> array = new ArrayList<Integer>(); array.add(otherChan); array.add(i);
				/**sets the channel as excluded and check to see if the channel panels are still there*/
				dialog.setChannelChoices(PanelStackDisplayOptions.EXCLUDE_CHANNEL_KEY, array);
				p = getChannelPanels(qf, i);
				assert(p.size()==0);
				p = getChannelPanels(qf, otherChan);
				assert(p.size()==0);
			}
			
			dialog.setChannelChoices(PanelStackDisplayOptions.EXCLUDE_CHANNEL_KEY, new ArrayList<Integer>());
			p = getChannelPanels(qf, i);
			assert(p.size()>0);//check to make sure the figure starts with that channel
			
				}
		
		
		
	}
	
	/**check to make sure that changes to the content of the merge panels option result in the
	 * figure lacking those channels
	 * @param qf
	 * @param dialog
	 */
	public void checkChannelMergeExclusion(FigureOrganizingLayerPane qf, PanelStackDisplayOptions dialog) {
		int nChan = qf.getPrincipalMultiChannel().getMultiChannelImage().nChannels();
		for(int i=1; i<=nChan; i++) {
			PanelListElement p = qf.getAllPanelLists().getMergePanel();
			assert(p.getChannelEntryList().hasChannelWithIndex(i));//check to make sure the figure starts with that channel
			
			/**sets the channel as excluded and check to see if the channel panels are still there*/
			dialog.setChannelChoices(PanelStackDisplayOptions.DONT_MERGE_CHANNELS_KEY, i);
			p = qf.getAllPanelLists().getMergePanel();
			assert(!p.getChannelEntryList().hasChannelWithIndex(i));//check to make sure the figure starts with that channel
			
			if(nChan>1) {
				/**attempts to exclude 2 channels*/
				int otherChan=1;
				if(i==1) otherChan=2;
				ArrayList<Integer> array = new ArrayList<Integer>(); array.add(otherChan); array.add(i);
				/**sets the channel as excluded and check to see if the channel panels are still there*/
				dialog.setChannelChoices(PanelStackDisplayOptions.DONT_MERGE_CHANNELS_KEY, array);
				p = qf.getAllPanelLists().getMergePanel();
				assert(!p.getChannelEntryList().hasChannelWithIndex(i));
				assert(!p.getChannelEntryList().hasChannelWithIndex(otherChan));
			}
			
			/**sets the less of excluded channels to empty*/
			dialog.setChannelChoices(PanelStackDisplayOptions.DONT_MERGE_CHANNELS_KEY, new ArrayList<Integer>());
			p = qf.getAllPanelLists().getMergePanel();
			assert(p.getChannelEntryList().hasChannelWithIndex(i));//check to make sure the figure starts with that channel
			
				}
		
		
		
	}

	/**returns the channel panels containing a particular channel
	 * @param qf
	 * @param i
	 * @return
	 */
	public ArrayList<PanelListElement> getChannelPanels(FigureOrganizingLayerPane qf, int i) {
		return qf.getAllPanelLists().getPanelsWith(CSFLocation .channelLocation(i), PanelListElement.CHANNEL_IMAGE_PANEL);
	}

	/**a series of automated tests to ensure that the figure is being changed when the merge options of the dialog is changed
	 * @param qf
	 * @param dialog
	 */
	public void performTestsForMergePanelChoice(FigureOrganizingLayerPane qf, PanelStackDisplayOptions dialog) {
		dialog.setChoiceIndex(PanelStackDisplayOptions.MERGE_PANEL_POSITION_KEY, ChannelUseInstructions.NO_MERGE_PANELS);
		assert(qf.getAllPanelLists().getPanelSubset(PanelListElement.CHANNEL_IMAGE_PANEL).size()!=0);
		assert(qf.getAllPanelLists().getPanelSubset(PanelListElement.MERGE_IMAGE_PANEL).size()==0);
		dialog.setChoiceIndex(PanelStackDisplayOptions.MERGE_PANEL_POSITION_KEY, ChannelUseInstructions.ONLY_MERGE_PANELS);
		assert(qf.getAllPanelLists().getPanelSubset(PanelListElement.CHANNEL_IMAGE_PANEL).size()==0);
		assert(qf.getAllPanelLists().getPanelSubset(PanelListElement.MERGE_IMAGE_PANEL).size()!=0);
		dialog.setChoiceIndex(PanelStackDisplayOptions.MERGE_PANEL_POSITION_KEY, ChannelUseInstructions.MERGE_FIRST);
		assert(qf.getAllPanelLists().getPanels().get(0).designation==PanelListElement.MERGE_IMAGE_PANEL);
		dialog.setChoiceIndex(PanelStackDisplayOptions.MERGE_PANEL_POSITION_KEY, ChannelUseInstructions.MERGE_LAST);
		assert(qf.getAllPanelLists().getPanels().get(0).designation==PanelListElement.CHANNEL_IMAGE_PANEL);
		dialog.revertAll();
	}


}

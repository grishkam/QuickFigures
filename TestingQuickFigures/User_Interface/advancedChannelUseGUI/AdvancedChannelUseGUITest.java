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

import org.junit.Test;

import channelMerging.ImageDisplayLayer;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import ij.IJ;
import testing.FigureTester;

/**
 this is a mostly manual test.
 Have not determined the best way to automatically test the advanced channel use dialog
 so this class just displays the dialog for manual edits to occur
 */
public class AdvancedChannelUseGUITest {

	@Test
	public void test() {
		FigureTester.setup();
		FigureOrganizingLayerPane f = new FigureTester(). createFigureFromExample1AImages();
		ImageDisplayLayer mm = f.getPrincipalMultiChannel();
		if (mm instanceof MultichannelDisplayLayer) {
			MultichannelDisplayLayer m=(MultichannelDisplayLayer) mm;
			AdvancedChannelUseGUI d = new AdvancedChannelUseGUI( mm.getPanelManager(), m.getChannelLabelManager());
			
			d.setVisible(true);
			
		}
		
		IJ.wait(100000);//100 seconds long enough for manual testing of this gui
	}
	

}

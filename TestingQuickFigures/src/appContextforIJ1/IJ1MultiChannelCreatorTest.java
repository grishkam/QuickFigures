/**
 * Author: Greg Mazo
 * Date Modified: Dec 20, 2020
 * Version: 2021.1
 */
package appContextforIJ1;

import java.awt.Dimension;

import org.junit.jupiter.api.Test;

import channelMerging.MultiChannelImage;
import figureOrganizer.MultichannelDisplayLayer;
import ij.IJ;
import ij.ImagePlus;
import logging.IssueLog;
import multiChannelFigureUI.MultiChannelDisplayCreator;

class IJ1MultiChannelCreatorTest {

	@Test
	void test() {
		IJ1MultichannelContext c = new IJ1MultichannelContext();
		ImageDisplayTester.setupImageJ();
		MultiChannelDisplayCreator cc = c.getMultichannelOpener();
		//test all visible image 
		
		/**tests the user dialog when there is no image open*/
		cc.creatMultiChannelDisplayFromUserSelectedImage(false, null);
		//tests what image is returned as the currently open one
		assert(cc.creatMultiChannelDisplayFromOpenImage().getSlot().getMultichannelImage()==null);
		
		/**tests to see if a multichannel is created*/
		ImagePlus i = IJ.createHyperStack("b", 400, 300, 3, 1, 1, 16);
		i.show();
		assert(cc.creatMultiChannelDisplayFromOpenImage()!=null);
		
		
		/**test user dialog for selecting an open image*/
		String string = "Select this option to complete test";
		IssueLog.showMessage("A dialog to choose an image will appear, choose the second one to complete the test");
		IJ.createHyperStack(string, 200, 300, 3, 1, 1, 16).show();
		MultichannelDisplayLayer mm = cc.creatMultiChannelDisplayFromUserSelectedImage(false, null);
		IssueLog.showMessage(mm.getTitle());
		
		assert(string.contains(mm.getTitle()));
		
		/**test of the create from buffered image*/
		MultiChannelImage creatMultiChannelFromImage = cc.creatMultiChannelFromImage(i.getBufferedImage());
		assert(creatMultiChannelFromImage!=null);
		assert(creatMultiChannelFromImage.getDimensions().equals(new Dimension(i.getWidth(), i.getHeight())));
	
	}

}

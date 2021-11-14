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
 * Date Modified: Dec 20, 2020
 * Version: 2021.2
 */
package appContextforIJ1;

import java.awt.Dimension;

import channelMerging.MultiChannelImage;
import figureOrganizer.MultichannelDisplayLayer;
import ij.IJ;
import ij.ImagePlus;
import logging.IssueLog;
import multiChannelFigureUI.MultiChannelDisplayCreator;
import testing.FigureTester;

class IJ1MultiChannelCreatorTest {

	
	public static void main(String[] args) {
		new IJ1MultiChannelCreatorTest().test();
	}
	

	void test() {
		IJ1MultichannelContext c = new IJ1MultichannelContext();
		ImageDisplayTester.setupImageJ();
		MultiChannelDisplayCreator cc = c.getMultichannelOpener();
		//test all visible image 
		
		/**tests the user dialog when there is no image open*/
		cc.creatMultiChannelDisplayFromUserSelectedImage(false, null);
		//tests what image is returned as the currently open one. if this test is run in isolation, none will be open
		FigureTester.closeAllWindows();
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

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
 * Version: 2022.1
 */
package appContextforIJ1;

import java.awt.Rectangle;

import org.junit.jupiter.api.Test;
import channelMerging.PreProcessInformation;
import figureOrganizer.FigureType;
import ij.IJ;
import ij.ImagePlus;
import logging.IssueLog;
import multiChannelFigureUI.MultiChannelDisplayCreator;
import objectDialogs.CroppingDialog;
import objectDialogs.CroppingDialog.CropDialogContext;
import testing.FigureTester;

/**Was written to check various image sizes to determine if the crop dialog should show appropriately for each.
 * Meant as a maual test of new method of sizing the crop dialog */
class CropDialogSizeTest {

	@Test
	void test() {
		IJ1MultichannelContext c = new IJ1MultichannelContext();
		ImageDisplayTester.setupImageJ();
		MultiChannelDisplayCreator cc = c.getMultichannelOpener();
		
		int[] xSizes = new int[] {500, 1000, 2000};
		int[] ySizes = new int[]{250, 1000, 2000};
		IssueLog.sytemprint=true;
		for(int xSize: xSizes)
		for(int ySize: ySizes)
			testCropDialogOfSize(c, cc, xSize, ySize);
	}

	/**
	 * @param c
	 * @param cc
	 * @param xSize
	 * @param ySize
	 */
	public void testCropDialogOfSize(IJ1MultichannelContext c, MultiChannelDisplayCreator cc, int xSize, int ySize) {
		
		ImagePlus i = IJ.createHyperStack("b", xSize, ySize, 3, 5, 8, 16);
		i=i.flatten();
		
		i.show();
		
		ImagePlusMultiChannelSlot mm2 = (ImagePlusMultiChannelSlot) cc.creatMultiChannelDisplayFromOpenImage().getSlot();
		
		assert(mm2.getImagePlus()!=null);
		
		/**tests ability to hide the image*/
		int start=c.getallVisibleMultichanal().size();
		mm2.hideImageWihtoutMessage();
		assert(c.getallVisibleMultichanal().size()==start-1);
		//i.createImagePlus().show();;
		
		//assert(FileChoiceUtil.yesOrNo("Testing: the test class will show a few images. You will click yes to confirm that"+ "they look as described"));
		
		
		/**test the crop function*/
		int x = 150;
		int y = 40;
		int width = 200;
		int h= 170;
		double scale=1;
		mm2.applyCropAndScale(new PreProcessInformation(new Rectangle(x, y, width, h), Math.PI*30/180, scale));
		assert(mm2.getImagePlus().getWidth()==width*scale);
		assert(mm2.getImagePlus().getHeight()==h*scale);
		CropDialogContext context = new CroppingDialog.CropDialogContext(1, FigureType.FLUORESCENT_CELLS);
		CroppingDialog.showCropDialogOfSize(mm2, null, context);
		//assert(FileChoiceUtil.yesOrNo("Did you see a cropping dialog with a rectangle (200 X 170) at 30 degrees?"));
		
		if(!context.lastDialog.wasOKed()) {
			IssueLog.log("Crop dialog issue for size ");
			IssueLog.log("x="+xSize+",  y="+ySize);
		}
		
		FigureTester.closeAllWindows();
	}

}

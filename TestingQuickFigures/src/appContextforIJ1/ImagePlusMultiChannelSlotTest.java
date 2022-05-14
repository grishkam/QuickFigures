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
import ij.IJ;
import ij.ImagePlus;
import multiChannelFigureUI.MultiChannelDisplayCreator;
import objectDialogs.CroppingDialog;
import testing.FigureTester;

/**performs a few checks related to the @see ImagePlusMultiChannelSlot
 * a class that holds an imageJ image, cropped copy and has methods that retrieve information related to the image */
class ImagePlusMultiChannelSlotTest {

	@Test
	void test() {
		IJ1MultichannelContext c = new IJ1MultichannelContext();
		ImageDisplayTester.setupImageJ();
		MultiChannelDisplayCreator cc = c.getMultichannelOpener();
		
		ImagePlus i = IJ.createHyperStack("b", 600, 500, 3, 5, 8, 16);
		i=i.flatten();
		
		i.show();
		
		ImagePlusMultiChannelSlot mm2 = (ImagePlusMultiChannelSlot) cc.creatMultiChannelDisplayFromOpenImage().getSlot();
		
		assert(mm2.getImagePlus()!=null);
		
		/**tests ability to hide the image*/
		int start=c.getallVisibleMultichanal().size();
		mm2.hideImageWihtoutMessage();
		assert(c.getallVisibleMultichanal().size()==start-1);
		i.createImagePlus().show();;
		
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
		CroppingDialog.showCropDialogOfSize(mm2, null, null);
		//assert(FileChoiceUtil.yesOrNo("Did you see a cropping dialog with a rectangle (200 X 170) at 30 degrees?"));
		
		mm2.getImagePlus().duplicate().show();
		//assert(FileChoiceUtil.yesOrNo("You should see a cropped+scaled vertion of the image. Is the image cropped to  (200 X 170) at an angle?"));
		
		/**test ability to retrieve scale info from an ImageJ image*/
		i.getCalibration().pixelHeight=18;
		i.getCalibration().pixelWidth=10;
		i.getCalibration().setUnit("Greg Unit");
		assert(mm2.getScaleInfo().getPixelWidth()==10);
		assert(mm2.getScaleInfo().getPixelHeight()==18);
		assert(mm2.getScaleInfo().getUnits().equals("Greg Unit"));
		
		mm2.saveImageEmbed();
		FigureTester.closeAllWindows();
	}

}

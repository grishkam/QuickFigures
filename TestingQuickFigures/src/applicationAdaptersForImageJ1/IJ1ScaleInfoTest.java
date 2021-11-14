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
package applicationAdaptersForImageJ1;


import org.junit.jupiter.api.Test;

import appContextforIJ1.ImageDisplayTester;
import ij.IJ;
import ij.ImagePlus;
import locatedObject.ScaleInfo;

/**tests the key traits of the scale info object*/
class IJ1ScaleInfoTest {

	@Test
	void test() {
		
		ImageDisplayTester.setupImageJ();
		
		ImagePlus i = IJ.createHyperStack("b", 600, 500, 3, 5, 8, 16);
		
		
		i.show();
		
		
		
		IJ1ScaleInfo oldScale = new IJ1ScaleInfo(i);
		
		/**test ability to retrieve scale info from an ImageJ image*/
		int ph = 18;
		int pw = 10;
		i.getCalibration().pixelHeight=ph;
		i.getCalibration().pixelWidth=pw;
		i.getCalibration().setUnit("Greg Unit");
		IJ1ScaleInfo newScale = new IJ1ScaleInfo(i);
		
		/**new scale should match the new numbers*/
		assert(newScale.getPixelWidth()==pw);
		assert(newScale.getPixelHeight()==ph);
		assert(newScale.getUnits().equals("Greg Unit"));
		
		/**old scale should not match the new numbers*/
		assert(oldScale.getPixelWidth()!=pw);
		assert(oldScale.getPixelHeight()!=ph);
		assert(!oldScale.getUnits().equals("Greg Unit"));
		
		/**Scale info objects must be scaled correctly when their images are scaled.
		  this just checks the math */
		for (double mag=1.5; mag<100; mag+=0.25 )
		{
			ScaleInfo s3 = newScale.copy();
			s3.scaleXY(mag);
			assert(s3.getPixelWidth()==pw/mag);
			assert(s3.getPixelHeight()==ph/mag);
			}
		
		
		
	}

}

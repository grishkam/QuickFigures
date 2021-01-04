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
 * Date Modified: Jan 4, 2021
 * Version: 2021.1
 */
package applicationAdaptersForImageJ1;

import ij.ImagePlus;
import ij.io.FileInfo;
import ij.measure.Calibration;
import locatedObject.ScaleInfo;

/**A ScaleInfo object whose initial values
 * are set to those of an ImageJ figure.
 this is used to share the information that is crucial to 
 maintaining accurate scale bar lengths
 */
public class IJ1ScaleInfo extends ScaleInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public IJ1ScaleInfo(ImagePlus imp) {
		if (imp==null) return;
		FileInfo fi=imp.getFileInfo();//.getFileInfo();
		if (fi==null) return;
		setToIJCalibration(imp.getCalibration());
		correctMicron();
	/**	setUnits(fi.unit);
		setPixelWidth(fi.pixelWidth);
		setPixelHeight(fi.pixelHeight);*/
		
	}
	
	private void setToIJCalibration(Calibration cal) {
		if (cal==null) return;
		setUnits(cal.getUnit());
		this.setPixelWidth(cal.pixelWidth);
		this.setPixelHeight(cal.pixelHeight);
		this.setPixelDepth(cal.pixelDepth);
	}
	
	

}

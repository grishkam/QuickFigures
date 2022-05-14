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
 * Version: 2022.1
 */
package applicationAdaptersForImageJ1;

import ij.ImagePlus;
import infoStorage.StringBasedMetaWrapper;
import infoStorage.MetaInfoWrapper;

/**This class is crucial for retrieving certain information from an image's metadata
 * For example, real channel names are obtained this way. See superclass for detail
  */
public class ImagePlusMetaDataWrapper extends StringBasedMetaWrapper  implements MetaInfoWrapper{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImagePlus image;

	public ImagePlusMetaDataWrapper(ImagePlus imp) {
		image=imp;
	}


	public String getProperty() {
		ImagePlus img = image;
		return (String) img.getProperty("Info");
	}
	
	public void setProperty(String newProp) {
		ImagePlus img = image;
		img.setProperty("Info", newProp);
	}
	
	
}

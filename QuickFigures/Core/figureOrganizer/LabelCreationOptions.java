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
 * Date Modified: Oct 28, 2021
 * Version: 2021.2
 */
package figureOrganizer;

import layout.RetrievableOption;

/**A set of properties that determine how figure labels are automatically generated*/
public class LabelCreationOptions {
	
	/**The current label creation options*/
	public static LabelCreationOptions current=new LabelCreationOptions() ;
	
	public static final int useFileNames=1,useFolderNames=2;
	@RetrievableOption(key = "use Image anmes", label="Use Image File Names to create labels", choices={"number", "use image file names", "use folder names"})
	public int useNames=1;//use the names of image files for their labels?
	
	@RetrievableOption(key = "clip labels", label="Clip Labels Longer Than")
	public double clipLabels=50;//The max length at which long labels be truncated
	
	/**returns true if file names will be used for labels*/
	boolean useFileOrFoldeName() {
		if (useNames==useFileNames)
			return true;
		if (useNames==useFolderNames)
			return true;
		return false;
	}

	/**
	 * @return
	 */
	public boolean usesFolderNames() {
		if (useNames==useFolderNames)
			return true;
		return false;
	}

}

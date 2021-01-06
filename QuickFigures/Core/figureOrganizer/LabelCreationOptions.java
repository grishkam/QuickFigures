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
package figureOrganizer;

import layout.RetrievableOption;

/**A set of properties that determine how figure labels are automatically generated*/
public class LabelCreationOptions {
	
	/**The current label creation options*/
	public static LabelCreationOptions current=new LabelCreationOptions() ;
	
	@RetrievableOption(key = "use Image anmes", label="Use Image Names To create labels")
	public boolean useImageNames=true;//use the names of image files for their labels?
	
	@RetrievableOption(key = "clip labels", label="Clip Labels Longer Than")
	public double clipLabels=50;//The max length at which long labels be truncated

}

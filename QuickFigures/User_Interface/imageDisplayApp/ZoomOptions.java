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
 * Date Modified: Jan 12, 2021
 * Version: 2023.2
 * 
 */
package imageDisplayApp;

import layout.RetrievableOption;

/**stored options related to the zooming*/
public class ZoomOptions {
	
	public static ZoomOptions current=new ZoomOptions();
	
	@RetrievableOption(key = "resizepostzoom", label="Resize Window After Every Zooming")
	public boolean resizeWindowsAfterZoom=true;

}

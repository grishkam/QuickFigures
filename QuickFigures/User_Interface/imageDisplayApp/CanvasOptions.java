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
package imageDisplayApp;

import layout.RetrievableOption;

/**stores a value that indicates whether to allow automatic resizing of
  the canvas of worksheets to fit the objects inside*/
public class CanvasOptions {
	
	public static CanvasOptions current=new CanvasOptions();
	
	/**The current canvas options determine if automatic edits to the size of worksheets
	  are performed. Users also have the option to block resizes for an individual worksheet (implemented elsewhere)
	  */
	@RetrievableOption(key = "Resize After Layout Edit", label="Automatically enlarge worksheets while editing?")
	public boolean resizeCanvasAfterEdit=true;

}

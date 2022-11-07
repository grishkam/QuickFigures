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
 * Version: 2022.2
 */
package appContext;

import basicAppAdapters.ToolColors;
//import basicAppAdapters.ToolAdapterG;
import ultilInputOutput.FileChoiceUtil;

/**Context Determines which library is currently used for multi-dimensional images  
  if currentContext is set to null, then none has been installed.
  As of writing this, the only implementation of  MultiDimensionalImageContext is for imageJ 1
  but I have tentative plans to either write one that uses ImgLib2.
  As long as the implementation of that context works, every part of QuickFigures should work*/
public class CurrentAppContext {

	/**The current context. Determines which package is used to open/find images*/
	public static MultiDimensionalImageContext currentContext=null;
	
	/**Work in progress, a single toolbar will be used by multiple tools*/
	public static ToolbarColorContext  currentColorContext=new ToolColors();
	
	/**Returns the system for handing multidimensional images
	 * @see MultiDimensionalImageContext 
	 * */
	public static MultiDimensionalImageContext getMultichannelContext() {
		return currentContext;
	}
	/**Sets up a system for handing multidimensional images
	 * @see MultiDimensionalImageContext 
	 * */
	public static void setMultichannelContext(MultiDimensionalImageContext mcu) {
		currentContext=mcu;
	}
	
	/**returns the context for the tool bar color*/
	public static ToolbarColorContext getGeneralContext() {
		return currentColorContext;
	}
	
	/**returns the default directory. That directory may change depending on what the user opens or saves
	  with the save dialog or open dialog*/
	public static String getDefaultDirectory() {
		if ( getMultichannelContext()==null) return FileChoiceUtil.getWorkingDirectory();
		if ( getMultichannelContext().getDefaultDirectory()==null)return FileChoiceUtil.getWorkingDirectory();
		return getMultichannelContext().getDefaultDirectory();
	}
	
	
}

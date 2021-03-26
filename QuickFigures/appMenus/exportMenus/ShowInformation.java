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
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package exportMenus;

import java.awt.Window;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import ultilInputOutput.FileChoiceUtil;

/**An class that only shows information*/
public class ShowInformation  extends BasicMenuItemForObj {

	

	private String name;
	String path = "File<Export";
	
	public ShowInformation(String name) {
		this.name=name;
	}

	@Override
	public String getMenuPath() {
		
		return path;
	}
	
	
	/**name for the meny item*/
	public String getNameText() {
		return name;
	}
	
	
	
	
	
}

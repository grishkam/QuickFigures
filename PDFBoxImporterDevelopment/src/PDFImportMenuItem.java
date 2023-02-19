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
 * Date Created Nov 27, 2021
 * Date Modified: Jan 3, 2022
 * Version: 2023.1
 */
import java.io.IOException;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import basicMenusForApp.MenuBarForApp;
import basicMenusForApp.MenuBarItemInstaller;
import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;



/**
 
 * 
 */
public class PDFImportMenuItem implements MenuBarItemInstaller {

	/**
		 
		 * 
		 */
	public class PDFImport extends BasicMenuItemForObj {

		@Override
		public void performActionDisplayedImageWrapper(DisplayedImage diw) {
			try {
				PDFReadTest.addPDFToFigure(FileChoiceUtil.getUserOpenFile(), diw);
			} catch (IOException e) {
				IssueLog.logT(e);
			}

		}

		@Override
		public String getCommand() {
			
			return "Import Plot From PDF";
		}

		@Override
		public String getNameText() {
			return "Import Plot From PDF";
		}

		@Override
		public String getMenuPath() {
			return "File<Import";
		}

		
	}

	@Override
	public void addToMenuBar(MenuBarForApp installer) {
		installer.installItem(new PDFImport());

	}

}

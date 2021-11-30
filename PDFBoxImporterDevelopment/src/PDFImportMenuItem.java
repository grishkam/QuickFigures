import java.io.IOException;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import basicMenusForApp.MenuBarForApp;
import basicMenusForApp.MenuBarItemInstaller;
import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;

/**
 * Author: Greg Mazo
 * Date Modified: Nov 27, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */

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

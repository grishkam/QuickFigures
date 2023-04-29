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
 * Date Created: April 28, 2023
 * Date Modified: April 28, 2023
 * Version: 2023.1
 */
package basicMenusForApp;

import java.io.File;
import java.util.ArrayList;

import ultilInputOutput.FileChoiceUtil;
import applicationAdapters.DisplayedImage;
import exportMenus.EPSQuickExport;
import exportMenus.PDFQuickExport;
import exportMenus.PNGQuickExport;
import exportMenus.PPTQuickExport;
import exportMenus.QuickExport;
import exportMenus.TiffQuickExport;
import figureFormat.DirectoryHandler;
import imageDisplayApp.ImageDisplayIO;
import imageDisplayApp.ImageWindowAndDisplaySet;
import logging.IssueLog;
import messages.ShowMessage;
import standardDialog.StandardDialog;
import standardDialog.choices.ChoiceInputPanel;

/**Implementa a menu item that.
 * Opens a series of saved files with figures, exports them all into another format*/
public class ConvertSavedWorksheets  extends BasicMenuItemForObj {

	
	public ArrayList<QuickExport> options=createExporterList();


	/**
	 * @return
	 */
	public ArrayList<QuickExport> createExporterList() {
		ArrayList<QuickExport> arrayList = new ArrayList<QuickExport>();
		arrayList.add(new PDFQuickExport(false));
		arrayList.add(new EPSQuickExport(false));
		arrayList.add(new PNGQuickExport(false));
		arrayList.add(new TiffQuickExport(false));
		arrayList.add(new PPTQuickExport(false));
		return arrayList;
	}
	
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		File[] files=FileChoiceUtil.getFiles();
	
		if(files.length==0) {
			ShowMessage.showOptionalMessage("no files", false, "you must select one or more .ser files with saved worksheets to use this option");
		}
		
		
		StandardDialog sd = new StandardDialog();
		sd.setWindowCentered(true);
		sd.setModal(true);
		sd.addMessage("Please select the file type");
		ChoiceInputPanel panel=new ChoiceInputPanel("Export as", options, 0, QuickExport.class, null);
		sd.add("choices", panel);
		sd.showDialog();
		QuickExport qe= (QuickExport) panel.getSelectedObject();
		
		File fd1 = FileChoiceUtil.getFolder("Choose where to save files");
		String basePath = fd1.getAbsolutePath()+File.separator;
	
		int count = 0;
		
		for(File f: files) try {
		
				ImageWindowAndDisplaySet open = ImageDisplayIO.showFile(f);
				String newpath = basePath+f.getName();
				newpath = qe.addExtension(newpath);
				
				qe.saveInPath(open, newpath);
				open.closeWindowButKeepObjects();
				count++;
				
		} catch (Throwable e) {
			IssueLog.log(e);
		}
		
		if(count==0) {
			ShowMessage.showOptionalMessage("Failed to export", false, "was not able to open/export files", "Perhaps the wrong file type or anyother issue occured");
		}
		
	}
	


	@Override
	public String getCommand() {
		return getNameText()+getMenuPath();
	}

	@Override
	public String getNameText() {
		return "Open many worksheets and export";
	}

	@Override
	public String getMenuPath() {
		return "File<Export<More<";
	}

}

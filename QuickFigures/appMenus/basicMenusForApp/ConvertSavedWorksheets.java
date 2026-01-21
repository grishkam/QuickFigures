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
 * Date Modified: Jan 19, 2026
 * Version: 2026.1
 */
package basicMenusForApp;

import java.awt.Desktop;
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
import imageDisplayApp.ImageDisplayIO;
import imageDisplayApp.ImageWindowAndDisplaySet;
import logging.IssueLog;
import messages.ShowMessage;
import standardDialog.StandardDialog;
import standardDialog.booleans.BooleanArrayInputPanel;
import standardDialog.booleans.BooleanInputPanel;
import standardDialog.choices.ChoiceInputPanel;

/**Implementa a menu item that.
 * Opens a series of saved files with figures, exports them all into one or more other formats*/
public class ConvertSavedWorksheets  extends BasicMenuItemForObj {

	
	public ArrayList<QuickExport> options=createExporterList();

	boolean allow_multiple_format_export=true;

	private boolean open_right_away=true;

	
	public ConvertSavedWorksheets(boolean multiples) {
		allow_multiple_format_export=multiples;
	}
	
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
		boolean[] startingBoxes = new boolean[options.size()];
		startingBoxes[0]=true;
		startingBoxes[4]=true;
		BooleanArrayInputPanel array_input = new BooleanArrayInputPanel("Export multiple formats", startingBoxes);
		
		if(this.allow_multiple_format_export) {
			String[] choices_as_strings = ChoiceInputPanel.setChoicesToStrings(options, null);
			array_input.setBoxNames(choices_as_strings);
			sd.add("multichoices", array_input);
		}
		else sd.add("choices", panel);
		
		sd.add("open right away", new BooleanInputPanel("open right away", this.open_right_away));
		
		
		sd.showDialog();
		open_right_away=sd.getBoolean("open right away");
		QuickExport[] qe2=null;
		if(!allow_multiple_format_export) {
				QuickExport qe= (QuickExport) panel.getSelectedObject();
				
				qe2=new QuickExport[] {qe};
		} else {
			boolean[] b = array_input.getArray();
			ArrayList<QuickExport> selected_formats=new ArrayList<QuickExport>();
			for(int i=0; i<b.length; i++) {
				if(b[i]) {
					QuickExport e = options.get(i);
					
					selected_formats.add(e);
				}
				
			}
			qe2=selected_formats.toArray(new QuickExport[selected_formats.size()] );
		}
		
		File fd1 = FileChoiceUtil.getFolder("Choose where to save files");
		String basePath = fd1.getAbsolutePath()+File.separator;
	
		int count = exportFilesToSelectedFormat(files, qe2, basePath);
		
		if(count==0) {
			ShowMessage.showOptionalMessage("Failed to export", false, "was not able to open/export files", "Perhaps the wrong file type or anyother issue occured");
		}
		
	}


	/**
	 * @param files
	 * @param qe
	 * @param basePath
	 * @return
	 */
	private int exportFilesToSelectedFormat(File[] files, QuickExport[] qe, String basePath) {
		int count = 0;
		
		for(File f: files) try {
				String worksheet_name = f.getName();
				ImageWindowAndDisplaySet open = ImageDisplayIO.showFile(f);
				
				saveInSeveralFormats(qe, basePath, open, worksheet_name);
				open.closeWindowButKeepObjects();
				count++;
				
		} catch (Throwable e) {
			IssueLog.log(e);
		}
		return count;
	}

	/**saves files into multiple format
	 * @param qe
	 * @param basePath
	 * @param open
	 * @param worksheet_name
	 * @throws Exception
	 */
	private void saveInSeveralFormats(QuickExport[] qe, String basePath, ImageWindowAndDisplaySet open,
			String worksheet_name) throws Exception {
		String newpath = basePath+worksheet_name;
		
		for(QuickExport qe2: qe) try {
			String newpath2 = qe2.addExtension(newpath);
			qe2.saveInPath(open, newpath2);
			if(open_right_away) {
				Desktop.getDesktop().open(new File(newpath2));
			}
		} catch (java.io.FileNotFoundException ex) {
			ShowMessage.showOptionalMessage("Had an issue with this file saving", true, "problem saving "+ex.getMessage());
		}
	}
	


	@Override
	public String getCommand() {
		return getNameText()+getMenuPath();
	}

	@Override
	public String getNameText() {
		if(allow_multiple_format_export)
			return "Worksheets to multiple formats";
		return "Open many worksheets and export";
	}

	@Override
	public String getMenuPath() {
		return "File<Export<More<";
	}

}

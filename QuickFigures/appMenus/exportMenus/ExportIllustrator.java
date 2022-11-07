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
 * Version: 2022.2
 */
package exportMenus;

import java.io.File;
import java.util.Collection;

import applicationAdapters.DisplayedImage;
import illustratorScripts.AdobeScriptMaker;
import illustratorScripts.ZIllustratorScriptGenerator;
import messages.ShowMessage;

/**A menu item for generating an a script that con run in Adobe Illustrator*/
public class ExportIllustrator extends QuickExport  {
	
	boolean askforFile=false;
	AdobeScriptMaker sm=	new AdobeScriptMaker();
	private String ext="ai";

	/**creates a new illustrator export
	   @param askForFile set to true if a file chooser should be shown*/
	public ExportIllustrator(boolean askForFile, String extension) {
		this.askforFile=askForFile;
		if (extension!=null)
			this.ext=extension;
	}

	public ExportIllustrator() {
		this(false, null);
	}
	
	
	
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if (diw!=null) {
			diw.updateDisplay();
			ShowMessage.showOptionalMessage("Illustrator script creator ", false, "Illustrator script generator will create a .jsx file", "open that file with Adobe Illustrator" , "always wait for one .jsx script to finish before another",  "For alternative, export as SVG first and open with Illustrator");
		
			File file=null;
			if (askforFile) {
				file=super.getFileAndaddExtension();
			}
			createInIllustrator(diw, file);
			
		}
	}


	


	/**creates an illustrator script to generate the figure in illustrator
	 * @param diw 
	 * @param file the save location where illustrator will save
	 */
	public void createInIllustrator(DisplayedImage diw, File file) {
		diw.updateDisplay();
		sm.sendWrapperToills(diw.getImageAsWorksheet().getAsWrapper(), true, file);
		ZIllustratorScriptGenerator.instance.execute();
	}
	
	/**creates an illustrator script to generate the figure in illustrator
	 * @param diw 
	 * @param file the save location where illustrator will save
	 */
	public void createInIllustrator(String folder, Collection<? extends DisplayedImage> diws) {
		for(DisplayedImage diw: diws) {
			
			diw.updateDisplay();
			
			String filename=folder+diw.getImageAsWorksheet().getTitle()+".ai";
			sm.sendWrapperToills(diw.getImageAsWorksheet().getAsWrapper(), true, new File(filename));
		}
		ZIllustratorScriptGenerator.instance.execute();
	}
	
	
	

	@Override
	public String getNameText() {
		if (ext.equals("eps"))
			return "Create in Illustrator and save as EPS";
		if (ext.equals("pdf"))
			return "Create in Illustrator and save as PDF";
		if (ext.equals("ai"))
			return  "Create in Illustrator";
		if (ext.equals("psd"))
			return  "Create in Illustrator and save as Photoshop";
		return "Create and run Illustrator Script";
	}




	@Override
	public String getMenuPath() {
		return "File<Export<Adobe Illustrator";
	}




	@Override
	protected String getExtension() {
		return ext;
	}




	@Override
	protected String getExtensionName() {
		return "Illustrator "+this.getExtension().toUpperCase();
	}
	
}

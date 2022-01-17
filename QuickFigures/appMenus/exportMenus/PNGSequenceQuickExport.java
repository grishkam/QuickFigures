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
 * Version: 2022.0
 */
package exportMenus;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import appContext.CurrentAppContext;
import applicationAdapters.DisplayedImage;
import logging.IssueLog;
import messages.ShowMessage;
import standardDialog.StandardDialog;
import uiForAnimations.KeyFrameHandling;

/**exporter for a sequence of .png files representing time frames within an animation*/
public class PNGSequenceQuickExport extends QuickExport {
	/**
	 * @param openNow if the com
	 */
	public PNGSequenceQuickExport() {
		super(false);
	}

	protected String getExtension() {
		return "png";
	}
	
	protected String getExtensionName() {
		return "PNG Images";
	}

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		
		try{
			
		File f=getFileAndaddExtension();
		if(f==null) return;
		String basename = saveToFolder(diw, f);
		 
		/**opens the image as a timeline*/
		CurrentAppContext.getMultichannelContext().getMultichannelOpener().createFromImageSequence(basename, null);
		
		} catch (Throwable t) {
			t.printStackTrace();
		}
	        
	}

	/**
	 * @param diw
	 * @param f
	 * @return
	 * @throws IOException
	 */
	private String saveToFolder(DisplayedImage diw, File f) throws IOException {
		String newpath=f.getAbsolutePath();
		
		
		String basename=newpath.substring(0, newpath.length()-4);
		String basenameFile=f.getName().substring(0, f.getName().length()-4);
		
		
		FlatCreator flat = new FlatCreator(true);
		flat.showDialog();
		StandardDialog window = ShowMessage.showNonModel( "export in progress", "export will take time", "Please do not click the window or press keys during export");
		
		IssueLog.waitSeconds(1);
	
		for(int i=0; i<diw.getEndFrame(); i++) {
			KeyFrameHandling.applyFrameAnimators(diw, i);
			BufferedImage bi = flat.createFlat(diw.getImageAsWorksheet());
			File nameToWrite = new File(basename+"/"+basenameFile+"_"+i+".PNG");
			nameToWrite.mkdirs();
			ImageIO.write(bi, "PNG",nameToWrite);
			IssueLog.log("Exported frame "+i);
		}
		window.setVisible(false);
		return basename;
	}

	@Override
	public String getCommand() {
		return "Export timeline as PNG sequence";
	}

	@Override
	public String getNameText() {
		return "Export Time Line as Image Sequence (.png)";
	}
	
	@Override
	public String getMenuPath() {
		return "File<Export<Animations";
	}
	
}

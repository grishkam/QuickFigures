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


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import applicationAdapters.DisplayedImage;
import ultilInputOutput.FileChoiceUtil;

/**this supports a menu item that exports a figure as PNG file*/
public class PNGQuickExport extends QuickExport {
	public static boolean showDialogEverytime=true;

	/**
	 * @param openNow determines if the exported will will be opened right away
	 */
	public PNGQuickExport(boolean openNow) {
		super(openNow);
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
		
		String newpath=getFileAndaddExtension().getAbsolutePath();
		FileChoiceUtil.overrideQuestion(new File(newpath));
		saveInPath(diw, newpath);
		
		} catch (Throwable t) {
			t.printStackTrace();
		}
	        
	}

	/** saves the image into a particular file path
	 * @param diw
	 * @param newpath
	 * @throws IOException
	 */
	public void saveInPath(DisplayedImage diw, String newpath) throws IOException {
		FlatCreator flat = new FlatCreator();
		
		flat.setUseTransparent(false);
		if (showDialogEverytime)flat.showDialog();
		
		writePNGFile(diw, newpath, flat);
	}

	/**writes a png file with the given flag creator
	 * @param diw
	 * @param newpath
	 * @param flat
	 * @throws IOException
	 */
	public void writePNGFile(DisplayedImage diw, String newpath, FlatCreator flat) throws IOException {
		diw.updateDisplay();
		BufferedImage bi = flat.createFlat(diw.getImageAsWorksheet());
		writeImage(newpath, bi);
	}

	/**
	 writes the buffered image to the save path given
	 */
	public void writeImage(String newpath, BufferedImage bi) throws IOException {
		ImageIO.write(bi, "PNG", new File(newpath));
	}

	@Override
	public String getCommand() {
		return "Export as PNG";
	}

	@Override
	public String getNameText() {
		return "Image (.png)";
	}
	

	
	
	
}

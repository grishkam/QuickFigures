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
package exportMenus;


import java.awt.image.BufferedImage;
import java.io.IOException;


import appContext.CurrentAppContext;

/**this class exports a figure as tiff file using ImageJ*/
public class TiffQuickExport extends PNGQuickExport {
	/**
	 * @param openNow
	 */
	public TiffQuickExport(boolean openNow) {
		super(openNow);
		// TODO Auto-generated constructor stub
	}

	protected String getExtension() {
		return "tiff";
	}
	
	protected String getExtensionName() {
		return "Tiff Image";
	}

	/**
	 writes the buffered image to the save path given
	 */
	public void writeImage(String newpath, BufferedImage bi) throws IOException {
		CurrentAppContext.getMultichannelContext().getMultichannelOpener().creatRGBFromImage(bi, newpath);
	}

	@Override
	public String getCommand() {
		return "Export as Tiff";
	}

	@Override
	public String getNameText() {
		return "Image (.tiff)";
	}
	
	
	
}

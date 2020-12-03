/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.batik.ext.awt.image.codec.png.PNGImageWriter;

import applicationAdapters.DisplayedImage;

/**this supports a menu item that exports a figure as PNG file*/
public class PNGQuickExport extends QuickExport {
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
		FlatCreator flat = new FlatCreator();
		flat.setUseTransparent(false);
		flat.showDialog();
		BufferedImage bi = flat.createFlat(diw.getImageAsWrapper());
		writeImage(newpath, bi);
		
		} catch (Throwable t) {
			t.printStackTrace();
		}
	        
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
		return "Export as Image (.png)";
	}
	
	public static void main(String[] args) throws IOException {
		String path=args[0];
		BufferedImage image = ImageIO.read(new File(path));
		PNGImageWriter pngwrit = new PNGImageWriter();
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		pngwrit.writeImage(image, bao);
		
		
	}
	
	
	
}

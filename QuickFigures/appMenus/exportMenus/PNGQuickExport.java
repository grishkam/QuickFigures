package exportMenus;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import applicationAdapters.DisplayedImage;

/**this class exports a figure as PNG file*/
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
	
	
	
}

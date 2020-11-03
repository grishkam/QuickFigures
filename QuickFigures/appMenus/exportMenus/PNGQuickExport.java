package exportMenus;


import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import applicationAdapters.DisplayedImageWrapper;

public class PNGQuickExport extends QuickExport {
	protected String getExtension() {
		return "png";
	}
	
	protected String getExtensionName() {
		return "PNG Images";
	}

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImageWrapper diw) {
		// TODO Auto-generated method stub
		try{
		
		String newpath=getFileAndaddExtension().getAbsolutePath();
		FlatCreator flat = new FlatCreator();
		flat.setUseTransparent(false);
		flat.showDialog();
		BufferedImage bi = flat.createFlat(diw.getImageAsWrapper());
		ImageIO.write(bi, "PNG", new File(newpath));
		 
		   /**
		  SVGsaver saver = new SVGsaver();
		   
		  saver.saveWrapper(newpath, diw);*/
		
		} catch (Throwable t) {
			t.printStackTrace();
		}
	        
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

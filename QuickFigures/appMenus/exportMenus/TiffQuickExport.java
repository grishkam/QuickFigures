package exportMenus;


import java.awt.image.BufferedImage;
import java.io.IOException;


import appContext.CurrentAppContext;

/**this class exports a figure as tiff file using ImageJ*/
public class TiffQuickExport extends PNGQuickExport {
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
		return "Export as Image (.tiff)";
	}
	
	
	
}

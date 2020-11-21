package exportMenus;


import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import appContext.CurrentAppContext;
import applicationAdapters.DisplayedImage;
import channelMerging.MultiChannelImage;
import uiForAnimations.KeyFrameHandling;

/**exporter for a sequence of .png files representing time frames within an animation*/
public class PNGSequenceQuickExport extends QuickExport {
	protected String getExtension() {
		return "png";
	}
	
	protected String getExtensionName() {
		return "PNG Images";
	}

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		// TODO Auto-generated method stub
		try{
		File f=getFileAndaddExtension();
		String newpath=f.getAbsolutePath();
		
		
		String basename=newpath.substring(0, newpath.length()-4);
		String basenameFile=f.getName().substring(0, f.getName().length()-4);
		
		
		FlatCreator flat = new FlatCreator(true);
		flat.showDialog();
		for(int i=0; i<diw.getEndFrame(); i++) {
			KeyFrameHandling.applyFrameAnimators(diw, i);
			BufferedImage bi = flat.createFlat(diw.getImageAsWrapper());
			File nameToWrite = new File(basename+"/"+basenameFile+"_"+i+".PNG");
			nameToWrite.mkdirs();
			ImageIO.write(bi, "PNG",nameToWrite);
		}
		
		 
		   new CurrentAppContext();
		
		MultiChannelImage a = CurrentAppContext.getMultichannelContext().getMultichannelOpener().createFromImageSequence(basename, null);
		
		} catch (Throwable t) {
			t.printStackTrace();
		}
	        
	}

	@Override
	public String getCommand() {
		return "Export timeline as PNG sequence";
	}

	@Override
	public String getNameText() {
		return "Export Time Line as Image Sequence (.png)";
	}
	
}

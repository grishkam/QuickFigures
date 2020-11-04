

import applicationAdaptersForImageJ1.ImagePlusWrapper;
import ij.ImageListener;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import logging.IssueLog;

/**A stub. will allow user to chose to automatically color the channels*/
public class Zvi_NameOnOpen implements PlugIn, ImageListener {
	
	static boolean added=false;
	
	public static void main(String[] args) {}

	@Override
	public void run(String arg0) {
	if (!added)	{
		ImagePlus.addImageListener(new Zvi_NameOnOpen());
		
	}
		added=true;
	}

	@Override
	public void imageClosed(ImagePlus arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void imageOpened(ImagePlus arg0) {
		IssueLog.log("Will determine colors for "+arg0.getOriginalFileInfo());
		if (arg0==null ||arg0.getOriginalFileInfo()==null) return;
		String name = arg0.getOriginalFileInfo().fileName;
		if (name==null) return;
		
		String lowerCase = name.toLowerCase();
		if   (    lowerCase.endsWith(".zvi")
				||lowerCase.endsWith(".lif")
				||lowerCase.endsWith(".lei")
				||lowerCase.endsWith(".czi")
				) {
			//IssueLog.log("Zvi, Czi and Lif Correction running");
			new ImagePlusWrapper(arg0).renameBasedOnRealChannelName();
			//new ImagePlusWrapper(arg0).colorBasedOnRealChannelName();
		}
		
		if   (    lowerCase.endsWith(".zvi")|| lowerCase.endsWith(".czi"))//on nov 3 2020 added czi
		{
			new ImagePlusWrapper(arg0).colorBasedOnRealChannelName();
		}
	}

	@Override
	public void imageUpdated(ImagePlus arg0) {
		// TODO Auto-generated method stub
		
	}

}

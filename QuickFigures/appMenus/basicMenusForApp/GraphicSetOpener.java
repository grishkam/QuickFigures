package basicMenusForApp;

import java.io.File;
import ultilInputOutput.FileChoiceUtil;
import applicationAdapters.DisplayedImageWrapper;
import imageDisplayApp.ImageDisplayIO;

/**Opens a figure display*/
public class GraphicSetOpener  extends BasicMenuItemForObj {

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImageWrapper diw) {
		File f=FileChoiceUtil.getOpenFile();
	
		ImageDisplayIO.showFile(f);
			
		
	}
	

	

	

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "openDisplaySet";
	}

	@Override
	public String getNameText() {
		// TODO Auto-generated method stub
		return "Figure Display";
	}

	@Override
	public String getMenuPath() {
		// TODO Auto-generated method stub
		return "File<Open<";
	}

}

package basicMenusForApp;

import java.io.File;
import applicationAdapters.DisplayedImage;
import ultilInputOutput.FileChoiceUtil;
import imageDisplayApp.GraphicContainingImage;
import imageDisplayApp.ImageDisplayIO;

public class GraphicSetSaver  extends BasicMenuItemForObj {

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		
		if (diw.getImageAsWrapper() instanceof GraphicContainingImage) {
			GraphicContainingImage theSet=(GraphicContainingImage) diw.getImageAsWrapper();
			File f=FileChoiceUtil.getSaveFile(theSet.getSavePath(), theSet.getSaveName());
		if (f==null) return;
		//diw.getImageAsWrapper();
		if (theSet.getTitle().equals(f.getName())) {} else {
			theSet.setTitle(f.getName());
				
		}
		
		/**performs update so new name can appear on window*/
		diw.updateWindowSize();
		
		/**does the actual saving*/
		ImageDisplayIO.writeToFile(f, theSet);
		}
			
		
	}
	
	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "saveDisplaySet";
	}

	@Override
	public String getNameText() {
		// TODO Auto-generated method stub
		return "Figure Display";
	}

	@Override
	public String getMenuPath() {
		// TODO Auto-generated method stub
		return "File<Save<";
	}

}

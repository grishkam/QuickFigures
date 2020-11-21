package basicMenusForApp;

import appContext.ImageDPIHandler;
import applicationAdapters.DisplayedImage;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageMenu.CanvasDialogResize;

public class NewCanvasDialog  extends BasicMenuItemForObj   {

	int width=8*ImageDPIHandler.getStandardDPI();
	int height=10*ImageDPIHandler.getStandardDPI();
	
	int type=1;
	
	
	@Override
	public String getMenuPath() {
		return "File<New";
	}
	
	@Override
	public String getNameText() {
		return "Empty Figure Display"+typeString();
	}
	
	String typeString() {
		
		return "";
	}
	

	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		diw=ImageWindowAndDisplaySet.createAndShowNew("New Image", width, height);
		CanvasDialogResize cdr = new CanvasDialogResize();
		cdr.fancy=false;
		new CanvasDialogResize().performActionDisplayedImageWrapper(diw);
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "newCanvas"+typeString();
	}

}

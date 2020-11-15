package basicMenusForApp;

import applicationAdapters.DisplayedImage;
import imageDisplayApp.GraphicSetDisplayWindow;

public class GraphicSetCloser2  extends BasicMenuItemForObj {

	boolean save=false;
	
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if (diw.getWindow() instanceof GraphicSetDisplayWindow) {
			GraphicSetDisplayWindow theSet=(GraphicSetDisplayWindow) diw.getWindow();
			theSet.closeGroupAndSupportingWindows(save);
				
		}
		
		
		}
			
		
	

		
		
	
	

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "closeDisplaySetAndSupporting";
	}

	@Override
	public String getNameText() {
		// TODO Auto-generated method stub
		return "Close Group";
	}

	@Override
	public String getMenuPath() {
		// TODO Auto-generated method stub
		return "File<";
	}

}

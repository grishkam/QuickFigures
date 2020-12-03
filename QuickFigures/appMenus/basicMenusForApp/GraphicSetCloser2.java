package basicMenusForApp;

import applicationAdapters.DisplayedImage;
import imageDisplayApp.GraphicSetDisplayWindow;

/**this class implements a menu item for closing the figure. 
  the command will also close the layers window.*/
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
		return "closeDisplaySetAndSupporting";
	}

	@Override
	public String getNameText() {
		return "Close Group";
	}

	@Override
	public String getMenuPath() {
		return "File<";
	}

}

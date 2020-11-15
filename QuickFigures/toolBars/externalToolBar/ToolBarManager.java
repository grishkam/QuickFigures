package externalToolBar;

import applicationAdapters.DisplayedImage;

public class ToolBarManager {
	static InterfaceExternalTool<DisplayedImage> tool=null;
	
	public static InterfaceExternalTool<DisplayedImage> getCurrentTool() {
		return tool;
		
		
	}

	public static void setCurrentTool(
			InterfaceExternalTool<DisplayedImage> currentTool) {
		tool=currentTool;
		
	}
	
	
}

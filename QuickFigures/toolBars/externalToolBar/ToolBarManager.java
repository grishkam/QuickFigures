package externalToolBar;

import applicationAdapters.DisplayedImageWrapper;

public class ToolBarManager {
	static InterfaceExternalTool<DisplayedImageWrapper> tool=null;
	
	public static InterfaceExternalTool<DisplayedImageWrapper> getCurrentTool() {
		return tool;
		
		
	}

	public static void setCurrentTool(
			InterfaceExternalTool<DisplayedImageWrapper> currentTool) {
		tool=currentTool;
		
	}
	
	
}

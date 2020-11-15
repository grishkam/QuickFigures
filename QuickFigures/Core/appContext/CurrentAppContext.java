package appContext;

import basicAppAdapters.ToolColors;
//import basicAppAdapters.ToolAdapterG;
import ultilInputOutput.FileChoiceUtil;

/**Context Determines which library is currently used for multi-dimensional images  
  if currentContext is set to null, then none has been installed.
  As of writing this, the only implementation of  MultiDimensionalImageContext is for imageJ 1
  but I have tentative plans to either write one that uses ImgLib2.
  As long as the implementation of that context works, every part of QuickFigures should work*/
public class CurrentAppContext {

	public static MultiDimensionalImageContext currentContext=null;
	//determines  how multi-dimensional images are viewed opened and merged
	public static ToolbarColorContext  currentColorContext=new ToolColors();
	
	public static MultiDimensionalImageContext getMultichannelContext() {
		return currentContext;
	}
	
	public static void setMultichannelContext(MultiDimensionalImageContext mcu) {
		currentContext=mcu;
	}
	
	/**returns the context for the tool bar color*/
	public static ToolbarColorContext getGeneralContext() {
		return currentColorContext;
	}
	
	/**returns the default directory. That directory may change depending on what the user opens or saves
	  with the save dialog or open dialog*/
	public static String getDefaultDirectory() {
		if ( getMultichannelContext()==null) return FileChoiceUtil.getWorkingDirectory();
		
		
		if ( getMultichannelContext().getDefaultDirectory()==null)return FileChoiceUtil.getWorkingDirectory();
	
		return getMultichannelContext().getDefaultDirectory();
	}
	
	
}

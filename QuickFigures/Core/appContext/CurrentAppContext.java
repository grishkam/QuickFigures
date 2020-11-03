package appContext;

import basicAppAdapters.ToolAdapterG;
import ultilInputOutput.FileChoiceUtil;

/**When i made this, most interfaces relied on imageJ1 classes. Wanted to design a
  single location where I could switch to sciInfo or imgLib2
 * Created this as a way to set the interfaces */
public class CurrentAppContext {

	public static MultichannelContext currentContext=null;
	public static GeneralAppContext  currentGenelalContext=new ToolAdapterG();//null;//new IJ1MultichannelContext();
	//static String defaultDir="/";
	
	
	public static MultichannelContext getMultichannelContext() {
		return currentContext;
	}
	
	public static void setMultichannelContext(MultichannelContext mcu) {
		currentContext=mcu;
	}
	
	/***/
	public static GeneralAppContext getGeneralContext() {
		return currentGenelalContext;
	}
	
	public static String getDefaultDirectory() {
		if ( getMultichannelContext()==null) return FileChoiceUtil.getWorkingDirectory();
		
		
		if ( getMultichannelContext().getDefaultDirectory()==null)return FileChoiceUtil.getWorkingDirectory();
	
		return getMultichannelContext().getDefaultDirectory();
	}
	
	
}

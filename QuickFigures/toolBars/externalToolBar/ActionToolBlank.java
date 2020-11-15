package externalToolBar;

import javax.swing.Icon;

/**An tool that performs an action rather than being used to draw or edit*/
public class ActionToolBlank<ImageType> extends BlankTool<ImageType>{

	protected String ActionCommand="";
	
	
	
	public boolean isActionTool() {
		return true;
	}
	
	@Override
	public Icon getToolPressedImageIcon() {
		return getToolImageIcon();
	}
	@Override
	public Icon getToolRollOverImageIcon()  {
		return getToolImageIcon();
	}
	
	
}

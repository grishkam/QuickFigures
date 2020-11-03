package externalToolBar;

import javax.swing.Icon;

public class ActionToolBlank<ImagePlus> extends BlankTool<ImagePlus>{

	protected String ActionCommand="";
	
	
	
	public boolean isActionTool() {
		return true;
	}
	
	@Override
	public Icon getToolPressedImageIcon() {
		return getToolImageIcon();
	}
	@Override
	public Icon getRollOverIcon()  {
		return getToolImageIcon();
	}
	
	
}

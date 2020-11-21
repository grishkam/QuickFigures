package externalToolBar;

import javax.swing.Icon;

/**Subclasses of this tool perform an action rather than being used to draw or edit
  */
public class ActionToolBlank<ImageType> extends BlankTool<ImageType>{

	protected String ActionCommand="";
	
	/**Lets the toolbar know that this is just an action tool*/
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

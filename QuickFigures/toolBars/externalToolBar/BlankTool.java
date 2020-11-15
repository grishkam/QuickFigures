package externalToolBar;
import javax.swing.Icon;



/**A default tool that can be used as both space filler in an external toolset or */
public class BlankTool<ImageType> extends  DummyTool<ImageType>{


	private IconSet iconSet=new IconSet("icons/Blank.jpg", "icons/BlankPressed.jpg", "icons/Blank.jpg","icons/Blank.jpg","icons/Blank.jpg","icons/Blank.jpg", "icons/Blank.jpg");

	@Override
	public Icon getToolImageIcon() {
		return getIconSet().getIcon(0);
	}

	@Override
	public Icon getToolPressedImageIcon() {
		return getIconSet().getIcon(1);
	}

	@Override
	public Icon getToolRollOverImageIcon() {
		if (isMenuOnlyTool()||isActionTool()) return  getToolImageIcon();
		return getIconSet().getIcon(2);//.getIcon(2);
	}

	public IconSet getIconSet() {
		return iconSet;
	}

	public void setIconSet(IconSet iconSet) {
		this.iconSet = iconSet;
	}



}

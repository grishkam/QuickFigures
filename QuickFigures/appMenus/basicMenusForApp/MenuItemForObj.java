package basicMenusForApp;

import javax.swing.Icon;

import applicationAdapters.DisplayedImageWrapper;

public interface MenuItemForObj {
	
	public void performActionDisplayedImageWrapper(DisplayedImageWrapper diw);
	public String getCommand();
	public String getNameText();
	public String getMenuPath();
	public Icon getIcon();

}

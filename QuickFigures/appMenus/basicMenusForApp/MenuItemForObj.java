package basicMenusForApp;

import javax.swing.Icon;

import applicationAdapters.DisplayedImage;

public interface MenuItemForObj {
	
	public void performActionDisplayedImageWrapper(DisplayedImage diw);
	public String getCommand();
	public String getNameText();
	public String getMenuPath();
	public Icon getIcon();

}

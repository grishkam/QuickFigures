package basicMenusForApp;

import javax.swing.Icon;

import applicationAdapters.DisplayedImage;

public abstract class BasicMenuItemForObj implements MenuItemForObj {

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return getMenuPath()+getNameText();
	}

	

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

}

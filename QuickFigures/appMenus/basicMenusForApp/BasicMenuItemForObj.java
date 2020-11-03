package basicMenusForApp;

import javax.swing.Icon;

import applicationAdapters.DisplayedImageWrapper;

public abstract class BasicMenuItemForObj implements MenuItemForObj {

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImageWrapper diw) {
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

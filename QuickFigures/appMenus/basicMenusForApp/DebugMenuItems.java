package basicMenusForApp;

import javax.swing.Icon;

import applicationAdapters.DisplayedImage;
import logging.IssueLog;

/**A menu item for turning on debugging mode*/
public  class DebugMenuItems implements MenuItemForObj{

	boolean on=true;
	public  DebugMenuItems() {
		this(true);
	}
	
	public  DebugMenuItems(boolean on) {
		this.on=on;
	}
	
	@Override
	public String getMenuPath() {
	
		return "Debug";
	}

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if (on) {
		IssueLog.reportAllFail(on);
		IssueLog.reportAllEvents();
		IssueLog.log("Testing Error Logging");
		}
		
		else {
			
		}
	}

	@Override
	public String getCommand() {
	if (!on)  return "Do NOT Log Errors to Window";
		return "Log Errors to Window";
	}

	@Override
	public String getNameText() {
		if (!on)  return "Do NOT Print Errors and Events to Windows";
		return "Print Errors and Events to Windows";
	}
	
	public static void main(String[] args) {
		new DebugMenuItems().performActionDisplayedImageWrapper(null);
		
	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
}

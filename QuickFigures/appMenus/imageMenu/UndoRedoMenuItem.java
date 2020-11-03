package imageMenu;


import applicationAdapters.DisplayedImageWrapper;
import basicMenusForApp.BasicMenuItemForObj;
import logging.IssueLog;

public class UndoRedoMenuItem  extends BasicMenuItemForObj {
	boolean undo=true;
	private Object stroke;
	
	public UndoRedoMenuItem(boolean un) {
		undo=un;
		
		}
	


	@Override
	public void performActionDisplayedImageWrapper(DisplayedImageWrapper diw) {
		
		try {
			if (undo) {diw.getUndoManager().undo();} 
			else diw.getUndoManager().redo();
		} catch (Exception e) {
			IssueLog.log("Cannot undo/redo "+diw.getUndoManager().getLastEditFromList());
			
		} 
		
		diw.updateDisplay();
	}

	@Override
	public String getCommand() {
		if (IssueLog.isWindows())  {
			if (!undo) return "Redo				 (ctrl+Y)";
			return "Undo				 (ctrl+Z)";
			
		}
		if (!undo) return "Redo				 \u2318Y";
		return "Undo				 \u2318Z";
	}

	@Override
	public String getNameText() {
		return getCommand();
	}

	@Override
	public String getMenuPath() {
		// TODO Auto-generated method stub
		return "Image";
	}

}

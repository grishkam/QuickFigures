package menuUtil;

import applicationAdapters.CanvasMouseEvent;
import undo.UndoManagerPlus;

public interface SmartMenuItem {
	public void setLastMouseEvent(CanvasMouseEvent e);
	public void setUndoManager(UndoManagerPlus undoManager);
	public UndoManagerPlus getUndoManager();

}

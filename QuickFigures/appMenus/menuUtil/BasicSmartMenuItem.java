package menuUtil;

import javax.swing.JMenuItem;

import applicationAdapters.CanvasMouseEvent;
import undo.UndoManagerPlus;

/**A special JMenu item that also stores an undo manager*/
public class BasicSmartMenuItem extends JMenuItem implements  SmartMenuItem{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected CanvasMouseEvent me;
	protected UndoManagerPlus undoManager;

	@Override
	public void setLastMouseEvent(CanvasMouseEvent e) {
		this.me=e;
		
	}

	@Override
	public void setUndoManager(UndoManagerPlus undoManager) {
		this.undoManager=undoManager;
		
	}

	@Override
	public UndoManagerPlus getUndoManager() {
		// TODO Auto-generated method stub
		return undoManager;
	}

}

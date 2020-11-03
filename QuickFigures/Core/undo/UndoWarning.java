package undo;

import standardDialog.InfoDisplayPanel;
import standardDialog.StandardDialog;

public class UndoWarning extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String st;
	
	public UndoWarning(String message) {
		this.st=message;
	}
	
	public void redo() {
		//undo();
	}
	
	public void undo() {
		
		StandardDialog undo = new StandardDialog("Undo Warning",false);
		undo.add("PPI", 
				new InfoDisplayPanel("Warning :", "Undo for this item is inperfect. Sorry about that"+'\n'+st));
		undo.setLocation(800, 800);
	//	undo.addKeyListener(new ToolBarKeyListener());
		undo.showDialog();
	}

}

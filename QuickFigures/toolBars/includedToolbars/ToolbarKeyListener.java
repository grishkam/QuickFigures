package includedToolbars;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import applicationAdapters.DisplayedImageWrapper;
import graphicActionToombar.CurrentSetInformerBasic;
import logging.IssueLog;

public class ToolbarKeyListener implements KeyListener {

	public ToolbarKeyListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
			DisplayedImageWrapper figureDisplay = new CurrentSetInformerBasic().getCurrentlyActiveDisplay();
			
			boolean WindowsOrMacMeta=false;
	 		if (IssueLog.isWindows() &&arg0.isControlDown()) {
	 			
	 			WindowsOrMacMeta=true;
	 		}
	 		if (!IssueLog.isWindows() &&arg0.isMetaDown()) WindowsOrMacMeta=true;
	 		
	 		
	 		/**implementation of undo and redo*/
	 		if (arg0.getKeyCode()==KeyEvent.VK_Z&&WindowsOrMacMeta) {
				if (figureDisplay.getUndoManager().canUndo()) {
					figureDisplay.getUndoManager().undo();
								
					};
			}
	 		

			if (arg0.getKeyCode()==KeyEvent.VK_Y&&WindowsOrMacMeta) {
						
						if (figureDisplay.getUndoManager().canRedo())figureDisplay.getUndoManager().redo();
					}
			if(figureDisplay!=null)
			 figureDisplay.updateDisplay();
	
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}

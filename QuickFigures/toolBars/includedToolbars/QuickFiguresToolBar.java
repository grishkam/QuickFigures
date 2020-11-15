package includedToolbars;

import java.awt.dnd.DropTarget;

import javax.swing.AbstractButton;

import applicationAdapters.DisplayedImage;
import basicMenusForApp.OpeningFileDropHandler;
import externalToolBar.AbstractExternalToolset;
import genericMontageUIKit.GeneralTool;
import genericMontageUIKit.ToolBit;

public class QuickFiguresToolBar extends AbstractExternalToolset<DisplayedImage>  {


	public QuickFiguresToolBar() {
		
	}
	
	public synchronized void addToolKeyListeners() {
		
		for (AbstractButton jb: super.buttons) {
			jb.addKeyListener(new ToolbarKeyListener());
		}
	}
	
	public void addToolBit(ToolBit t) {
		addTool(new  GeneralTool( t));
	}
	public void addDragAndDrop() {
		new DropTarget(getframe(), new OpeningFileDropHandler());
	}



}

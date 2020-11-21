package graphicActionToolbar;

import java.util.Collection;

import javax.swing.undo.UndoableEdit;

import applicationAdapters.DisplayedImage;
import graphicalObjects.FigureDisplayContainer;

/**Interface meant to keep track of which figure is the currently active window
 methods tell an object what the currently active display set is*/
public interface CurrentSetInformer {

	public FigureDisplayContainer getCurrentlyActiveOne() ;
	
	
	public DisplayedImage getCurrentlyActiveDisplay() ;
	
	public Collection<DisplayedImage> getVisibleDisplays() ;

	public void updateDisplayCurrent();
	public void addUndo(UndoableEdit e);
	
}

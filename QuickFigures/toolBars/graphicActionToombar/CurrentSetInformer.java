package graphicActionToombar;

import java.util.Collection;

import javax.swing.undo.UndoableEdit;

import applicationAdapters.DisplayedImageWrapper;
import graphicalObjects.GraphicSetDisplayContainer;

/**meant to tell an object what the currently active display set is*/
public interface CurrentSetInformer {

	public GraphicSetDisplayContainer getCurrentlyActiveOne() ;
	
	
	public DisplayedImageWrapper getCurrentlyActiveDisplay() ;
	
	public Collection<DisplayedImageWrapper> getVisibleDisplays() ;

	public void updateDisplayCurrent();
	public void addUndo(UndoableEdit e);
	
}

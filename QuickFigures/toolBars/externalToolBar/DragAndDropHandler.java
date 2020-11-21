package externalToolBar;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;

import imageDisplayApp.ImageWindowAndDisplaySet;

/**Interface is used to determine what happens when a 
 * user drops something on an open figure*/
public interface DragAndDropHandler {

	void drop(ImageWindowAndDisplaySet displaySet, DropTargetDropEvent arg0);

	void dropActChange(ImageWindowAndDisplaySet displaySet, DropTargetDragEvent arg0);

	void dragOver(ImageWindowAndDisplaySet displaySet, DropTargetDragEvent arg0);

	void dragExit(ImageWindowAndDisplaySet displaySet, DropTargetEvent arg0);

	void dragEnter(ImageWindowAndDisplaySet displaySet, DropTargetDragEvent arg0);

}

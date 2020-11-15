package externalToolBar;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;

import imageDisplayApp.ImageAndDisplaySet;

/**Interface is used to determine what happens when a 
 * user drops something on an open figure*/
public interface DragAndDropHandler {

	void drop(ImageAndDisplaySet displaySet, DropTargetDropEvent arg0);

	void dropActChange(ImageAndDisplaySet displaySet, DropTargetDragEvent arg0);

	void dragOver(ImageAndDisplaySet displaySet, DropTargetDragEvent arg0);

	void dragExit(ImageAndDisplaySet displaySet, DropTargetEvent arg0);

	void dragEnter(ImageAndDisplaySet displaySet, DropTargetDragEvent arg0);

}

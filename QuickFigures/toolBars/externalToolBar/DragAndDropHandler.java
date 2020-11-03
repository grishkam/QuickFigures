package externalToolBar;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;

import imageDisplayApp.ImageAndDisplaySet;

public interface DragAndDropHandler {

	void drop(ImageAndDisplaySet displaySet, DropTargetDropEvent arg0);

	void dropActCahnge(ImageAndDisplaySet displaySet, DropTargetDragEvent arg0);

	void dragOver(ImageAndDisplaySet displaySet, DropTargetDragEvent arg0);

	void dragExit(ImageAndDisplaySet displaySet, DropTargetEvent arg0);

	void dragEnter(ImageAndDisplaySet displaySet, DropTargetDragEvent arg0);

}

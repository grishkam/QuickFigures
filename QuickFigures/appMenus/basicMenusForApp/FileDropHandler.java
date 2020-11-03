package basicMenusForApp;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.ArrayList;

import ultilInputOutput.ForDragAndDrop;

public abstract class FileDropHandler implements DropTargetListener{

	@Override
	public void dragEnter(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragExit(DropTargetEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drop(DropTargetDropEvent arg0) {
		if(arg0.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

			ArrayList<File> files = ForDragAndDrop.dropedFiles(arg0);
			performFileListAction(files);
			for(File f: files) {
				performsingleFileAction(f);
			}
			
			return;
			}
	}

	public  abstract void performsingleFileAction(File f);

	public abstract void performFileListAction(Iterable<File> files);

	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}

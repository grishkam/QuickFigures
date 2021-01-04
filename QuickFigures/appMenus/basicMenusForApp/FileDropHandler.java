/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
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
		
		
	}

	@Override
	public void dragExit(DropTargetEvent arg0) {
		
		
	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) {
		
		
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
		
		
	}

}

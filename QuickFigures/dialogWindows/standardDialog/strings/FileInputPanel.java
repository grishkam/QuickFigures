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
/**
 * Author: Greg Mazo
 * Date Created: Mar 26, 2022
 * Date Modified: Nov 12, 2022
 * Version: 2022.2
 */
package standardDialog.strings;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;

import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;
import ultilInputOutput.ForDragAndDrop;

/**
 
 * 
 */
public class FileInputPanel extends StringInputPanel implements  DropTargetListener{

	static final String choose="No File";
	private JButton selectContent=new JButton("Choose File");
	File file=null;
	private String searchIn="";
	
	/**
	 * @param labeln
	 * @param contend
	 */
	public FileInputPanel(String labeln, String contend, String searchForm) {
		super(labeln, contend);
		setup();
		this.searchIn=searchForm;
	}
	
	/**
	 * 
	 */
	private void setup() {
		selectContent.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				file=FileChoiceUtil.getOpenFile(searchIn);
				
				if(file!=null)
					field.setText(file.getAbsolutePath());
				else {
					field.setText(choose);
					
					
				
				
				}
				
				dispatchStringInputEvent();
				
			}});
		new DropTarget(selectContent, this);
		new DropTarget(field, this);
	}

	public FileInputPanel(String labeln) {
		super(labeln, choose);
		setup();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Override
	public int gridWidth() {
		
		return 3;
	}
	
	/**Places the items with the given grid bag constraints into the grid of the container
	 * @param jp
	 * @param x0
	 * @param y0
	 * @param gc
	 */
	protected void layItems(Container jp, int x0, int y0, GridBagConstraints gc) {
		super.layItems(jp, x0, y0, gc);
		gc.gridx++;
		jp.add( selectContent, gc);
	}
	
	public File getFile() {
		
		return file;
		}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		if(dtde.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

			ArrayList<File> files = ForDragAndDrop.dropedFiles(dtde);
			performFileListAction(files);
			for(File f: files) {
				performsingleFileAction(f);
			}
			
			return;
			}
		
	}

	/**
	 * @param f
	 */
	private void performsingleFileAction(File f) {
		getTextComponent().setText(f.getAbsolutePath());
		dispatchStringInputEvent();
	}

	/**
	 * @param files
	 */
	private void performFileListAction(ArrayList<File> files) {
		// TODO Auto-generated method stub
		
	}

}

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
 * Date Created: Mar 26, 2022
 * Version: 2022.1
 */
package standardDialog.strings;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;

import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;

/**
 
 * 
 */
public class FileInputPanel extends StringInputPanel {

	static final String choose="No File";
	private JButton selectContent=new JButton("Choose File");
	File file=null;
	
	/**
	 * @param labeln
	 * @param contend
	 */
	public FileInputPanel(String labeln, String contend) {
		super(labeln, contend);
		setup();
	}
	
	/**
	 * 
	 */
	private void setup() {
		selectContent.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				file=FileChoiceUtil.getOpenFile();
				if(file!=null)
					field.setText(file.getAbsolutePath());
				else {
					field.setText(choose);
					
					
				
				
				}
				
				dispatchStringInputEvent();
				
			}});
		
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

}

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
 * Date Created: Dec 3, 2022
 * Date Modified: Dec 3, 2022
 * Version: 2022.2
 */
package storedValueDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;

import layout.RetrievableOption;
import logging.IssueLog;
import standardDialog.StandardDialog;
import standardDialog.strings.StringInputEvent;
import standardDialog.strings.StringInputListener;
import storedValueDialog.StoredValueDilaog.FileInput;

/**
 
 * 
 */
public class FileSlot implements CustomSlot, StringInputListener {

	public File file=null;
	public FileInput lastInput=null;
	private FileSlot sister;
	
	public FileSlot() {
	}
	
	public FileSlot(FileSlot sister) {
		this.sister=sister;
	}
	
	@Override
	public void addInput(StandardDialog d, RetrievableOption o, CustomSlot so) {
		JButton swap =null;
		try {
			if(sister!=null) {
				swap = new JButton("^");
				swap.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						swapWSister();
						
					}});
			}
			
			FileInput fi = new StoredValueDilaog.FileInput(d, this, this.getClass().getDeclaredField("file"), o, swap);
			fi.addStringInputListener(this);
			this.lastInput=fi;
		} catch (Exception e) {
			IssueLog.logT(e);
		}

	}

	/**
	 * 
	 */
	protected void swapWSister() {
		File f1 = sister.getFile();
		File f2 = this.getFile();
		this.setFile(f1);
		sister.setFile(f2);
		this.lastInput.dispatchStringInputEvent();
	}

	@Override
	public void stringInput(StringInputEvent sie) {
		
		
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		if(this.lastInput!=null) {
			lastInput.changeFile(file);
		}
	}

}

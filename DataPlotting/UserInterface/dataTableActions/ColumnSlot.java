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
package dataTableActions;

import java.io.File;
import java.util.ArrayList;

import channelMerging.ChannelEntry;
import dataTableDialogs.ExcelTableReader;
import dataTableDialogs.TableReader;
import layout.RetrievableOption;
import logging.IssueLog;
import standardDialog.StandardDialog;
import standardDialog.channels.ChannelListChoiceInputPanel;
import standardDialog.choices.ChoiceInputEvent;
import standardDialog.choices.ChoiceInputListener;
import standardDialog.strings.StringInputEvent;
import standardDialog.strings.StringInputListener;
import storedValueDialog.CustomSlot;
import storedValueDialog.FileSlot;
import storedValueDialog.StoredValueDilaog.FileInput;

/**
 
 * 
 */
public class ColumnSlot implements CustomSlot, StringInputListener, ChoiceInputListener{

	private FileSlot fileOrigin;
	private FileInput inputPanel;
	private ArrayList<ChannelEntry> channelsAvailable;
	private ChannelListChoiceInputPanel cip;
	
	
	private ChannelEntry theDefault;
	private ChannelEntry chosen;
	private String keyword=null;
	private String label=null;

	/**
	 * @param templateFile
	 */
	public ColumnSlot(FileSlot templateFile, String key) {
		fileOrigin=templateFile;
		setupInputPanel();
		
	}
	
	/**
	 * @param templateFile
	 */
	public ColumnSlot(FileSlot templateFile, ChannelEntry theDefault) {
		this(templateFile, theDefault.getLabel());
		this.theDefault=theDefault;
	}

	/**
	 * 
	 */
	public void setupInputPanel() {
		inputPanel=fileOrigin.lastInput;
		if(inputPanel!=null)
			inputPanel.addStringInputListener(this);
	}

	/**updats the choices after input of a new file*/
	@Override
	public void stringInput(StringInputEvent sie) {
		updateFromChosenFile();
		ArrayList<Integer> start = new ArrayList<Integer>();
		start.add(this.getDefaultStartIndex());
		cip.setupChannelOptions(channelsAvailable, start);
	
	}

	/**
	 * updates the channel options in response to new file input
	 */
	public void updateFromChosenFile() {
		
		File file = inputPanel.getFile();
		useExcelFile(file);
		
	}
	
	/**
	 *  updates the channel options in response to existing file
	 */
	public void updateFromFile() {
		
		File file = fileOrigin.getFile();
		IssueLog.log("   file is "+file);
		useExcelFile(file);
		
	}

	/**
	 * @param file
	 */
	public void useExcelFile(File file) {
		
		ExcelTableReader data = ExcelTableReader.openExcelFile(file);
		channelsAvailable = getColumnHeaders(data);
			
	}

	@Override
	public void addInput(StandardDialog d, RetrievableOption o, CustomSlot so) {
		useExcelFile(fileOrigin.getFile());
		 String label = this.getLabel();
		 if(label==null)
			 label=o.label();
		 String key = o.key();
		 
		addFieldToDialog(d, label, key, getDefaultStartIndex());

	}

	/**
	 * @param d
	 * @param label
	 * @param key
	 */
	public void addFieldToDialog(StandardDialog d, String label, String key, int start) {
		setupInputPanel();
		try {
			updateFromFile();
			
			cip = new ChannelListChoiceInputPanel(label, channelsAvailable,
					start, "None", true );
			 cip.addChoiceInputListener(this);
			
			d.add(key, cip);
		} catch (Exception e) {
			IssueLog.logT(e);
		}
	}
	
	/**
	 * @param item
	 * @return
	 */
	public ArrayList<ChannelEntry> getColumnHeaders(TableReader item) {
		
		
		ArrayList<ChannelEntry> channels=new ArrayList<ChannelEntry>();
		if(item==null)
			return channels;
		ArrayList<String> h = item.getColumnHeaders();
		
		int columnCount = h.size();
		for(int i=0; i<columnCount; i++) {
			
			
				channels.add(i, new ChannelEntry(""+h.get(i), i));
			
		}
		
		
		return channels;
	}

	@Override
	public void valueChanged(ChoiceInputEvent ne) {
		
		if(channelsAvailable!=null&&channelsAvailable.size()>0) {
			chosen=channelsAvailable.get((int) ne.getChoiceIndex());
			
			
		}
	}

	/**
	 * @param i
	 */
	public void setIndex(int i) {
		
		chosen=new ChannelEntry("", i);
		
	}

	/**
	 * @return 
	 * 
	 */
	public double getIndex() {
		if(chosen!=null)
			return chosen.getOriginalChannelIndex();
		return getDefaultStartIndex();
		
	}

	/**
	 * @return
	 */
	public int getDefaultStartIndex() {
		if(this.theDefault!=null)
			return theDefault.getOriginalChannelIndex();
		return 0;
	}

	/**
	 * @param string
	 */
	public void setKeyword(String string) {
		keyword=string;
		
	}

	/**
	 * @param string
	 */
	public void setLabel(String string) {
		this.label=string;
		
	}

	public String getLabel() {
		return label;
	}

	public String getKeyword() {
		return keyword;
	}

}

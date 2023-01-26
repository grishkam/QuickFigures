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
 * Date Modified: Jan 14, 2023
 * Version: 2022.2
 */
package dataTableActions;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
	ChannelListChoiceInputPanel cip;
	
	
	private ChannelEntry theDefault;
	private ChannelEntry chosen;
	private String keyword=null;
	private String label=null;
	private ExcelTableReader data;
	private boolean required;

	/**
	 * @param templateFile
	 */
	public ColumnSlot(FileSlot templateFile, String key, boolean require) {
		fileOrigin=templateFile;
		setupInputPanel();
		label=key;
		this.required=require;
	}
	
	/**
	 * @param templateFile
	 */
	public ColumnSlot(FileSlot templateFile) {
		fileOrigin=templateFile;
		setupInputPanel();
		
	}
	
	/**
	 * @param templateFile
	 */
	public ColumnSlot(FileSlot templateFile, ChannelEntry theDefault) {
		this(templateFile);
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
		if(hasDefaultValue())
			start.add(this.getDefaultStartIndex());
		
		cip.setupChannelOptions(channelsAvailable, start);
		updateRequirementIndicator();
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
		
		useExcelFile(file);
		
	}

	/**
	 * @param file
	 */
	public void useExcelFile(File file) {
		
		data = ExcelTableReader.openExcelFile(file);
		channelsAvailable = getColumnHeaders(data);
		updateRequirementIndicator();
	}

	@Override
	public void addInput(StandardDialog d, RetrievableOption o, CustomSlot so) {
		useExcelFile(fileOrigin.getFile());
		 String label = this.getLabel();
		 if(label==null)
			 label=o.label();
		 String key = o.key();
		 
		Integer defaultStartIndex = getDefaultStartIndex();
		if(!hasDefaultValue()) {
			defaultStartIndex =null;
		}
		
		addFieldToDialog(d, label, key, defaultStartIndex);

	}

	/**
	 * @param d
	 * @param label
	 * @param key
	 */
	public void addFieldToDialog(StandardDialog d, String label, String key, Integer start) {
		setupInputPanel();
		try {
			updateFromFile();

			cip = new ChannelListChoiceInputPanel(label, channelsAvailable,
					start, "None", true );
			updateRequirementIndicator();
			cip.boxWidthLimit=150;
			 cip.addChoiceInputListener(this);
			
			d.add(key, cip);
		} catch (Exception e) {
			IssueLog.logT(e);
		}
	}
	
	/**returns the list of available choices
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
	
	public ArrayList<ChannelEntry> getUniqueValuesInColumn() {
		return getUniqueValuesInColumn(this.getAsString());
	}
	
	/**returns the unique values in the current column*/
	public ArrayList<ChannelEntry> getUniqueValuesInColumn(String col) {
		
		ArrayList<ChannelEntry> channels=new ArrayList<ChannelEntry>();
		if(data==null) {
			IssueLog.log("could not read excel data due to missing reader");
			return channels;
		}
		if(col==null) {
			IssueLog.log("could not read excel data due to missing column name");
			return channels;
		}
		
		ArrayList<ChannelEntry> h = getColumnHeaders(data);
		ChannelEntry c=null;
		for(ChannelEntry h1: h) {
			if(h1.getLabel().contentEquals(col))
				c=h1;
		}
		if(c==null)
			{
				IssueLog.log("named column not found");
				return channels;
				}
		
		
		
		HashMap<String, Integer> eachValueRead=new HashMap<String, Integer>();
		
		for(int i=1; i<data.getColumnCount(); i++) {
			Object value = data.getValueAt(i, c.getOriginalChannelIndex());
		
			if(value==null)
				continue;
			if(!eachValueRead.containsKey(value))	{
				eachValueRead.put(""+value, i);
				
				}
			
			
		}
		
		int i=1;
		for(String c1:eachValueRead.keySet() ) {
			channels.add(new ChannelEntry(c1, i));
			i++;
		}
	
		return channels;
	}

	@Override
	public void valueChanged(ChoiceInputEvent ne) {
		
		if(channelsAvailable!=null&&channelsAvailable.size()>0) {
			int choiceIndex = (int) ne.getChoiceIndex();
			ArrayList<Integer> b = cip.getCurrentValues();
			if(b.size()==1)
				chosen=channelsAvailable.get(choiceIndex);
			else chosen=null;
			
			updateRequirementIndicator();
			
			
		}
	}

	/**
	 * changes the background color to indicate to the user if input is required
	 */
	public void updateRequirementIndicator() {
		if(!required)
			return;
		if(cip==null)
			return;
		if(chosen==null&&this.required) {
		
			cip.getBox().setBackground(bad_input);
		} else {
			cip.getBox().setBackground(good_input);
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
	 * 
	 */
	public String getAsString() {
		if(chosen!=null)
			return chosen.getLabel();
		if(this.theDefault!=null)
			return theDefault.getLabel();
		return null;
	}
	
	/**if no column is selected*/
	public boolean isEmpty() {
		return getAsString()==null;
	}

	/**
	 * @return
	 */
	public Integer getDefaultStartIndex() {
		if(hasDefaultValue())
			return theDefault.getOriginalChannelIndex();
		return null;
	}
	
	public boolean hasDefaultValue() {return theDefault!=null;}

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

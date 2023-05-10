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
 * Version: 2023.2
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
import storedValueDialog.StoredValueDilaog.StringInput;

/**
 
 * 
 */
public class ColumnSelectionSlot implements CustomSlot, StringInputListener, ChoiceInputListener{

	private  ColumnSlot fileOrigin;
	private ChannelListChoiceInputPanel inputPanel;
	private ArrayList<ChannelEntry> channelsAvailable=new ArrayList<ChannelEntry>();
	private ChannelListChoiceInputPanel cip;
	
	

	private String keyword=null;
	private String label=null;
	private ExcelTableReader data;
	private boolean required;

	/**
	 * @param templateFile
	 */
	public ColumnSelectionSlot(ColumnSlot templateFile, String key, boolean require) {
		fileOrigin=templateFile;
		setupInputPanel();
		label=key;
		this.required=require;
	}
	
	/**
	 * @param templateFile
	 */
	public ColumnSelectionSlot(ColumnSlot templateFile) {
		fileOrigin=templateFile;
		setupInputPanel();
		
	}
	


	/**
	 * 
	 */
	public void setupInputPanel() {
		inputPanel=fileOrigin.cip;
		if(inputPanel!=null)
			inputPanel.addChoiceInputListener(this);
	}

	/**updats the choices after input of a new file*/
	@Override
	public void stringInput(StringInputEvent sie) {
		updateFromChosen();
		ArrayList<Integer> start = new ArrayList<Integer>();
		
		
		cip.setupChannelOptions(channelsAvailable, start);
		updateRequirementIndicator();
	}

	/**
	 * updates the channel options in response to new file input
	 */
	public void updateFromChosen() {
		
		useSlot();
		
	}
	
	/**
	 *  updates the channel options in response to existing file
	 */
	public void updateFromFile() {
		
		
		useSlot();
		
	}

	/**
	 * @param file
	 */
	public void useSlot() {
		
		
		
		updateRequirementIndicator();
	}

	@Override
	public void addInput(StandardDialog d, RetrievableOption o, CustomSlot so) {
		useSlot();
		 String label = this.getLabel();
		 if(label==null)
			 label=o.label();
		 String key = o.key();
		 
		addFieldToDialog(d, label, key);

	}

	/**
	 * @param d
	 * @param label
	 * @param key
	 */
	public void addFieldToDialog(StandardDialog d, String label, String key) {
		setupInputPanel();
		try {
			updateFromFile();
			
			cip = new ChannelListChoiceInputPanel(label, channelsAvailable,
					new ArrayList<Integer>() );
			updateRequirementIndicator();
			cip.boxWidthLimit=150;
			
			
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
	
	/**returns the unique values in the current column*/
	public ArrayList<ChannelEntry> getUniqueValuesInColumn(String col) {
		ArrayList<ChannelEntry> channels=new ArrayList<ChannelEntry>();
		if(data==null||col==null)
			return channels;
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
		
		return channels;
	}

	@Override
	public void valueChanged(ChoiceInputEvent ne) {
		
		updateValues();
	}

	/**
	 * updates the options
	 */
	public void updateValues() {
		channelsAvailable=fileOrigin.getUniqueValuesInColumn();
		cip.setupChannelOptions(channelsAvailable, new ArrayList<Integer>());
	
	}

	/**
	 * changes the background color to indicate to the user if input is required
	 */
	public void updateRequirementIndicator() {
		if(!required)
			return;
		if(cip==null)
			return;
		if(this.required) {
		
			cip.getBox().setBackground(bad_input);
		} else {
			cip.getBox().setBackground(good_input);
		}
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

	/**
	 * @return
	 */
	public boolean isEmpty() {
		if(cip.getSelectedIndices()==null||cip.getSelectedIndices().size()==0)
			return true;
		return false;
	}

	public ArrayList<String> getAllSelected() {
		ArrayList<String> s=new ArrayList<String>();
		for(int i: cip.getSelectedIndices())
		for(ChannelEntry c: this.channelsAvailable) {
			if(i==c.getOriginalChannelIndex())
				s.add(c.getLabel());
		}
		return s;
	}
	
	
}

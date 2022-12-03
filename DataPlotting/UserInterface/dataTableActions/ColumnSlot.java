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
	private double indexCol;
	private ChannelEntry chosen;
	private int indexChosen;

	/**
	 * @param templateFile
	 */
	public ColumnSlot(FileSlot templateFile) {
		fileOrigin=templateFile;
		setupInputPanel();
	}

	/**
	 * 
	 */
	public void setupInputPanel() {
		inputPanel=fileOrigin.lastInput;
		if(inputPanel!=null)
			inputPanel.addStringInputListener(this);
	}

	@Override
	public void stringInput(StringInputEvent sie) {
		updateFromChosenFile();
		cip.setupChannelOptions(channelsAvailable, new ArrayList<Integer>());
		

		
		
	}

	/**
	 * 
	 */
	public void updateFromChosenFile() {
		
		File file = inputPanel.getFile();
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
		setupInputPanel();
		try {
			updateFromChosenFile();
			 cip = new ChannelListChoiceInputPanel(o.label(), channelsAvailable,
					0, "None" );
			 cip.addChoiceInputListener(this);
			d.add(o.key(), cip);
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
		indexCol=ne.getChoiceIndex();
		if(channelsAvailable!=null&&channelsAvailable.size()>0) {
			chosen=channelsAvailable.get((int) ne.getChoiceIndex());
			indexChosen=chosen.getOriginalChannelIndex();
			indexCol=indexChosen;
		}
	}

	/**
	 * @param i
	 */
	public void setIndex(int i) {
		indexCol=i;
		
	}

	/**
	 * @return 
	 * 
	 */
	public double getIndex() {
		return indexCol;
		
	}

}

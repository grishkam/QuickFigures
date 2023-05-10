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
 * Version: 2023.2
 */
package dataTableActions;

import java.util.ArrayList;

import channelMerging.ChannelEntry;
import layout.RetrievableOption;
import standardDialog.StandardDialog;
import storedValueDialog.CustomSlot;
import storedValueDialog.FileSlot;

/**
 
 * 
 */
public class ColumnSlotList implements CustomSlot {

	ArrayList<ColumnSlot> eachSlot=new ArrayList<ColumnSlot>();
	
		public ColumnSlotList(ColumnSlot... args) {
			int i=0;
			for(ColumnSlot f:args ) {
				eachSlot.add(f);
				f.setKeyword(""+i);
			}
		}	
		
		public ColumnSlotList(FileSlot file, String[] keys, String[] titles, int[] startingValues) {
			
			createColumnSlots(file, keys, titles, startingValues);
		}
		
public ColumnSlotList(FileSlot file, String[] keys) {
			int[] startingValues=new int[keys.length];
			for(int i=0; i<startingValues.length; i++) {
				startingValues[i]=i;
			}
			createColumnSlots(file, keys, keys, startingValues);
		}

		/**
		 * @param file
		 * @param keys
		 * @param titles
		 * @param startingValues
		 */
		public void createColumnSlots(FileSlot file, String[] keys, String[] titles, int[] startingValues) {
			for(int i=0; i<keys.length; i++ ) {
				ColumnSlot f = new ColumnSlot(file, new ChannelEntry(keys[i], startingValues[i]));
				eachSlot.add(f);
				f.setKeyword(keys[i]);
				if(titles!=null&&titles.length>i)
					{
					f.setLabel(titles[i]);
					}
			}
		}	
	
	@Override
	public void addInput(StandardDialog d, RetrievableOption o, CustomSlot so) {
		int i=1;
		for(ColumnSlot f:eachSlot ) {
			
			String label = ""+i;
			if(i==1)
				label=o.label()+" "+i;
			if(f.getLabel()!=null)
				label=f.getLabel();
			String key = o.key()+i;
			if(f.getKeyword()!=null) {
				key=f.getKeyword();
			}
		
			f.addFieldToDialog(d, label, key, f.getDefaultStartIndex());
			
			i++;
		}

	}

	/**
	 * @param i
	 * @return
	 */
	public int getIndex(int i) {
		return (int) this.eachSlot.get(i).getIndex();
	}
	
	public int getIndex(String i) {
		for(ColumnSlot c: eachSlot) {
			if(i.equals(c.getKeyword()))
				return (int) c.getIndex();
		}
		new NullPointerException();
		return 0;
	}

	/**
	 * @param i
	 * @param i2
	 */
	public void setIndex(int i, int i2) {
		this.eachSlot.get(i).setIndex(i2);
		
	}

}

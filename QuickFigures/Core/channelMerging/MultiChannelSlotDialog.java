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
 * Date Modified: Jan 4, 2021
 * Version: 2022.2
 */
package channelMerging;

import javax.swing.JComboBox;

import standardDialog.StandardDialog;
import standardDialog.choices.ChoiceInputPanel;

/**Work in progress
 * Dialog appears when the user chooses Saving Options for an image.
  Gives the user an option to load the image from a save location 
  after a figure is de-Serialized. 
  In most cases, the default for embedding the image is convenient. 
  Arguably, the user has no reason to change this from the default
  however some figures with many very large file size images might 
  be more easily handled if the original image is saved.*/
public class MultiChannelSlotDialog extends StandardDialog  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MultiChannelSlot theslot;

	public MultiChannelSlotDialog(MultiChannelSlot slot) {
		super("Retrieval", true);
		theslot = slot;
		JComboBox<?> retOps = new JComboBox<String>(MultiChannelSlot.retrivalOptions);
		retOps.setSelectedIndex(slot.getRetrieval());
		this.add("Retrival", new ChoiceInputPanel("How to store multichannel image", retOps));
		
	}
	
	public void setItemsToDialog() {
		theslot.setRetrival(super.getChoiceIndex("Retrival"));
	}
	
	/**what action to take when the ok button is pressed*/
	protected void onOK() {
		setItemsToDialog() ;
	}

}

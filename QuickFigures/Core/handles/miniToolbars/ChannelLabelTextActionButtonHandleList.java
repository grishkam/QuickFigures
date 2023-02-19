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
 * Date Modified: Jan 12, 2021
 * Date Created: Jan 12, 2021
 * Version: 2023.1
 */
package handles.miniToolbars;

import actionToolbarItems.ChannelLabelButton;
import channelLabels.MergeLabelStyle;
import channelLabels.ChannelLabelTextGraphic;

/**
 
 * 
 */
public class ChannelLabelTextActionButtonHandleList extends TextActionButtonHandleList {

	/**
	 * @param t
	 */
	public ChannelLabelTextActionButtonHandleList(ChannelLabelTextGraphic t) {
		super(t);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**creates the handles and adds them to the list*/
	public void addItems() {
		super.addItems();
		addMergeLabelButton();
	}

	/**
	 Adds an item for changing the style of the merge label
	 */
	private void addMergeLabelButton() {
		if(text instanceof ChannelLabelTextGraphic) {
			ChannelLabelTextGraphic chanlabel=(ChannelLabelTextGraphic) text;
			if (chanlabel.isThisMergeLabel()) {
				this.addOperationList(new  ChannelLabelButton(MergeLabelStyle.SIMPLY_LABEL_AS_MERGE), ChannelLabelButton.getAllMergeLabelFroms());
			}
		}
	}

}

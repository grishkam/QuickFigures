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
 * Date Modified: Jan 5, 2021
 * Version: 2022.2
 */
package undo;


import channelLabels.ChannelLabelProperties;

/**An undo for changes in the channel label properties*/
public class ChannelLabelPropertiesUndo extends AbstractUndoableEdit2 {

	private ChannelLabelProperties properties;
	private ChannelLabelProperties iProp;
	private ChannelLabelProperties fProp;

	public ChannelLabelPropertiesUndo(ChannelLabelProperties prop) {
		this.properties=prop;
		iProp=properties.copy();
	}
	
	public void establishFinalState() {
		fProp=properties.copy();
	}
	public void redo() {
		properties.copyOptionsFrom(fProp);
	}
	
	public void undo() {
		properties.copyOptionsFrom(iProp);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
}

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
 * Version: 2023.1
 */
package channelLabels;

import java.io.Serializable;
import java.util.ArrayList;

import channelMerging.ChannelEntry;

/**The channel label object must perform certain tasks.
 */
public interface ChannelLabel extends Serializable {
	
	/**Channel entries*/
	public ArrayList<ChannelEntry> getChanEntries();
	
	/**updates the text to march the channels*/
	public void setParaGraphToChannels();
	
	/**returns the channel label properties object*/
	public ChannelLabelProperties getChannelLabelProperties();
	
	/**sets the channel label properties object*/
	public void setChannelLabelproperties(ChannelLabelProperties channelLabelproperties) ;
	
}

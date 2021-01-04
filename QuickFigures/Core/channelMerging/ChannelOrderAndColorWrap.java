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
 * Version: 2021.1
 */
package channelMerging;

import java.awt.Color;

/**interface for reordering the channels of a multichannel image
 * or reordering their colors*/
public interface ChannelOrderAndColorWrap {

	/**Swaps the channel at position a with that at position b*/
	public void swapChannelsOfImage(int a, int b);
	/**Swaps the channel color at position a with that at position b*/
	public void swapChannelLuts(int a, int b);
	
	/**set the channel colors*/
	public void setChannelColor(Color c, int chan);
	void setChannelColor(byte[][] lut, int chan);//sets the lookup table
	
	/**moves the channel at position 1 to position 2. */
	public void moveChannelOfImage(int choice1, int choice2);
	/**moves the channel color at position 1 to position 2. */
	public void moveChannelLutsOfImage(int choice1, int choice2);
	
}

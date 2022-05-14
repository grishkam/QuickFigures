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
 * Version: 2022.1
 */
package channelMerging;



/**interface for reordering the channels of a multichannel image
 * or reordering their colors*/
public interface ChannelOrderAndColorWrap extends ChannelColorWrap{

	/**Swaps the channel at position a with that at position b*/
	public void swapChannelsOfImage(int a, int b);

	
	/**moves the channel at position 1 to position 2. */
	public void moveChannelOfImage(int choice1, int choice2);
	
	
	
	
}

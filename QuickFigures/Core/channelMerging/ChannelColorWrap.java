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
 
 * 
 */
package channelMerging;

import java.awt.Color;

/**
 
 */
public interface ChannelColorWrap {
	/**moves the channel color at position 1 to position 2. */
	public void moveChannelLutsOfImage(int choice1, int choice2);
	
	/**Swaps the channel color at position a with that at position b*/
	public void swapChannelLuts(int a, int b);
	
	/**set the channel colors*/
	public void setChannelColor(Color c, int chan);
	void setChannelColor(byte[][] lut, int chan);//sets the lookup table
}

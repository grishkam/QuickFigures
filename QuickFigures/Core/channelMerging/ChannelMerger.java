/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package channelMerging;


import applicationAdapters.PixelWrapper;
import figureOrganizer.PanelListElement;

/**Implementations of ChannelMerger merges several channels together into an RGB image
 * that can be displayed in a panel*/
public interface ChannelMerger {
	
	/**Merges the channels listed in entry to generate a merged RGB.
	  If there is only one channel, the second argument indicates whether to create a greyscale*/
	public PixelWrapper generateMergedRGB( PanelListElement entry, int ChannelsInGrayScale);

}

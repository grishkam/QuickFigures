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

import java.awt.Window;

/**an interface of displayed image that is written to 
  contain a multi dimensional image that is displayed*/
public interface MultiChannelDisplayWrapper  {

	public  MultiChannelImage getContainedMultiChannel();
	public int getCurrentChannel();
	public int getCurrentFrame();
	public int getCurrentSlice();
	
	public Window getWindow() ;
	void updateDisplay();
}

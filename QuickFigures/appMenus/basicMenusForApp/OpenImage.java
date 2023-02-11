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
 * Date Created: Feb 11, 2023
 * Date Modified: Feb 11, 2023
 * Version: 2022.2
 */
package basicMenusForApp;

import appContext.CurrentAppContext;
import appContext.ImageDPIHandler;
import applicationAdapters.DisplayedImage;
import figureOrganizer.MultichannelDisplayLayer;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageMenu.CanvasDialogResize;

/**A menu item that creates a new worksheet for the new menu*/
public class OpenImage  extends BasicMenuItemForObj   {

	public static enum OpenForm{ from_many, from_one}
	OpenForm form=OpenForm.from_many;

	public OpenImage(OpenForm i) {
		form=i;
	}
	
	@Override
	public String getMenuPath() {
		return "File<Open<Image";
	}
	
	@Override
	public String getNameText() {
		if(form==OpenForm.from_one)
			return "From File";
		return "From Seperate Channnel Files";
	}
	
	String typeString() {
		return getNameText();
	}
	

	/**Creates a new worksheet*/
	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		if(this.form==OpenForm.from_one) {
			
				MultichannelDisplayLayer i = CurrentAppContext.getMultichannelContext().getMultichannelOpener().creatMultiChannelDisplayFromUserSelectedImage(true, null);
				i.getSlot().showImage();
			
			}
		else
			CurrentAppContext.getMultichannelContext().getMultichannelOpener().createMultichannelFromImageSequence(null, null, null, true);
	}

	@Override
	public String getCommand() {
		return "import images"+typeString();
	}

}

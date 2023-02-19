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
 * Date Created: Mar 23, 2022
 * Date Modified: June 13, 2022
 * Version: 2023.1
 */
package appContextforIJ1;

import appContext.PendingFileOpenActions;
import applicationAdaptersForImageJ1.ImagePlusWrapper;
import figureOrganizer.MultichannelDisplayLayer;
import ij.ImageListener;
import ij.ImagePlus;
import logging.IssueLog;

/**
 
 * 
 */
public class ImageOpenListener implements ImageListener {

	@Override
	public void imageClosed(ImagePlus arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void imageOpened(ImagePlus arg0) {
		MultichannelDisplayLayer zero = new IJ1MultiChannelCreator().createDisplayFromImagePlus(arg0);
		boolean actionDone=false;
		for(PendingFileOpenActions action: PendingFileOpenActions.pendingList) try {
			
			if(!action.isActive())
				continue;
			if(action.isTargetFile(new ImagePlusWrapper(arg0).getPath())) {
				action.performActionOnImageDisplayLayer(zero);
				actionDone=true;
				
				break;
			}
		} 
		
		
		
		catch (Throwable t ) {
			IssueLog.logT(t);
		}

		if(!actionDone) {
			zero.getSlot().kill();
		}
		
	}

	@Override
	public void imageUpdated(ImagePlus arg0) {
		// TODO Auto-generated method stub

	}

}

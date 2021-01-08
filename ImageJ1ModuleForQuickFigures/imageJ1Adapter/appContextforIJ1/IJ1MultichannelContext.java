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
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package appContextforIJ1;

import java.util.ArrayList;

import appContext.MultiDimensionalImageContext;
import applicationAdaptersForImageJ1.ImagePlusWrapper;
import channelMerging.MultiChannelImage;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.io.OpenDialog;
import logging.IssueLog;
import multiChannelFigureUI.MultiChannelDisplayCreator;

/**implementation of @see MultiDimensionalImageContext  for ImageJ*/
public class IJ1MultichannelContext implements MultiDimensionalImageContext {

	IJ1MultiChannelCreator item=new IJ1MultiChannelCreator();
	
	@Override
	public MultiChannelDisplayCreator getMultichannelOpener() {
		
		return item;
	}

	@Override
	public MultiChannelDisplayCreator createMultichannelDisplay() {
		
		return new IJ1MultiChannelCreator();
	}
	


	@Override
	public ArrayList<MultiChannelImage> getallVisibleMultichanal() {
		
		int[] list1 = WindowManager.getIDList();
		ArrayList<MultiChannelImage> output=new ArrayList<MultiChannelImage>();
		
		if (list1==null|| list1.length==0) IssueLog.log("No multichannel images are open");
		if (list1!=null) for(int i: list1) {
			
			ImagePlus im = WindowManager.getImage(i);
			if(im==null) continue;
			output.add(new ImagePlusWrapper(WindowManager.getImage(i)));
		}
		return output;
	}

	@Override
	public MultiChannelImage getCurrentMultichanal() {
		
		return new ImagePlusWrapper(IJ.getImage());
	}

	@Override
	public String getDefaultDirectory() {
		 return OpenDialog.getDefaultDirectory();
	}

}

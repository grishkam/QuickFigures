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
package applicationAdaptersForImageJ1;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import locatedObject.Selectable;
import undo.UndoManagerPlus;

import java.awt.Cursor;
import java.awt.Window;

import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWrapper;
import channelMerging.MultiChannelDisplayWrapper;
import channelMerging.MultiChannelImage;
import graphicalObjects.CordinateConverter;
import handles.SmartHandleList;

/**See interface.  Very few of the methods from the interfaces 
 need be implemented for this class to perform its function so most are not implemented.
 	*/
public class ImagePlusDisplayWrap implements MultiChannelDisplayWrapper, DisplayedImage {

	ImagePlus imp;

	public ImagePlusDisplayWrap(ImagePlus imp) {
		this.imp=imp;
	}
	
	@Override
	public void updateDisplay() {
		if (imp!=null) imp.updateAndDraw();

	}

	

	@Override
	public CordinateConverter getConverter() {
		return new CordinateConverterIJ1(imp);
	}

	@Override
	public Window getWindow() {
		if (imp==null) return null;
		return imp.getWindow();
	}


	

	@Override
	public MultiChannelImage getContainedMultiChannel() {
		if (imp==null) return null;
		return new ImagePlusWrapper(imp);
	}

	@Override
	public int getCurrentChannel() {
		// TODO Auto-generated method stub
		return imp.getChannel();
	}


	@Override
	public int getCurrentFrame() {
		// TODO Auto-generated method stub
		return imp.getFrame();
	}

	@Override
	public int getCurrentSlice() {
		// TODO Auto-generated method stub
		return imp.getSlice();
	}

	

	

	

	@Override
	public void closeWindowButKeepObjects() {
		imp.getWindow().setVisible(false);
		
	}

	

}

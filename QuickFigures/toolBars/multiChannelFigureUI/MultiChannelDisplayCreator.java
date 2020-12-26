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
package multiChannelFigureUI;

import java.awt.Image;

import channelMerging.MultiChannelImage;
import figureOrganizer.MultichannelDisplayLayer;

public interface MultiChannelDisplayCreator {
	static final String useActiveImage="ActiveImage";
	
	/**Creates a multiChannel Display for the user selected open image or file. if OpenFile is false, this will 
	  either open a dialog for the user to select an image or use the path string to find an open image*/
	public MultichannelDisplayLayer creatMultiChannelDisplayFromUserSelectedImage(boolean openFile, String path) ;
	public MultichannelDisplayLayer creatMultiChannelDisplayFromOpenImage() ;
	
	public MultiChannelImage creatMultiChannelFromImage(Image img) ;
	public MultiChannelImage creatRGBFromImage(Image img, String savePath) ;
	
	public String imageTypeName();
	
	public MultiChannelImage createFromImageSequence(String path, int[] dims);
	
}

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
package multiChannelFigureUI;

import java.awt.Image;
import java.io.File;

import channelMerging.MultiChannelImage;
import figureOrganizer.MultichannelDisplayLayer;

public interface MultiChannelDisplayCreator {
	static final String useActiveImage="ActiveImage";//instructions to use the currently active image can be passes as a path to the methods
	
	/**Creates a multiChannel Display for the user selected open image or file. if OpenFile is false, this will 
	  either open a dialog for the user to select an image or use the path string to find an open image*/
	public MultichannelDisplayLayer creatMultiChannelDisplayFromUserSelectedImage(boolean openFile, String path) ;
	public MultichannelDisplayLayer creatMultiChannelDisplayFromOpenImage() ;
	
	public MultiChannelImage creatMultiChannelFromImage(Image img) ;
	public MultiChannelImage creatRGBFromImage(Image img, String savePath) ;
	
	public String imageTypeName();
	
	/**when given the path of a folder with a series of images, this opens them as an image sequence (time line)*/
	public MultiChannelImage createFromImageSequence(String path, int[] dims);
	
	/**when given a series of images, this assumes they are distinct channels*/
	public String createMultichannelFromImageSequence(Iterable<File> input, int[] dims, String savePath, boolean show);

	
	
}

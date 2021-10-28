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
 * Date Modified: April 7, 2021
 * Version: 2021.2
 */
package appContextforIJ1;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import appContext.MultiDimensionalImageContext;
import applicationAdaptersForImageJ1.ImagePlusWrapper;
import channelMerging.MultiChannelImage;
import graphicalObjects_Shapes.RegularPolygonGraphic;
import graphicalObjects_Shapes.SimpleStar;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ShapeRoi;
import ij.io.OpenDialog;
import ij.process.ImageProcessor;
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
	
	
	/**shows a demo image that has the channel numbers printed in each split channel 
	 * image along with some shapes*/
	@Override
	public MultiChannelImage getDemoExample(boolean show,String path, int nChan, int rowIndex, int scale) {
		ImagePlus image = IJ.createHyperStack("b", 200*scale, 150*scale, nChan, 1, 1, 16);
		
		for(int i=1; i<=4&&i<=nChan; i++) {
			ImageProcessor processor = image.getStack().getProcessor(i);
			int fontSize=processor.getHeight()/6;
			processor.setFont(new Font("Arial", Font.BOLD, fontSize));
			processor.setColor(Color.white);
			processor.drawString(" "+i, 160*scale, (2+i)*fontSize);
			RegularPolygonGraphic shape = new RegularPolygonGraphic(new Rectangle(image.getWidth()/4,image.getHeight()/20+20*(i-1), image.getWidth()/2,image.getHeight()/2), 2+i);
			
			if(rowIndex==2) {
				shape=new SimpleStar(shape.getRectangle().getBounds(), 2*i+2);
				shape.moveLocation((2-i)*25, 0);
			}
			if (i==4) {shape.setWidth(15); shape.setHeight(15); shape.moveLocation(80, 10);;}
			if(i==1) shape.rotate(Math.PI/4);
			processor.fill(new ShapeRoi(shape.getOutline()));
		}
		
		if(path!=null) {
			File f = new File(path);
			f.getParentFile().mkdirs();
			IJ.saveAsTiff(image, path);
			
		}
		if(show)
			image.show();
		WindowManager.setCurrentWindow(image.getWindow());
		return new ImagePlusWrapper(image);
	}

}

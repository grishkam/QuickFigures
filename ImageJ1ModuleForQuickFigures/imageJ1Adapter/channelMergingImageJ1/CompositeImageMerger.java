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
 * Version: 2021.1
 */
package channelMergingImageJ1;

import ij.CompositeImage;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ColorProcessor;
import ij.process.LUT;
import logging.IssueLog;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

import applicationAdapters.PixelWrapper;
import applicationAdaptersForImageJ1.ImagePlusWrapper;
import applicationAdaptersForImageJ1.ProcessorWrapper;
import channelMerging.ChannelEntry;
import channelMerging.ChannelMerger;
import channelMerging.ChannelUseInstructions;
import figureOrganizer.PanelListElement;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;

/**this class handles the merging of multiple channels into rgb images that 
 * are to be displayed in image panels*/
public class CompositeImageMerger implements ChannelMerger, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImagePlusWrapper impw;
	

	
	public CompositeImageMerger(ImagePlusWrapper imagePlusWrapper) {
		this.impw=imagePlusWrapper;
	}

	public PixelWrapper  generateMergedRGB(
			PanelListElement entry, int ChannelsInGrayScale) {
		
		ArrayList<ChannelEntry> channels=entry.getChannelEntries();
		if (impw instanceof ImagePlusWrapper) {
			ImagePlusWrapper impw2=(ImagePlusWrapper) impw;
			return  generateMergedRGB(impw2.getImagePlus(), channels, entry.targetSliceNumber, entry.targetFrameNumber, ChannelsInGrayScale);
		}
		else {throw new IllegalArgumentException("Must have ImagePlusWrapper as argumenr");}
		// TODO Auto-generated method stub
		
	}

	private PixelWrapper mergedComposite(CompositeImage imm){
		imm.setMode(CompositeImage.COMPOSITE);
		ImagePlus m = new ImagePlus("temp image", imm.getProcessor().createProcessor(0,0)); m.setImage(imm.getImage());
		imm.setMode(CompositeImage.COLOR);
		return new ProcessorWrapper(m.getProcessor());
	}
	
	/**makes an rgb image by merging the given channels*/
	private PixelWrapper generateMergedRGB(ImagePlus imp, ArrayList<ChannelEntry> channels, int slice, int frame, int ChannelsInGrayScale) {
		try {
		
		LUT[] luts =imp.getLuts();
		
		LUT[] newLut=new LUT[channels.size()];
		ProcessorWrapper image=new ProcessorWrapper(imp.getStack().getProcessor(imp.getStackIndex(1, slice, frame)));
		ImageStack st=	new ImageStack(image.width(), image.height());
		
		
		if (imp instanceof CompositeImage) {
			/**adds each channel entry to the stack and array*/
			for (int i=0; i<channels.size(); i++) {
				ChannelEntry channl=channels.get(i);
				
				int index=imp.getStackIndex(channl.getOriginalChannelIndex(), slice, frame);
				
				st.addSlice(imp.getStack().getProcessor(index));
				newLut[i]=luts[channl.getOriginalChannelIndex()-1];
				
			}
			/**Creates a temporary composite image*/
			CompositeImage tempcomposte = new CompositeImage(new ImagePlus("", st));
			tempcomposte.setLuts(newLut);
			tempcomposte.updateAndDraw();
			if (channels.size()==1) 
				return sliceOfComposite(tempcomposte, 1, ChannelsInGrayScale==ChannelUseInstructions.CHANNELS_IN_GREYSCALE); 
			else 
				return mergedComposite(tempcomposte);
				}
		else if (imp.getChannelProcessor() instanceof ColorProcessor)
			{return image;}
			
		else {
			/**adds each channel entry to the stack and array*/
			for (int i=0; i<channels.size(); i++) {
				ChannelEntry channl=channels.get(i);
				
				int index=imp.getStackIndex(channl.getOriginalChannelIndex(), slice, frame);
				
				st.addSlice(imp.getStack().getProcessor(index));
				newLut[i]=luts[channl.getOriginalChannelIndex()-1];
			}
			/**Creates a temporary composite image*/
			CompositeImage tempcomposte = new CompositeImage(new ImagePlus("", st));
			tempcomposte.setLuts(newLut);
			tempcomposte.updateAndDraw();
			if (channels.size()==1) return sliceOfComposite(tempcomposte, 1, ChannelsInGrayScale==1); else 
				return mergedComposite(tempcomposte);
				}
		
		
		}  catch (Throwable t) {
			IssueLog.logT(t);
			return createEmpty(imp);
			}
		
		}
			//return image;

	/**
	 * @param imp
	 * @return
	 */
	protected PixelWrapper createEmpty(ImagePlus imp) {
		BufferedImage image = ImagePanelGraphic.createImageWithText("Empty", imp.getWidth(),  imp.getHeight(), imp.getWidth()/5);
		return new ProcessorWrapper(new ColorProcessor(image));
	}
		
	
	
	/**Returns a slice of composite image as a color processor. */
	private PixelWrapper sliceOfComposite(CompositeImage imm, int c, Boolean ChannelsInGrayScale) {
		if (ChannelsInGrayScale&&!imm.isInvertedLut()) {
			imm.setChannelLut(LUT.createLutFromColor(java.awt.Color.WHITE));
			}   
		
		imm.setPositionWithoutUpdate(c, imm.getSlice(), imm.getFrame());
		if (ChannelsInGrayScale&&!imm.isInvertedLut()) {imm.setChannelLut(LUT.createLutFromColor(java.awt.Color.WHITE)); imm.setMode(CompositeImage.GRAYSCALE); imm.updateAndDraw(); } 	
		Image img = imm.getImage();
		return new ProcessorWrapper(new ColorProcessor(img));
	}
	
	

}

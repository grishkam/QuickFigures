/**
 * Author: Greg Mazo
 * Date Created: Oct 26, 2021
 * Date Modified: Oct 26, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package channelMergingImageJ1;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import appContext.CurrentAppContext;
import appContextforIJ1.ImageDisplayTester;
import applicationAdaptersForImageJ1.ImagePlusWrapper;
import channelMerging.ChannelOrderAndColorWrap;
import channelMerging.ImageDisplayLayer;
import channelMerging.MultiChannelImage;
import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects_SpecialShapes.BarGraphicTest;
import ij.IJ;
import ij.ImagePlus;
import logging.IssueLog;
import messages.ShowMessage;
import testing.FigureTester;
import utilityClasses1.ArraySorter;

/**
 
 * 
 */
class IJ1ChannelOrderWrapTest {

	static final String modeName="chan names", modeData="chan data";
	String themode= modeData;
	
	@Test
	void test() {
		ImageDisplayTester.main(new String[] {});
		IssueLog.sytemprint=true;
		IssueLog.windowPrint=false;
		IssueLog.waitSeconds(1);
		
		ArrayList<File> files = FigureTester.getTestFilesForTestNumber("4/");
		IssueLog.log(files);
		
		for(File f: files) {
			testChannelSwap(f);
		}
		
		IssueLog.waitSeconds(30);
	}

	/**
	 * @param f
	 */
	private void testChannelSwap(File fig1) {
		ImageDisplayLayer mc = new BarGraphicTest().createFigure(fig1).getPrincipalMultiChannel();
		  
		  
		  MultiChannelImage imagePlusWrapper = mc.getMultiChannelImage();
		 
		  themode= modeName;
		  performAllSwaps(imagePlusWrapper);
		  
		  themode= modeData;
		  performAllSwaps(imagePlusWrapper);
		 
		  
		}

	/**
	 * @param imagePlusWrapper
	 */
	protected void performAllSwaps(MultiChannelImage imagePlusWrapper) {
		int nChannels = imagePlusWrapper.nChannels();
		for(int i=1; i<=nChannels; i++) {
			for(int j=1;j<=nChannels; j++) {
				if(i==j)
					continue;
			  testSpecificSwap(i,j, imagePlusWrapper);
			}
		  }
	}

	/**Tests to make sure that the channel swap changes the channel names
	 * @param i
	 * @param j
	 * @param imagePlusWrapper
	 */
	private void testSpecificSwap(int i, int j, MultiChannelImage imagePlusWrapper) {
		IssueLog.log("Testing swap "+i+", "+j +" for "+imagePlusWrapper.getTitle());
		
		ArrayList<String> expectedOrder = imagePlusWrapper.getRealChannelNamesInOrder();
		new ArraySorter<String>().swapObjectPositionsInArrayIndex(i-1, j-1, expectedOrder);
		
		 Object expectectChannelI = getTestedObject(j, imagePlusWrapper);
		 Object expectectChannelJ =getTestedObject(i, imagePlusWrapper);
		 
		 IssueLog.log("will switch "+expectectChannelI+" wich "+expectectChannelJ);
		 if(i!=j)
			 assert(!expectectChannelI.equals(expectectChannelJ));// to make sure each channel has a distinct name
		 
		imagePlusWrapper.getChannelSwapper().swapChannelsOfImage(i, j);
		
		
		 Object observedChannelI =getTestedObject(i, imagePlusWrapper);
		 Object observedChannelJ = getTestedObject(j, imagePlusWrapper);
		 
		 /**confirm that the channels have been swapped*/
		 assert(expectectChannelI.equals(observedChannelI));
		 assert(expectectChannelJ.equals(observedChannelJ));
		 if(i!=j)
			 assert(!observedChannelI.equals(observedChannelJ));// to make sure each channel still has a distinct name
		 
		 if(themode==modeName) {
			 ArrayList<String> observedOrder = imagePlusWrapper.getRealChannelNamesInOrder();
			IssueLog.log("expect order"+expectedOrder);
			IssueLog.log("observe order"+observedOrder);
		}
		 
	}

	/**returns an object representing the channel j of the multichannel image
	 * @param j
	 * @param imagePlusWrapper
	 * @return
	 */
	protected Object getTestedObject(int j, MultiChannelImage imagePlusWrapper) {
		if(themode==modeData) {
			Object pixels = imagePlusWrapper.getPixelWrapperForSlice(j, 1,1);
			return pixels;
		}
		
		return imagePlusWrapper.getRealChannelName(j);
	}
		
	}



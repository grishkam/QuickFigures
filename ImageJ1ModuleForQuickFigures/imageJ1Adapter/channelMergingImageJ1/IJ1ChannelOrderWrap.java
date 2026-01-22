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
 * Version: 2023.2
 */
package channelMergingImageJ1;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import applicationAdaptersForImageJ1.ImagePlusWrapper;
import channelMerging.ChannelColorWrap;
import channelMerging.ChannelOrderAndColorWrap;
import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.LutLoader;
import ij.process.LUT;
import imageDisplayApp.UserPreferences;
import logging.IssueLog;
import messages.ShowMessage;
import standardDialog.StandardDialog;
import standardDialog.choices.JListInputPanel;
import ultilInputOutput.FileChoiceUtil;

/**ImageJ implementation of the channel order interface 
 * @see ChannelOrderAndColorWrap*/
public class IJ1ChannelOrderWrap implements ChannelOrderAndColorWrap{

	private ImagePlus imp;
	private ImagePlusWrapper container;
	
	private IJ1ChannelSwapper swapper=new IJ1ChannelSwapper();


	private ChannelSwapListener listener;
	

	public IJ1ChannelOrderWrap(ImagePlus s) {
		this.imp=s;
		
	}
	
	public IJ1ChannelOrderWrap(ImagePlus imp2, ImagePlusWrapper imagePlusWrapper) {
		imp=imp2;
		container= imagePlusWrapper;
	}

	@Override
	public void swapChannelsOfImage(int a, int b) {
		
		swapper.swapChannelsOfImage(imp, a, b);
		
		listener.afterChanSwap();
	}

	@Override
	public void swapChannelLuts(int a, int b) {
		swapper.swapChannelLuts(imp, a, b);
		listener.afterChanSwap();
	}

	@Override
	public void setChannelColor(Color c, int chan) {
		
		setLutColor(c,chan);
	}
	
	@Override
	public void setChannelColor(byte[][] lut, int chan) {
		LUT l = new LUT(lut[0], lut[1], lut[2]);
		setLut(chan, l);
	}
	
	private void setLutColor(Color lut, int chan) {
		
		if (chan<=0) {
			IssueLog.log(" Was asked to change color for channel '0' but channel numbering starts from 1");
			
			return;
			}
		LUT createLutFromColor = LUT.createLutFromColor(lut);
		
		/**inverted lut created for some colors*/
		if (Color.black.equals(lut)) {
			createLutFromColor = LUT.createLutFromColor(Color.white);
			createLutFromColor=createLutFromColor.createInvertedLut();
		}
		setLut(chan, createLutFromColor);
	}

	private void setLut(int chan, LUT createLutFromColor) {
		if (chan<=0) {
			IssueLog.log(" Was asked to change color for channel '0' but channel numbering starts from 1");
			
			return;
			}
		
		if (imp instanceof CompositeImage)
			setLutColorWithoutDisplayRangeEdit((CompositeImage)imp, createLutFromColor, chan);
		else {
			try {
				imp.getProcessor().setLut(createLutFromColor);
			} catch (Exception e) {
				IssueLog.log("Problem, failed to set channel color");
			}
		}
	}
	
	/**Sets the channel LUT of the channel*/
	private static void setLutColorWithoutDisplayRangeEdit(CompositeImage ci4, LUT lut, int a) {
		if (a==0) {
			IssueLog.log("Error, Was asked to chang color for channel '0' but channel numbering starts from 1");
			return;
		}
		LUT[] oldluts=ci4.getLuts().clone();
		lut.min=oldluts[a-1].min;lut.max=oldluts[a-1].max;
		ci4.setChannelLut(lut, a);
	}
	
	

	@Override
	public void moveChannelOfImage(int choice1, int choice2) {
		 swapper.moveChannels(imp, choice1, choice2);
		
	}

	@Override
	public void moveChannelLutsOfImage(int choice1, int choice2) {
		swapper.moveChannelsLuts(imp, choice1, choice2);
		
	}

	public void addChannelSwapListener(ChannelSwapListener imagePlusWrapper) {
		this.listener=imagePlusWrapper;
		
	}
	
	public static String[] getApplicationSpecificColorChoices() {
		String[] output = new String[] {};
		
		
		return output;
	}

	@Override
	public void setChannelColorToSavedLut(String lut, int chan) {
		
		if(new File(lut).exists()) {
			setLut(chan, LutLoader.openLut(new File(lut).getAbsolutePath()));
			return;
		}
		
		File the_lut_file = findLutFileWithName(lut);
		
		if(the_lut_file==null) {
			lut=JListInputPanel.getChoiceFromUser("Could not find that lut file", getLutOptions());
			the_lut_file = findLutFileWithName(lut);
		}
		
		if(the_lut_file==null) {
			ShowMessage.showOptionalMessage("Could not find that lut file ", true,"Could not find that lut file "+the_lut_file, "Try these "+getExampleLutFileNames() );
			
		} else {
			
			setLut(chan, LutLoader.openLut(the_lut_file.getAbsolutePath()));
		}
		
		
	}

	/**
	 * @param lut
	 * @return
	 */
	private File findLutFileWithName(String lut) {
		File[] files = getListOfLutFiles();
		
		File the_lut_file=null;
		
		
		if(files!=null)
		for(File a_file: files) {
			if(a_file.getName().toLowerCase().equals(lut.toLowerCase())|| a_file.getName().toLowerCase().equals((lut.toLowerCase()+".lut"))) {
				the_lut_file=a_file;
				
			}
			IssueLog.log(a_file.getName());
		}
		return the_lut_file;
	}

	/**
	 * @return
	 */
	private static File[] getListOfLutFiles() {
		String path=getLutFilePath();
		
		File f = new File(path);
		File[] files = f.listFiles();
		return files;
	}
	
	public static ArrayList<String> getLutOptions() {
		File[] f=getListOfLutFiles();
		ArrayList<String> output = new ArrayList<String>();
		for(File file: f) {
			if(file.getName().endsWith(".lut")) {
				output.add(file.getName().replace(".lut", ""));
			}
		}
		return output;
	}

	/**
	 * @return
	 */
	private static String getLutFilePath() {
		return IJ.getDirectory("luts");
	}
	
	/**returns some found lut file names to help the user*/
	public static String getExampleLutFileNames() {
		File[] files = getListOfLutFiles();
		if(files==null||files.length==0)
			return "[Could not fine any lut files or failed to find folder]";
		String output = "Examples: " +files[0].getName();
			for(int i=1; i<files.length&i<5; i++)
				output+=", "+files[i].getName();
										
		return output;
		
	}
	

	/**Sets the channel color*/
	@Override
	public void setChannelColorTo(Object c, int chan) {
		if(c instanceof Color)
			this.setChannelColor((Color)c, chan);
		if(c instanceof LUT) {
			setLut(chan, (LUT)c);
		}
		if(c instanceof byte[][]) {
			this.setChannelColor((byte[][]) c, chan);
		}
	}
	
}

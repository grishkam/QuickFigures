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
package applicationAdaptersForImageJ1;

import ij.CompositeImage;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.gui.Roi;
import ij.gui.Toolbar;
import ij.measure.Calibration;
import ij.plugin.CanvasResizer;
import ij.process.ImageProcessor;
import ij.process.LUT;
import imageDisplayApp.OverlayObjectManager;
import imageScaling.Interpolation;
import infoStorage.BasicMetaDataHandler;
import infoStorage.MetaInfoWrapper;
import locatedObject.LocatedObject2D;
import locatedObject.ScaleInfo;
import logging.IssueLog;
import multiChannelFigureUI.ChannelManipulations;
import undo.UndoManagerPlus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.ArrayList;

import channelMerging.ChannelColorWrap;
import channelMerging.ChannelEntry;
import channelMerging.ChannelMerger;
import channelMerging.ChannelOrderAndColorWrap;
import channelMerging.MultiChannelImage;
import channelMerging.PreProcessInformation;
import channelMergingImageJ1.ChannelSwapListener;
import channelMergingImageJ1.CompositeImageMerger;
import channelMergingImageJ1.IJ1ChannelOrderWrap;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWorkSheet;
import applicationAdapters.OpenFileReference;
import applicationAdapters.PixelWrapper;

/**An implementation of several interfaces. Some are required for an imageJ image to
  be used by QuickFigures as a multichannel image*/
public class ImagePlusWrapper implements  ImageWorkSheet, MultiChannelImage, ChannelSwapListener, OpenFileReference {

	private static final int FRAME = 2;
	private static final int SLICE = 1;
	private static final int CHANNEL = 0;
	private OverlayObjectManager selectionManagger;
	private CompositeImageMerger merger=new CompositeImageMerger(this);
	
	ImagePlus imp;
	private ArrayList<String> channames;
	private ArrayList<String> chanexposures;
	
	public ImagePlusWrapper(ImagePlus imp) {
		
		this.imp=imp;
		{
			this.setSelectionManagger(new IJ1Selections(imp));
		}
		checkVoid(); 
		
		if(imp!=null)
		ChannelManipulations.innitializeDisplayRangetoMinMax(this);
		}
	
	public ImagePlus getImagePlus() {
		return imp;
	}
	
	public void checkVoid() {
	
	}
	
	/***/
	@Override
	public void takeFromImage(LocatedObject2D roi) {
		checkVoid();
		if (roi instanceof RoiWrapper) {
			((RoiWrapper)roi).takeFromImage(imp);
		} 

	}

	@Override
	public void addItemToImage(LocatedObject2D roi) {
		checkVoid();
		if (roi instanceof RoiWrapper) {
			((RoiWrapper)roi).addToImage(imp);
		}
	}

	@Override
	public void addRoiToImageBack(LocatedObject2D roi) {
		addItemToImage(roi);

	}

	@Override
	public ArrayList<LocatedObject2D> getLocatedObjects() {
		
		if (imp==null) {
			return new ArrayList<LocatedObject2D>() ;
			}
		ArrayList<LocatedObject2D> output=new ArrayList<LocatedObject2D>();
		if (imp.getOverlay()!=null) {
		Roi[] rois=imp.getOverlay().toArray();
		for(Roi roi: rois) {if (roi!=null) output.add(new RoiWrapper(roi));}
		}
		
		return output;
	}

	@Override
	public LocatedObject2D getSelectionObject() {
		checkVoid();
		if (imp.getRoi()==null) return null;
		return new RoiWrapper(imp.getRoi());
	}


	public PixelWrapper getPixelWrapper() {
		checkVoid();
		return new ProcessorWrapper(imp.getProcessor());
	}

	@Override
	public void updateDisplay() {
		checkVoid();
		imp.updateAndDraw();
		
	}

	/**resizes the canvas of pixels but does not move the objects*/
	@Override
	public void worksheetResize(int width, int height, int xOff, int yOff) {
		Prefs.set("resizer.zero", false); 
		Toolbar.setBackgroundColor(Color.white);
		ImageStack newStack=new CanvasResizer().expandStack(imp.getStack(), width, height, xOff, yOff);	
		imp.setStack(newStack);
	}

	@Override
	public String getTitle() {
		if (imp==null) return "";
		return imp.getTitle();
	}

	@Override
	/**Returns the path where the file is saved*/
	public String getPath() {
		if (imp==null) return null;
		String title=imp.getTitle(); 
		if (imp.getOriginalFileInfo()==null) {
			if (!title.endsWith(".tif")) title+=".tif"; 
			return title;
			}
		
		String folderDirectory=imp.getOriginalFileInfo().directory;
	
		if (folderDirectory==null||title==null) return null;
	
		String path=folderDirectory+title; 
		
		if (path!=null && !"".equals(path.trim()) ) return path;
		return path;
	}
	
	@Override
	public
	Window window() {
		checkVoid();
		if (imp==null) return null;
		return imp.getWindow();
	}
	public int width() {
		checkVoid();
		if (imp==null) return 0;
		return imp.getWidth();
		}
	public int height() {
		checkVoid();
		if (imp==null) return 0;
		return imp.getHeight();
		}
	
	public void show() {
		if (imp!=null)
		imp.show();
		 
	 }
	

	@Override
	public ScaleInfo getScaleInfo() {
		if (imp!=null)
		return new IJ1ScaleInfo(imp);
		return null;
	}
	
	@Override
	public void setScaleInfo(ScaleInfo scaleInfo) {
		Calibration cal = imp.getCalibration();
		cal.setUnit(scaleInfo.getUnits());
		cal.pixelWidth=scaleInfo.getPixelWidth();
		cal.pixelHeight=scaleInfo.getPixelHeight();
		cal.pixelDepth=scaleInfo.getPixelDepth();

		imp.setCalibration(cal);
		
		
	}



	@Override
	public GraphicLayer getTopLevelLayer() {

		return null;
	}

	

	@Override
	public void onItemLoad(ZoomableGraphic z) {

		
	}

	
	public String toString() {
		return ""+imp;
	}

	@Override
	public Dimension getCanvasDims() {
		if (imp==null) return new Dimension();
		return new Dimension(imp.getWidth(), imp.getHeight());
	}

	@Override
	public ImageWorkSheet getAsWrapper() {
		return this;
	}

	@Override
	public int nChannels() {
		if(imp==null)  return 1;
		return imp.getNChannels();
	}

	@Override
	public int nFrames() {
		if(imp==null)  return 1;
		return imp.getNFrames();
	}

	@Override
	public int nSlices() {
		if(imp==null)  return 1;
		return imp.getNSlices();
	}

	
	/**retrives the pixels from the given channel slice frame and other dimensions*/
	@Override
	public PixelWrapper getPixelWrapperForSlice(int channel, int slice,
			int frame, int... dim) {
		checkVoid();
		if(imp==null) {
			IssueLog.log("Image has been set to null");
			return null;
			}
		/**checks for validaty and warns*/
		if (channel>nChannels()) {
			IssueLog.log("asked for impossible channel in "+this.getTitle());
		}
		if (slice>nSlices()) {
			IssueLog.log("asked for impossible slice in "+this.getTitle());
		}
		if (frame>nFrames()) {
			IssueLog.log("asked for impossible frame in "+this.getTitle());
		}
		int i=imp.getStackIndex(channel, slice, frame);
		if (imp.getStackSize()<i||imp.getStack()==null||imp.getStack().getSize()==0) {
			IssueLog.log("asked for impossible stack index "+this.getTitle());
		}
		ImageProcessor p = imp.getStack().getProcessor(i);
		return new ProcessorWrapper(p);
		
	}

	@Override
	public MetaInfoWrapper getMetadataWrapper() {
		return new ImagePlusMetaDataWrapper(imp);
	}

	public OverlayObjectManager getOverlaySelectionManagger() {
			
		return selectionManagger;
	}

	public void setSelectionManagger(OverlayObjectManager selectionManagger) {
		this.selectionManagger = selectionManagger;
	}

	

	@Override
	public int getID() {
		if (imp!=null) return imp.getID();
		return 0;
	}

	@Override
	public int getStackIndex(int channel, int slice, int frame, int...dim) {
		return imp.getStackIndex(channel, slice, frame);
	}

	@Override
	public boolean containsImage() {
		if (imp==null||imp.getStack()==null||imp.getStack().getSize()<1) return false;
		
		return true;
	}

	@Override
	/**returns a name for the channel. 
	  In this case, the labels of the first stack slices are
	 used as channel names. if other names are found and slice labels
	 are null, sets the slice labels to generic channel names
	 channel indexes go from 1 onward*/
	public String getGenericChannelName(int c) {
		int index=imp.getStackIndex(c, 1, 1);
		ImageStack stack=imp.getStack();
		if (stack.getSize()<index) return null;
		String name=stack.getSliceLabel(index);
		if (name==null) {
			setSliceLabelToChannel(c, index, stack);
			name=stack.getSliceLabel(index);
		}
		return name;
		
	}

	/**sets the stack slice label at index to indicate the channel index*/
	private void setSliceLabelToChannel(int c, int index, ImageStack stack) {
		stack.setSliceLabel("c:"+c+"/"+imp.getNChannels(), index);
		if(imp.getNChannels()<stack.getSize())
			for( int f=1; f<=imp.getNFrames(); f++) {
				for(int s=1; s<=imp.getNSlices(); s++) {
					index=imp.getStackIndex(c, s, f);
					stack.setSliceLabel("c:"+c+"/"+imp.getNChannels(), index);
				}
			}
		}

	/**gets the channel lut as a color*/
	@Override
	public Color getChannelColor(int c) {
		// TODO Auto-generated method stub
		if(imp==null) return null;
		LUT[] eachChanLUT=imp.getLuts(); 
		if (eachChanLUT==null)
			return getColorFromLUT(imp.getProcessor().getLut(), 255);
		else if (c==0) {
			IssueLog.log("problem with lut arrays");
			return null;
		} else if (eachChanLUT.length==0){
			return Color.white;	
		}
		else {
			
			return getColorFromLUT(eachChanLUT[c-1], 255);
			}
		
	}
	
	
	/**These methods return a color from specified part of a lut defined by int in*/
	public static Color getColorFromLUT(LUT lut, int in){return new Color(lut.getRed(in), lut.getGreen(in), lut.getBlue(in));}

	
	/**returns the name of the indicated stack slice*/
	@Override
	public String getSliceName(int channel, int slice, int frame, int... dim) {
		int i=this.getStackIndex(channel, slice, frame, dim);
		
		return imp.getStack().getSliceLabel(i);
	}

	@Override
	public boolean containsSplitedChannels() {
		return imp instanceof CompositeImage;
	}

	/**returns the bounds of the roi or one of the first few overlay objects*/
	public Rectangle getSelectionRectangle( int n) {
		try{
		if (n==0&&imp.getRoi()!=null) return imp.getRoi().getBounds();
		if (n==1&& imp.getOverlay()!=null) return  imp.getOverlay().toArray()[0].getBounds();
		if (n==2&& imp.getOverlay()!=null) return  imp.getOverlay().toArray()[1].getBounds(); 
		if (n==3&& imp.getOverlay()!=null) return  imp.getOverlay().toArray()[2].getBounds(); 
		if (n==4&& imp.getOverlay()!=null) return  imp.getOverlay().toArray()[3].getBounds(); 
		} catch (Exception e) {}
		return null;
	}
	
	/**Removes the roi*/
	public void eliminateSelection( int i) {
		try{
		if (i==0&&imp.getRoi()!=null)imp.killRoi();;
		} catch (Exception e) {}
		
	}

	/**returns true if the same image plus is given by the argument*/
	@Override
	public boolean isSameImage(Object o) {
		if (o==imp) return true;
		if (o instanceof ImagePlusWrapper) {
			ImagePlusWrapper o2=(ImagePlusWrapper) o;
			if (o2.getImagePlus()==imp) return true;
		}
		return false;
	}

	@Override
	public ChannelOrderAndColorWrap getChannelSwapper() {
		IJ1ChannelOrderWrap cs = new IJ1ChannelOrderWrap(imp, this);
		cs.addChannelSwapListener(this);
		this.channames=null;//since the channel swapper might change the channel order, making the chan names null ensures 
		return cs;
	}
	
	@Override
	public ChannelColorWrap getChannelColors() {
		IJ1ChannelOrderWrap cs = new IJ1ChannelOrderWrap(imp, this);
		return cs;
	}

	@Override
	public int[] convertIndexToPosition(int i) {
		return imp.convertIndexToPosition(i);
	}

	
	@Override
	public void setSliceName(String name, int i) {
		imp.getStack().setSliceLabel(name, i);
		
	}

	@Override
	public String getSliceName(int stackIndex) {
		return imp.getStack().getSliceLabel(stackIndex);
	}

	@Override
	public double getChannelMax(int chan) {
		if (lutsNotAvailable(chan)) return 0;
		if(getLut(chan)==null) return 0;
		return getLut(chan).max;
	}

	public boolean lutsNotAvailable(int chan) {
		try {
			return imp==null||imp.getLuts()==null||imp.getLuts().length==0||imp.getLuts().length<chan;
		} catch (Exception e) {
			return true;
		}
		
		
	
	}

	public LUT getLut(int chan) {
		try {
			return imp.getLuts()[chan-1];
		} catch (Exception e) {
			IssueLog.logT(e);
			
		}
		return null;
	}

	@Override
	public double getChannelMin(int chan) {
		if (lutsNotAvailable(chan)) return 0;
		if(getLut(chan)==null) return 0;
		return getLut(chan).min;
	}

	@Override
	public void setChannelMax(int chan, double max) {
		if (lutsNotAvailable(chan)) return ;
		try {
			imp.setSlice(chan);
			imp.setC(chan);
			double smin = getLut(chan).min;
			imp.setDisplayRange(smin, max);
			imp.updateImage();
		} catch (Exception e) {
			IssueLog.logT(e);
		}
		
	}

	@Override
	public void setChannelMin(int chan, double min) {
		if (lutsNotAvailable(chan)) return ;
		try {
			imp.setSlice(chan);
			imp.setC(chan);
			double smax = getLut(chan).min;
			imp.setDisplayRange(min, smax);
			imp.updateImage();
		} catch (Exception e) {
			IssueLog.logT(e);
		}
	}
	
	


	@Override
	public void setTitle(String st) {
		imp.setTitle(st);
		
	}

	@Override
	public ChannelEntry getSliceChannelEntry(int channel, int slice, int frame,
			int... dim) {
		int index2=getStackIndex(channel, slice, frame,
				 dim);
		String title2=getSliceName(channel, slice, frame, dim);
		ChannelEntry ce = new ChannelEntry(title2,  this.getChannelColor(channel), channel);
		ce.setRealChannelName(this.getRealChannelName(channel));
		ce.setOriginalStackIndex(index2);
		return ce;
	}

	@Override
	public DisplayedImage getImageDisplay() {
		if (imp==null) return null;
		//return new ImagePlusDisplayWrap(imp);
		return  null;
	}

	
	/**makes sure the channel names are set up
	 * returns the channel names
	 * @return */
	public ArrayList<String> setUpChannelNames() {
		if (this.channames==null){
			
			ImagePlusMetaDataWrapper mcw = new ImagePlusMetaDataWrapper(imp);
			this.channames  =
				(new BasicMetaDataHandler()).channelNamesInOrder(mcw);
			if (channames.size()==0) 
				(new BasicMetaDataHandler()).createChanNamesFor(mcw, imp.getNChannels());//if there are no channel names, creates artificial ones
			this.chanexposures=(new BasicMetaDataHandler()).getChannelExposuresInOrder(mcw);
		}
		return channames;
	}
	
	/**returns the real channel name from a list field. first channel is 1, second is 2*/
	@Override
	public String getRealChannelName(int i) {
		 setUpChannelNames() ;
		if (channames.size()>=i) {
			return channames.get(i-1);
			
		}
		return null;
	}
	
	/**if the exposure of the given channel is available, returns it*/
	@Override
	public String getRealChannelExposure(int i) {
		 setUpChannelNames() ;
		if (this.chanexposures.size()>=i) {
			
			return chanexposures.get(i-1);
			
		}
		return null;
	}

	/**returns the index of the channel with real channel name. example
	  if eGFP is in the second channel then this will return a 2
	  for an input of "eGFP" */
	@Override
	public int getIndexOfChannel(String realname) {
		if (realname==null) return 0;
		for(int c=1; c<=this.nChannels(); c++) {
			String rc = getRealChannelName(c);
			if (rc==null) continue;
			if ((rc.trim()).equals(realname.trim())) return c;
		}
		return 0;
	}

	@Override
	public ChannelMerger getChannelMerger() {
		return merger;
	}

	@Override
	public ArrayList<ChannelEntry> getChannelEntriesInOrder() {
		ArrayList<ChannelEntry> output = new  ArrayList<ChannelEntry>();
		// TODO Auto-generated method stub
		for(int c=1; c<=this.nChannels(); c++) {
			ChannelEntry entry= getSliceChannelEntry(c, 1,1);
			output.add(entry);
		}
		return output;
	}

	@Override
	public int getStackSize() {
		if (imp==null) return 0;
 		return imp.getStackSize();
	}

	@Override
	public UndoManagerPlus getUndoManager() {
		return new UndoManagerPlus();
	}

	@Override
	public void colorBasedOnRealChannelName() {
		for(int i=1; i<=this.nChannels(); i++) try {
		
			String name=this.getRealChannelName(i);
			if (name==null) {IssueLog.log("Cannot find real Channel name");
								continue;}
			
			//IssueLog.log("Will try to set channel color for "+name);//+ " of exposure "+this.getRealChannelExposure(i) +" ms");
			ChannelOrderAndColorWrap channelSwapper = this.getChannelSwapper();
			
			 ChannelManipulations.setChannelColor(i, name, channelSwapper);
			
		} catch (Throwable t) {
			IssueLog.log("problem setting colors based on real channel names "+i);
			IssueLog.log("meta data may be incomplete or inconsistent "+i);
			IssueLog.logT(t);
			
		}
		
	}

	
	/**names the first few stack slices to include the channel information
	  assumes that the stack is in an order that starts with the channels*/
	@Override
	public void renameBasedOnRealChannelName() {
		for(int i=1; i<=this.nChannels(); i++) {
		
			String rName = this.getRealChannelName(i);
			String name=rName+"; "+this.getRealChannelExposure(i) +" ms";
			name=name+";";
			int iIndex=i;
			this.setSliceName(name+this.getSliceName(iIndex),iIndex);;
		}
		
	}

	/**not yet implemented. returns a scaled version of this*/
	@Override
	public MultiChannelImage scaleBilinear(double d) {

		return new ImagePlusWrapper(imp.duplicate());
	}

	/**returns a cropped and scaled version of this*/
	public ImagePlusWrapper cropAtAngle(PreProcessInformation p) {
		if(p==null) {
			
			IssueLog.log("no preprocess given");
			return this;
		}
		return cropAtAngle(p.getRectangle(), p.getAngle(), p.getScale(), p.getInterpolationType());
		
	}
	
	/** returns a scaled version of this that is also cropped using a rectangle that 
	  may be rotated at a certain angle*/
	private ImagePlusWrapper cropAtAngle(Rectangle r, double angle, double scale, Interpolation interpolateMethod) {
		imp.deleteRoi();
		ImagePlus d = imp.duplicate();
		ImageStack oldstack = d.getStack();

		ImageStack nstack = null;
		for(int i=1; i<=oldstack.getSize(); i++) {
			ProcessorWrapper p = new ProcessorWrapper(oldstack.getProcessor(i));
			p.setInterpolationType(interpolateMethod);
			if(r!=null)
				p.cropAtAngle(r, angle);
			
			if (scale!=1) {
				p.setInterpolationType(interpolateMethod);
				p.scaleWithCurrentInterpolationMethod(scale);
			}
			
			if (nstack == null)
				nstack=new ImageStack(p.getPixels().getWidth(), p.getPixels().getHeight());
			nstack.addSlice(oldstack.getSliceLabel(i), p.getPixels());
		}
		
		
		
		d.setStack(nstack);
		

		
		ImagePlusWrapper imagePlusWrapper = new ImagePlusWrapper(d);
		imagePlusWrapper.setChannelNames(this.channames);
		
		
		ScaleInfo scaled = this.getScaleInfo().getScaledCopyXY(scale);
		imagePlusWrapper.setScaleInfo(scaled);
		
		return imagePlusWrapper;
		
	
	}

	/**
	sets the channel names of this image wrapper. sometimes called to avoid time lag that comes with digging channel names out of metadata
	 */
	private void setChannelNames(ArrayList<String> channames2) {
		this.channames=channames2;
		
	}

	@Override
	public Dimension getDimensions() {
		return new Dimension(imp.getWidth(), imp.getHeight());
	}

	/**after channel swap, this sets the arrays with channel names/exposures to null.
	 The order in these arrays will be obsolete after a channel swap and null values indicate a 
	 fresh reset of channel names from metadata is required */
	@Override
	public void afterChanSwap() {
		channames=null;
		chanexposures=null;
	}

	/**returns either the current channel, current slice or frame depending on the argument*/
	@Override
	public Integer getSelectedFromDimension(int i) {
		if(i==CHANNEL) return imp.getChannel();
		if(i==SLICE) return imp.getSlice();
		if(i==FRAME) return imp.getFrame();
		return 0;
	}

	@Override
	public double bitDepth() {
		if (imp==null) return 8;
		return imp.getBitDepth();
	}

	@Override
	public boolean setPrimarySelectionObject(Object d) {
		return false;
	}

	@Override
	public boolean allowAutoResize() {
		return true;
	}

	@Override
	public void setAllowAutoResize(boolean allow) {
		
		
	}

	@Override
	public boolean hasChannelNameList() {
		return channames!=null;
	}

	/**returns a copy of the channel name list in the order that they appear*/
	@Override
	public ArrayList<String> getRealChannelNamesInOrder() {
		ArrayList<String> output = new ArrayList<String>();
		output.addAll(this.setUpChannelNames());
		return output;
	}






	
	
}

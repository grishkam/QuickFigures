package channelMerging;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;

import applicationAdapters.ImageFileWrapper;
import applicationAdapters.PixelWrapper;

/**A multidimensional image wrapper*/
public interface MultiChannelWrapper extends ImageFileWrapper {


	
	public int nChannels();
	public int nFrames();
	public int nSlices();
	
	
	
	/**wrapper class for getting a single section and doing operations on it*/
	public PixelWrapper getPixelWrapperForSlice(int channel, int slice, int frame, int... dim) ;
	
	public int getStackIndex(int channel, int slice, int frame, int... dim);
	public int getStackSize();
	public Dimension getDimensions();
	
	/**returns an array containing the channel slice and frame of the given index*/
	public int[] convertIndexToPosition(int i);
	
	
	public String channelName(int i);
	
	/**names as specified by the microscope channelname and not the user. this can be edited by the channel swapper tool.*/
	public String getRealChannelName(int i);
	public String getRealChannelExposure(int i);
	
	public void colorBasedOnRealChannelName();
	/**starting from one. gets the index of the channel with name 'realname'. if not found, returns 0.*/
	public int getIndexOfChannel(String realname);
	
	public String getSliceName(int channel, int slice, int frame, int...dim);
	public ChannelEntry getSliceChannelEntry(int channel, int slice, int frame, int...dim);
	public ArrayList<ChannelEntry> getChannelEntriesInOrder();
	
	public void setSliceName( String name, int i );
	
	/**returns the stack slice name. null returns if an invalid index*/
	public String getSliceName(int stackIndex);

	

	
	/**sets the display range of the channel*/
	public double getChannalMax(int chan);
	public double getChannalMin(int chan);
	public void setChannalMax(int chan, double max);
	public void setChannalMin(int chan, double max);
	
	public boolean containsSplitedChannels();
	
	public Rectangle getSelectionRectangle(int i);
	public void eliminateSelection(int i);
	
	/**for handling channel colors*/
	public Color getChannelColor(int i);
	public ChannelOrderAndColorWrap getChannelSwapper();
	
	public void updateDisplay();
	
	/**gets the class with methods to generate a merged RGB*/
	public ChannelMerger getChannelMerger();
	void renameBasedOnRealChannelName();
	
	
	/**Returns a scaled version, false otherwise*/
	public MultiChannelWrapper scaleBilinear(double d);
	
	/** returns a cropped and scaled version of this*/
	public MultiChannelWrapper  cropAtAngle(Rectangle r, double angle, double scale);
	public MultiChannelWrapper  cropAtAngle(PreProcessInformation p);
	public Integer getSelectedFromDimension(int i);
	public double bitDepth();
	
}

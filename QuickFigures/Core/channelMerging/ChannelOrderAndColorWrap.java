package channelMerging;

import java.awt.Color;

/**interface for reordering the channels of a multichannel image*/
public interface ChannelOrderAndColorWrap {

	/**Swaps the channel at position a with that at position b*/
	public void swapChannelsOfImage(int a, int b);
	/**Swaps the channel color at position a with that at position b*/
	public void swapChannelLuts(int a, int b);
	
	/**set the channel colors*/
	public void setChannelColor(Color c, int chan);
	void setChannelColor(byte[][] lut, int chan);//sets the lookup table
	
	/**moves the channel at position 1 to position 2. */
	public void moveChannelOfImage(int choice1, int choice2);
	/**moves the channel color at position 1 to position 2. */
	public void moveChannelLutsOfImage(int choice1, int choice2);
	
}

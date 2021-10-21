/**
 * Author: Greg Mazo
 * Date Modified: Oct 20, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package channelMerging;

import java.awt.Color;

/**
 
 */
public interface ChannelColorWrap {
	/**moves the channel color at position 1 to position 2. */
	public void moveChannelLutsOfImage(int choice1, int choice2);
	
	/**Swaps the channel color at position a with that at position b*/
	public void swapChannelLuts(int a, int b);
	
	/**set the channel colors*/
	public void setChannelColor(Color c, int chan);
	void setChannelColor(byte[][] lut, int chan);//sets the lookup table
}

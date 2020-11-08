package channelMerging;

import java.awt.Color;

public interface ChannelOrderAndColorWrap {

	public void swapChannelsOfImage(int a, int b);
	public void swapChannelLuts(int a, int b);
	public void setChannelColor(Color c, int chan);
	void setChannelColor(byte[][] lut, int chan);//sets the lookup table
	
	public void moveChannelOfImage(int choice1, int choice2);
	public void moveChannelLutsOfImage(int choice1, int choice2);
	
}

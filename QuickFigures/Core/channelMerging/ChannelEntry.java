/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package channelMerging;

import java.awt.Color;
import java.io.Serializable;

/**Information about a particular channel of a particular frame and slice.
  Used to keep track of information such as
  which channels are in a particular panel, what color a channel is, channel name, exposure time
  */
public class ChannelEntry implements Serializable{
	
	/**
	 * 
	 */
	/**strings describing the channel*/
	private String label="";
	private String additional="";
	private String realChannelName;
	
	private Color originalLutCol=Color.BLACK;
	private int originalChannelIndex;
	private int originalStackIndex;
	
	
	private static final long serialVersionUID = 1L;
	public ChannelEntry(String label, Color c, int number) {
		this.setLabel(label);
		this.setColor(c);
		this.setOriginalChannelIndex(number);
	}
	
	public String toString() {
		return this.getShortLabel()+"  "+getOriginalChannelIndex();
	}
	
	public String getLabel() {
		return label;
	}
	
	/**Returns a short version of the label that can be displayed in part of the user interface*/
	public String getShortLabel() {
		if (label==null) return getSimpleLabel();
		String short1 = label.split(";")[0];
		if(short1.length()>20) return getSimpleLabel();
		return short1;
	}

	private String getSimpleLabel() {
		return "Channel "+ getOriginalChannelIndex();
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public Color getColor() {
		return originalLutCol;
	}

	public void setColor(Color originalLutCol) {
		this.originalLutCol = originalLutCol;
	}

	public int getOriginalStackIndex() {
		return originalStackIndex;
	}

	
	public void setOriginalStackIndex(int originalStackIndex) {
		this.originalStackIndex = originalStackIndex;
	}

	public int getOriginalChannelIndex() {
		return originalChannelIndex;
	}

	public void setOriginalChannelIndex(int originalChannelIndex) {
		this.originalChannelIndex = originalChannelIndex;
	}

	public String getAdditionalString() {
		return additional;
	}

	public void setAdditionalString(String additional) {
		this.additional = additional;
	}
	
	/**Getter and setter methods for channel names. not updated properly when channels swap*/
	public void setRealChannelName(String realChannelName) {
		this.realChannelName=realChannelName;
		
	}
	public String getRealChannelName() {
		return realChannelName;
	}
	
	
	/**updates this channel entry to match the given channel
	 in the event of a color change, this may be called*/
	public void updateFrom(ChannelEntry ce) {
		this.label=ce.label;
		this.originalLutCol=ce.originalLutCol;//lut color change
		this.originalChannelIndex=ce.originalChannelIndex;//in the even the channel order was change
		this.realChannelName=ce.getRealChannelName();

	}
	
	
	

}
